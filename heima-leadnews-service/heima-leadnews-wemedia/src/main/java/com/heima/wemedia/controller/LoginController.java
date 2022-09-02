package com.heima.wemedia.controller;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.wemedia.service.WmUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 自媒体登录
 */
@RestController
@Slf4j
@Api(value = "自媒体登录管理" ,tags = "WmUserLogin")
public class LoginController {

    @Autowired
    private WmUserService wmUserService;

    @PostMapping("/login/in")
    @ApiOperation("自媒体登录的接口")
    public ResponseResult login(@RequestBody WmLoginDto wmLoginDto) {
        log.info("自媒体登录");

        return wmUserService.login(wmLoginDto);
    }
}
