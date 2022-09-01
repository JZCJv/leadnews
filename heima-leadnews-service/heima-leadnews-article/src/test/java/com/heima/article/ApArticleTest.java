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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
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

        Long id = 1383827787629252610L;
        QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", id);
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);

        //判断对象和内容不为空
        if (apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())) {
            //读取文章详情页模板
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


}
