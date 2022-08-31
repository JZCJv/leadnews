package com.heima.article.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PathVariable;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableKnife4j
public class SwaggerConfiguration {

    @Bean
    public Docket buildDocket() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(bulidApiInfo())
                .select()
                // 要扫描的API(Controller)基础包
                .apis(RequestHandlerSelectors.basePackage("com.heima"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo bulidApiInfo() {
        Contact contact = new Contact("CJZ", "url", "邮箱");
        return  new ApiInfoBuilder()
                .title("黑马头条-用户管理API文档")
                .description("用户管理服务API")
                .contact(contact)
                .version("1.0.0")
                .build();

    }


}
