package com.heima.wemedia.controller;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.service.WmNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/news")
@Api(value = "自媒体文章列表", tags = "自媒体文章列表")
public class WmNewsController {


    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 查询自媒体文章列表
     *
     * @return
     */
    @ApiOperation("查询自媒体文章列表的接口")
    @PostMapping("/list")
    public ResponseResult fileList(@RequestBody WmNewsPageReqDto wmNewsPageReqDto) {

        log.info("查询自媒体文章列表");
        return wmNewsService.fileList(wmNewsPageReqDto);
    }


    /**
     * 发表自媒体文章
     *
     * @param wmNewsDto
     * @return
     */
    @ApiOperation("发表自媒体文章的接口")
    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto wmNewsDto) {
        log.info("发表自媒体文章");

        ResponseResult submit = wmNewsService.submit(wmNewsDto);

        log.info("文章");

        return submit;
    }


    /**
     * 文章回显
     * @return
     */
    @ApiOperation("文章回显的接口")
    @GetMapping("/one/{id}")
    public  ResponseResult findOne ( @PathVariable("id") Long id) {

        return ResponseResult.okResult(wmNewsService.getById(id));
    }
}
