package com.heima.wemedia;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 自媒体服务
 */
@EnableDiscoveryClient  //<spring.cloud.version>Hoxton.SR9 版本的可以省略这个注解
@MapperScan("com.heima.wemedia.mapper")
@SpringBootApplication
@EnableFeignClients("com.heima.*.feign")//开启feign调用功能
@EnableAsync //开启异步线程
public class WemediaApplication {
    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class, args);
    }

    /**
     * MyBatis pusl 分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return  new PaginationInterceptor();
    }
}
