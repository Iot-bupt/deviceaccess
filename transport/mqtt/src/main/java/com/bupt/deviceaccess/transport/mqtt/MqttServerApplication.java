package com.bupt.deviceaccess.transport.mqtt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by hasee on 2018/4/11.
 */
@SpringBootApplication
@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan({"com.bupt.deviceaccess"})
public class MqttServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MqttServerApplication.class, args);
    }
}
