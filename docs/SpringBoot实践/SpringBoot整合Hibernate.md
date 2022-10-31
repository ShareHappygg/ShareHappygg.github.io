### Spring Boot整合Hibernate

Hibernate特性之一是将实体类映射数据库表结构

> 即是不需要编写sql语句生成表，只需要编写实体类交给Hibernate类生成就行

下面使用一个学生类生成数据库表

#### 1.引入Hibernate相关依赖

```java
	<!--JDBC相关依赖-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>
		<!--MySQL相关依赖-->
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
		</dependency>
		<!--阿里巴巴druid连接池-->
		<dependency>
			<groupId>com.alibaba</groupId>
			<artifactId>druid</artifactId>
			<version>1.0.29</version>
		</dependency>
		<!--一个处理xml的框架-->
		<dependency>
			<groupId>dom4j</groupId>
			<artifactId>dom4j</artifactId>
			<version>1.6.1</version>
		</dependency>
		<!--jpa相关依赖-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<!--lombok依赖-->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
		</dependency>
```

#### 2.编写Hibernate的配置文件

编写hibernate.cfg.xml文件❗❗`文件名必须是hibernate.cfg.xml`

```java
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <property name="connection.url">jdbc:mysql://localhost:3306/springboot_demo?useUnicode=true&amp;characterEncoding=utf-8&amp;serverTimezone=Asia/Shanghai</property>

        <property name="connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <!-- 数据库类型-->
        <property name="dialect">org.hibernate.dialect.MySQL8Dialect</property>
        <!--实体类生成数据库表操作-->
        <property name="hibernate.hbm2ddl.auto"> update</property>
        <!-- 启用将所有生成的SQL语句-->
        <property name="show_sql">true</property>
        <!--  格式化生成的SQL语句，使其更具可读性，-->
        <property name="format_sql">true</property>
        <!--操作数据库的实体类-->
        <mapping class="com.example.springboot_hibernate_demo.entity.Student"/>
    </session-factory>

</hibernate-configuration>

```

##### 配置文件参数✅

| **属性名**                              | **用途**                                                     |
| :-------------------------------------- | :----------------------------------------------------------- |
| Hibernate.dialect                       | 针对特定的关系数据库生成优化的 SQL                           |
| Hibernate.format_sql                    | **格式化输出 SQL 语句（true,false）**                        |
| Hibernate.default_schema                | 将给定的 schema/tablespace 附加到表名上（schema name）       |
| Hibernate.hbm2ddl.auto                  | 自动构建数据库结构，通过映射生成 DDL 语句。 create-drop: 运行时，先创建，运行完，再删除 create: 每次运行前都会删除已存在的再创建。 测试时，可以使用 create |
| Hibernate.cache.use_query_cache         | 允许查询缓存，个别查询仍然需要被设置为可缓存的               |
| Hibernate.cache_user_second_level_cache | 禁止使用二级缓存，对于在`映射中定义中指定的类，会默认开启二级缓存` |
| Hibernate.cahce_query_cache_factory     | 自定义实现 querycache 接口的类名，默认为内建的 standardQueryCache |

hibernate.hbm2ddl.auto 参数⭕

- ddl-auto:create----每次运行该程序，没有表格会新建表格，表内有数据会清空
- ddl-auto:create-drop----每次程序结束的时候会清空表
- ddl-auto:update----每次运行程序，没有表格会新建表格，表内有数据不会清空，只会更新
- ddl-auto:validate----运行程序会校验数据与数据库的字段类型是否相同，不同会报错

#### 3.编写实体`Student`类

```java
@Entity
@Table(name="student",schema = "springboot_demo")
public class Student {

    @Id//声明是ID
    @Column(name = "id")//绑定id列
    @GeneratedValue(strategy = GenerationType.AUTO)//生成自增的ID
    private  Long id;

    //学生姓名
    @Column(name = "name")
    private  String name;

    //邮箱地址
    @Column(name =  "email")
    private  String email;
}

```

@GeneratedValue注解确定主键生成器类型.**GenerationType** 是一个枚举类型，有如下几个选择：

- **AUTO**：**Hibernate** 区分数据库系统，自动选择最佳策略；
- **IDENTITY：** 适合具有自动增长类型的数据库，如 **MySql**……
- **SEQUENCE：** 适合如 **Oracle** 类型数据库；
- **TABLE：** 使用 **Hibernate** 提供的 **TableGenerator** 生成器，不常用。

