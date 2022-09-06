package com.heima.wemedia.service.impl;

import com.heima.article.feign.ApArticleFeign;
import com.heima.common.aliyun.GreenImageScan;
import com.heima.common.aliyun.GreenTextScan;
import com.heima.common.constants.RedisConstant;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.minio.MinIOFileStorageService;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.BeanHelper;
import com.heima.utils.common.JsonUtils;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 文章自动审核：抽取文章文本和图片
 *
 * @author CAIJIAZHEN
 * @date 2022/09/04
 */
@Slf4j
@Service
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private MinIOFileStorageService storageService;

    @Autowired
    private WmNewsMapper wmNewsMapper;


    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private WmChannelMapper wmChsnnrlMapper;

    @Autowired
    private ApArticleFeign apArticleFeign;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    private ITesseract tesseract; //图片文字识别


    /**
     * 自动扫描wm新闻
     *
     * @param wmNews wm新闻
     */
    @GlobalTransactional
    @Async //标明当前方法是一个异步方法
    public void autoScanWmNews(WmNews wmNews) {


        //判断当前状态是否为审核状态
        if (wmNews.getStatus() != 1) {
            return;
        }


        //判断状态是否为待审核状态
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {


            //提取文章中的图片
            ArrayList<byte[]> imageList = getImageFromWmNews(wmNews);

            //提取文章中的文字
            List<String> textList = getTextFromWmNews(wmNews,imageList);


            //自定义敏感词
            if (CollectionUtils.isNotEmpty(textList)) {

                boolean flag = handleSensitiveScan(textList, wmNews);
                if (!flag) {
                    return;  //如果审核失败则退出
                }
            }


            //提交给阿里云内容接口检测，根据结果修改文章
            //把文字交给阿里云检测
            if (CollectionUtils.isNotEmpty(textList)) {

                try {
                    Map result = greenTextScan.greeTextScan(textList);
                    //处理检测结果 抽取出来的方法
                    boolean flag = handleScanResult(result, wmNews);
                    if (!flag) {
                        return; //如果审核失败，则直接退出
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("调用阿里云接口失败，原因：{}" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }


            //把图片交给阿里云检测
            if (CollectionUtils.isNotEmpty(imageList)) {

                try {
                    Map result = greenImageScan.imageScan(imageList);
                    // 处理检测结果 抽取出来的方法
                    boolean flag = handleScanResult(result, wmNews);

                    if (!flag) {
                        return; //如果审核失败，则直接退出
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("调用阿里云接口失败，原因：{}" + e.getMessage());
                    throw new RuntimeException(e);


                }


            }


            //判断用户选择的发布时间是否大于当前时间，如果大于则标记为8（代表暂时不发布）

            if (wmNews.getPublishTime() != null && wmNews.getPublishTime().after(new Date())) {
                //该文章为定时发布
                //TODO 实现定时发布业务

                //暂时将状态改为8 （待发布）
                wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
                wmNews.setReason("文章审核通过，进入定时发布队列");
                wmNewsMapper.updateById(wmNews);
            }


            //如果发布时间小于等于当前时间，立即发布（把文章存入App端的库中，修改文章状态为9）
            //立即发表文章
            publishArticle(wmNews);


        }


    }


    /**
     * 自定义敏感词
     *
     * @param textList 文本列表
     * @param wmNews   wm新闻
     * @return boolean
     */
    private boolean handleSensitiveScan(List<String> textList, WmNews wmNews) {

        boolean flag = true;

        List<String> wordList = null;

        //从redis查询数据
        String redisData = redisTemplate.opsForValue().get(RedisConstant.SENSITIVE_WORD);
        if (StringUtils.isEmpty(redisData)) {

            //从数据库查询所有敏感词
            List<WmSensitive> wmSensitiveList = wmSensitiveMapper.selectList(null);

            if (CollectionUtils.isNotEmpty(wmSensitiveList)) {

                wordList = wmSensitiveList.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());


                //把敏感词存入redis
                redisTemplate.opsForValue().set(RedisConstant.SENSITIVE_WORD, JsonUtils.toString(wordList));


            }

        }  else {

                //转换格式
                wordList = JsonUtils.toList(redisData, String.class);

            }

                //构建敏感词词库
                SensitiveWordUtil.initMap(wordList);

                if (CollectionUtils.isNotEmpty(textList)) {
                    String collect = textList.stream().collect(Collectors.joining(""));

                    //匹配敏感词库
                    Map<String, Integer> result = SensitiveWordUtil.matchWords(collect);
                    if (result != null && result.size() > 0) {

                        //获取违规词
                        Set<String> keys = result.keySet();

                        //修改文章状态
                        wmNews.setStatus(WmNews.Status.FAIL.getCode());
                        wmNews.setReason("文章存在违规词" + keys);
                        wmNewsMapper.updateById(wmNews);
                        flag = false;
                    }
                }

        return flag;
    }


    /**
     * 发表文章
     *
     * @param wmNews wm新闻
     */
    public void publishArticle(WmNews wmNews) {

        ApArticleDto articleDto = BeanHelper.copyProperties(wmNews, ApArticleDto.class);

        // 将自媒体表的App文章ID 和 App文章的ID 绑定
        articleDto.setId(wmNews.getArticleId());


        //设置作者信息
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {

            articleDto.setAuthorId(Long.valueOf(wmUser.getId()));
            articleDto.setAuthorName(wmUser.getNickname());
        }

        //设置频道信息
        WmChannel wmChannel = wmChsnnrlMapper.selectById(wmNews.getChannelId());

        if (wmChannel != null) {
            articleDto.setChannelId(wmChannel.getId());
            articleDto.setChannelName(wmChannel.getName());

        }


        //文章布局（封面类型）
        articleDto.setLayout(wmNews.getType());
        articleDto.setFlag((byte) 0);
        articleDto.setLikes(0);
        articleDto.setCollection(0);
        articleDto.setViews(0);
        articleDto.setComment(0);
        //保存App文章

        ResponseResult<Long> responseResult = apArticleFeign.save(articleDto);


        //TODO  模拟Seata分布式异常
       // int a= 9/0;

        if (responseResult.getCode().equals(200)) {

            Long articleID = responseResult.getData();
            //修改自媒体文章数据  状态为9
            wmNews.setStatus(WmNews.Status.PUBLISHED.getCode());
            wmNews.setReason("文章已发布");
            wmNews.setArticleId(articleID);//设置app端的文章ID
            wmNewsMapper.updateById(wmNews);

        }

    }


    /**
     * 处理文字检测结果
     *
     * @param result 结果
     * @param wmNews wm新闻
     * @return boolean
     */
    private boolean handleScanResult(Map result, WmNews wmNews) {

        boolean flag = false;

        String suggestion = (String) result.get("suggestion");
        if (StringUtils.isNotEmpty(suggestion)) {

            //审核不通过
            if ("block".equals(suggestion)) {

                wmNews.setStatus(WmNews.Status.FAIL.getCode());
                wmNews.setReason("文章存在违规内容，请检查");

                wmNewsMapper.updateById(wmNews);
            }

            //待人工审核
            if ("review".equals(suggestion)) {

                wmNews.setStatus(WmNews.Status.ADMIN_AUTH.getCode());
                wmNews.setReason("文章存在可疑内容，待人工审核");

                wmNewsMapper.updateById(wmNews);
            }

            //审核通过
            if ("pass".equals(suggestion)) {
                wmNews.setStatus(WmNews.Status.SUCCESS.getCode());
                return true;

            }
        }


        return flag;
    }


    /**
     * 提取文章中的文字
     *
     * @param wmNews
     * @return
     */
    private List<String> getTextFromWmNews(WmNews wmNews,List<byte[]> imageList) {

        //存储文字
        List<String> textList = new ArrayList<>();

        //标题文字
        if (StringUtils.isNotEmpty(wmNews.getTitle())) {
            textList.add(wmNews.getTitle());
        }

        //标签文字

        if (StringUtils.isNotEmpty(wmNews.getLabels())) {
            textList.add(wmNews.getLabels());
        }

        //内容文字
        if (StringUtils.isNotBlank(wmNews.getContent())) {

            List<Map> mapList = JsonUtils.toList(wmNews.getContent(), Map.class);
            mapList.forEach(map -> {
                if (map.get("type").equals("text")) {
                    textList.add((String) map.get("value"));
                }
            });

        }

        //识别所有图片，提取文字
        if (CollectionUtils.isNotEmpty(imageList)) {

            for (byte[] image : imageList) {

                try {
                    InputStream inputStream = new ByteArrayInputStream(image);
                    BufferedImage bufferedImage = ImageIO.read(inputStream);
                    String result = tesseract.doOCR(bufferedImage);
                    if (StringUtils.isNotEmpty(result)) {
                        textList.add(result);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("ORC识别失败：{}",e.getMessage());
                }
            }
        }

        return textList;
    }


    /**
     * 提取文章中的图片
     *
     * @param wmNews
     * @return
     */
    private ArrayList<byte[]> getImageFromWmNews(WmNews wmNews) {


        //设计一个Set存储所有图片路径（用Set去重）
        Set<String> urlSet = new HashSet<>();


        //内容图片
        if (StringUtils.isNotEmpty(wmNews.getContent())) {

            List<Map> mapList = JsonUtils.toList(wmNews.getContent(), Map.class);
            mapList.forEach(map -> {
                if (map.get("type").equals("image")) {
                    urlSet.add((String) map.get("value"));
                }
            });
        }

        //封面图片
        if (StringUtils.isNotEmpty(wmNews.getImages())) {
            String[] array = wmNews.getImages().split(",");
            urlSet.addAll(Arrays.asList(array));
        }


        //根据url到MinIO下载的图片
        ArrayList<byte[]> imageList = new ArrayList<>();

        //判断集合是否为空
        if (CollectionUtils.isNotEmpty(urlSet)) {
            for (String url : urlSet) {
                //下载图片
                byte[] image = storageService.downLoadFile(url);
                imageList.add(image);
            }
        }

        return imageList;

    }
}
