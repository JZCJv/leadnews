package com.heima.wemedia.controller;


import com.heima.common.dtos.PageResponseResult;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.ScriptClass;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * wm材料控制器
 * 自媒体素材管理
 *
 * @author CAIJIAZHEN
 * @date 2022/09/04
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/material")
@Api(value = "自媒体素材管理", tags = "material/upload_picture")
public class WmMaterialController {


    @Autowired
    private WmMaterialService wmMaterialService;


    @ApiOperation("自媒体上传素材的接口")
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        log.info("自媒体上传素材:{}", multipartFile);
        return wmMaterialService.uploadPicture(multipartFile);
    }

    /**
     * 查询素材
     *
     * @return
     */
    @ApiOperation("自媒体素材列表查询的接口")
    @PostMapping("/list")
    public PageResponseResult fileList(@RequestBody WmMaterialDto dto) {
        log.info("查询素材：{}", dto);
        return wmMaterialService.fileList(dto);
    }

    /**
     * 收藏素材
     */
    @ApiOperation("收藏素材接口")
    @GetMapping("/collect/{id}")
    public  ResponseResult collect(@PathVariable Integer id) {
        log.info("收藏素材");

       return   wmMaterialService.collect(id);

    }

    /**
     * 取消收藏素材
     */
    @ApiOperation("取消收藏素材接口")
    @GetMapping("/cancel_collect/{id}")
    public  ResponseResult cancelCollect(@PathVariable Integer id) {
        log.info("取消收藏素材");

     return    wmMaterialService.cancelCollect(id);

    }



}
