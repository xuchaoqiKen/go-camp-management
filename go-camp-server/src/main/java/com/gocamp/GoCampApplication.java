package com.gocamp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gocamp.mapper")
public class GoCampApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoCampApplication.class, args);
    }
}