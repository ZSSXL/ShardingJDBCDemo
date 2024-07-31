package com.zss.sda.util;

import com.zss.sda.config.RedisPoolConfig;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/23 15:18
 * @desc Redis工具
 */
public class RedisUtil {

    /**
     * redis自增
     *
     * @param key 键
     * @return 返回值 - long
     */
    public static long incr(String key) {
        StatefulRedisConnection<String, String> redis = RedisPoolConfig.getRedis();
        if (redis != null) {
            RedisCommands<String, String> sync = redis.sync();
            Long incr = sync.incr(key);
            RedisPoolConfig.returnRedis(redis);
            return incr;
        } else {
            return 0L;
        }
    }
}
