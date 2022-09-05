package com.heima.article.feign;


import com.heima.common.dtos.ResponseResult;
import com.heima.model.article.dtos.ApArticleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "leadnews-article" ,path = "/api/v1/article")
public interface ApArticleFeign {

    /**
     * 新增App文章
     */
    @PostMapping("/save")
    public ResponseResult<Long> save(@RequestBody ApArticleDto dto);
}
