package com.heima.article;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.common.minio.MinIOFileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.utils.common.JsonUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.swing.plaf.PanelUI;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArticleApplication.class)
public class ApArticleTest {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    //工具类
    @Autowired
    private MinIOFileStorageService minIOFileStorageService;

    //实体类
    @Autowired
    private ApArticleMapper apArticleMapper;


    /**
     * 为文章生成详情静态页面
     */
    @Test
    public void testUploadFile() throws Exception {

        //需要生成文章的id
        Long id = 1383827787629252610L;
        QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
        //查询条件为id
        queryWrapper.eq("article_id", id);

        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);

        //判断对象和内容不为空
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            //读取文章详情页模板(静态)
            Template template = configuration.getTemplate("article.ftl");

            Map<String, Object> map = new HashMap<>();
            List<Map> context = JsonUtils.toList(apArticleContent.getContent(), Map.class);
            map.put("context", context);
            //生成文章的静态页面
            StringWriter writer = new StringWriter();
            template.process(map, writer);

            //上传到minio 获取url地址
            InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());

            String fileName = id + ".html";

            String staticUrl = minIOFileStorageService.uploadHtmlFile(null, fileName, inputStream);

            //修改文章表的url
            ApArticle apArticle = new ApArticle();
            apArticle.setStaticUrl(staticUrl);
            apArticle.setId(id);

            apArticleMapper.updateById(apArticle);

            //释放资源
            inputStream.close();
            writer.close();


        }


    }

    /**
     * 为文章生成详情静态页面2
     */

    @Test
    public void testUploadFile2() throws Exception {
        Long id = 1383827787629252610L;

        //设置查询条件
        QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);

        //判断是否为空 和是否有内容
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {

            //读取文章详情模板
            Template template = configuration.getTemplate("article.ftl");

            Map<String, Object> map = new HashMap<>();
            //将查询到的文档转为json存到map集合中
            List<Map> context = JsonUtils.toList(apArticleContent.getContent(), Map.class);
            map.put("context", context);
            //字符缓冲流
            StringWriter writer = new StringWriter();


            //生成模板
            template.process(map, writer);
            System.out.println("writer = " + writer);
            System.out.println("template = " + template);
            System.out.println("map = " + map);


            //上传到minion上的文件名字
            String fileName = id + ".html";
            InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());

            //上传到minio
            String url = minIOFileStorageService.uploadHtmlFile(null, fileName, inputStream);
            ApArticle apArticle = new ApArticle();
            apArticle.setId(id);
            apArticle.setStaticUrl(url);
            apArticleMapper.updateById(apArticle);

            //关闭资源
            inputStream.close();
            writer.close();


        }

    }


    /**
     * 为文章生成静态页面
     *
     * @throws Exception
     */
    @Test
    public void testUploadFile3() throws Exception {

        Long id = 1383827787629252610L;
        //设置条件
        QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);

        //判断是否为空和是否有内容
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            //读取文章模板
            Template template = configuration.getTemplate("article.ftl");


            //把内容转为json 存储到Map集合中
            List<Map> context = JsonUtils.toList(apArticleContent.getContent(), Map.class);

            Map<String, Object> map = new HashMap<>();
            map.put("context", context);

            //生成文章的静态页面
            StringWriter writer = new StringWriter();
            template.process(map, writer);
            InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());

            String fileName = id + ".html";

            //上传文件
            String url = minIOFileStorageService.uploadHtmlFile(null, fileName, inputStream);

            //根据id设置url
            ApArticle apArticle = new ApArticle();
            apArticle.setStaticUrl(url);
            apArticle.setId(id);

            //修改内容
            apArticleMapper.updateById(apArticle);

            //关闭资源
            inputStream.close();
            writer.close();


        }


    }


}
