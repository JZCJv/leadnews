package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ApArticleMapper extends BaseMapper<ApArticle> {

    /**
     * 加载首页文章
     * @param articleDto
     * @param type
     * @return
     */
    List<ApArticle> loadApArticle(@Param("articleDto") ArticleDto articleDto, @Param("type") int type);

}
