package com.chinasoft.gangjiantou;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.chinasoft.gangjiantou.mapper")
@EnableScheduling
public class ApprovalApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApprovalApplication.class, args);
    }

}
