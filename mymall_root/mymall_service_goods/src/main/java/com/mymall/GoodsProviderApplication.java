package com.mymall;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDubbo
@MapperScan(basePackages="com.mymall.**.**.dao")
public class GoodsProviderApplication {

    public static void main(String[] args) {
        LoggerFactory.setLevel(Level.OFF);
        SpringApplication.run(GoodsProviderApplication.class, args);
    }

    @PostConstruct
    void init() {
        System.setProperty("es.set.netty.runtime.available.processors", "false");
    }

}
