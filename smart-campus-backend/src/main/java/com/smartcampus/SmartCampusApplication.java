package com.smartcampus;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 智慧校园管理平台启动类
 */
@SpringBootApplication
@MapperScan("com.smartcampus.mapper")
public class SmartCampusApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCampusApplication.class, args);
        System.out.println("========================================");
        System.out.println("智慧校园管理平台启动成功！");
        System.out.println("接口文档地址: http://localhost:8080/api");
        System.out.println("========================================");
    }

}
