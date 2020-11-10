package com.bpfaas.starter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author longweier
 */
@Data
@ConfigurationProperties(prefix = "bp.web.minio")
public class WebMinioProperties {

    private String endPoint;

    private String accessKey;

    private String securityKey;

    private String bucketName;

    private String bucketUrl;

}
