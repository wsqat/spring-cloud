## 一、Feign简介
> Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。

简而言之：
- Feign 采用的是基于接口的注解
- Feign 整合了ribbon

## 二、准备工作
继续用上一节的工程， 启动eureka-server，端口为8761; 启动eureka-client 两次，端口分别为8762 、8773.

## 三、创建一个feign的服务
### 1、创建服务
新建一个spring-boot工程，取名为serice-feign，在它的pom文件引入Feign的起步依赖spring-cloud-starter-feign、Eureka的起步依赖spring-cloud-starter-eureka、Web的起步依赖spring-boot-starter-web，代码如下：
```
<dependencies>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-eureka</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-feign</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### 2、配置文件
在工程的配置文件application.properties文件，指定程序名为service-feign，端口号为8765，服务注册地址为http://localhost:8761/eureka/ ，代码如下：
```
server.port = 8765

eureka.instance.hostname = localhost
eureka.client.service-url.default-zone=http://localhost:8761/eureka/

spring.application.name=service-feign
```
### 3、程序的启动类
在程序的启动类ServiceFeignApplication ，加上@EnableFeignClients注解开启Feign的功能：
```
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceFeignApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceFeignApplication.class, args);
    }
}
```
### 4、接口类
定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。比如在代码中调用了EUREKACLIENT服务的“/hi”接口，代码如下：
```
@FeignClient(value = "EUREKACLIENT")
public interface SchedualServiceHi {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
```
### 5、控制类
在Web层的controller层，对外暴露一个”/hi”的API接口，通过上面定义的Feign客户端SchedualServiceHi 来消费服务。代码如下：
```
@RestController
public class HiController {

    @Autowired
    SchedualServiceHi schedualServiceHi;
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        return schedualServiceHi.sayHiFromClientOne(name);
    }
}
```

### 6、结果
启动程序，多次访问http://localhost:8765/hi?name=forezp,浏览器交替显示：

`hi forezp,i am from port:8762`

`hi forezp,i am from port:8763`

## 附录

[史上最简单的SpringCloud教程 | 第三篇: 服务消费者（Feign）](http://blog.csdn.net/forezp/article/details/69808079)

[示例代码-github]()