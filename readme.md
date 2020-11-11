
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.bpfaas/bps-web-spring-cloud-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.bpfaas/bps-web-spring-cloud-starter/)
[![License](https://img.shields.io/github/license/bpfaas/java-bps-web-spring-cloud-starter)](https://opensource.org/licenses/MIT)


```html
<dependency>
    <groupId>com.bpfaas</groupId>
    <artifactId>bps-web-spring-cloud-starter</artifactId>
    <version>0.0.5</version>
</dependency>
```

使用此starter后, 无需再引入spring-boot-starter-web; 引入后可实现如下功能:

- response body的格式化.
- FeignClient编解码.
- request/resposne 日志.
- redis连接池配置.

> FeignClient 定义的接口时, 返回类型必须为 `Msg`, `Msg<T>`, `Response`, `void` 或子类之一;

## Config

bootstrap.properties

```properties

# enable (cannot be refreshed dynamically)
bp.web.enable = true                    # enable web starter.
                                        # conflict with SpringCloudGateway, must be "false" in Gateway project.

# redis
bp.web.redis.enable = true              # enable redis.

# minio file
bp.web.minio.enable = true              # enable minio.

# log
bp.web.logging.feignClientLevel = "full"  # log feign client request (cannot be dynamic refresh).
bp.web.logging.level = "full"             # log level.
```

如果使用日志, 请打开

```properties
logging.level.com.bpfaas.*: DEBUG
```

如果使用Minio，请设置

```properties
##配置minio服务器地址
bp.web.minio.endPoint = http://10.210.200.7:9000
##配置minio服务accessKey
bp.web.minio.accessKey = minio
##配置minio服务securityKey
bp.web.minio.securityKey = bpfaas123
##配置minio服务bucketName
bp.web.minio.bucketName = faas
```

```java
@Autowired
private MinioClient minioClient;
```
## MinioClient API

查看[MinioClient](https://docs.min.io/docs/java-client-api-reference)

## 

## 格式化 Response body

查看[Response](./doc/response.md)

## 
