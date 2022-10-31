### Spring使用JdbcTemplate操作数据库

#### 简介✔

> JdbcTemplate是Spring框架提供的

JdbcTemplate相比于传统的jdbc是进一步的封装，下面我们来看看它用法

#### 1，引入依赖

```java
  <!--SpringJdbc-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </dependency>
        <!--数据库连接-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
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
```

#### 2，编写实体类

```java
public class User {
    //用户id
    private Long id;

    //用户密码
    private String username;

    //用户密码
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

```

#### 3,编写服务类

```java
public interface UserService {

    /**
     * 添加用户
     */
    void save(User user);

    /**
     * 修改用户
     * @param user
     */
    void update(User user);


    /**
     * 获取所有用户
     * @return
     */
    List<User> getAllUsers();

    /**
     * 删除用户
     * @param id
     */
    void delete(Integer id);
}
```

#### 4,编写服务类实现类

```java
@Service
public class UserServiceImpl implements UserService {

    //注入JdbcTemplate类操作数据库
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void save(User user) {
         jdbcTemplate.update("insert into user (username,password) values (?,?);", user.getUsername(), user.getPassword());
    }

    //获取自增主键
//    public int save(User user) {
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        int update = jdbcTemplate.update(new PreparedStatementCreator() {
//            @Override
//            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
//                PreparedStatement ps = connection.prepareStatement("insert into user (username,password) values (?,?);", Statement.RETURN_GENERATED_KEYS);
//                ps.setString(1, user.getUsername());
//                ps.setString(2, user.passowrd());
//                return ps;
//            }
//        }, keyHolder);
//        user.setId(keyHolder.getKey().longValue());
//        System.out.println(user);
//        return update;
//    }

    @Override
    public void update(User user) {
        jdbcTemplate.update("update user set username=?,password=? where id=?", user.getUsername(), user.getPassword(),user.getId());
    }

    //手动映射
//    @Override
//    public List<User> getAllUsers() {
//        return jdbcTemplate.query("select * from user", new RowMapper<User>() {
//            @Override
//            public User mapRow(ResultSet resultSet, int i) throws SQLException {
//                String username = resultSet.getString("username");
//                String address = resultSet.getString("password");
//                long id = resultSet.getLong("id");
//                User user = new User();
//                user.setPassword(user.getPassword());
//                user.setUsername(username);
//                user.setId(id);
//                return user;
//            }
//        });
//    }

    //交给BeanPropertyRowMapper去映射
    public List<User> getAllUsers() {
        return jdbcTemplate.query("select * from user", new BeanPropertyRowMapper<>(User.class));
    }


    //删除
    @Override
    public void delete(Integer id) {
         jdbcTemplate.update("delete from user where id=?", id);
    }
}
```

#### 5,编写yml文件

```yml
server:
  port: 8080

spring:
  #DataSource配置
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/springboot_demo?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: qwer123+456
```

#### 测试类⭕

```java
@SpringBootTest
class SpringbootJdbctemplateDemoApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
        for (User user :userService.getAllUsers())
        {
            System.out.println(user);
        }

    }

}
```

#### 获得结果✅

user类数据库

```ini
User{id=1, username='qrx', password='e10adc3949ba59abbe56e057f20f883e'}
```

#### 使用JdbcTemplate好处❗：

- 不需要编写xml文件
- 轻便，简洁
- 不依赖class文件，因为他存在Spring框架❗❗❗
