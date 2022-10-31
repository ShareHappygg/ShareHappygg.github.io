# 一、MySQL

## 1、MySQL环境搭建

### 1.1、MySQL的下载、安装、配置（百度）

### 1.2、mysql的启动和停止

- 使用图形界面工具

  - 在任务管理器中的服务找到相关的服务，然后点击启动或停止

- 使用命令行工具

  - ```mysql
    # 启动 MySQL 服务命令：
    net start MySQL服务名
    
    # 停止MySQL 服务命令：
    net stop MySQL服务名
    ```

  

### 1.3、mysql的登录和退出

- 在命令行中操作：

  - ```mysql
    #登录 （方式一）： （可切换不同端口，登录不同版本的mysql）
    mysql -uroot -p000214 -hlocalhost -P3306
    
    # 登录（方式二）：
    mysql -u root -p
    # 可切换不同mysql版本(-h 后可加不同主机的mysql)
    mysql -u root -P 3307 -h localhost -p
    
    # 退出
    quit
    ```

​	        **注：只需要修改mysql密码和默认端口号，就可以切换不同版本的mysql**



- **如何在Windows系统删除之前的未卸载干净的MySQL服务列表？**
  - 以管理员身份打开cmd，在命令行中输入“sc delete MySQL服务名”，即可删除服务

### 1.4、使用的演示(命令行中)

- 查看数据库

  ```mysql
  show database；
  ```

- 创建数据库

  ```mysql
  create database 数据库名；
  ```

- 使用数据库

  ```mysql
  use 数据库名;
  ```

- 查看某个库的所有表

  ```mysql
  show tables; # 要求前面有use语句
  
  show tables from 数据库名；
  ```

- 创建新的表

  ```mysql
  create table 表名称(
  	字段名   数据类型，
  	字段名   数据类型
  );
  ```

- 查看一个表的数据

  ```mysql
  select * form 数据库表名称;
  ```

- 添加一条记录

  ```mysql
  insert into 表名称 values(值列表);
  ```

- 查看表的创建信息

  ```mysql
  show create table 表名称\G;
  ```

- 查看数据库的创建信息

  ```mysql
  show create database 数据库名\G;
  ```

- 删除表

  ```mysql
  drop table 表名称；
  ```

- 删除数据库

  ```mysql
  drop database 数据库名;
  ```

  

### 1.5、mysql的编码设置

- MySQL5.7中

**问题再现: 命令行操作sql乱码问题**

```mysql
mysql> INSERT INTO t_stu VALUES(1, '张三', '男');
ERROR 1366(hy000): Incorrect string value: '\xD5\xC5\xC8\xFD' for column 'sname' at row 1
```

**问题解决**

- **步骤1： 查看编码命令**

  ```mysql
  show variables like 'character_%';
  show variables like 'collation_%';
  ```

- **步骤2： 修改mysql的数据目录下的my.ini配置文件**

  ```ini
  [mysql]
  ...
  default-character-set=utf8  #默认字符集
  
  [mysql]
  ...
  character-set-server=utf8
  collation-server=utf8_general_ci
  ```

- 步骤3： 重启服务

- 步骤4： 查看编码命令

## 2、基本的SELECT语句

### 2.1、SQL的分类

- **DDL： 数据定义语言。** CREATE \ ALTER \ DROP \ RENAME \ TRUNCATE
- **DML:   数据操作语言。**INSERT \ DELETE \ UPDATE \ SELECT 
- **DCL:     数据控制语言。**COMMIT \ ROLLBACK \ SAVEPOINT \ GRANT \ REVOKE

### 2.2、数据的导入

- ​	**在命令行中导入**

  ```mysql
  mysql> source d:\mysqldb.sql
  ```

- 通过可视化工具导入

### 2.3、列的别名

```sql
# as:全称： alias(别名)， 可以省略
# 列的别名可以使用一对“”引起来，不要使用''。
select employee_id emp_id, last_name AS lname, department_id "部门id", salary * 12 AS "annual sal" FROM employees;
```

### 2.4、去除重复行

```sql
SELECT DISTINCT department_id FROM employees;
```

### 2.5、空值参与运算

```sql
# 1. 控制： null
# 2. null不等同于0, '', 'null'
# 3. 空值参与运算： 结果一定也为空。
SELECT employee_id, salary "月工资", salary * (1 + commission_pct) * 12 "年工资",commission_pct FROM employees;

# 问题的解决方案： 引入IFNULL
SELECT employee_id, salary "月工资", salary * (1 + IFNULL(commission_pct, 0)) * 12 "年工资", commission_pct FROM employees;
```

### 2.6、显示表结构

```sql
DESCRIBE employees;

DESC employees;

DESC departments;
```



## 3、运算符

### 3.1、<=> 安全等于。

```mysql
WHERE commission_pct <=> NULL;
```

### 3.2、IS NULL 和 IS NOT NULL

```SQL
# 查询为空的数据
WHERE commission_pct IS NULL;
# 或
WHERE ISNULL(commission_pct);

# 查询不为空的数据
WHERE commission_pct IS NOT NULL;
```

### 3.3、BETWEEN  条件1 AND 条件2 

- 查询条件1和条件2范围内的数据，包含边界

  ```sql
  WHERE salary between 600 and 8000;
  ```

### 3.4、 IN 和 NOT IN 

```sql
WHERE department_id IN (10,20,30);
WHERE SALARY NOT IN (6000,7000,,8000);
```

### 3.5、 LIKE (模糊查询)

```sql
# LIKE: 模糊查询
# %： 代表不确定个数的字符(0个， 1个， 或多个)

# 包含'a'的员工信息
WHERE last_name LIKE '%a%';

# 以字符'a'开头的员工信息
WHERE last_name LIKE 'a%';

# 查询包含字符'a'且包含字符'e'的员工信息
WHERE last_name LIKE '%a' AND last_name LIKE '%e%';
WHERE last_name LIKE '%a%e%' OR last_name LIKE '%e%a%';

# _: 代表一个不确定的字符
WHERE last_name LIKE '_a%';
```

## 4、 排序与分页

### 4.1、排序

````sql
# 使用 ORDER BY 对查询到的数据进行排序操作。
# 升序： ASC
# 降序： DESC

ORDER BY salary DESC;
````

**二级排序**

```sql
# 显示员工信息，按照department_id的降序排列，salary的升序排列
ORDER BY department_id DESC,salary ASC;
```





# 二、Mybatis

## 1、简介

### 1.1、什么是Mybatis

- Mybatis是一款优秀的持久层框架
- 它支持定制化SQL 、 存储过程以及高级映射。
- Mybatis避免了几乎所有的JDBC 代码和手动设置参数以及获取结果集。
- Mybatis 可以使用简单的XML 或注解来配置和映射原生类型、接口和java的POJO未数据库中的记录。



**如何获取Mybatis：**

- maven仓库：

```xml
<dependency>
	<groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.2</version>
</dependency>

```

### 1.2、为什么需要Mybatis

- 帮助程序员将数据存入到数据库中。
- 方便
- 传统的JDBC代码太复杂了。简化。框架。自动化。
- 不用Mybatis也可以。  更容易上手。
- 优点：
  - 简单易学
  - 灵活
  - sql和代码的分离，提高了可维护性
  - 提供映射标签，支持对象与数据库的orm字段关系映射
  - 提供对象关系映射标签，支持对象关系组建维护
  - 提供xml标签，支持编写动态sql

## 2、Mybatis程序