**`常用主键生成器一览：`**

1. **org.hibernate.id.IncrementGenerator（increment）**：对 **long**、**short** 或 **int** 的数据列生成自动增长主键；
2. **org.hibernate.id.IdentityGenerator**（**identity**）： 适用于 **SQL server，MySql** 等支持自动增长列的数据库，适合 **long**、**short** 或 **int** 数据列类型；
3. **org.hibernate.id.SequenceGenerator**（**sequecne**）：适用 **oracle，DB2** 等支持 **Sequence** 的数据库，适合 **long、short** 或 **int** 数据列类型；
4. **org.hibernate.id.UUIDGenerator**（**uuid**）：对字符串列的数据采用 128 - 位 **uuid** 算法生成唯一的字符串主键；
5. **org.hibernate.id.Assigned（assigned）**：由应用程序指定，也是默认生成策略。

**还有以下注解👇**：

- @Table：描述实体类对应的表名；
- @Idclass：指定充当主键的类；
- **@Entity：** 标注类是实体类；
- **@Id：** 描述哪个属性对应表中的主键字段；
- @Column：指定与属性对应的字段名；
- @Basic：`等价于没有定义注解的属性，对于其它没有标注任何注解的属性，**Hibernate** 默认为数据库的表中有与属性同名的字段`；
- @Transient ：属性不被持久化。

- 如同 @**Entity**、@**Table** 是类级别注解，对类做整体说明；
- 如同 @**Id**、@**Column**、@**Transient** 这几个注解是属性、方法级别的，可对属性或方法做说明

#### 4,编写Mapper类

```java
public interface  StudentMapper  extends CrudRepository<Student,Long> {

    Student save(Student student);

    @Override
    List<Student> findAll();
    
    Student findAllById(Long id);

    @Override
    Optional<Student> findById(Long aLong);
}
```

CrudRepository<T,R>需要声明一个泛型是实体类类型，一个是主键类型

> 除了继承外CrudRepository,还有一个方法是和继承Repository接口是一样的,那就是用@RepositoryDefinition注解效果都是一样的,

使用CrudRepository就使用Spring Data jPA 底层已经编写的sql语句，我们直接调方法执行即可。

#### 5，编写服务类

```java
public interface StudentService {

    public void insertStudent(Student student);

    public void deleteStudent(Long id);

    public void updateStudent(Student student);

    public List<Student> getStudent();

    public Student getStudentBysId(Long id);


}
```

#### 6，编写实现服务类

```java
@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentMapper studentMapper;

    @Override
    public void insertStudent(Student student) {
        studentMapper.save(student);
    }

    @Override
    public void deleteStudent(Long id) {
        studentMapper.deleteById(id);
    }

    @Override
    public void updateStudent(Student student) {
        studentMapper.save(student);
    }

    @Override
    public List<Student> getStudent() {
        return studentMapper.findAll();
    }

    @Override
    public Student getStudentBysId(Long id) {
        return studentMapper.findAllById(id);
    }
}

```

#### 说明😜

以上使用的注解的并不在**Hibernate** 相关联的包中，而是在都在 **javax.persistence 包**中，标准的 **Java** 包，这个包全称 **Java Persistence API（持久化API）。**

**为什么在这个包下❓**

 **J2EE5.0 后推出了 JPA 注解规范：**

- **JPA** 是 **Java** 持久化解决方案，与 **Hibernate** 一样负责把数据保存进数据库；
- 但 **JPA** 只是一种标准、规范，而不是框架；
- **JPA** 自身并没有具体的实现，类似于Jdbc规范；
- 旨在是规范 **ORM** 框架，使 **ORM** 框架有统一的接口、统一的用法。

注意：

**Hiberante** 要求持久化对象实现序列化的好处

- **缓存数据：** 如先把一个查询出来的对象数据以序列化的方式存储到内存或磁盘中，需要时再读出来，再持久化到数据库中；
- **网络数据传输：** 需要把持久化数据从一个系统传到另一个系统时，可能两个系统是基于两个平台，在异构化的系统中通过二进制进行数据传递，可打破这种壁垒。

`现在直接启动项目，我们可以发现数据库多了一个学生表`			