package com.heima.article;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


/**
 * 启动类
 */
@SpringBootApplication
@MapperScan("com.heima.article.mapper")
public class ArticleApplicstion {
    public static void main(String[] args) {
        SpringApplication.run(ArticleApplicstion.class,args);
    }


    /**
     * MyBatis分页插件
     */
    @Bean
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }




}