### 2.1、搭建环境

- 搭建数据库

```sql
create database `mybatis`;

use `mybatis`;

create table `user` (
	`id` int(20) NOT NULL PRIMARY KEY,
	`name` VARCHAR(30) DEFAULT NULL,
	`pwd` VARCHAR(30) DEFAULT NULL
)ENGINE=INNODB DEFAULT CHARSET=UTF8;


INSERT INTO `user`(`id`,`name`,`pwd`) VALUES
(1, '狂', '11222'),
(2, '东东', '2212'),
(3, '但是', '11121')
```

- 新建项目

  - 新建一个普通的maven项目

  - 删除src目录

  - 导入maven依赖

    ```xml
     <!--  导入依赖  -->
        <dependencies>
            <!-- mysql驱动   -->
            <dependency>
                <groupId>mysql</groupId>
                <artifactId>mysql-connector-java</artifactId>
                <version>8.0.15</version>
            </dependency>
            <!--  mybatis  -->
            <dependency>
                <groupId>org.mybatis</groupId>
                <artifactId>mybatis</artifactId>
                <version>3.5.9</version>
            </dependency>
            <!--  junit   -->
            <dependency>
                <groupId>junit</groupId>
                <artifactId>junit</artifactId>
                <version>3.8.2</version>
            </dependency>
        </dependencies>
    
    ```

### 2.2、创建模块

- 编写mybatis的核心配置文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLEC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <environments default="development">
        <environment id="development">
            <transctionManager type="JDBC" />
            <dataSource type="POOLED">
                <poperty name="driver" value="com.mysql.jdbc.Driver"/>
                <poperty name="url" value="jdbc:mysql://localhost:3306/mybatis?useSSL=true&amp;useUnicode=true&amp;characterEncoding=UTF-8&amp;serverTimezone=GMT"/>
                <poperty name="username" value="root"/>
                <poperty name="password" value="000214"/>
            </dataSource>
        </environment>
    </environments>
   <!-- 每一个Mapper.xml都需要在Mybatis核心配置文件中注册 -->
    <mappers>
        <mapper resource="com/cjq/dao/UserMapper.xml"/>
    </mappers>
</configuration>

```

- 编写mybatis工具类

```java
public class MybatisUtils {

    private static SqlSessionFactory sqlSessionFactory;

