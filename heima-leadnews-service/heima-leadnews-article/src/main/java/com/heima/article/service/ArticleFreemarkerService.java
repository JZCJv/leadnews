package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

public interface ArticleFreemarkerService {


    /**
     * 生成文章内容的详情页面，上传到Minio
     *
     * @param apArticle 美联社文章
     * @param content   内容
     */
    public void buildArticleToMinIO(ApArticle apArticle,String content);
}
