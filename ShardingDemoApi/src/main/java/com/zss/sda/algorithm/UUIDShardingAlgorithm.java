package com.zss.sda.algorithm;


import com.zss.sda.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/24 10:52
 * @desc 自定义分片算法 - 获取UUID第一个字符，获取其byte序列，进行取模
 * -- Sharding提供了以下4种算法接口
 * ---- PreciseShardingAlgorithm：   精准分片算法
 * ---- RangeShardingAlgorithm：     范围分片算法
 * ---- HintShardingAlgorithm：      提示分片算法
 * ---- ComplexKeysShardingAlgorithm：复合键分片算法
 */
@Slf4j
@SuppressWarnings("unused")
public class UUIDShardingAlgorithm implements PreciseShardingAlgorithm<String> {

    /**
     * 在加载配置文件时，会解析表分片规则。将实际表名存储到 Collection中
     * shardingValue: logicTableName=逻辑表明, columnName=字段名称, value=字段值
     *
     * @param collection    实际表名集合
     * @param shardingValue Sql中对应的
     * @return String
     */
    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<String> shardingValue) {
        String value = String.valueOf(shardingValue.getValue());
        // 获取UUID的第一个字符对应的ASC码，不管是数值还是字符的ASC码都是数值格式
        byte[] charByte = value.substring(0, 1).getBytes();
        // 去除符号[]
        String temp = Arrays.toString(charByte).replaceAll("[\\[\\]]", "");
        // 取模
        int result = Integer.parseInt(temp) % 2;
        for (Object name : collection) {
            String tableName = String.valueOf(name);
            if (tableName.endsWith(String.valueOf(result + 1))) {
                return tableName;
            }
        }
        return null;
    }

    /**
     * 测试
     */
    public static void main(String[] args) {
        int zeroNum = 0;
        int oneNum = 0;
        int times = 500;
        for (int i = 0; i < times; i++) {
            String id = IdUtil.getId();
            byte[] charByte = id.substring(0, 1).getBytes();
            // 去除【】
            String temp = Arrays.toString(charByte).replaceAll("[\\[\\]]", "");
            // 取模
            int result = Integer.parseInt(temp) % 2;
            if (result == 0) {
                zeroNum++;
            } else if (result == 1) {
                oneNum++;
            }
        }
        System.out.println("ZeroNum: " + zeroNum + ", OneNum: " + oneNum);
        // ZeroNum: 247, OneNum: 253
        // 还是挺平均的
    }

}
