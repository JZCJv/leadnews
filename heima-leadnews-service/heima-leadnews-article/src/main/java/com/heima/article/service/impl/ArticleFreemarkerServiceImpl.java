package com.heima.article.service.impl;

import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.minio.MinIOFileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.utils.common.JsonUtils;
import com.jayway.jsonpath.internal.function.numeric.Min;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.SameLen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private MinIOFileStorageService storageService;

    @Autowired
    private ApArticleMapper apArticleMapper;


    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {

        if (apArticle != null && StringUtils.isNotEmpty(content)) {
            try {
                //读取文章首页模板
                Template template = configuration.getTemplate("article.ftl");
                //创建map集合存储模板
                Map<String, Object> data = new HashMap<>();
                //将string转为List
                List<Map> articleContent = JsonUtils.toList(content, Map.class);
                data.put("content", articleContent);

                //生成静态页数据
                //临时字符缓存流
                StringWriter writer = new StringWriter();
                template.process(data, writer);//将静态数据临时写入字符缓存流


                //把静态页数据存储到minio中
                String fileName = apArticle.getId() + "html";
                InputStream inputStream = new ByteArrayInputStream(writer.toString().getBytes());

                String url = storageService.uploadHtmlFile("", fileName, inputStream);

                //更新到数据库中的url
                apArticle.setStaticUrl(url);
                apArticleMapper.updateById(apArticle);



            } catch (Exception e) {
                e.printStackTrace();
                log.error("生成静态页面失败：{}",e.getMessage());
            }

        }


    }
}
