package com.heima.common.minio;


import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化MinIO连接对象的配置类
 */
@Configuration
@EnableConfigurationProperties(MinIOProperties.class)
public class MinIOConfiguration {

    @Autowired
    private  MinIOProperties minIOProperties;


    @Bean
    public MinioClient createMinioClient() {
        return MinioClient.builder()
                .credentials(minIOProperties.getAccessKey(), minIOProperties.getSecretKey())
                .endpoint(minIOProperties.getEndpoint())
                .build();
    }


}