    static {
        try {
            // 使用Mybatis第一步： 获取sqlSessionFactory对象
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    // 既然后了SqlSessionFactory，我们就可以从中获得SqlSession的实例了。
    // SqlSession 完全包含了面向数据库执行SQL命令所需的所有方法。
    public static SqlSession getSqlSession() {
        return sqlSessionFactory.openSession();
    }
}
```

### 2.3、 编写代码

- **实体类**

```java
package com.cjq.pojo;

public class User {
    private int id;
    private String name;
    private String pwd;

    public User() {
    }
    public User(int id, String name, String pwd) {
        this.id = id;
        this.name = name;
        this.pwd = pwd;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPwd() {
        return pwd;
    }
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}

```

- **Dao接口**

```java
public interface UserDao {
    List<User> getUserList();
}
```

- **接口实现类由原来的UserDaoImpl转变为一个Mapper配置文件**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<!--namespace=绑定一个对应的Dao/Mapper接口-->
<mapper namespace="com.cjq.dao.UserDao">
    <!--select查询语句-->
    <select id="getUserList" resultType="com.cjq.pojo.User">
        select * form mybatis.user
    </select>
</mapper>
```

- **测试**

```java
	@Test
	public void test() {
        //第一步： 获取SqlSession对象
        SqlSession sqlSession = MybatisUtils.getSqlSession();
        
         //方式一：getMapper
        UserDao userDao = sqlSession.getMapper(UserDao.class);
        List<User> userList = userDao.getUserList();

        for(User user: userList) {
            System.out.println(user);
        }

        //关闭SqlSession
        sqlSession.close();
    }
```

### 2.4、使用map查询

```xml
<!-- 对象中的属性，可以直接取出来 -->
<insert id="addUser" parameterType="com.cjq.pojo.User">
    insert into mybatis.user (id, name, pwd) values(#{id}, #{name}, #{pwd});
</insert>

<!-- 对象中的属性，可以直接取出来  -->
<insert id="addUser" parameterType="map">
	insert into mybatis.user(id, pwd) values (#{userid}, #{password});
</insert>
```

### 2.5、模糊查询

- java代码执行的时候，传递通配符%%

```java
List<User> userList = mapper.getUserLike("%李%");
```

- 在sql拼接中使用通配符！

```sql
select * from mybatis.user where name like "%"#{value}"%";
```

## 3、配置解析

### 3.1、核心配置文件

- mybatis-config.xml
- MyBatis 的配置文件包含了会深深影响MyBatis行为的设置和属性信息。

```
configuration(配置)
properties(属性)
settings(设置)
typeAliases(类型别名)
typeHandlers(类型处理器)
objectFactory(对象工厂)
plugins(插件)
environments(环境配置)
environment(环境变量)
transactionManager(事务管理器)
dataSource(数据源)
databaseIdProvider(数据库厂商标识)
mappers(映射器)
```

### 3.2、环境配置(environments)

MyBatis可以配置成适应多种环境

**不过要记住： 尽管可以配置多个环境，但每个SqlSessionFactory实例只能选择一种环境。**

Mybatis默认的事务管理器就是JDBC， 连接池： POOLED

### 3.3、属性(properties)

我们可以通过properties属性来实现引用配置文件

这些属性都是可外部配置且可动态替换的，既可以在典型的java属性文件中配置，亦可通过properties元素的子元素来传递。【db.properties】

**编写一个配置文件：**

**da.properties**

```properties
driver=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/mybaties？useSSL=true&useUnicode=true&characterEncoding=UTF-8
username=root
password=000214
```

**在核心配置文件中映入：**

```xml
<!-- 引入外部配置文件 -->
<properties resource="db.properties">
	<property name="username" value="root" />
    <property name="pwd"  value="111" />
</properties>
```

- 可以直接引入外部文件
- 可以在其中增加一些属性配置
- 如果两个文件有同一个字段，优先使用外部配置文件的！

### **3.4、类型别名(typeAliases)**

- 类型别名是为java类型设置一个短的名字。
- 存在的意义仅在于用来减少类完全限定名的冗余。

```xml
<!-- 可以给实体类起别名 -->
<typeAliases>
	<typeAlias type="com.cjq.pojo.User" alias="User"/>
</typeAliases>
```

也可以指定一个包名，MyBatis会在包名下面搜索需要的 java Bean，比如：

扫描实体类的包，它的默认别名就为这个类的 类名，首字母小写！

```xml
<!--可以给实体类起别名-->
<typeAliases>
	<package name="com.kuang.pojo" />
</typeAliases>
```

### 3.5、映射器(mappers)

MapperRegistry: 注册绑定我们的Mapper文件

**方式一：**【推荐使用】

```xml
<!-- 每一个Mapper.XML 都需要在Mybatis核心配置文件中注册 -->
<mappers>
	<mapper resource="com/cjq/dao/UserMapper.xml" />
</mappers>
```

**方式二：使用calss文件绑定注册**

```xml
<!-- 每一个Mapper.XML 都需要在Mybatis核心配置文件中注册 -->
<mappers>
	<mapper classs="com/cjq/dao/UserMapper" />
</mappers>
```

**注意点：**

- 接口和它的Mapper配置文件必须同名！
- 接口和它的Mapper配置文件必须在同一个包下！

**方式三：使用扫描包进行注入绑定**

```xml
<!-- 每一个Mapper.XML 都需要在Mybatis核心配置文件中注册 -->
<mappers>
	<package name="com.cjq.dao" />
</mappers>
```

**注意点：**

- 接口和它的Mapper配置文件必须同名！
- 接口和它的Mapper配置文件必须在同一个包下！

### 3.6、resultMap

**注： 解决属性名和字段名不一致的问题**

```
id  name  pwd
id  name  password
```

```xml
<!--结果集映射--><!--相同的字段可以不写-->
<resultMap id="UserMap" type="User">
	<!-- column 数据库中的字段， property实体类中的属性 -->
    <result column="id" property="id"/>
    <result column="name" property="name"/>
    <result column="pwd" property="password"/>
</resultMap>

<select id="getUserById"  resultMap="UserMap">
	select * from mybatis.user where id=#{id}
</select>
```

## 4、日志

### 4.1、日志工厂

如果一个数据库操作，出现了异常，我们需要排错。日志就是最好的助手！

![image-20220711111526230](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220711111526230.png)

在Mybatis中具体使用哪个日志实现，在设置中设定！

STDOUT_LOGGING标准日志输出

在mybatis核心配置文件中，配置我们的日志！

```xml
<settings>
	<setting name="logImpl" value="STDOUT_LOGGING"/>
</settings>
```

### 4.2、Log4j

- 可以控制每一条日志的输出格式；
- 通过定义每一条日志信息的级别，我们能够更加细致地控制日志的生成过程。
- 通过一个配置文件来灵活地进行配置，而不需要修改应用的代码。

**(1).先导入log4j的包**

```xml
<!-- https://mvnrepository.com/artifact/log4j/log4j -->
<dependency>
	<groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
```

**(2).log4j.properties**

```properties
# 将登记为DEBUG的日志信息输出到console和file这两个目的地，console和file的定义在下面的代码
log4j.rootLogger=DEBUG,console, file

#控制台输出的相关设置
log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.Target = Systen.out
log4j.appender.console.Threshold=DEBUG
log4j.appender.console.layout = org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=[%c]-%m%n

#文件输出的相关设置
log4j.appender.file = org.apache.log4j.RollingFileAppender
log4j.appender.file.File = ./log/luang.log
log4j.appender.file.MaxFilesSize = 10mb
log4j.appender.file.Threshold = DEBUG 
log4j.appender.file.layout = org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern = [%p][%d{yy-MM-dd}][%c]%m%n

#日志输出级别
log4j.logger.org.mybatis=DEBUG
log4j.logger.java.sql = DEBUG
log4j.logger.java.sql.Statement = DEBUG
log4j.logger.java.sql.ResultSet = DEBUG
log4j.logger.java.sql.PreparedStatement = DEBUG
```

**(3).配置log4j为日志的实现**

```xml
<settings>
	<setting name="logImpl" value="" />
</settings>
```



**简单使用**

1、在要使用Log4j的类中，导入包 import org.apache.log4j.Logger;

2、日志对象，参数为当前类的class

```java
static Logger logger = Logger.getLogger(UserDaoTest.class)
```

3、日志级别

```java
logger.info("info: 进入了testLog4j");
logger.debug("debug: 进入了testLog4j");
logger.error("error: 进入了testLog4j");
```



## 5、分页

### 5.1、使用Limit分页

```sql
语法： SELECT * from user limit startIndex, pageSize;
SELECT * from user limit 3; #[0,n]
```

- **使用Mybatis实现分页，核心SQL**

1、接口

```java
// 分页
List<User> getUserByLimit(Map<String,Integer> map);
```

2、Mapper.xml

```xml
<!--分页-->
<select id="getUserByLimit" parameterType="map" resultMap="UserMap">
	select * from mybatis.user limit #{startIndex}, #{pageSize}
</select>
```

3、测试

```java
@Test
public void getUserByLimit() {
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    UserMapper mapper = sqlSession.getMapper(UserMapper.class);
    
    HashMap<String, Integer> map = new HashMap<String, Integer>();
    map.put("startIndex", 1);
    map.put("pageSize", 2);
    List<User> userList = mapper.getUserByLimit(map);
    for (User user : userList) {
        System.out.println(user);
    }
    sqlSession.close();
}
```

### 5.2、RowBounds分页

不再使用SQL实现分页

1、接口

```java
List<User> getUserByRowBounds();
```

2、mapper.xml

```xml
<select id="getUserByRowBounds" resultMap="UserMap">
	select * from mybatis.user
</select>
```

3、测试

```java
@Test
public void getUserByRowBounds() {
    SqlSession sqlSession = MybatisUtils.getSqlSession();
    
    // RowBounds实现
    RowBounds rowBounds = new RowBounds(1,2);
    
    // 通过java代码层面实现分页
    List<User> userList = sqlSession.selectList("com.cjq.dao.UserMapper.getUserByRowBounds", null, rowBounds);
    for(User user: userList) {
        System.out.println(user);
    }
    sqlSession.close();
}
```

### 5.3、分页插件





## 6、多对一处理

### 测试环境搭建

- 导入lombok
- 新建实体类 Teacher， Student
- 建立Mapper接口
- 简历Mapper.XML文件
- 在核心配置文件中绑定注册我们的Mapper接口或者文件！
- 测试查询是否能够成功！

### 按照查询嵌套处理

```xml
<!-- 
思路： 
	1. 查询所有的学生信息
	2. 根据查询出来的学生的tid，寻找对应的老师！  子查询
-->

<select id="getStudent" resultMap="StudentTeacher">
	select * from student
</select>

<resultMap id="StudentTeacher" type="Student">
	<result property="id" column="id"/>
	<result property="name" column="name"/>
	<!--复杂的属性，我们需要单独处理  对象： association   集合： collection-->
    <association property="teacher" column="tid" javaType="Teacher" select="getTeacher"/>
</resultMap>

<select id="getTeacher" resultType="Teacher">
	select * from teacher where id = #{id}
</select>
```

### 按照结果嵌套处理

```xml
<!--按照结果嵌套处理-->
<select id="getStudent2" resultMap="StudentTeacher2">
	select s.id sid, s.name sname,t.name tname
    from student s, teacher t
    where s.tid = t.id;
</select>

<resultMap id="StudentTeacher2" type="Student">
	<result property="id" column="sid" />
    <result property="name" column="sname"/>
    <association property="teacher" javaType="Teacher">
    	<result property="name" column="tname" />
    </association>
</resultMap>
```



## 7、一对多处理

比如： 一个老师拥有多个学生！

对老师而言，就是一对多的关系！

**实体类：**

```java
@Data
public class Student {
    private int id;
    private String name;
    private int tid;
}
```

```java
@Data
public class Teacher {
    private int id;
    private String name;
   
    // 一个老师拥有多个学生
    private List<Student> students;
}
```

### 按照结果嵌套处理

```xml
<!--按结果嵌套查询-->
<select id="getTeacher" resultMap="TeacherStudent">
	select s.id sid, s.name sname, t.name tname, t.id tid
    from student s, teacher t
    where s.tid = t.id and t.id = #{tid}
</select>

<resultMap id="TeacherStudent" type="Teacher">
	<result property="id" column="tid"/>
    <result property="name" column="tname"/>
    <!--复杂的属性，我们需要单独处理  对象：association  集合： collection
	javaType=""  指定属性的类型
	集合中的泛型信息，我们使用ofType获取
	-->
    <collecion property="students" ofType="Studnet">
    	<result property="id" column="sid"/>
        <result property="name" column="sname"/>
        <result property="tid" column="tid"/>
    </collecion>
</resultMap>
```

### 按照查询嵌套处理

```xml
<select id="getTeacher2" resultMap="TeacherStudent2">
	select * from mybatis.teacher where id = #{tid}
</select>

<resultMap id="TeacherStudent2" type="Teacher">
	<collection property="students" javaType="ArrayList" ofType="Student" select="getStudentByTeacherId" column="id"/>
</resultMap>

<select id="getStudentByTeacherId" resultType="Studnet">
	select * from mybatis.student where tid=#{tid}
</select>
```

**小结**

1.关联-association 【多对一】

2.集合-collection 【一对多】

3.javaType   & ofType

​	1.JavaType  用来指定实体类中属性的类型

​	2.ofType  用来指定映射到List或者集合中的 pojo类型，泛型中的约束类型！

 

**慢SQL   1s    1000s**

- Mysql引擎
- InnoDB底层原理
- 索引
- 索引优化



## 8、动态SQL

**什么是动态SQL： 动态SQL就是指根据不同的条件生成不同的SQL语句**

### 搭建环境

1、创建表

2、创建一个基础工程

-  导包

- 编写配置文件

- 编写实体类

  - ```java
    @Data
    public class Blog {
    	private int id;
    	private String title;
    	private String author;
    	private Data createTime;
    	private int views;
    }
    ```

    

- 编写实体类对应Mapper接口和Mapper.XML文件



### IF

```xml
<select id="queryBlogIF" parameterType="map" resultType="blog">
	select * from mybatis.blog
    <where>
    	<if test="test != null">
    		and title = #{title}
    	</if>
    	<if test="author != null">
    		and author = #{author}
    	</if>
    </where>
</select>
```



### choose(when, otherwise)

```xml
<select id="queryBlogChoose" parameterType="map" resultType="blog">
	select * from mybatis.blog
    <where>
    	<choose>
        	<when test="title != null">
            	title =#{title}
            </when>
            <when test="author != null">
            	and author = #{author}
            </when>
            <otherwise>
            adn views = #{views}
            </otherwise>
        </choose>
    </where>
</select>
```





### trim(where, set)

```xml
<select id="queryBlogIF" parameterType="map" resultType="blog">
	select * form mybatis.blog
    <where>
    	<if test="title != null">
        	title = #{title}
        </if>
        <if test="author != null">
        	and author = #{author}
        </if>
    </where>
</select>
```

```xml
<update id="updateBlog" parameterType="map">
	update mybatis.blog
    <set>
    	<if test="title != null">
        	title = #{title
        </if>
        <if test="author != null">
        	author = #{author}
        </if>
    </set>
    where id = #{id}
</update>
```

### SQL片段

有的时候，我们可能会将一些功能的部分抽取出来，方便复用！

1、使用SQL标签抽取公共的部分

```xml
<sql id="if-title-author">
	<if test="title != null">
    	title = #{title}
    </if>
    <if test="author != null">
    	and author = #{author}
    </if>
</sql>
```

2、 在需要使用的地方使用include标签引入即可

```xml
<select id="queryBlogIF" parameterType="map" resultType="blog">
	select * from mybatis.blog
    <where>
    	<include refid="if-title-author"></include>
    </where>
</select>
```

**注意事项：**

- 最好基于单表来定义SQL片段！
- 不要存在where标签

### Foreach

```xml
<!--传递一个map，这map中可以存在一个集合！-->
<select id="queryBlogForeach" parameterType="map" resultType="blog">
	select * from mybatis.blog
    <where>
    	<foreach collection="ids" item="id" open="and (" close=")" separator="or">
        	id = #{id}
        </foreach>
    </where>
</select>
```



**动态SQL就是在拼接SQL与，我们只要保证SQL的正确性，按照SQL的格式，去排列组合就可以了.**



## 9、缓存

### 9.1、简介

- **什么是缓存？**
  - 存在内存中的临时数据。
  - 将用户经常查询的数据放在缓存(内存)中，用户去查询数据就不用从磁盘上(关系型数据库数据文件)查询，从缓存中查询，从而提高查询效率，解决了高并发系统的性能问题。
- **为什么使用缓存？**
  - 减少和数据库的交互次数，减少系统开销，提高系统效率。
- **什么样的数据能使用缓存？**
  - 经常查询并且不经常改写的数据。

### 9.2、Mybatis缓存

- Mybatis包含了一个非常强大的查询缓存特性，它可以非常方便地定制和配置缓存。缓存可以极大的提升查询效率。
- MyBatis系统中默认定义了两级缓存： **一级缓存**和**二级缓存**
  - 默认情况下，只有一级缓存开启。（SqlSession级别的缓存，也称为本地缓存)
  - 二级缓存需要手动开启和配置，它是基于nmaspace级别的缓存。
  - 为了提高扩展性，Mybatis定义了缓存接口Cache。我们可以通过实现Cache接口来自定义二级缓存

### 9.3、一级缓存

- 一级缓存也叫本地缓存： SqlSession
  - 与数据库同一次会话期间查询到的数据会放到本地缓存中。
  - 以后如果需要获取相同的数据，直接从缓存中拿，没必要再去查询数据库。



**测试步骤：**

1、开启日志！

2、测试在一个Session中查询两次相同记录

3、查看日志输出

![image-20220712111933116](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220712111933116.png)

**缓存失效的情况：**

1、查询不同的东西

2、增删改操作，可能会改变原来的数据，所以必定会刷新缓存！

3、查询不同的Mapper.xml

4、手动清除缓存！

![image-20220712112454119](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220712112454119.png)

### 9.4、二级缓存

- 二级缓存也叫全局缓存，一级缓存作用域太低了，所以诞生了二级缓存
- 基于namespace级别的缓存，一个名称空间，对应一个二级缓存；
- 工作机制
  - 一个会话查询一条数据，这个数据就会被放在当前会话的一级缓存中；
  - 如果当前会话关闭了，这个会话对应的一级缓存就没了；但是我们想要的是，会话关闭了，一级缓存中的数据被保存到二级缓存中；
  - 新的会话查询信息，就可以从二级缓存中获取内容；
  - 不同的mapper查出数据会放在自己对应的缓存中；

**步骤：**

1、开启全局缓存

```xml
<!--开启全局缓存-->
<setting name="cacheEnabled" value="true"/>
```

2、在要使用二级缓存的Mapper中开启

```xml
<!--在当前Mapper.xml中使用二级缓存-->
<cache/>
```

也可以自定义参数

```xml
<!--在当前Mapper.xml中使用二级缓存-->
<cache eviction="FIFO"
       flushInterval="6000"
       size="512"
       readOnly="true"
       />
```

### 9.5、缓存原理

![image-20220712114858390](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220712114858390.png)









# 三、SpringCloud



## 1、微服务

### 1.1、什么是微服务

- **将单一的应用程序划分为一组小的服务。**
- **微服务化的核心就是将传统的一站式应用，根据业务拆分成一个一个的服务，彻底地去耦合，每一个微服务提供单个业务功能的服务，一个服务做一件事情，从技术角度看就是一种小而独立的处理过程，类型进程的概念，能够自行单独启动或销毁，拥有自己独立的数据库。**

### 1.2、微服务优缺点

- 优点
  - 单一职责原则
  - 每个服务足够内聚，足够小，代码容易理解，这样能聚焦一个指定的业务功能或业务需求；
  - 开发简单，开发效率提高，一个服务可能就是专一的只干一件事；
  - 微服务是松耦合的，是有功能意义的服务，无论是在开发阶段或部署阶段都是独立的。
  - 微服务能使用不同的语言开发
  - 易于和第三方集成，微服务允许容易且灵活的方式集成自动部署，通过持续集成工具，如jenkins，Hudson，bamboo
  - 微服务允许利用融合最新技术
  - 微服务只是业务逻辑的代码，不会和HTML , CSS或其他界面混合
  - 每个微服务都有自己的存储能力，可以有自己的数据库，也可以有统一数据库
- 缺点
  - 开发人员要处理分布式系统的复杂性
  - 多服务运维难度，随着服务的增加，运维的压力页增大
  - 系统部署依赖
  - 服务间通信成本
  - 数据一致性
  - 系统集成测试
  - 性能监控

## 2、springCloud简介

### 2.1、什么是springCloud

![image-20220712160258729](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220712160258729.png)

### 2.1、SpringCloud和SpringBoot关系

![image-20220712160343090](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220712160343090.png)

### 2.3、Dubbo和SpringCloud技术选型

**1、分布式 + 服务治理Dubbo**

目前成熟的互联网架构： 应用服务化拆分 + 消息中间件

![image-20220712161003109](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220712161003109.png)





# 四、SpringSecurity

## 	1、简介

**SpringSecurity**是一个安全管理框架

一般Web应用的需要进行**认证**和**授权。**

​	**认证：验证当前访问系统的是不是本系统的用户，并且要确认具体是那个用户**

​	**授权：经过认证后判断当前用户是否有权限进行某个操作**

## 2、搭建环境

创建工程，导入对应依赖

```xml
<parent>
   	<groupId>org.springframework.boot</groupId>
   	<artifactId>spring-boot-starter-parent</artifactId>
   	<version>2.6.7</version>
   	<relativePath/> <!-- lookup parent from repository -->
</parent>
 <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
      <!--  Security框架  -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
            <version>2.1.6.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.20</version>
        </dependency>
</dependencies>
```

## 3、认证

**登录校验流程**

![image-20220717101056773](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220717101056773.png)

**认证权限流程**

![image-20220717102649001](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220717102649001.png)

Authentication接口： 它的实现类，表示当前访问系统的用户，封装了用户相关信息。

AuthenticationManager接口： 定义了认证Authentication的方法

UserDetailsService接口： 加载用户特定数据的核心接口。里面定义了一个根据用户名查询用户信息的方法。

UserDetail接口：提供核心用户信息。通过UserDetailsService根据用户名获取处理的用户信息要封装成UserDetails对象返回。然后将这些信息封装到Authentication对象中。

**思路分析：**

- 登录
  - 自定义登录接口
    - 调用ProvierManager的方法进行认证  如果认证通过生成jwt
    - 把用户信息存入redis中
  - 自定义UserDetailsService
    - 在这个实现类中去查询数据库
- 校验
  - 自定义jwt认证过滤器
    - 获取token
    - 解析token获取其中的userid
    - 从redis中获取用户信息
    - 存入SecurityContextHolder

![image-20220717103214554](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220717103214554.png)

![image-20220717103313846](C:\Users\admin\AppData\Roaming\Typora\typora-user-images\image-20220717103313846.png)



### 3.1、登录接口

- 接下我们需要自定义登陆接口，然后让SpringSecurity对这个接口放行,让用户访问这个接口的时候不用登录也能访问。
- 在接口中我们通过AuthenticationManager的authenticate方法来进行用户认证,所以需要在SecurityConfig中配置把AuthenticationManager注入容器。
-  认证成功的话要生成一个jwt，放入响应中返回。并且为了让用户下回请求时能通过jwt识别出具体的是哪个用户，我们需要把用户信息存入redis，可以把用户id作为key。

```java
@RestController
public class LoginController {
    @Autowired
    private LoginServcie loginServcie;

    @PostMapping("/user/login")
    public ResponseResult login(@RequestBody User user){
        return loginServcie.login(user);
    }
}
```

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
```

```java
@Service
public class LoginServiceImpl implements LoginServcie {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //使用userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //authenticate存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);
        //把token响应给前端
        HashMap<String,String> map = new HashMap<>();
        map.put("token",jwt);
        return new ResponseResult(200,"登陆成功",map);
    }
}

```

### 3.2、认证过滤器

-  我们需要自定义一个过滤器，这个过滤器会去获取请求头中的token，对token进行解析取出其中的userid。
-  使用userid去redis中获取对应的LoginUser对象。
-  然后封装Authentication对象存入SecurityContextHolder

```java
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取token
        String token = request.getHeader("token");
        if (!StringUtils.hasText(token)) {
            //放行
            filterChain.doFilter(request, response);
            return;
        }
        //解析token
        String userid;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            userid = claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("token非法");
        }
        //从redis中获取用户信息
        String redisKey = "login:" + userid;
        LoginUser loginUser = redisCache.getCacheObject(redisKey);
        if(Objects.isNull(loginUser)){
            throw new RuntimeException("用户未登录");
        }
        //存入SecurityContextHolder
        //TODO 获取权限信息封装到Authentication中
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser,null,null);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        //放行
        filterChain.doFilter(request, response);
    }
}

