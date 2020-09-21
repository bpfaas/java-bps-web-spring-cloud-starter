package com.bpfaas.starter.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.stereotype.Component;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.ReadFrom;
import io.lettuce.core.TimeoutOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions.RefreshTrigger;

@ConditionalOnProperty(name = "bp.web.redis.enable", havingValue = "true")
@Component
@Configuration
public class WebRedisConnectionFactoryConfig {

  @Autowired
  RedisProperties redisProperties;

  /**
   * 在构建LettuceConnectionFactory时，如果不使用内置的destroyMethod，可能会导致Redis连接早于其它Bean被销毁
   * 
   * @return ConnectionFactory
   */
  @Bean(destroyMethod = "destroy")
  @RefreshScope
  public LettuceConnectionFactory lettuceConnectionFactory() {
    return redisProperties.getCluster() != null ? getClusterFactory() : getStandaloneFactory();
  }

  private LettuceConnectionFactory getClusterFactory() {

    // 集群变更时, 自动刷新去连接新的master节点.
    ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
        .enablePeriodicRefresh(Duration.ofSeconds(60)) // 开启周期刷新(默认60秒)
        .enableAdaptiveRefreshTrigger(RefreshTrigger.ASK_REDIRECT, RefreshTrigger.UNKNOWN_NODE).build();

    ClusterClientOptions clusterClientOptions = ClusterClientOptions.builder()
        .topologyRefreshOptions(clusterTopologyRefreshOptions) // 拓扑刷新
        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS).autoReconnect(true)
        .socketOptions(io.lettuce.core.SocketOptions.builder().keepAlive(true).build())
        .timeoutOptions(TimeoutOptions.enabled(redisProperties.getTimeout())).validateClusterNodeMembership(false)// 取消校验集群节点的成员关系
        .build();
    LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder().clientOptions(clusterClientOptions)
        .readFrom(ReadFrom.REPLICA_PREFERRED).build();

    // config.
    RedisProperties.Cluster clusterProperties = redisProperties.getCluster();
    RedisClusterConfiguration config = new RedisClusterConfiguration(clusterProperties.getNodes());
    if (clusterProperties.getMaxRedirects() != null) {
      config.setMaxRedirects(clusterProperties.getMaxRedirects());
    }
    if (redisProperties.getPassword() != null) {
      config.setPassword(RedisPassword.of(redisProperties.getPassword()));
    }

    LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
    factory.setDatabase(redisProperties.getDatabase());
    return factory;
  }

  private LettuceConnectionFactory getStandaloneFactory() {
    // config.
    RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisProperties.getHost(),
        redisProperties.getPort());
    if (redisProperties.getPassword() != null) {
      config.setPassword(RedisPassword.of(redisProperties.getPassword()));
    }

    config.setDatabase(redisProperties.getDatabase());

    LettuceClientConfiguration clientConfig = LettucePoolingClientConfiguration.builder()
        .commandTimeout(redisProperties.getTimeout()).build();

    return new LettuceConnectionFactory(config, clientConfig);
  }
}