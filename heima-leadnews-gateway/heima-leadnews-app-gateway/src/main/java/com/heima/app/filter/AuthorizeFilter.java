package com.heima.app.filter;

import com.heima.common.dtos.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.common.JwtUtils;
import com.heima.utils.common.Payload;
import com.heima.utils.common.RsaUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPatch;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.security.PublicKey;
import java.util.List;

/**
 * 统一鉴权
 */
@Component
@Order(1)
@Slf4j
public class AuthorizeFilter implements GlobalFilter {


    @Value("${leadnews.jwt.publicKeyPath}")
    private String publicKeyPath;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("网关微服务app");

        //获取请求和响应
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //获取请求头 判断请求头是否为登录的 是就放行
        String uri = request.getURI().getPath(); //  /user/api/v1/login/login_auth
        if (uri.contains("/login")) {
            //放行
            return chain.filter(exchange);
        }

        //获取token
        String token = request.getHeaders().getFirst("token");
        //判断token是否为空
        if (StringUtils.isEmpty(token)) {
            //为空 返回401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //中断请求
            return response.setComplete();
        }
        //获取公钥
        try {
            //从配置文件中获取公钥信息
            PublicKey publicKey = RsaUtils.getPublicKey(publicKeyPath);

            //获取token中的用户信息
            Payload<ApUser> payload = JwtUtils.getInfoFromToken(token, publicKey, ApUser.class);

            //从token获取用户信息
            ApUser user = payload.getInfo();

            //放入请求头 Header
            request.mutate().header("token", user.getId().toString());

            //放行
            return chain.filter(exchange);

        } catch (Exception e) {
            e.printStackTrace();
            //返回401
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            //中断请求
            return response.setComplete();
        }


    }


}