```

```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //把token校验过滤器添加到过滤器链中
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

```

### 3.3、退出登录

- 我们只需要定义一个登陆接口，然后获取SecurityContextHolder中的认证信息，删除redis中对应的数据即可。

```java
@Service
public class LoginServiceImpl implements LoginServcie {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(),user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //使用userid生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //authenticate存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);
        //把token响应给前端
        HashMap<String,String> map = new HashMap<>();
        map.put("token",jwt);
        return new ResponseResult(200,"登陆成功",map);
    }

    @Override
    public ResponseResult logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        Long userid = loginUser.getUser().getId();
        redisCache.deleteObject("login:"+userid);
        return new ResponseResult(200,"退出成功");
    }
}

```

## 4、授权

- 不同的用户可以使用不同的功能。这就是权限系统要去实现的效果。

-  我们不能只依赖前端去判断用户的权限来选择显示哪些菜单哪些按钮。因为如果只是这样，如果有人知道了对应功能的接口地址就可以不通过前端，直接去发送请求来实现相关功能操作。

-  所以我们还需要在后台进行用户权限的判断，判断当前用户是否有相应的权限，必须具有所需权限才能进行相应的操作。

### 4.1、授权基本流程

- 在SpringSecurity中，会使用默认的FilterSecurityInterceptor来进行权限校验。在FilterSecurityInterceptor中会从SecurityContextHolder获取其中的Authentication，然后获取其中的权限信息。当前用户是否拥有访问当前资源所需的权限。

-  所以我们在项目中只需要把当前登录用户的权限信息也存入Authentication。

-  然后设置我们的资源所需要的权限即可。

### 4.2、实现（详细看）



## 5、自定义失败处理

### 5.1、自定义实现类

```java
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ResponseResult result = new ResponseResult(HttpStatus.FORBIDDEN.value(), "权限不足");
        String json = JSON.toJSONString(result);
        WebUtils.renderString(response,json);

    }
}
```

```java
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ResponseResult result = new ResponseResult(HttpStatus.UNAUTHORIZED.value(), "认证失败请重新登录");
        String json = JSON.toJSONString(result);
        WebUtils.renderString(response,json);
    }
}
```

### 5.2、配置给SpringSecurity

- 先注入对应的处理器

  - ```java
    @Autowired
        private AuthenticationEntryPoint authenticationEntryPoint;
    
        @Autowired
        private AccessDeniedHandler accessDeniedHandler;
    
    ```

    - ​	使用HttpSecurity对象的方法配置

      ```java
      http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).
                      accessDeniedHandler(accessDeniedHandler);
      ```

      

## 6、跨域

- 浏览器出于安全的考虑，使用 XMLHttpRequest对象发起 HTTP请求时必须遵守同源策略，否则就是跨域的HTTP请求，默认情况下是被禁止的。 同源策略要求源相同才能正常进行通信，即协议、域名、端口号都完全一致。

-  前后端分离项目，前端项目和后端项目一般都不是同源的，所以肯定会存在跨域请求的问题。

-  所以我们就要处理一下，让前端能进行跨域请求。

①先对SpringBoot配置，运行跨域请求

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
      // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许cookie
                .allowCredentials(true)
                // 设置允许的请求方式
                .allowedMethods("GET", "POST", "DELETE", "PUT")
                // 设置允许的header属性
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }
}

```

