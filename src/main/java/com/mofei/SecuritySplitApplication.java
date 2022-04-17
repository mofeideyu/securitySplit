package com.mofei;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mofei.dao")
@SpringBootApplication
public class SecuritySplitApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecuritySplitApplication.class, args);
    }

}
