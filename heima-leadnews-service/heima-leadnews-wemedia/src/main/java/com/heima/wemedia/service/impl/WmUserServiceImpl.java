package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.common.dtos.ResponseResult;
import com.heima.common.exception.LeadNewsException;
import com.heima.model.wemedia.dtos.WmLoginDto;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.BCrypt;
import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.RsaUtils;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * impl wm用户服务
 *
 * @author CAIJIAZHEN
 * @date 2022/09/04
 */
@Transactional
@Service
public class WmUserServiceImpl extends ServiceImpl<WmUserMapper, WmUser> implements WmUserService {


    @Value("${leadnews.jwt.privateKeyPath}")
    private String privateKeyPath;

    @Value("${leadnews.jwt.expire}")
    private int expire;


    /**
     * 自媒体登录
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult login(WmLoginDto dto) {

        //处理前端传过来的数据
        if (StringUtils.isEmpty(dto.getName()) || StringUtils.isEmpty(dto.getPassword())) {

            throw new LeadNewsException(AppHttpCodeEnum.PARAM_INVALID);

        }

        //验证账户是否存储
        QueryWrapper<WmUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", dto.getName());
        WmUser loginUser = getOne(queryWrapper);

        if (loginUser == null) {
            throw new LeadNewsException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
        }

        //验证密码是否正确 前端的密码和数据库的比较
        if (!BCrypt.checkpw(dto.getPassword(), loginUser.getPassword())) {
            throw new LeadNewsException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        try {
            //读取私钥
            PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);


            //去除敏感信息
            loginUser.setPassword(null);
            //产生token
            String token = JwtUtils.generateTokenExpireInMinutes(loginUser, privateKey, expire);


            //返回给前端
            Map<Object, Object> map = new HashMap<>();
            map.put("token", token);
            map.put("user", loginUser);

            return ResponseResult.okResult(map);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }
}