②开启SpringSecurity的跨域访问

由于我们的资源都会收到SpringSecurity的保护，所以想要跨域访问还要让SpringSecurity运行跨域访问。

```java
@Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //关闭csrf
                .csrf().disable()
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/user/login").anonymous()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated();

        //添加过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        //配置异常处理器
        http.exceptionHandling()
                //配置认证失败处理器
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        //允许跨域
        http.cors();
    }

```



# 五、使用验证码

- **导入插件**

  - ```xml
        <!-- google kaptcha依赖 -->
        <dependency>
          <groupId>com.github.axet</groupId>
          <artifactId>kaptcha</artifactId>
          <version>0.0.9</version>
        </dependency>
    ```

- **添加配置类**

  - ```java
    package com.cjq.server.config;
    
    import com.google.code.kaptcha.impl.DefaultKaptcha;
    import com.google.code.kaptcha.util.Config;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    
    import java.util.Properties;
    
    /**
     * 验证码配置类
     */
    @Configuration
    public class CaptchaConfig {
    
      @Bean
      public DefaultKaptcha defaultKaptcha() {
        // 验证码生成器
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        //配置
        Properties properties = new Properties();
        // 是否有边框
        properties.setProperty("kaptcha.border", "yes");
        //设置边框颜色
        properties.setProperty("kaptcha.border.color", "105,179,90");
        // 边框粗细度，默认为1
    //    properties.setProperty("kaptcha.border.thickness", "1");
        // 验证码
        properties.setProperty("kaptcha.session.key", "code");
        // 验证码文本字符颜色 默认为黑色
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        // 设置字体样式
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");
        //字体大小，默认40
        properties.setProperty("kaptcha.textproducer.font.size", "30");
        //验证码文本支付内容范围  默认为abced2345678gfynmnpwx
    //    properties.setProperty("kaptcha.textproducer.char.string", "");
    
        // 字符长度， 默认为5
        properties.setProperty("kaptcha.textproducer.char.length","4");
        // 字符间距 默认为2
        properties.setProperty("kaptcha.textproducer.char.space", "4");
        //验证码图片高度  默认为40
        properties.setProperty("kaptcha.image.height", "40");
        Config config  = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
      }
    }
    
    ```

