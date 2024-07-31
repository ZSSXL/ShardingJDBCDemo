package com.zss.hsl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author zhoushs@dist.com.cn
 * @date 2021/2/23 17:33
 * @desc 启动类
 */
@EntityScan("com.zss.sda")
@SpringBootApplication(scanBasePackages = "com.zss")
@ComponentScan(basePackages = "com.zss")
public class HorizontalSubLibraryApplication {

    public static void main(String[] args) {
        SpringApplication.run(HorizontalSubLibraryApplication.class, args);
    }
}
