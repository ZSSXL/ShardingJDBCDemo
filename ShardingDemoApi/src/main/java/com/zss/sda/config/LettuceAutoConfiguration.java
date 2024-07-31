package com.zss.sda.config;

import com.zss.sda.properties.LettuceProperties;
import com.zss.sda.properties.redis.LettucePoolProperties;
import com.zss.sda.properties.redis.LettuceSingleProperties;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * @author zhoushs@dist.com.cn
 * @date 2020/8/11 10:44
 * @desc Lettuce自动配置
 */
@Slf4j
@Configuration
@ConditionalOnClass
public class LettuceAutoConfiguration {

    @Resource
    private LettuceProperties lettuceProperties;

    @Bean(destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    // ========================== Single单机配置 Start ========================== //

    @Bean
    @ConditionalOnProperty(name = "lettuce.single.host")
    public RedisURI singleRedisUri() {
        LettuceSingleProperties single = lettuceProperties.getSingle();
        return RedisURI.builder()
                .withHost(single.getHost())
                .withPort(single.getPort())
                .withDatabase(single.getDatabase())
                .withTimeout(Duration.of(single.getTimeout(), ChronoUnit.SECONDS))
                .build();
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(name = "lettuce.single.host")
    public RedisClient singleRedisClient(ClientResources clientResources,
                                         @Qualifier("singleRedisUri") RedisURI singleRedisUri) {
        return RedisClient.create(clientResources, singleRedisUri);
    }

    /**
     * Redis连接池配置
     *
     * @param singleRedisClient redis客户端实例
     * @return 连接池实例
     * 问题：本来想@Bean(destroyMethod = "returnObject")
     * -----但是由于returnObject(T)这个方法需要带参，所有不行
     */
    @Bean
    @ConditionalOnProperty(name = "lettuce.single.host")
    public GenericObjectPool<StatefulRedisConnection<String, String>> singleRedisPool(
            @Qualifier("singleRedisClient") RedisClient singleRedisClient) {
        LettucePoolProperties pool = lettuceProperties.getPool();
        GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig =
                new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxTotal(pool.getMaxTotal());
        poolConfig.setTestOnBorrow(pool.getTestOnBorrow());
        poolConfig.setTestOnReturn(pool.getTestOnReturn());
        poolConfig.setTestOnCreate(pool.getTestOnCreate());
        poolConfig.setMaxWaitMillis(pool.getMaxWaitMills());
        poolConfig.setJmxEnabled(false);
        return ConnectionPoolSupport
                .createGenericObjectPool(singleRedisClient::connect, poolConfig);
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnProperty(name = "lettuce.single.host")
    public StatefulRedisConnection<String, String> singleRedisConnection(
            @Qualifier("singleRedisPool") GenericObjectPool<StatefulRedisConnection<String, String>> singleRedisPool) {
        try {
            return singleRedisPool.borrowObject();
        } catch (Exception e) {
            log.error("从Redis连接池中获取实例失败: [{}]", e.getMessage());
            return null;
        }
    }
    // ========================== Single单机配置 End ========================== //
}
