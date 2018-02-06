## 一、Mybatis

### 1、介绍
&emsp;&emsp;MyBatis 是一款优秀的持久层框架，它支持定制化 SQL、存储过程以及高级映射。MyBatis 避免了几乎所有的 JDBC 代码和手动设置参数以及获取结果集。MyBatis 可以使用简单的 XML 或注解来配置和映射原生信息，将接口和 Java 的 POJOs(Plain Old Java Objects,普通的 Java对象)映射成数据库中的记录。

### 2、Mybatis VS Hibernate
&emsp;&emsp;orm框架的本质是简化编程中操作数据库的编码，发展到现在基本上就剩两家了，一个是宣称可以不用写一句SQL的hibernate，一个是可以灵活调试动态sql的mybatis,两者各有特点，在企业级系统开发中可以根据需求灵活使用。发现一个有趣的现象：传统企业大都喜欢使用hibernate,互联网行业通常使用mybatis。
Mybatis优势

 - MyBatis可以进行更为细致的SQL优化，可以减少查询字段。
 - MyBatis容易掌握，而Hibernate门槛较高。

Hibernate优势

- Hibernate的DAO层开发比MyBatis简单，Mybatis需要维护SQL和结果映射。
- Hibernate对对象的维护和缓存要比MyBatis好，对增删改查的对象的维护要方便。
- Hibernate数据库移植性很好，MyBatis的数据库移植性不好，不同的数据库需要写不同SQL。
- Hibernate有更好的二级缓存机制，可以使用第三方缓存。MyBatis本身提供的缓存机制不佳。

&emsp;&emsp;hibernate特点就是所有的sql都用Java代码来生成，不用跳出程序去写（看）sql，有着编程的完整性，发展到最顶端就是spring data jpa这种模式了，基本上根据方法名就可以生成对应的sql了，有不太了解的可以看我的上篇文章springboot(五)：spring data jpa的使用。
&emsp;&emsp;mybatis初期使用比较麻烦，需要各种配置文件、实体类、dao层映射关联、还有一大推其它配置。当然mybatis也发现了这种弊端，初期开发了generator可以根据表结果自动生产实体类、配置文件和dao层代码，可以减轻一部分开发量；后期也进行了大量的优化可以使用注解了，自动管理dao层和配置文件等，发展到最顶端就是今天要讲的这种模式了，mybatis-spring-boot-starter就是springboot+mybatis可以完全注解不用配置文件，也可以简单配置轻松上手。

### 3、mybatis-spring-boot-starter

官方说明：`MyBatis Spring-Boot-Starter will help you use MyBatis with Spring Boot`
其实就是myBatis看spring boot这么火热也开发出一套解决方案来凑凑热闹,但这一凑确实解决了很多问题，使用起来确实顺畅了许多。mybatis-spring-boot-starter主要有两种解决方案，一种是使用注解解决一切问题，一种是简化后的老传统。

当然任何模式都需要首先引入mybatis-spring-boot-starter的pom文件,现在最新版本是1.3.1
```
<dependency>
	<groupId>org.mybatis.spring.boot</groupId>
	<artifactId>mybatis-spring-boot-starter</artifactId>
	<version>1.3.1</version>
</dependency>
```

> 好了下来分别介绍两种开发模式

## 二、无配置文件注解版
> 就是一切使用注解搞定。

### 1、添加相关maven文件
```
<dependency>
	<groupId>org.mybatis.spring.boot</groupId>
	<artifactId>mybatis-spring-boot-starter</artifactId>
	<version>1.3.1</version>
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

<dependency>
          <groupId>org.springframework.boot</groupId>
          <artifactId>spring-boot-starter</artifactId>
      </dependency>

      <dependency>
	<groupId>mysql</groupId>
	<artifactId>mysql-connector-java</artifactId>
</dependency>
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-devtools</artifactId>
	<optional>true</optional>
</dependency>
```
### 2、application.properties 添加相关配置
```
server.port=8080

mybatis.type-aliases-package=com.example.mybatisspring.entity

spring.datasource.driverClassName = com.mysql.jdbc.Driver
spring.datasource.url = jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf-8
spring.datasource.username = root
spring.datasource.password = 1234567890
```

springboot会自动加载spring.datasource.*相关配置，数据源就会自动注入到sqlSessionFactory中，sqlSessionFactory会自动注入到Mapper中，对了你一切都不用管了，直接拿起来使用就行了。

