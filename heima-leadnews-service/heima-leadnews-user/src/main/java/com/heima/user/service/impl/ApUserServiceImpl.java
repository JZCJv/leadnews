package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.common.exception.LeadNewsException;
import com.heima.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.BCrypt;
import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.RsaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * APP用户信息表 服务实现类
 * </p>
 *
 * @author itheima
 * @since 2022-07-20
 */
@Service
@Transactional
@Slf4j
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Value("${leadnews.jwt.privateKeyPath}")
    private String privateKeyPath; //私有文件路径

    @Value("${leadnews.jwt.expire}")
    private Integer expire; //token过期时间


    /**
     * 登录
     *
     * @param loginDto
     * @return
     */
    @Override
    public ResponseResult<Map> login(LoginDto loginDto) {

        log.info("登录");

        //判断是用户还是游客
        if (StringUtils.isNotBlank(loginDto.getPhone()) && StringUtils.isNoneBlank(loginDto.getPassword())) {

            QueryWrapper<ApUser> queryWrapper = new QueryWrapper<>();
            //验证身份是否合法
            queryWrapper.eq("phone", loginDto.getPhone());
            //获取用户
            ApUser loginUser = getOne(queryWrapper);

            //判断用户是否存在
            if (loginUser == null) {
               // throw new RuntimeException("用户不存在");
                throw new LeadNewsException(AppHttpCodeEnum.AP_USER_DATA_NOT_EXIST);
            }

            //判断密码是否正确 数据库密码 和 前端传过来的密码 判断
            boolean checkpw = BCrypt.checkpw(loginDto.getPassword(), loginUser.getPassword());
            if (!checkpw) {
                throw new LeadNewsException(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }

            try {
                //读取rsa私钥
                PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);

                //生成token
                loginUser.setPassword(null);//过滤掉敏感信息
                String token = JwtUtils.generateTokenExpireInMinutes(loginUser, privateKey, expire);

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("user", loginUser);

              return   ResponseResult.okResult(data);


            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);

            }


        }else {

            //游客

            try {
                //读取rsa私钥
                PrivateKey privateKey = RsaUtils.getPrivateKey(privateKeyPath);

                ApUser visitUser = new ApUser();
                visitUser.setId(0); //0代表游客
                //生成token
                String token = JwtUtils.generateTokenExpireInMinutes(visitUser, privateKey, expire);

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);

              return   ResponseResult.okResult(data);


            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);

            }
        }
    }

}