- **使用**（此处把生成的验证码保存在session中，实际开发可以放在redis中）

  - ```java
    package com.cjq.server.controller;
    
    import com.google.code.kaptcha.impl.DefaultKaptcha;
    import io.swagger.annotations.ApiOperation;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RestController;
    
    import javax.imageio.ImageIO;
    import javax.servlet.ServletOutputStream;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import java.awt.image.BufferedImage;
    import java.io.IOException;
    
    /**
     * 验证码
     */
    @RestController
    public class CaptchaController {
    
      @Autowired
      private DefaultKaptcha defaultKaptcha;
    
        @ApiOperation(value = "验证码")
        @GetMapping(value = "/captcha", produces = "image/jpeg")
        public void captcha(HttpServletRequest request, HttpServletResponse response) {
          //定义response输出类型为image/jpeg类型
          response.setDateHeader("Expires", 0);
          response.setHeader("Cache-Control", "no-store,no-cache, must-revalidate");
          response.addHeader("Cache-Control", "post-check=0, pre-check=0");
          response.setHeader("Pragma", "no-cache");
    
          response.setContentType("image/jpeg");
    
          String text = defaultKaptcha.createText();
          System.out.println("验证码内容：" + text); //将验证码放入session中
          request.getSession().setAttribute("captcha", text); //根据文本内容创建图形验证码
          BufferedImage image = defaultKaptcha.createImage(text);
          ServletOutputStream outputStream = null;
          try {
            outputStream = response.getOutputStream(); //输出流输出图片，格式jpg
            ImageIO.write(image, "jpg", outputStream);
            outputStream.flush();
          } catch (IOException e) {
            e.printStackTrace();
          } finally {
            if (null != outputStream) {
              try {
                outputStream.close();
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        }
    }
    
    ```

    ```java
     @Override
        public RespBean login(String username, String password, String code, HttpServletRequest request) {
            String captcha = (String) request.getSession().getAttribute("captcha");
            if(StringUtils.isEmpty(code) || !captcha.equalsIgnoreCase(code)) {
                return RespBean.error("验证码输入错误，请重新输入！");
            }
    
            // 登录
            UserDetails userDetails= userDetailsService.loadUserByUsername(username);
            if(null == userDetails || !passwordEncoder.matches(password,userDetails.getPassword())) {
                return RespBean.error("用户名或密码不正确");
            }
            if(!userDetails.isEnabled()) {
                return RespBean.error("账号被禁用，请联系管理员！");
            }
    
            // 更新security登录用户对象
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,
                    null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    
            // 生成token
            String token = jwtTokenUtil.generateToken(userDetails);
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("tokenHead", tokenHead);
            return RespBean.success("登录成功", tokenMap);
        }
    ```

    

# 六、Swagger2

## 1、导入依赖

- ```xml
    <!--swagger2 依赖-->
      <dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.7.0</version>
      </dependency>
      <!--Swagger第三方ui依赖-->
      <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>swagger-bootstrap-ui</artifactId>
        <version>1.9.6</version>
      </dependency>
  ```

## 2、添加配置类

