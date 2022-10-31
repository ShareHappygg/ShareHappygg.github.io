### SpringBoot动态添加定时任务

> 前面我们使用注解实现定时任务，但是我们发现项目启动，它就自动运行，无法停止，而且`@Schedule 注解有一个缺点，其定时的时间不能动态的改变，而基于 SchedulingConfigurer 接口的方式可以做到。`
>
> 本节我们来实现这些功能

#### `动态定时任务`

> 根据你orm框架来决定是否执行sql,本文使用hibernate所以无需编写sql

建立数据库

```sql
CREATE TABLE `tb_cron`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '动态定时任务时间表',
  `cron_expression` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '定时任务表达式',
  `cron_describe` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

INSERT INTO `tb_cron` VALUES (1, '0 0/1 * * * ?', '每分钟执行一次');

```

`导入依赖`

```xml
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
	</dependencies>
```

`编写实体类`

```java
@Entity
@Table(name = "tb_cron",schema = "springboot_demo")
@Data
public class Cron {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name ="id")
    private  Integer id;

    @Column(name = "cron_expression")
    private String cronExpression;

    @Column(name = "cron_describe")
    private String cronDescribe;

}

```

编写mapper类

```java
public interface CronMapper extends CrudRepository<Cron,Integer> {


}

```

`ScheduledTaskRegistrar`源码，我们发现该对象初始化完成后会执行`scheduleTasks()`方法，在该方法中添加任务调度信息，最终所有的任务信息都存放在名为`scheduledFutures`的集合中，等待执行。

> 所以我们只需动态修改集合，实现任务调度的添加，取消，重置

```
@Component
public class CompleteScheduleConfig implements SchedulingConfigurer {

    @Autowired
    private CronMapper cronMapper;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.addTriggerTask(()-> System.out.println("执行动态定时任务1: " + LocalDateTime.now().toLocalTime()+",此任务执行周期由数据库中的cron表达式决定"),
                triggerContext -> {
                    //2.1 从数据库获取执行周期
                    String cron = cronMapper.findById(1).get().getCronExpression();
                    //2.2 合法性校验.
                    if (cron!=null) {
                        // Omitted Code ..
                    }
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
            );
    }
```

原文链接：https://juejin.cn/post/7013234573823705102
