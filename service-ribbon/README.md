> 在微服务架构中，业务都会被拆分成一个独立的服务，服务与服务的通讯是基于http restful的。Spring cloud有两种服务调用方式，一种是ribbon+restTemplate，另一种是feign。在这一篇文章首先讲解下基于ribbon+rest。

## 一、Ribbon架构
> Ribbon is a client side load balancer which gives you a lot of control over the behaviour of HTTP and TCP clients. Feign already uses Ribbon, so if you are using @FeignClient then this section also applies.      
> —–摘自官网


ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。

ribbon 已经默认实现了这些配置bean：

- IClientConfig ribbonClientConfig: DefaultClientConfigImpl
- IRule ribbonRule: ZoneAvoidanceRule
- IPing ribbonPing: NoOpPing
- ServerList ribbonServerList: ConfigurationBasedServerList
- ServerListFilter ribbonServerListFilter: ZonePreferenceServerListFilter
- ILoadBalancer ribbonLoadBalancer: ZoneAwareLoadBalancer

## 二、准备工作
- 启动eureka-server工程；
- 启动eureka-client工程，它的端口为8762；
- 关闭此项目，重新打开eureka-client，并修改配置文件的端口改为8763,并启动；
- 这时你会发现：eurekaclient在eureka-server注册了2个实例，这就相当于一个小的集群。访问localhost:8761如图所示：

![clients](http://upload-images.jianshu.io/upload_images/688387-1b0f1c87c393e124.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 三、建一个服务消费者
### 1、新建工程
重新新建一个spring-boot工程，取名为：service-ribbon; 
在它的pom.xml文件分别引入起步依赖spring-cloud-starter-eureka、spring-cloud-starter-ribbon、spring-boot-starter-web，代码如下：
```
<dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-eureka</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-ribbon</artifactId>
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
在工程的配置文件指定服务的注册中心地址为http://localhost:8761/eureka/，程序名称为 service-ribbon，程序端口为8764。配置文件application.properities如下：
```
server.port = 8764

eureka.instance.hostname = localhost
eureka.client.service-url.default-zone=http://localhost:8761/eureka/

spring.application.name=service-ribbon
```

### 3、负载均衡
在工程的启动类中,通过@EnableDiscoveryClient向服务中心注册；并且向程序的ioc注入一个bean: restTemplate;并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能。
```
@SpringBootApplication
@EnableDiscoveryClient
public class ServiceRibbonApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiceRibbonApplication.class, args);
	}

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
```

### 4、测试服务
写一个测试类HelloService，通过之前注入ioc容器的restTemplate来消费service-hi服务的“/hi”接口，在这里我们直接用的程序名替代了具体的url地址，在ribbon中它会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的url替换掉服务名，代码如下：
```
@Service
public class HelloService {

    @Autowired
    RestTemplate restTemplate;

    public String hiService(String name) {
        return restTemplate.getForObject("http://EUREKACLIENT/hi?name="+name,String.class);
    }
}
```
### 5、控制器
写一个controller，在controller中用调用HelloService 的方法，代码如下：
```
@RestController
public class HelloControler {

    @Autowired
    HelloService helloService;
    @RequestMapping(value = "/hi")
    public String hi(@RequestParam String name){
        return helloService.hiService(name);
    }
}
```

### 6、结果
在浏览器上多次访问http://localhost:8764/hi?name=forezp，浏览器交替显示：
`
hi forezp,i am from port:8762
hi forezp,i am from port:8763
`




![ari](http://upload-images.jianshu.io/upload_images/2279594-9f10b702188a129d.png)

- 一个服务注册中心，eureka server,端口为8761
- service-hi工程跑了两个实例，端口分别为8762,8763，分别向服务注册中心注册
- sercvice-ribbon端口为8764,向服务注册中心注册
- 当sercvice-ribbon通过restTemplate调用service-hi的hi接口时，因为用ribbon进行了负载均衡，会轮流的调用service-hi：8762和8763 两个端口的hi接口；
 

## 附录

[史上最简单的SpringCloud教程 | 第二篇: 服务消费者（rest+ribbon）](http://blog.csdn.net/forezp/article/details/69788938)

[示例代码-github]()