在启动类中添加对mapper包扫描@MapperScan
```
@SpringBootApplication
@MapperScan("com.example.mybatisspring.mapper") 
//在启动类中添加对mapper包扫描@MapperScan
public class MybatisSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(MybatisSpringApplication.class, args);
	}
}

```
或者直接在Mapper类上面添加注解@Mapper,建议使用上面那种，不然每个mapper加个注解也挺麻烦的

### 3、开发Mapper

首先在mysql中执行如下sql语句`users.sql`：
```
create database test;
use test;
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `userName` varchar(32) DEFAULT NULL COMMENT '用户名',
  `passWord` varchar(32) DEFAULT NULL COMMENT '密码',
  `user_sex` varchar(32) DEFAULT NULL,
  `nick_name` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
```
接下来的这一步是最关键的一块，sql生产都在这里
```
public interface UserMapper {
	
	@Select("SELECT * FROM users")
	@Results({
		@Result(property = "userSex",  column = "user_sex", javaType = UserSexEnum.class),
		@Result(property = "nickName", column = "nick_name")
	})
	List<UserEntity> getAll();
	
	@Select("SELECT * FROM users WHERE id = #{id}")
	@Results({
		@Result(property = "userSex",  column = "user_sex", javaType = UserSexEnum.class),
		@Result(property = "nickName", column = "nick_name")
	})
	UserEntity getOne(Long id);

	@Insert("INSERT INTO users(userName,passWord,user_sex) VALUES(#{userName}, #{passWord}, #{userSex})")
	void insert(UserEntity user);

	@Update("UPDATE users SET userName=#{userName},nick_name=#{nickName} WHERE id =#{id}")
	void update(UserEntity user);

	@Delete("DELETE FROM users WHERE id =#{id}")
	void delete(Long id);

}
```
为了更接近生产我特地将user_sex、nick_name两个属性在数据库加了下划线和实体类属性名不一致，另外user_sex使用了枚举

- @Select 是查询类的注解，所有的查询均使用这个
- @Result 修饰返回的结果集，关联实体类属性和数据库字段一一对应，如果实体类属性和数据库属性名保持一致，就不需要这个属性来修饰。
- @Insert 插入数据库使用，直接传入实体类会自动解析属性到对应的值
- @Update 负责修改，也可以直接传入对象
- @delete 负责删除
了解更多属性参考这里

注意，使用#符号和$符号的不同：
```
// This example creates a prepared statement, something like select * from teacher where name = ?;
@Select("Select * from teacher where name = #{name}")
Teacher selectTeachForGivenName(@Param("name") String name);

// This example creates n inlined statement, something like select * from teacher where name = 'someName';
@Select("Select * from teacher where name = '${name}'")
Teacher selectTeachForGivenName(@Param("name") String name);
```


### 4、测试类使用
上面三步就基本完成了相关dao层开发，使用的时候当作普通的类注入进入就可以了
```
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserMapperTest {

	@Autowired
	private UserMapper UserMapper;

	@Test
	public void testInsert() throws Exception {
		UserMapper.insert(new UserEntity("aa", "a123456", UserSexEnum.MAN));
		UserMapper.insert(new UserEntity("bb", "b123456", UserSexEnum.WOMAN));
		UserMapper.insert(new UserEntity("cc", "b123456", UserSexEnum.WOMAN));

		Assert.assertEquals(3, UserMapper.getAll().size());
	}

	@Test
	public void testQuery() throws Exception {
		List<UserEntity> users = UserMapper.getAll();
		System.out.println(users.toString());
	}
	
	@Test
	public void testUpdate() throws Exception {
		UserEntity user = UserMapper.getOne(3l);
		System.out.println(user.toString());
		user.setNickName("neo");
		UserMapper.update(user);
		Assert.assertTrue(("neo".equals(UserMapper.getOne(3l).getNickName())));
	}
}
```

### 5、运行使用

