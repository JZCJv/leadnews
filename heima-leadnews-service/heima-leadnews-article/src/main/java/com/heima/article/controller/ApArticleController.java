package com.heima.article.controller;


import com.heima.article.service.ApArticleService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ArticleDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
@Api(value = "App首页展示管理", tags = "leadnews-article", description = "leadnews-article")
@Slf4j
public class ApArticleController {

    @Autowired
    private ApArticleService apArticleService;

    @ApiOperation("加载首页文章的接口")
    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleDto articleDto) {

        log.info("加载首页文章:{}", articleDto);
        return apArticleService.loadApArticle(articleDto, 1); //1 代表上拉(小于判断)

    }


    @ApiOperation("加载更多文章（上拉）的接口")
    @PostMapping("/loadmore")
    public ResponseResult loadmore(@RequestBody ArticleDto articleDto) {
        log.info("加载更多文章（上拉）:{}", articleDto);

        return apArticleService.loadApArticle(articleDto, 1); //1 代表上拉(小于判断)

    }


    @ApiOperation("加载更多文章（下拉）的接口")
    @PostMapping("/loadnew")
    public ResponseResult loadnew(@RequestBody ArticleDto articleDto) {
        log.info("加载更多文章（下拉):{}", articleDto);

        return apArticleService.loadApArticle(articleDto, 2); //2 代表下拉（大于判断）

    }
}
