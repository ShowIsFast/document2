package com.mymall;

import org.apache.dubbo.common.logger.Level;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
@EnableDubbo
public class SeckillApplication {

    public static void main(String[] args) {
        LoggerFactory.setLevel(Level.OFF);
        SpringApplication.run(SeckillApplication.class, args);
    }

}
