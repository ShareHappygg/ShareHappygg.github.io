### SpringBoot的定时任务

`定时任务场景⬇⬇⬇`

- 彩票定时开奖
- 一次性任务，抽一次奖，1分钟开奖

#### 注解实现定时任务

**第一步： 主启动类上加上 @EnableScheduling 注解**

```java

@EnableScheduling
@SpringBootApplication
public class SpringBootScheduled {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootScheduled.class);
    }
}
```

写一个类，注入到Spring，关键就是 `@Scheduled` 注解。 （) 里就是 cron 表达式，用来说明这个方法的执行周期的。 

```java
/**
 * 定时任务 静态定时任务
 *
 * 第一位，表示秒，取值0-59
 * 第二位，表示分，取值0-59
 * 第三位，表示小时，取值0-23
 * 第四位，日期天/日，取值1-31
 * 第五位，日期月份，取值1-12
 * 第六位，星期，取值1-7，1表示星期天，2表示星期一
 * 第七位，年份，可以留空，取值1970-2099
 * @author crush
 * @since 1.0.0
 * @Date: 2021-07-27 21:13
 */
@Component
public class SchedulingTaskBasic {

    /**
     * 每五秒执行一次
     */
    @Scheduled(cron = "*/5 * * * * ?")
    private void printNowDate() {
        long nowDateTime = System.currentTimeMillis();
        System.out.println("固定定时任务执行:--->"+nowDateTime+"，此任务为每五秒执行一次");
    }
}
```

##### `@Scheduled注解`

```java
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Schedules.class)
public @interface Scheduled {

    String cron() default "";
    String zone() default "";
    long fixedDelay() default -1L;
    String fixedDelayString() default "";
    long fixedRate() default -1L;
    String fixedRateString() default "";
    long initialDelay() default -1L;
    String initialDelayString() default "";
}
```

**@Scheduled参数部分，总共包含8各部分，我们来分别看一下其作用：**

- cron：一个类似cron的表达式，扩展了通常的UN * X（`时间戳`）定义，包括秒，分，时，星期，月，年的触发器。
- fixedDelay：**在最后一次调用结束和下一次调用开始之间以固定周期（以毫秒为单位）执行带注释的方法。**（要等待上次任务完成后）
- fixedDelayString：同上面作用一样，只是String类型
- fixedRate：在调用之间以固定的周期（以毫秒为单位）执行带注释的方法。（不需要等待上次任务完成）
- fixedRateString：同上面作用一样，只是String类型
- initialDelay：第一次执行fixedRate()或fixedDelay()任务之前延迟的毫秒数 。
- initialDelayString：同上面作用一样，只是String类型
- zone：指明解析cron表达式的时区。

**cron参数**❗

一个cron表达式可以有6个元素或者7个元素组成（“年”这个元素可以省略，省略之后就是默认“每一年”）

**1：按顺序依次为：**

1. 秒（0~59）
2. 分钟（0~59）
3. 小时（0~23）
4. 天（0~31）
5. 月（0~11）
6. 星期（1~7 ）或者（ SUN，MON，TUE，WED，THU，FRI，SAT。其中SUN = 1）
7. 年份（1970－2099）

**2：每个元素可以接受的值：**

| 字段 | 允许值            | 允许的特殊字符  |
| :--- | :---------------- | :-------------- |
| 秒   | 0-59              | , - * /         |
| 分   | 0-59              | , - * /         |
| 小时 | 0-23              | , - * /         |
| 日期 | 1-31              | , - * ? / L W C |
| 月份 | 1-12 或者 JAN-DEC | , - * /         |
| 星期 | 1-7 或者 SUN-SAT  | , - * ? / L C # |
| 年   | 空, 1970-2099     | , - * /         |

3：一些特殊字符解释与注意事项，可以结合下面的小案例来理解：

- 其中每个元素可以是一个值(如6),一个连续区间(9-12),一个间隔时间(8-18/4)(/表示每隔4小时),一个列表(1,3,5),通配符。
- 其中的“日”由于"月份中的日期"和"星期"这两个元素互斥的，必须要对其中一个设置“？”。
- 有些子表达式能包含一些范围或列表 
  - 例如：子表达式（天（星期））可以为 “MON-FRI”，“MON，WED，FRI”，“MON-WED,SAT”
