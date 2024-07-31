package com.zss.rws;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/24 14:54
 * @desc 启动类
 */
@EntityScan("com.zss.sda")
@SpringBootApplication(scanBasePackages = "com.zss")
@ComponentScan(basePackages = "com.zss")
public class ReadWriteSeparationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReadWriteSeparationApplication.class, args);
    }
}