- ```java
  package com.cjq.server.config;
  
  
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import springfox.documentation.builders.ApiInfoBuilder;
  import springfox.documentation.builders.PathSelectors;
  import springfox.documentation.builders.RequestHandlerSelectors;
  import springfox.documentation.service.*;
  import springfox.documentation.spi.DocumentationType;
  import springfox.documentation.spi.service.contexts.SecurityContext;
  import springfox.documentation.spring.web.plugins.Docket;
  import springfox.documentation.swagger2.annotations.EnableSwagger2;
  
  import java.util.ArrayList;
  import java.util.List;
  
  /**
   * swagger2 配置类
   */
  
  @Configuration
  @EnableSwagger2
  public class Swagger2Config {
  
    /**
     * 创建 API 应用
     * apiInfo() 增加API相关信息
     *
     * @return
     */
    @Bean
    public Docket createRestApi() {
      return new Docket(DocumentationType.SWAGGER_2)
              // 通过调用自定义方法apiInfo，获得文档的主要信息
              .apiInfo(apiInfo())
              // 通过select() 函数返回一个ApiSelectorBuilder实例，用来控制哪些接口暴露给swagger来展示
              .select()
              // 扫描指定包
              .apis(RequestHandlerSelectors.basePackage("com.cjq.server.controller"))
              .paths(PathSelectors.any())
              .build()
              .securityContexts(securityContexts())
              .securitySchemes(securitySchemes());
    }
  
    /**
     * 创建该API 的基本信息(这些基本信息会展现在文档页面中)
     * 访问地址： http://项目实际地址/doc.html
     * @return
     */
    private ApiInfo apiInfo() {
      return new ApiInfoBuilder()
              // 接口文档标题
              .title("云E办接口文档")
              // 对于接口文档的相关描述
              .description("云E办接口文档")
              // 接口文档内容的补充
              .contact(new Contact("xxx", "http:localhost:8081/doc.html", "xxxx@xxxx.com"))
              .version("1.0")
              .build();
    }
  
    private List<ApiKey> securitySchemes() {
      // 设置请求头信息
      List<ApiKey> result = new ArrayList<>();
      ApiKey apiKey = new ApiKey("Authorization", "Authorization", "Header");
      result.add(apiKey);
  
      return result;
    }
  
    private List<SecurityContext> securityContexts() {
      // 设置需要登录认证的路径
      List<SecurityContext> result = new ArrayList<>();
      result.add(getContextByPath("/hello/.*"));
      return result;
    }
  
    private SecurityContext getContextByPath(String pathRegex) {
      return SecurityContext.builder()
              .securityReferences(defaultAuth())
              .forPaths(PathSelectors.regex(pathRegex))
              .build();
    }
  
    private List<SecurityReference> defaultAuth() {
      List<SecurityReference> result = new ArrayList<>();
      AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
      AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
      authorizationScopes[0] = authorizationScope;
      result.add(new SecurityReference("Authorization", authorizationScopes));
      return result;
  
    }
  
  }
  
  ```

## 3、使用

- **@Api** (@Api 用在类上，说明该类的作用。可以标记一个 Controller 类作为 Swagger 文档资源)

  ```java
  @Api(tags={"用户接口"})
  @RestController
  public class UserController {
  }
  ```

- **@ApiParam** (@ApiParam 用于 Controller 中方法的参数说明)

  ```java
  @PostMapping("/user")
  public UserDto addUser(@ApiParam(value = "新增用户参数", required = true) @RequestBody AddUserParam param) {
      System.err.println(param.getName());
      return new UserDto();
  }
  ```

- **@ApiOperation** (@ApiOperation 用在 Controller 里的方法上，说明方法的作用，每一个接口的定义)

  ```java
  @ApiOperation(value="新增用户", notes="详细描述")
  public UserDto addUser(@ApiParam(value = "新增用户参数", required = true) @RequestBody AddUserParam param) {
  
  }
  ```

- **@ApiModel** (@ApiModel 用在实体类上，表示对类进行说明，用于实体类中的参数接收说明)

  ```java
  @ApiModel(value = "com.znzz.user", description = "新增用户参数")
  public class AddUserParam {
  }
  ```

- @ApiModelProperty

  ```java
  @Data
  @ApiModel(value = "com.znzz.user", description = "新增用户参数")
  public class AddUserParam {
      @ApiModelProperty(value = "ID")
      private String id;
      @ApiModelProperty(value = "名称")
      private String name;
      @ApiModelProperty(value = "年龄")
      private int age;
  }
  ```

  

# 七、POI 的使用

## 1、导入依赖

```xml
 <!--03版本-->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi</artifactId>
            <version>3.14</version>
        </dependency>
        <!--07版本-->
        <dependency>
            <groupId>org.apache.poi</groupId>
            <artifactId>poi-ooxml</artifactId>
            <version>3.14</version>
        </dependency>

        <!-- 日期格式化工具 -->
        <dependency>
            <groupId>joda-time</groupId>
            <artifactId>joda-time</artifactId>
            <version>2.10.1</version>
        </dependency>
```



## 2、写入Excel操作

### 03版本

```java
package com.cjq.poitest.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelWrite03 {
    public static void main(String[] args) throws IOException {
        
        String PATH = "F:\\poiTest\\";
        
        //1、创建一个工作簿 03
        Workbook workbook = new HSSFWorkbook();
        //2、创建一个工作表
        Sheet sheet = workbook.createSheet("观众统计表");
        //3、创建一个行（1,1）
        Row row1 = sheet.createRow(0);
        //4、创建一个单元格
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("今日新增观众");
        // (1,2)
        Cell cell12 = row1.createCell(1);
        cell12.setCellValue("666");
        
        //第二行 （2,1）
        Row row2 = sheet.createRow(1);
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue("统计时间");
        //(2,2)
        Cell cell22 = row2.createCell(1);
        String time = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        cell22.setCellValue(time);
        
        //生成一张表（IO） 03 版本就是使用xls结尾
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "aaaa03.xls");
        //输出
        workbook.write(fileOutputStream);
        //关闭流
        fileOutputStream.close();
        
        System.out.println("03版本表已经生成");
    }
}

```

### 07版本

```java
package com.cjq.poitest.test;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import java.io.FileOutputStream;
import java.io.IOException;

public class ExcelWrite07 {
    public static void main(String[] args) throws IOException {
        
        String PATH = "F:\\poiTest\\";
        
        //1、创建一个工作簿 03
        Workbook workbook = new XSSFWorkbook();
        //2、创建一个工作表
        Sheet sheet = workbook.createSheet("观众统计表");
        //3、创建一个行（1,1）
        Row row1 = sheet.createRow(0);
        //4、创建一个单元格
        Cell cell11 = row1.createCell(0);
        cell11.setCellValue("今日新增观众");
        // (1,2)
        Cell cell12 = row1.createCell(1);
        cell12.setCellValue("666");
        
        //第二行 （2,1）
        Row row2 = sheet.createRow(1);
        Cell cell21 = row2.createCell(0);
        cell21.setCellValue("统计时间");
        //(2,2)
        Cell cell22 = row2.createCell(1);
        String time = new DateTime().toString("yyyy-MM-dd HH:mm:ss");
        cell22.setCellValue(time);
        
        //生成一张表（IO） 03 版本就是使用xls结尾
        FileOutputStream fileOutputStream = new FileOutputStream(PATH + "aaaa07.xlsx");
        //输出
        workbook.write(fileOutputStream);
        //关闭流
        fileOutputStream.close();
        
        System.out.println("07版本表已经生成");
    }
}

```

## 3、读取Excel操作

### 03版本

```java
 public static void main(String[] args) throws IOException {
        
        String PATH = "F:\\poiTest\\";
        
        //获取文件流
        FileInputStream inputStream = new FileInputStream(PATH + "aaaa03.xls");
        //1、创建一个工作簿 03
        Workbook workbook = new HSSFWorkbook(inputStream);
        //2、得到表
        Sheet sheet = workbook.getSheetAt(0);
        //3、得到行
        Row row = sheet.getRow(0);
        //4、得到列
        Cell cell = row.getCell(1);
        
        //读取值的时候，一定要注意类型！
        //getStringCellValue   字符串
        //System.out.println(cell.getStringCellValue());
        System.out.println(cell.getNumericCellValue());
        inputStream.close();
     
    }
```

### 07版本

