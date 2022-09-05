package com.heima.article.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ApArticleDto;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.utils.common.BeanHelper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 文章服务impl
 *
 * @author CAIJIAZHEN
 * @date 2022/09/04
 */
@Service
@Transactional
public class ArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

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

    /**
     * 新增App文章
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveArticle(ApArticleDto dto) {

        ApArticle apArticle = BeanHelper.copyProperties(dto, ApArticle.class);

        //判断dto的id是否为空，如果为空，新增，否则，执行修改
        if (dto.getId() == null) {
            save(apArticle);


            //保存ap_article_config
            ApArticleConfig apArticleConfig = new ApArticleConfig();
            apArticleConfig.setArticleId(apArticle.getId());
            apArticleConfig.setIsComment(true);
            apArticleConfig.setIsForward(true);
            apArticleConfig.setIsDown(false);
            apArticleConfig.setIsDelete(false);
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 ap_article_content
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);


        } else {

            //修改
            //ap_article
            updateById(apArticle);

            QueryWrapper<ApArticleContent> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("article_id", dto.getId());
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(queryWrapper);
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);


        }

        //返回文章ID
        return ResponseResult.okResult(apArticle.getId());
    }
}
