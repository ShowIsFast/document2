package com.mymall;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages="com.mymall.**.**.dao")
public class SeckillProviderApplication {

    public static void main(String[] args) {
        LoggerFactory.setLevel(Level.OFF);
        SpringApplication.run(SeckillProviderApplication.class, args);
    }

}