```java
 public static void main(String[] args) throws IOException {
        
        String PATH = "F:\\poiTest\\";
        
        //获取文件流
        FileInputStream inputStream = new FileInputStream(PATH + "aaaa07.xlsx");
        //1、创建一个工作簿 03
        Workbook workbook = new XSSFWorkbook(inputStream);
        //2、得到表
        Sheet sheet = workbook.getSheetAt(0);
        //3、得到行
        Row row = sheet.getRow(0);
        //4、得到列
        Cell cell = row.getCell(1);
        
        //读取值的时候，一定要注意类型！
        //getStringCellValue   字符串
        //System.out.println(cell.getStringCellValue());
        System.out.println(cell.getNumericCellValue());
        inputStream.close();
     
    }
```

## 4、读取不同类型的数据

```java
   public static void main(String[] args) throws IOException {
    
        String PATH = "F:\\poiTest\\";
        
        //获取文件流
        FileInputStream inputStream = new FileInputStream(PATH + "aaaa03.xls");
        
        //创建一个工作簿。使用excel能操作的这边都可以操作
        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        //获取标题内容
        Row rowTitle = sheet.getRow(0);
        if(rowTitle != null) {
            int cellCount = rowTitle.getPhysicalNumberOfCells();
            for (int cellNum = 0; cellNum < cellCount; cellNum++) {
                Cell cell = rowTitle.getCell(cellNum);
                if(cell != null) {
                    int cellType = cell.getCellType();
                    String cellValue = cell.getStringCellValue();
                    System.out.print(cellValue + " | ");
                }
            }
            System.out.println();
        }
        
        // 获取表中的内容
        int rowCount = sheet.getPhysicalNumberOfRows();
        for (int rowNum = 1; rowNum < rowCount; rowNum++) {
            Row rowData = sheet.getRow(rowNum);
            if(rowData != null) {
                //读取列
                int cellCount = rowTitle.getPhysicalNumberOfCells();
                for (int cellNum = 0; cellNum < cellCount; cellNum++) {
                    System.out.print("["+(rowNum+1) + "-" + (cellNum+1) + "]");
                    
                    Cell cell = rowData.getCell(cellNum);
                    // 匹配列的数据类型
                    if(cell != null) {
                        int cellType = cell.getCellType();
                        String cellValue = "";
                        
                        switch (cellType) {
                            case HSSFCell.CELL_TYPE_STRING: // 字符串
                                System.out.print("【String】");
                                cellValue = cell.getStringCellValue();
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN: // 布尔
                                System.out.print("【BOOLEAN】");
                                cellValue = String.valueOf(cell.getBooleanCellValue());
                                break;
                            case HSSFCell.CELL_TYPE_BLANK: //  空
                                System.out.print("【BLANK】");
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC: // 数字（日期、普通数字）
                                System.out.print("【NUMERIC】");
                                if(HSSFDateUtil.isCellDateFormatted(cell)) {
                                    System.out.print("【日期】");
                                    Date date = cell.getDateCellValue();
                                    cellValue = new DateTime(date).toString("yyyy-MM-dd");
                                } else {
                                    // 不是日期格式，防止数字过长！
                                    System.out.print("【转换为字符串输出】");
                                    cell.setCellType(HSSFCell.CELL_TYPE_STRING);
                                    cellValue = cell.toString();
                                }
                                break;
                            case HSSFCell.CELL_TYPE_ERROR: //  空
                                System.out.print("【数字类型错误】");
                                break;
                        }
                        System.out.println(cellValue);
                    }
                }
            }
        }
        inputStream.close();
    }
```



## 5、计算公式

```java
 public static void main(String[] args) throws IOException {
        
        String PATH = "F:\\poiTest\\";
        
        FileInputStream inputStream = new FileInputStream(PATH + "formula.xls");
        Workbook workbook = new HSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);
        
        Row row = sheet.getRow(4);
        Cell cell = row.getCell(0);
        
        //拿到计算公式 eval
        FormulaEvaluator formulaEvaluator = new HSSFFormulaEvaluator((HSSFWorkbook) workbook);
        
        //输出单元格的内容
        int cellType = cell.getCellType();
        switch (cellType) {
            case Cell.CELL_TYPE_FORMULA:
                String formula = cell.getCellFormula();
                System.out.println(formula);
                
                //计算
                CellValue evaluate = formulaEvaluator.evaluate(cell);
                String cellValue = evaluate.formatAsString();
                System.out.println(cellValue);
                break;
        }
     
    }
```





## 6、EasyExcel的使用

**创建实体类：**

```java
@Data
public class DemoData {
    
    @ExcelProperty("字符串标题")
    private String string;
    @ExcelProperty("日期标题")
    private Date date;
    @ExcelProperty("数字标题")
    private Double doubleData;
    /**
     * 忽略这个字段
     */
    @ExcelIgnore
    private String ignore;
    
}

```



### 1、写入操作

```java
 public static void main(String[] args) {
        String PATH = "F:\\poiTest\\";
        
        //写法一
        String fileName = PATH + "EasyTest.xlsx";
        //这里 需要指定用哪个class去写，然后写到第一个sheet，名字为模板，然后文件流会自动关闭
        //write (filenName,格式类）
        //sheet（表名）
        // doWrite(数据)
        EasyExcel.write(fileName, DemoData.class).sheet("模板").doWrite(data());
    }
    
    
    private static List<DemoData> data() {
        List<DemoData> list = new ArrayList<DemoData>();
        for(int i = 0; i<10; i++) {
            DemoData data = new DemoData();
            data.setString("字符串" + i);
            data.setDate(new Date());
            data.setDoubleData(0.56);
            list.add(data);
        }
        return list;
    }
    
```

### 2、读取操作

#### 2.1、创建DemoDAO类

```java

public class DemoDAO {
    public void save(List<DemoData> list) {
        //持久化操作
        // 如果是mybatis，尽量别直接调用多次insert，自己写一个mapper里面新增一个方法batchInsert，所有数据一次性插入
    }
}
```



#### 2.2、创建DemoDataListener类

```java
// 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new，然后里面用到spring可以构造方法传进来
public class DemoDataListener extends AnalysisEventListener<DemoData> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DemoDataListener.class);
    
    private static final int BATCH_COUNT = 5;
    List<DemoData> list = new ArrayList<>();
    
    private DemoDAO demoDAO;
    public DemoDataListener() {
        demoDAO = new DemoDAO();
    }
    
    public DemoDataListener(DemoDAO demoDAO) {
        this.demoDAO = demoDAO;
    }
    
    //读取数据会执行 invoke 方法
    // DemoData 类型
    // AnalysisContext 分析上问
    @Override
    public void invoke(DemoData demoData, AnalysisContext context) {
        System.out.println(JSON.toJSONString(demoData));
        list.add(demoData);
        //达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条在内存，容易OOM
        if(list.size() >= BATCH_COUNT) {
            saveData();
            // 存储完成清理 list
            list.clear();
        }
    }
    
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        saveData();
        LOGGER.info("所有数据解析完成!");
    }
    
    /**
     * 加上存储数据库
     */
    private void saveData() {
        LOGGER.info("{}条数据，开始存储数据库！",list.size());
        demoDAO.save(list);
        LOGGER.info("存储数据库成功");
    }
}

```

#### 2.3、测试

```java
public class ReadTest {
    public static void main(String[] args) {
        String PATH = "F:\\poiTest\\";
        
        String filaName = PATH + "EasyTest.xlsx";
    
        EasyExcel.read(filaName, DemoData.class, new DemoDataListener()).sheet().doRead();
    }
}

```

