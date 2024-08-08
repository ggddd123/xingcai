package com.xingcai;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@SpringBootApplication
@EnableFeignClients(basePackages = "com.xingcai.content.feignclient")
public class ContentApplication {
    public static void main(String[] args) {

        SpringApplication.run(ContentApplication.class, args);

    }
}
