package com.mymall;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication(exclude={SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class})
@EnableDubbo
@MapperScan(basePackages="com.mymall.**.**.dao")
public class UserProviderApplication {

    public static void main(String[] args) {
        LoggerFactory.setLevel(Level.OFF);
        SpringApplication.run(UserProviderApplication.class, args);
    }

}
