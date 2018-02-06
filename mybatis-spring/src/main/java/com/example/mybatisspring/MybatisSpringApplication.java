package com.example.mybatisspring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mybatisspring.mapper") //在启动类中添加对mapper包扫描@MapperScan
public class MybatisSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisSpringApplication.class, args);
	}
}
