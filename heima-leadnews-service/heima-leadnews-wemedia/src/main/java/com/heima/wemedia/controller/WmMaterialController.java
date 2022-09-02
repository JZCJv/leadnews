package com.heima.wemedia.controller;


import com.heima.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.objects.annotations.ScriptClass;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 自媒体素材管理
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/material")
@Api( value= "自媒体素材管理" ,tags = "material/upload_picture")
public class WmMaterialController {


    @Autowired
    private WmMaterialService wmMaterialService;





    @ApiOperation("自媒体上传素材")
    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile ) {
        log.info("自媒体上传素材:{}",multipartFile);
        return wmMaterialService.uploadPicture(multipartFile);
    }


}
