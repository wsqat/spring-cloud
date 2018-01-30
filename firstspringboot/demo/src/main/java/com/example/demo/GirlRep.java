package com.example.demo;

//创建Dao接口, springboot 将接口类会自动注解到spring容器中，不需做任何配置，只需要继承JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

//其中第二个参数为Id的类型
public interface GirlRep extends JpaRepository<Girl,Integer> {
}
