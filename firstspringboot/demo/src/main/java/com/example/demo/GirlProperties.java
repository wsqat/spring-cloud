package com.example.demo;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//通过ConfigurationProperties注解，将属性注入到bean中，通过Component注解将bean注解到spring容器中：
@ConfigurationProperties(prefix="girl")
@Component
public class GirlProperties {

    private String name;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}