package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.LeadNewsException;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.BeanHelper;
import com.heima.utils.common.JsonUtils;
import com.heima.utils.common.ThreadLocalUtils;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmNewsService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * impl wm新闻服务
 *
 * @author CAIJIAZHEN
 * @date 2022/09/04
 */
@Transactional
@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    private WmMaterialMapper wmMaterialMapper;

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    /**
     * 查询自媒体文章列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult fileList(WmNewsPageReqDto dto) {

        //判断参数是否为空 设置默认值
        dto.checkParam();

        WmUser user = (WmUser) ThreadLocalUtils.get();
        if (user == null) {
            throw new LeadNewsException(AppHttpCodeEnum.NEED_LOGIN);

        }
        //设置分页查询条件
        IPage<WmNews> ipage = new Page<>(dto.getPage(), dto.getSize());
        QueryWrapper<WmNews> queryWrapper = new QueryWrapper<>();

        //判断登录的用户
        queryWrapper.eq("user_id", user.getId());

        //所属频道id
        if (dto.getChannelId() != null) {
            queryWrapper.eq("channel_id", dto.getChannelId());
        }

        if (dto.getStatus() != null) {
            //状态
            queryWrapper.eq("status", dto.getStatus());

        }

        //关键字
        if (dto.getKeyword() != null) {
            queryWrapper.like("title", dto.getKeyword());
        }

        if (dto.getBeginPubDate() != null && dto.getBeginPubDate() != null) {
            //时间
            queryWrapper.between("created_time", dto.getBeginPubDate(), dto.getEndPubDate());

        }

        //按照创建时间倒序
        queryWrapper.orderByDesc("created_time");

        ipage = page(ipage, queryWrapper);

        //封装分页数据
        PageResponseResult pageResponseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) ipage.getTotal());
        pageResponseResult.setCode(200);
        pageResponseResult.setErrorMessage("查询成功");
        pageResponseResult.setData(ipage.getRecords());


        return pageResponseResult;
    }

    /**
     * 发表自媒体文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult submit(WmNewsDto dto) {


        //准备实体类
        WmNews wmNews = BeanHelper.copyProperties(dto, WmNews.class);

        //获取当前登录的用户
        WmUser wmUser = (WmUser) ThreadLocalUtils.get();
        if (wmUser == null) {
            throw new LeadNewsException(AppHttpCodeEnum.NEED_LOGIN);
        }

        //文章作者的id
        wmNews.setUserId(wmUser.getId());

        //获取文章内容图片
        List<String> contentImages = getContentImageFromNews(wmNews);


        //1、自动封面处理 type==-1
        if (wmNews.getType() == -1) {

            //如果有无图图
            if (contentImages.size() == 0) {
                wmNews.setType((short) 0);
                wmNews.setImages(null);

            }

            //如果有单张图 或两张图
            if (contentImages.size() >= 1 && contentImages.size() <= 2) {
                wmNews.setType((short) 1);
                wmNews.setImages(contentImages.get(0));

            }


            //如果有三张图 或多张图
            if (contentImages.size() >= 3) {
                wmNews.setType((short) 3);
                //subList(): 从集合截割出元素
                List<String> imageList = contentImages.subList(0, 3); //前三张图 包前不包后
                wmNews.setImages(imageList.stream().collect(Collectors.joining(","))); //逗号号切割

            }


        } else {
            //非自动封面处理
            //把images使用逗号拼接成字符串存入
            List<String> images = dto.getImages();
            if (CollectionUtils.isNotEmpty(images)) {
                wmNews.setImages(images.stream().collect(Collectors.joining(",")));

            }
        }


        //2、判断当前操作是新增还是修改

        if (dto.getId() == null) {
            //新增
            save(wmNews);

        } else {
            //修改
            updateById(wmNews);

            //删除当前文章与素材的关系
            QueryWrapper<WmNewsMaterial> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("news_id", dto.getId());
            wmNewsMaterialMapper.delete(queryWrapper);
        }


        //在文章发布成功后调用审核的方法  待审核的状态为1
        if (dto.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {

            try {
                wmNewsAutoScanService.autoScanWmNews(wmNews);

            } catch (Exception e) {
                log.info("审核失败，原因是：{}",e.getMessage());
                throw  new LeadNewsException( AppHttpCodeEnum.SERVER_ERROR);//服务器异常
            }

        }







        //3、绑定文章和内容素材、封面素材的关系

        //内容素材与文章的关系绑定
        //根据素材的url地址查询素材id
        List<Integer> materialIds = getMaterialIdsFromUrl(contentImages);

        if (CollectionUtils.isNotEmpty(materialIds)) {

            wmNewsMaterialMapper.saveNewMaterialIds(materialIds, wmNews.getId(), 0);

        }


        //绑定封面素材与文章的关系绑定

        //获取图片的url //可能有多个
        String images = wmNews.getImages();
        //切割
        List<String> imageList = Arrays.asList(images.split(","));
        List<Integer> coverMaterialIds = getMaterialIdsFromUrl(imageList);
        if (CollectionUtils.isNotEmpty(coverMaterialIds)) {

            wmNewsMaterialMapper.saveNewMaterialIds(materialIds, wmNews.getId(), 1);

        }



        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    /**
     * 根据素材的url地址查询素材id
     *
     * @param contentImages
     */
    private List<Integer> getMaterialIdsFromUrl(List<String> contentImages) {


        if (CollectionUtils.isNotEmpty(contentImages)) {

            QueryWrapper<WmMaterial> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("url", contentImages);
            List<WmMaterial> wmMaterialList = wmMaterialMapper.selectList(queryWrapper);

            if (CollectionUtils.isNotEmpty(wmMaterialList)) {


                return wmMaterialList.stream().map(WmMaterial::getId).collect(Collectors.toList());
            }

        }


        return null;
    }


    /**
     * 获取文章内容的图片
     *
     * @param wmNews
     * @return
     */
    public List<String> getContentImageFromNews(WmNews wmNews) {

        ArrayList<String> contentImages = new ArrayList<>();

        if (StringUtils.isNotBlank(wmNews.getContent())) {

            List<Map> mapList = JsonUtils.toList(wmNews.getContent(), Map.class);

            mapList.forEach(map -> {
                if (map.get("type").equals("image")) {

                    contentImages.add((String) map.get("value"));
                }
            });
        }
        return contentImages;
    }
}
