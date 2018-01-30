## 一、序言
### Spring Cloud简介
&emsp;&emsp;Spring Cloud 为开发人员提供了快速构建分布式系统的一些工具，包括配置管理、服务发现、断路器、路由、微代理、事件总线、全局锁、决策竞选、分布式会话等等。它运行环境简单，可以在开发人员的电脑上跑。
### Spring Boot 简介
&emsp;&emsp;Spring Boot 致力于简洁，让开发者写更少的配置，程序能够更快的运行和启动。它是下一代javaweb框架，并且它是Spring Cloud （微服务）的基础。
### 二者关系
&emsp;&emsp;此外，最最最重要的是Spring Boot做为下一代 web 框架，当下作为最新最火的微服务的翘楚的Spring Cloud则是基于Spring Boot的，所以需要开发中对Spring Boot有一定的了解，


## 二、实例
> 开发环境：Mac + IDEA + Mysql

### 1、搭建Spring Boot程序

具体步骤：

-  **new prpject -> spring initializr -> next**
-  **再次，next**
-  **Web -> Web -> next**

应用创建成功后，会生成相应的目录和文件。其中有一个Application类,它是程序的入口。

### 2、程序的入口文件：DemoApplication.java
```
@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

```

### 3、程序配置文件：application.properties
在resources文件下有一个文件，它是程序的配置文件。默认为空，写点配置 ,程序的端口为8080,context-path为 /springboot：
```
server.port=8080
server.context-path=/springboot

girl.name =B
girl.age = 18
girl.content = content:${name},age:${age}

spring.profiles.active=prod
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
# 在mysql中创建dbgirl数据库
spring.datasource.url=jdbc:mysql://localhost:3306/dbgirl?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8
spring.datasource.username=root
spring.datasource.password=1234567890

#通过jpa方式操作数据库
spring.jpa.hibernate.ddl-auto=create
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL5Dialect
```

这些都是数据库常见的一些配置没什么可说的，其中ddl_auto: create 代表在数据库创建表，update 代表更新，首次启动需要create ,如果你想通过hibernate 注解的方式创建数据库的表的话，之后需要改为 update.



### 4、项目的依赖配置文件
由于本案例采用通过jpa方式操作Mysql数据库。需要导入相应的jar ，在pom.xml中添加依赖:
```
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
</dependency>
```

### 5、实体文件：Girl.java
创建一个实体girl，这是基于hibernate的:
```

@Entity
public class Girl {

    @Id
    @GeneratedValue
    private Integer id;
    private String cupSize;
    private Integer age;

    public Girl() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCupSize() {
        return cupSize;
    }

    public void setCupSize(String cupSize) {
        this.cupSize = cupSize;
    }
}
```

### 6、实体属性注入文件：GirlProperties.java
通过ConfigurationProperties注解，将属性注入到bean中，通过Component注解将bean注解到spring容器中：
```
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

```


### 7、DAO接口文件：GirlRep.java
创建Dao接口, springboot 将接口类会自动注解到spring容器中，不需要做任何配置，只需要继承JpaRepository 即可：
```
//其中第二个参数为Id的类型
public interface GirlRep extends JpaRepository<Girl,Integer> {
}
```

### 8、路由控制文件：GirlController.java
```
@RestController
public class GirlController {

    @Autowired
    private GirlRep girlRep;

    /**
     * 查询所有女生列表
     * @return
     */
    @RequestMapping(value = "/girls",method = RequestMethod.GET)
    public List<Girl> getGirlList(){
        return girlRep.findAll();
    }
}
```

### 9、运行程序

 -  运行前务必在mysql中创建dbgirl数据库
 -  启动DemoApplication文件的main函数
 -  访问网址，http://localhost:8080/springboot/girls，结果`[]`。
 -  访问mysql，执行插入语句，`mysql> insert into girl(age,cup_size) values(22,"A");
`
 -  刷新页面，http://localhost:8080/springboot/girls，结果`[[{"id":1,"cupSize":"A","age":22}]]`，查询数据库成功。



### Q & A
1、报错：解决问题Unable to start EmbeddedWebApplicationContext due to missing EmbeddedServletContainerFactory bean
方案：在DemoApplication.java文件中添加如下代码：
```
@Bean
public EmbeddedServletContainerFactory servletContainer() {

	TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	return factory;

}
```

2、报错：springboot configuration annotation processor not found in classpath
方案：在pom.xml添加
```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```
最后，声明如下：

- 本案例借鉴了大牛的博客[SpringBoot非官方教程 | 第二十五篇：2小时学会springboot](http://blog.csdn.net/forezp/article/details/61472783)，案例仅供参考，有比较污的地方本人概不负责……
- 代码地址：https://github.com/wsqat/spring-cloud/firstspringboot 望star。