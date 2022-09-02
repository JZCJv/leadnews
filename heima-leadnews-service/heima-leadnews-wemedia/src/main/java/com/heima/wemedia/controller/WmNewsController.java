package com.heima.wemedia.controller;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
@Api(value = "查询自媒体文章列表")
public class WmNewsController {


    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 查询自媒体文章列表
     * @return
     */
    @ApiOperation("查询自媒体文章列表的接口")
    @PostMapping("/list")
    public ResponseResult fileList(@RequestBody WmNewsPageReqDto wmNewsPageReqDto) {

        return wmNewsService.fileList(wmNewsPageReqDto);
    }

}
