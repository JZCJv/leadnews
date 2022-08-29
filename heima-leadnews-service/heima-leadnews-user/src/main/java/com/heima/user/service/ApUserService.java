package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;

import java.util.Map;

/**
 * <p>
 * APP用户信息表 服务类
 * </p>
 *
 * @author itheima
 * @since 2022-07-20
 */
public interface ApUserService extends IService<ApUser> {

    /**
     * 登录
     * @param loginDto
     * @return
     */
    ResponseResult login(LoginDto loginDto);
}
