package com.xingcai;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableSwagger2Doc
@SpringBootApplication
@MapperScan("com.xingcai.content.mapper")
@EnableFeignClients(basePackages = "com.xingcai.content.feignclient")
public class ContentApplication {
    public static void main(String[] args) {
        SpringApplication.run(ContentApplication.class, args);
    }
}
