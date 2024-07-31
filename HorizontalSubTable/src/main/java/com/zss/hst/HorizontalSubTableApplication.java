package com.zss.hst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/20 10:55
 * @desc 水平分表启动类
 */
@EntityScan("com.zss.sda")
@SpringBootApplication(scanBasePackages = "com.zss")
@ComponentScan(basePackages = "com.zss")
public class HorizontalSubTableApplication {

    public static void main(String[] args) {
        SpringApplication.run(HorizontalSubTableApplication.class, args);
    }
}
