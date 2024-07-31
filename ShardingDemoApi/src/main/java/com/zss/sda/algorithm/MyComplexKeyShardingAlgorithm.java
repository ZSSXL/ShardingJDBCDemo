package com.zss.sda.algorithm;



import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import java.util.Collection;
import java.util.Properties;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/24 13:57
 * @desc 自定义多分片键分片算法
 */
@SuppressWarnings("unused")
public class MyComplexKeyShardingAlgorithm implements ComplexKeysShardingAlgorithm<String> {


    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<String> shardingValue) {
        return null;
    }

}
