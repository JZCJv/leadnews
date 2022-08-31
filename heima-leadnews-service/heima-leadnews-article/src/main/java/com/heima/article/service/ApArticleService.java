package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;

import java.util.List;

public interface ApArticleService extends IService<ApArticle> {

    /**
     * 加载首页
     * @param articleDto
     * @param  type       1上拉、2下拉
     * @return
     */
    ResponseResult<List<ApArticle>> loadApArticle(ArticleDto articleDto, int type);
}
