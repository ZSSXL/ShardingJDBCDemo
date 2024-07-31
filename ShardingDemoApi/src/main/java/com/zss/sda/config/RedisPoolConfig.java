package com.zss.sda.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

/**
 * @author zhoushs@dist.com.cn
 * @date 2020/8/7 17:13
 * @desc Redis配置
 */
@Slf4j
@SuppressWarnings("unused")
public class RedisPoolConfig {

    /**
     * 地址
     */
    private static final String HOST;
    /**
     * 端口
     */
    private static final Integer PORT;
    /**
     * 数据库索引号
     */
    private static final Integer DATABASE;
    /**
     * 连接超时
     */
    private static final Long TIME_OUT;
    /**
     * 对象池中最大的空闲对象个数，默认值是8。
     */
    private static final Integer MAX_IDLE;
    /**
     * 对象池中最小的空闲对象个数，默认值是0。
     */
    private static final Integer MIN_IDLE;
    /**
     * 对象池中管理的最多对象个数默，认值是8。
     */
    private static final Integer MAX_TOTAL;
    /**
     * 当连接池资源耗尽时，等待时间，超出则抛异常，默认为-1即永不超时, 单位ms
     */
    private static final Long MAX_WAIT_MILLS;
    /**
     * 在获取对象的时候检查有效性, 默认false
     */
    private static final Boolean TEST_ON_BORROW;
    /**
     * 在归还对象的时候检查有效性, 默认false
     */
    private static final Boolean TEST_ON_RETURN;
    /**
     * 在创建对象时检测对象是否有效，默认值是false。
     */
    private static final Boolean TEST_ON_CREATE;

    static {
        Properties properties = new Properties();
        InputStream resourceAsStream = RedisPoolConfig.class
                .getClassLoader()
                .getResourceAsStream("redis.properties");
        try {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            log.error("Redis配置文件加载失败: [{}]", e.getMessage());
        }
        HOST = properties.getProperty("redis.host");
        PORT = Integer.valueOf(properties.getProperty("redis.port"));
        DATABASE = Integer.valueOf(properties.getProperty("redis.database"));
        TIME_OUT = Long.valueOf(properties.getProperty("redis.time_out"));
        MAX_IDLE = Integer.valueOf(properties.getProperty("redis.max_idle"));
        MIN_IDLE = Integer.valueOf(properties.getProperty("redis.min_idle"));
        MAX_TOTAL = Integer.valueOf(properties.getProperty("redis.max_total"));
        MAX_WAIT_MILLS = Long.valueOf(properties.getProperty("redis.max_wait_mills"));
        TEST_ON_BORROW = Boolean.valueOf(properties.getProperty("redis.test_on_borrow"));
        TEST_ON_CREATE = Boolean.valueOf(properties.getProperty("redis.test_on_create"));
        TEST_ON_RETURN = Boolean.valueOf(properties.getProperty("redis.test_on_return"));
    }

    /**
     * Redis连接池
     */
    private static GenericObjectPool<StatefulRedisConnection<String, String>> genericObjectPool;

    /**
     * 连接Redis
     *
     * @return redis连接实例
     */
    public static StatefulRedisConnection<String, String> getRedis() {
        // 创建连接信息
        RedisURI redisUri = RedisURI.builder()
                .withHost(HOST)
                .withPort(PORT)
                .withDatabase(DATABASE)
                .withTimeout(Duration.of(TIME_OUT, ChronoUnit.SECONDS))
                .build();

        // 配置连接池
        GenericObjectPoolConfig<StatefulRedisConnection<String, String>> poolConfig =
                new GenericObjectPoolConfig<>();
        poolConfig.setMaxIdle(MAX_IDLE);
        poolConfig.setMinIdle(MIN_IDLE);
        poolConfig.setMaxTotal(MAX_TOTAL);
        poolConfig.setTestOnBorrow(TEST_ON_BORROW);
        poolConfig.setTestOnReturn(TEST_ON_RETURN);
        poolConfig.setTestOnCreate(TEST_ON_CREATE);
        poolConfig.setMaxWaitMillis(MAX_WAIT_MILLS);

        // 创建客户端连接
        RedisClient redisClient = RedisClient.create(redisUri);

        genericObjectPool = ConnectionPoolSupport
                .createGenericObjectPool(redisClient::connect, poolConfig);
        try {
            return genericObjectPool.borrowObject();
        } catch (Exception e) {
            log.error("Redis连接池配置失败: [{}]", e.getMessage());
            return null;
        }
    }

    /**
     * 将连接实例对象归还给对象连接池
     *
     * @param redis 连接实例
     */
    public static void returnRedis(StatefulRedisConnection<String, String> redis) {
        genericObjectPool.returnObject(redis);
    }

    /**
     * 获取连接池信息
     * NumActive : 对象池中有对象对象是活跃的
     * NumIdle : 对象池中有多少对象是空闲的
     */
    public static void getPoolMessage() {
        int numActive = genericObjectPool.getNumActive();
        int numIdle = genericObjectPool.getNumIdle();
        System.out.println("NumActive : [" + numActive + "], " +
                "NumIdle : [" + numIdle + "]");
    }
}
