package com.bpfaas.starter.file;

import com.bpfaas.common.exception.BpRuntimeException;
import com.bpfaas.starter.file.impl.FileProviderImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.util.StringUtils;

@ConditionalOnProperty(name = "bp.web.minio.enable", havingValue = "true")
@EnableConfigurationProperties(WebMinioProperties.class)
@Configuration
public class WebMinioConfig {

    @Lazy
    @Bean
    public FileProvider fileProvider(WebMinioProperties properties) {
        if (StringUtils.isEmpty(properties.getEndPoint())) {
            throw new BpRuntimeException("Minio文件服务缺少endPoint配置");
        }
        if (StringUtils.isEmpty(properties.getAccessKey())) {
            throw new BpRuntimeException("Minio文件服务缺少accessKey配置");
        }
        if (StringUtils.isEmpty(properties.getSecurityKey())) {
            throw new BpRuntimeException("Minio文件服务缺少securityKey配置");
        }
        if (StringUtils.isEmpty(properties.getBucketName())) {
            throw new BpRuntimeException("Minio文件服务缺少bucketName配置");
        }
        return new FileProviderImpl(properties);
    }

}