- “*”字符代表所有可能的值
- “/”字符用来指定数值的增量 
  - 例如：在子表达式（分钟）里的“0/15”表示从第0分钟开始，每15分钟
  - 在子表达式（分钟）里的“3/20”表示从第3分钟开始，每20分钟（它和“3，23，43”）的含义一样
- “？”字符仅被用于天（月）和天（星期）两个子表达式，表示不指定值 
  - 当2个子表达式其中之一被指定了值以后，为了避免冲突，需要将另一个子表达式的值设为“？”
- “L” 字符仅被用于天（月）和天（星期）两个子表达式，它是单词“last”的缩写 
  - 如果在“L”前有具体的内容，它就具有其他的含义了。例如：“6L”表示这个月的倒数第６天
  - 注意：在使用“L”参数时，不要指定列表或范围，因为这会导致问题
- “W” 字符代表着平日(Mon-Fri)，并且仅能用于日域中。它用来指定离指定日的最近的一个平日。大部分的商业处理都是基于工作周的，所以 W 字符可能是非常重要的。 
  - 例如，日域中的 15W 意味着 “离该月15号的最近一个平日。” 假如15号是星期六，那么 trigger 会在14号(星期五)触发，因为星期四比星期一离15号更近。
- “C”：代表“Calendar”的意思。它的意思是计划所关联的日期，如果日期没有被关联，则相当于日历中所有日期。例如5C在日期字段中就相当于日历5日以后的第一天。1C在星期字段中相当于星期日后的第一天。

**4：一些小案例❗❗：**

- “0 0 10,14,16 * * ?”  每天上午10点，下午2点，4点
- “0 0/30 9-17 * * ?”   朝九晚五工作时间内每半小时
- “0 0 12 ? * WED”     表示每个星期三中午12点
- “0 0 12 * * ?”           每天中午12点触发
- “0 15 10 ? * *”         每天上午10:15触发（这个和下一个案例说明，必须"月份中的日期"和"星期"中有一个设置为“？”）
- “0 15 10 * * ?”         每天上午10:15触发
- “0 15 10 * * ? *”       每天上午10:15触发（7个元素类型案例，第七个元素代表年）
- “0 15 10 * * ? 2005”     2005年的每天上午10:15触发
- “0 * 14 * * ?”           在每天下午2点到下午2:59期间的每1分钟触发
- “0 0/5 14 * * ?”       在每天下午2点到下午2:55期间的每5分钟触发
- “0 0/5 14,18 * * ?”  在每天下午2点到2:55期间和下午6点到6:55期间的每5分钟触发
- “0 0-5 14 * * ?”       在每天下午2点到下午2:05期间的每1分钟触发
- “0 10,44 14 ? 3 WED”      每年三月的星期三的下午2:10和2:44触发
- “0 15 10 ? * MON-FRI”    周一至周五的上午10:15触发
- “0 15 10 15 * ?”       每月15日上午10:15触发
- “0 15 10 L * ?”         每月最后一日的上午10:15触发
- “0 15 10 ? * 6L”       每月的最后一个星期五上午10:15触发
- “0 15 10 ? * 6L 2002-2005”    2002年至2005年的每月的最后一个星期五上午10:15触发
- “0 15 10 ? * 6#3”     每月的第三个星期五上午10:15触发

到这个地方你应该对@Scheduled有一个较全面的理解了，下面我们就来简单的看一下其实现原理吧~

#### [原理简介]()

**1：主要过程：**

1.  spring在使用applicationContext将类全部初始化。 
2.  调用`ScheduledAnnotationBeanPostProcessor类中的postProcessAfterInitialization方法获取项目中所有被注解 @Scheduled注解的方法` 。 
3.  通过`processScheduled方法将所有定时的方法存放在Set tasks = new LinkedHashSet(4); 定时任务队列中，并解析相应的参数。顺序存放，任务也是顺序执行。`存放顺序为cron>fixedDelay>fixedRate 
4.  将解析参数后的`定时任务存放在一个初始容量为16 的map`中，key为bean name，value为定时任务：private final Map<Object, Set> scheduledTasks = new IdentityHashMap(16); 
5.  之后交给`ScheduledTaskRegistrar类的方法scheduleTasks去添加定时任务`。

做定时任务还可以使用java自带的原生API，Timer和TimerTask去设计。

-  Timer：一种工具，线程用其安排以后在后台线程中执行的任务。可安排任务执行一次，或者定期重复执行。 
-  TimerTask：定义一个被执行的任务，Timer 安排该任务为一次执行或重复执行的任务。