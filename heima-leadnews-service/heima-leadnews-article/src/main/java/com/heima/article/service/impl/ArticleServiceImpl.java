package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    /**
     * 加载首页文章
     *
     * @param articleDto
     * @param type       1上拉、2下拉
     * @return
     */
    @Override
    public ResponseResult loadApArticle(ArticleDto articleDto, int type) {

        if (articleDto.getMinBehotTime() == null) articleDto.setMinBehotTime(new Date());
        if (articleDto.getMaxBehotTime() == null) articleDto.setMaxBehotTime(new Date());
        if (articleDto.getSize() == null) articleDto.setSize(10);
        if (articleDto.getTag() == null) articleDto.setTag("__all__");
        List<ApArticle> apArticleList = apArticleMapper.loadApArticle(articleDto, type);
        return ResponseResult.okResult(apArticleList);


    }
}
