# Spring Cloud之路：（二） 服务的注册与发现（Eureka）

> eureka是一个服务注册和发现模块。

## 一、创建服务注册中心 (eureka server)

### 1、启动一个服务注册中心，只需要一个注解@EnableEurekaServer，这个注解需要在springboot工程的启动application类上加
```
@EnableEurekaServer
@SpringBootApplication
public class EurekaserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaserverApplication.class, args);
	}
}
```

### 2、eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成），在默认情况下erureka server也是一个eureka client ,必须要指定一个 server。eureka server的配置文件appication.properties：
```
server.port = 8761

eureka.instance.hostname = localhost
eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.client.service-url.default-zone=http://${eureka.instance.hostname}:${server.port}/eureka/
```

通过eureka.client.registerWithEureka：false和fetchRegistry：false来表明自己是一个eureka server.

### 3、eureka server 是有界面的，启动工程,打开浏览器访问： http://localhost:8761 

## 二、创建一个服务提供者 (eureka client)

> 当client向server注册时，它会提供一些元数据，例如主机和端口，URL，主页等。Eureka server 从每个client实例接收心跳消息。 如果心跳超时，则通常将该实例从注册server中删除。


### 1、通过注解@EnableEurekaClient 表明自己是一个eurekaclient.

```
@SpringBootApplication
@EnableEurekaClient
@RestController
public class EurekaclientApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaclientApplication.class, args);
	}

	@Value("${server.port}")
	String port;
	@RequestMapping("/hi")
	public String home(@RequestParam String name) {
		return "hi "+name+",i am from port:" +port;
	}

}
```


### 2、仅仅@EnableEurekaClient是不够的，还需要在配置文件中注明自己的服务注册中心的地址，application.properties配置文件如下：

```
eureka.client.service-url.default-zone=http://localhost:8761/eureka/

server.port=8762
spring.application.name=eurekaclient
```

需要指明spring.application.name,这个很重要，这在以后的服务与服务之间相互调用一般都是根据这个name 。 

### 3、启动工程，打开http://localhost:8761 ，即eureka server 的网址：
![image.png](http://upload-images.jianshu.io/upload_images/688387-cbb83528bbfb67ff.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

你会发现一个服务已经注册在服务中了，服务名为SERVICE-HI ,端口为7862

这时打开 http://localhost:8762/hi?name=forezp ，你会在浏览器上看到 :

`hi forezp,i am from port:8762`
