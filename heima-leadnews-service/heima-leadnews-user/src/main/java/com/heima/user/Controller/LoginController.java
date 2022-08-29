package com.heima.user.Controller;

import com.heima.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.user.service.ApUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
@Api(value = "App用户登录",tags = "userLogin",description = "App用户登录--")
public class LoginController {

    @Autowired
    private ApUserService userService;

    /**
     * 登录
     * @param loginDto
     * @return
     */
    @ApiOperation("用户登录")
    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto loginDto) {


        return userService.login(loginDto);
    }
}
