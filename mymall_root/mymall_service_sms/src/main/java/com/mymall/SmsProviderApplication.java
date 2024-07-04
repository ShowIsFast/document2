package com.mymall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication(exclude = RabbitAutoConfiguration.class)
@PropertySource("classpath:sms.properties")
public class SmsProviderApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmsProviderApplication.class, args);
    }

}
