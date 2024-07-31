package com.zss.ya;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/3/11 14:24
 * @desc 启动类
 */
@EntityScan("com.zss.sda")
@SpringBootApplication(scanBasePackages = "com.zss")
@ComponentScan(basePackages = "com.zss")
public class YamlApplication {

    public static void main(String[] args) {
        SpringApplication.run(YamlApplication.class, args);
    }
}
