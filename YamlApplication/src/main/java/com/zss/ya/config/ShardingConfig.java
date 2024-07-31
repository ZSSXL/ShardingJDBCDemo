package com.zss.ya.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.shardingjdbc.api.yaml.YamlShardingDataSourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/25 16:28
 * @desc Shading-JDBC 配置
 */
@Slf4j
@Configuration
public class ShardingConfig {

    @Bean(name = "dataSource")
    public DataSource initSharding() throws IOException, SQLException {
        File yamlFile = new ClassPathResource("sharding/sharding.yml").getFile();
        DataSource dataSource = YamlShardingDataSourceFactory.createDataSource(yamlFile);
        Connection connection = dataSource.getConnection();
        log.info("DataSource Connection: [{}]", connection.toString());
        return dataSource;
    }

}