- 运行MybatisSpringApplication
- 运行MybatisSpringApplicationTests
- 运行UserMapperTest  的 testInsert()
- 访问`http://localhost:8080/getUsers`
![insert.png](http://upload-images.jianshu.io/upload_images/688387-8dac158f255392de.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- mysql中执行`insert into users(userName,passWord,user_sex,nick_name) values("root","123456","MAN","sage_wang");`
- 再次访问`http://localhost:8080/getUsers`
![image.png](http://upload-images.jianshu.io/upload_images/688387-eb7cccbbcbca36c5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
- 访问`http://localhost:8080/delete/4`
- 再次访问`http://localhost:8080/getUsers`
![insert.png](http://upload-images.jianshu.io/upload_images/688387-8dac158f255392de.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


## 三、极简xml版本
极简xml版本保持映射文件的老传统，优化主要体现在不需要实现dao的是实现层，系统会自动根据方法名在映射文件中找对应的sql.

### 1、配置
pom文件和上个版本一样，只是application.properties新增以下配置
`
mybatis.config-locations=classpath:mybatis/mybatis-config.xml
mybatis.mapper-locations=classpath:mybatis/mapper/*.xml
`

指定了mybatis基础配置文件和实体类映射文件的地址`mybatis-config.xml` 配置
```
<configuration>
	<typeAliases>
		<typeAlias alias="Integer" type="java.lang.Integer" />
		<typeAlias alias="Long" type="java.lang.Long" />
		<typeAlias alias="HashMap" type="java.util.HashMap" />
		<typeAlias alias="LinkedHashMap" type="java.util.LinkedHashMap" />
		<typeAlias alias="ArrayList" type="java.util.ArrayList" />
		<typeAlias alias="LinkedList" type="java.util.LinkedList" />
	</typeAliases>
</configuration>
```
这里也可以添加一些mybatis基础的配置

### 2、添加User的映射文件
```
<mapper namespace="com.neo.mapper.UserMapper" >
    <resultMap id="BaseResultMap" type="com.neo.entity.UserEntity" >
        <id column="id" property="id" jdbcType="BIGINT" />
        <result column="userName" property="userName" jdbcType="VARCHAR" />
        <result column="passWord" property="passWord" jdbcType="VARCHAR" />
        <result column="user_sex" property="userSex" javaType="com.neo.enums.UserSexEnum"/>
        <result column="nick_name" property="nickName" jdbcType="VARCHAR" />
    </resultMap>
    
    <sql id="Base_Column_List" >
        id, userName, passWord, user_sex, nick_name
    </sql>

    <select id="getAll" resultMap="BaseResultMap"  >
       SELECT 
       <include refid="Base_Column_List" />
	   FROM users
    </select>

    <select id="getOne" parameterType="java.lang.Long" resultMap="BaseResultMap" >
        SELECT 
       <include refid="Base_Column_List" />
	   FROM users
	   WHERE id = #{id}
    </select>

    <insert id="insert" parameterType="com.neo.entity.UserEntity" >
       INSERT INTO 
       		users
       		(userName,passWord,user_sex) 
       	VALUES
       		(#{userName}, #{passWord}, #{userSex})
    </insert>
    
    <update id="update" parameterType="com.neo.entity.UserEntity" >
       UPDATE 
       		users 
       SET 
       	<if test="userName != null">userName = #{userName},</if>
       	<if test="passWord != null">passWord = #{passWord},</if>
       	nick_name = #{nickName}
       WHERE 
       		id = #{id}
    </update>
    
    <delete id="delete" parameterType="java.lang.Long" >
       DELETE FROM
       		 users 
       WHERE 
       		 id =#{id}
    </delete>
</mapper>
```
其实就是把上个版本中mapper的sql搬到了这里的xml中了

### 3、编写Dao层的代码
```
public interface UserMapper {
	
	List<UserEntity> getAll();
	
	UserEntity getOne(Long id);

	void insert(UserEntity user);

	void update(UserEntity user);

	void delete(Long id);

}
```
对比上一步这里全部只剩了接口方法

### 4、使用
使用和上个版本没有任何区别，大家就看代码吧

## 四、如何选择
两种模式各有特点，注解版适合简单快速的模式，其实像现在流行的这种微服务模式，一个微服务就会对应一个自已的数据库，多表连接查询的需求会大大的降低，会越来越适合这种模式。

老传统模式比适合大型项目，可以灵活的动态生成SQL，方便调整SQL，也有痛痛快快，洋洋洒洒的写SQL的感觉。


## 附录

[如何优雅的使用mybatis](http://www.ityouknow.com/springboot/2016/11/06/springboot(%E5%85%AD)-%E5%A6%82%E4%BD%95%E4%BC%98%E9%9B%85%E7%9A%84%E4%BD%BF%E7%94%A8mybatis.html)

[示例代码-github](https://github.com/wsqat/spring-cloud/tree/master/mybatis-spring)