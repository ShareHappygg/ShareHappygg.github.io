# 启动redis

<img src="assets/image-20220425211705826.png" alt="image-20220425211705826"  />

<img src="assets/image-20220425211716849.png" alt="image-20220425211716849" style="zoom:150%;" />

<img src="assets/image-20220425211722560.png" alt="image-20220425211722560" style="zoom:150%;" />

**输入密码登录：**

```java
auth 000214
```



# redis基本知识

（1）使用select进行切换数据库

<img src="assets/image-20220425212031897.png" alt="image-20220425212031897"  />

（2）使用 key * 查看数据库所有的key

<img src="assets/image-20220425213541608.png" alt="image-20220425213541608"  />

（3）清除当前数据库  **flushdb**

（4）清除全部数据库的内容  **FLUSHALL**



>**Rides 是单线程的！**





# 五大数据类型

## 	Redis-key

<img src="assets/image-20220425220043454.png" alt="image-20220425220043454" style="zoom:150%;" />

<img src="assets/image-20220425220104998.png" alt="image-20220425220104998" style="zoom:150%;" />

<img src="assets/image-20220425220142686.png" alt="image-20220425220142686" style="zoom:150%;" />

<img src="assets/image-20220425220328382.png" alt="image-20220425220328382" style="zoom:150%;" />





## String(字符串)

<img src="assets/image-20220425220822078.png" alt="image-20220425220822078" style="zoom:150%;" />

<img src="assets/image-20220425221411662.png" alt="image-20220425221411662" style="zoom:150%;" />

<img src="assets/image-20220425221438118.png" alt="image-20220425221438118" style="zoom:150%;" />

<img src="assets/image-20220425222718831.png" alt="image-20220425222718831" style="zoom:150%;" />

<img src="assets/image-20220425223456949.png" alt="image-20220425223456949" style="zoom:150%;" />

<img src="assets/image-20220425223815134.png" alt="image-20220425223815134" style="zoom:150%;" />

<img src="assets/image-20220425224113374.png" alt="image-20220425224113374" style="zoom:150%;" />

<img src="assets/image-20220425224327062.png" alt="image-20220425224327062" style="zoom:150%;" />







## List

在redis中，我们可以把list玩成栈、队列、阻塞队列

<img src="assets/image-20220425225246533.png" alt="image-20220425225246533" style="zoom:150%;" />

<img src="assets/image-20220425225434607.png" alt="image-20220425225434607" style="zoom:150%;" />

<img src="assets/image-20220425225650862.png" alt="image-20220425225650862" style="zoom:150%;" />

<img src="assets/image-20220425225823374.png" alt="image-20220425225823374" style="zoom:150%;" />

<img src="assets/image-20220426205053425.png" alt="image-20220426205053425" style="zoom:150%;" />

<img src="assets/image-20220426205337770.png" alt="image-20220426205337770" style="zoom:150%;" />

![image-20220426205516755](assets/image-20220426205516755.png)

![image-20220426205726580](assets/image-20220426205726580.png)







## Set(集合)

set中的值是不能重读的！

<img src="assets/image-20220426210008163.png" alt="image-20220426210008163" style="zoom:150%;" />

<img src="assets/image-20220426210210674.png" alt="image-20220426210210674" style="zoom:150%;" />

<img src="assets/image-20220426210403588.png" alt="image-20220426210403588" style="zoom:150%;" />

<img src="assets/image-20220426210551195.png" alt="image-20220426210551195" style="zoom:150%;" />

<img src="assets/image-20220426210613236.png" alt="image-20220426210613236" style="zoom:150%;" />

<img src="assets/image-20220426211140836.png" alt="image-20220426211140836" style="zoom:150%;" />

<img src="assets/image-20220426211154578.png" alt="image-20220426211154578" style="zoom:150%;" />







## Hash(哈希)

Map集合，key-map! 这个值是一个map集合！本质和String类型没有太大的区别，还是一个简单的key-value！

set myhash field kuangshen

<img src="assets/image-20220426211856637.png" alt="image-20220426211856637" style="zoom:150%;" />

<img src="assets/image-20220426212020206.png" alt="image-20220426212020206" style="zoom:150%;" />

<img src="assets/image-20220426212204067.png" alt="image-20220426212204067" style="zoom:150%;" />

<img src="assets/image-20220426212240540.png" alt="image-20220426212240540" style="zoom:150%;" />

<img src="assets/image-20220426212448532.png" alt="image-20220426212448532" style="zoom:150%;" />





## Zset(有序集合)

在set的基础上，增加了一个值， set k1 v1   zset k1 score1 v1 

<img src="assets/image-20220426213043411.png" alt="image-20220426213043411" style="zoom:150%;" />

<img src="assets/image-20220426213234446.png" alt="image-20220426213234446" style="zoom:150%;" />

<img src="assets/image-20220426213621261.png" alt="image-20220426213621261" style="zoom:150%;" />

<img src="assets/image-20220426213451445.png" alt="image-20220426213451445" style="zoom:150%;" />

<img src="assets/image-20220426213835645.png" alt="image-20220426213835645" style="zoom:150%;" />

<img src="assets/image-20220426214035332.png" alt="image-20220426214035332" style="zoom:150%;" />





# 三种特殊数据类型



## Geospatial地理位置

朋友的定位、附近的人，打车距离计算？

Redis的Geo在Redis3.2版本就推出了！这个功能可以推算地理位置的信息，两地之间的距离，方圆几里的人！

>**getadd**

<img src="assets/image-20220426215215279.png" alt="image-20220426215215279" style="zoom:150%;" />

>**geopos**

<img src="assets/image-20220426215347982.png" alt="image-20220426215347982" style="zoom:150%;" />

>**geodist**

<img src="assets/image-20220426215732889.png" alt="image-20220426215732889" style="zoom:150%;" />

>georadius 以给定的经纬度为中心，找出某一半径内的元素

我附近的人？ （获得所有附件的人的地址，定位！）通过半径来查询

获得指定数量的人，200

所有数据应该都录入： china:city ,才会让结果更加真实

<img src="assets/image-20220426220458602.png" alt="image-20220426220458602" style="zoom:150%;" />

<img src="assets/image-20220426220521184.png" alt="image-20220426220521184" style="zoom:150%;" />

>GEORADIUSBYMEMBER

<img src="assets/image-20220426220714881.png" alt="image-20220426220714881" style="zoom:150%;" />

>**GEOHASH 命令-返回一个或多个位置元素的Geohash表示**

该命令将返回11个字符的Geohash字符串！

<img src="assets/image-20220426220859313.png" alt="image-20220426220859313" style="zoom:150%;" />

>GEO 底层的实现原理其实就是Zset！我们可以使用Zset命令来操作geo

<img src="assets/image-20220426221133745.png" alt="image-20220426221133745" style="zoom:150%;" />



## Hyperloglog

>**简介**

Redis Hyperloglog 基数统计的算法！

优点：占用的内存是固定，2^64 不同的元素的计数，只需要废12KB内存！如果要从内存的角度来比较的话 Hyperloglog首选！

**网页的 UV （一个人访问一个网站多次，但是还是算作一个人！）**

传统的方式，set保存用户的id，然后就可以统计set中的元素数量作为标准判断！

这个方式如果保存大量的用户id，就会比较麻烦！我们的目的是为了计数，而不是保存用户id；

<img src="assets/image-20220426222402332.png" alt="image-20220426222402332" style="zoom:150%;" />



## Bitmaps

>**位存储**

统计用户信息，活跃，不活跃！登录、未登录！打卡，365打卡！两个状态的，都可以使用Bitmaps！

Bitmaps 位图，数据结构！ 都是操作二进制位来进行记录，就只有0和1两个状态！

365 天 = 365 bit     1字节 = 8bit  46个字节左右！ 

<img src="assets/image-20220426223159298.png" alt="image-20220426223159298" style="zoom:150%;" />

<img src="assets/image-20220426223426250.png" alt="image-20220426223426250" style="zoom:150%;" />





# 事务

Redis 事务本质： 一组命令的集合！ 一个事务中的所有命令都会被序列化，在事务执行过程中，会按照顺序执行！

一次性、顺序性、排他性！执行一些列的命令！

```
-------队列  set set set 执行 -------
```

Redis 事务没有隔离级的概念！

所有的命令在事务中，并没有直接执行！只要发起执行命令的时候才会执行！Exec

Redis 单条命令式保存原子性的，但是事务不保证原子性！

redis的事务： 

- ​	开启事务（）
- ​	命令入队（）
- ​	执行事务（）

>**正常执行事务！**

<img src="assets/image-20220426230530930.png" alt="image-20220426230530930" style="zoom:150%;" />

>**放弃事务！**

<img src="assets/image-20220426230752309.png" alt="image-20220426230752309" style="zoom:150%;" />

>**编译型异常（代码有问题！命令有错！），事务中所哟的命令都不会被执行！**

<img src="assets/image-20220426231124839.png" alt="image-20220426231124839" style="zoom:150%;" />

>**运行时异常（1/0)，如果事务队列中存在语法性，那么执行命令的时候，其他命令是可以正常执行的，错误命令抛出异常！**

<img src="assets/image-20220426231434452.png" alt="image-20220426231434452" style="zoom:150%;" />

>**监控！ Watch**

**悲观锁：**

- ​	无论做什么都会加锁！

**乐观锁：**

- 更新数据的时候去判断一下，在此期间是否有人修改过这个数据
- 获取version
- 更新的时候比较version

>**Redis 监测测试**

**正常执行成功！**

<img src="assets/image-20220427223253326.png" alt="image-20220427223253326" style="zoom:150%;" />

测试多线程修改值，使用watch 可以当做redis的乐观锁操作！

<img src="assets/image-20220427223442743.png" alt="image-20220427223442743" style="zoom:150%;" />

如果修改失败，获取最新的值就好

<img src="assets/image-20220427223857120.png" alt="image-20220427223857120" style="zoom:150%;" />







# Jedis

**使用Java来操作Redis**

>什么是Jedis 是 Redis官方推荐的 java 连接开发工具！使用Java操作Redis中间件！ 如果你要使用java操作redis，那么一定要对Jedis十分的熟悉!

>**测试**

**1、导入对应的依赖**

<img src="assets/image-20220427225203832.png" alt="image-20220427225203832" style="zoom:150%;" />

**2、编码测试：**

- **连接数据库**
- **操作命令**
- **断开连接**

<img src="assets/image-20220427225314932.png" alt="image-20220427225314932" style="zoom:150%;" />



## 常用的API

String 

List

Set

Hash

Zset

>所有的api命令，就是我们对应的上面学习的指令，一个都没有变化！





# SpringBoot整合

说明： 在SpringBoot2.x之后，原来使用的jedis被替换为了lettuce？

jedis： 采用的直连，多个线程操作的话，是不安全的，如果想要避免不安全的，使用jedis pool连接池！更像 BIO 模式

lettuce： 采用netty，实例可以再多个线程中进行共享，不存在线程不安全的情况！可以减少线程数据了，更像 NIO 模式

>整合测试一下

1、导入依赖

```xml
<dependency>
      <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-data-redis</artifactId>
 </dependency>
```

2、配置连接

```properties
spring.redis.host=127.0.0.1
spring.redis.post=6379
```

3、测试

```java
package com.cjq.redisspringboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisSpringBootApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        // redisTemplate  操作不同的数据类型，api和我们的指令是一样的
        // opsForValue 操作字符串 类似String
        // opsForList 操作List 类似List
        // opsForSet
        // opsForHash
        // opsForZSet
        // opsForGeo
        // opsForHyperLogLog

        //除了基本的操作，我们常用的方法都可以直接通过redisTemplate操作，比如事务，和基本的CRUD

        // 获取redis的连接对象
//        RedisConnection connection = redisTemplate.getConnectionFactory().getConnection();
//        connection.flushDb();
//        connection.flushAll();

        redisTemplate.opsForValue().set("mykey", "cjq");
        System.out.println(redisTemplate.opsForValue().get("mykey"));
    }

}

```

关于对象保存：(所有的对象，都需要序列化)

```java
 @Test
    public  void test() throws JsonProcessingException{
        User user = new User("狂神", 3);
        String jsonUser = new ObjectMapper().writeValueAsString(user);
        redisTemplate.opsForValue().set("user",jsonUser);
        System.out.println(redisTemplate.opsForValue().get("user"));
    }
```

**编写一个自己的RedisTemplate：**

```java
@Configuration
public class RedisConfig {
    //编写我们自己的 redisTemplate
    @Bean
    @SuppressWarnings("all")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        // 我们未来自己开发方便，一般直接使用<String, Object>
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        // Json 序列化配置
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        // String 的序列化
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // key 采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value 序列化方式采用Jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashKeySerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }
}
```





# Redis.conf详解

启动的时候，就通过配置文件来启动！

<img src="assets/image-20220503144100193.png" alt="image-20220503144100193" style="zoom:150%;" />

1、配置文件 unit单位 对大小写不敏感！

<img src="assets/image-20220503144213117.png" alt="image-20220503144213117" style="zoom:150%;" />



>网络

```java
bind 127.0.0.1  # 绑定的ip
protected-mode yes  # 保护模式
port 6379  #端口设置
```

>通用 GENERAL

```java
daemonize yes  # 以守护进程的方式运行，默认是 no
    
pidfile /var/run/redis_6379.pid  #如果以后台的方式运行，我们就需要指令一个pid文件

# 日志
# Specify thi server verbosity level.
# This can be one of:
# debug (a lot of information, useful for development/testing)
# verbose (many rarely useful info, but not a mess like thie debug level)
# notice (moderately verbose, what you want in production probably) 生产环境
# warning (only very important / critical messages are logged)
loglevel notice 
logfile ""  # 日志的文件位置名
databases 16  # 数据库的数量，默认是 16 个数据库
always-show-logo  yes # 是否总是显示 LOGO
```

>快照

持久化，在规定的时间内，执行了多少次操作，则会持久化到文件 .rdb .aof

redis 是内存数据库，如果没有持久化，那么数据断电及失！

<img src="assets/image-20220503150304223.png" alt="image-20220503150304223" style="zoom:150%;" />



>REPLICATION 复制



>SECURITY 安全

可以在这里设置reids的密码，默认是没有密码的！

<img src="assets/image-20220503150533701.png" alt="image-20220503150533701" style="zoom:150%;" />



>限制 CLIENTS

<img src="assets/image-20220503150814318.png" alt="image-20220503150814318" style="zoom:150%;" />



>APPEND ONLY 模式 aof配置

<img src="assets/image-20220503151039214.png" alt="image-20220503151039214" style="zoom:150%;" />





# Redis 持久化

Redis 是内存数据库，如果不将内存中的数据库状态保存到磁盘，那么一旦服务器进程退出，服务器中的数据库状态也会消失。所以Redis提供了持久化功能！

## RDB

<img src="assets/image-20220503204047850.png" alt="image-20220503204047850" style="zoom:150%;" />

<img src="assets/image-20220503204132171.png" alt="image-20220503204132171" style="zoom:150%;" />

<img src="assets/image-20220503204326141.png" alt="image-20220503204326141" style="zoom:150%;" />

<img src="assets/image-20220503204447480.png" alt="image-20220503204447480" style="zoom:150%;" />

<img src="assets/image-20220503204511401.png" alt="image-20220503204511401" style="zoom:150%;" />

<img src="assets/image-20220503204653436.png" alt="image-20220503204653436" style="zoom:150%;" />



## AOF

<img src="assets/image-20220503205142882.png" alt="image-20220503205142882" style="zoom:150%;" />

<img src="assets/image-20220503210540307.png" alt="image-20220503210540307" style="zoom:150%;" />

<img src="assets/image-20220503210611092.png" alt="image-20220503210611092" style="zoom:150%;" />

<img src="assets/image-20220503210631223.png" alt="image-20220503210631223" style="zoom:150%;" />

<img src="assets/image-20220503210649232.png" alt="image-20220503210649232" style="zoom:150%;" />

<img src="assets/image-20220503210841017.png" alt="image-20220503210841017" style="zoom:150%;" />

<img src="assets/image-20220503210748941.png" alt="image-20220503210748941" style="zoom:150%;" />

<img src="assets/image-20220503210950801.png" alt="image-20220503210950801" style="zoom:150%;" />

<img src="assets/image-20220503211003622.png" alt="image-20220503211003622" style="zoom:150%;" />



# Redis发布订阅

Redis 发布订阅（pub/sub）是一种消息通信模式： 发送者（pub）发送消息，订阅者接受消息。微博、微信、关注系统！

Redis 客户端可以订阅任意数量的频道。

订阅/发布消息图：

第一个： 消息发送者， 第二个：频道   第三个：消息订阅者

<img src="assets/image-20220504101748949.png" alt="image-20220504101748949" style="zoom:150%;" />

<img src="assets/image-20220504102045707.png" alt="image-20220504102045707" style="zoom:150%;" />

<img src="assets/image-20220504102658494.png" alt="image-20220504102658494" style="zoom:150%;" />

**使用场景：**

1、实时消息系统！

2、实时聊天！（频道当做聊天室，将信息回显给所有人即可！）

3、订阅，关注系统都是可以的！

稍微复杂的场景我们就会使用 消息中间件MQ（）





# Redis主从复杂

<img src="assets/image-20220504112514218.png" alt="image-20220504112514218" style="zoom:150%;" />

<img src="assets/image-20220504112539097.png" alt="image-20220504112539097" style="zoom:150%;" />

<img src="assets/image-20220504112619265.png" alt="image-20220504112619265" style="zoom:150%;" />

主从复制，读写分离！80%的情况下都是在进行读操作！减缓服务器的压力！架构中经常使用！一主二从！ 

## 环境配置

只配置从库，不用配置主库！

<img src="assets/image-20220504114555380.png" alt="image-20220504114555380" style="zoom:150%;" />

复制3个配置文件，然后修改对应的信息

1、端口

2、pid名字

3、log文件名字

4、dump.rdb名字

修改完毕之后，启动我们的3个redis服务器，可以通过进程信息查看！

<img src="assets/image-20220504114715852.png" alt="image-20220504114715852" style="zoom:150%;" />

## 一主二从

**默认情况下，每台redis服务器都是主节点;**我们一般情况下只用配置从机就好了！

<img src="assets/image-20220504115136716.png" alt="image-20220504115136716" style="zoom:150%;" />

<img src="assets/image-20220504115216765.png" alt="image-20220504115216765" style="zoom:150%;" />

<img src="assets/image-20220504115248402.png" alt="image-20220504115248402" style="zoom:150%;" />

真实的从主配置应该在配置文件中配置，这样的话是永久的，我们这里使用的是命令，暂时的！

>细节

主机可以写，从机不能写只能读！主机中的所有信息和数据，都会自动被从机保存！

主机写：

<img src="assets/image-20220504115640294.png" alt="image-20220504115640294" style="zoom:150%;" />

从机只能读取内容：

<img src="assets/image-20220504115701241.png" alt="image-20220504115701241" style="zoom:150%;" />

测试：主机断开连接，从机依旧连接到主机的，但是没有操作，这个时候，主机如果回来了，从机依旧可以直接获取到主机写的信息！

如果是使用命令行，来配置的主从，这个时候如果重启了，就会变回主机！只要变为从机，立马就会从主机中获取值！

<img src="assets/image-20220504120250418.png" alt="image-20220504120250418" style="zoom:150%;" />

![image-20220504153439850](assets/image-20220504153439850.png)

如果主机断开了连接，我们可以使用 SLAVEOF  no  one  让自己变成主机！其他的节点就可以手动连接到最新的这个主节点（手动）！如果这个时候老大修复了，那就重新连接！



## 哨兵模式

（自动选举老大的模式）

主从切换技术的方法是：当主服务器宕机后，需要手动把一台从服务器切换为主服务器，这就需要人工干预，费时费力，还会造成一段时间内服务不可用。这不是一种推荐的方式，更多时候，我们优先考虑哨兵模式。Redis从2.8开始正式提供了Sentinel（哨兵）架构来解决这个问题。

**后台监控主机是否故障，如果故障了根据投票数自动将从库转换为主库。**

哨兵模式是一种特殊的模式，首先Redis提供了哨兵的命令，哨兵是一个独立的进程，作为进程，它会独立运行。其原理是**哨兵通过发送命令，等待Redis服务器响应，从而监控运行的多个Redis实例。**

<img src="assets/image-20220504154430680.png" alt="image-20220504154430680" style="zoom:150%;" />

这里的哨兵有两个作用：

- 通过发送命令，让Redis服务器返回监控其运行状态，包括主服务器和从服务器。
- 当哨兵监测到master宕机，会自动将slave切换成master，然后通过发布订阅模式通知其他的从服务器，修改配置文件，让它们切换主机。

然而一个哨兵进程对Redis服务器进行监测，可能会出现问题，为此，我们可以使用多个哨兵进行监控。各个哨兵之间还会进行监控，这样就形成了多哨兵模式。

<img src="assets/image-20220504155044966.png" alt="image-20220504155044966" style="zoom:150%;" />

>**测试**

我们目前的状态是一主二从！

**1、配置哨兵配置文件 sentinel.conf**

<img src="assets/image-20220504155501264.png" alt="image-20220504155501264" style="zoom:150%;" />

后面的这个数字1，代表主机挂了，slave投票看让谁接替成为主机，票数最多的，就会成为主机!

**2、启动哨兵**

<img src="assets/image-20220504155741936.png" alt="image-20220504155741936" style="zoom:150%;" />

<img src="assets/image-20220504155755452.png" alt="image-20220504155755452" style="zoom:150%;" />

如果Master节点断开了，这个 时候就会从从机中随机选择一个服务器！（这里面有一个投票算法）

<img src="assets/image-20220504155915470.png" alt="image-20220504155915470" style="zoom:150%;" />

哨兵日志！

<img src="assets/image-20220504160027987.png" alt="image-20220504160027987" style="zoom:150%;" />

如果主机此时回来了，只能归并到新的主机下，当作从机，这就是哨兵模式的规则！

>**哨兵模式**

**优点：**

​	1、哨兵集群，基于主从复制模式，所有的主从配置优点，它全有

​	2、主从可以切换，故障可以转移，系统的可用性就会更好

​	3、哨兵模式就是主从模式的升级，手动到自动，更加健壮！

**缺点：**

​	1、Redis不好在线扩容，集群容量一旦到达上限，在线扩容就十分麻烦！

​	2、实现哨兵模式的配置其实是很麻烦的，里面有很多选择！



>**哨兵模式的全部配置！**

<img src="assets/image-20220504160812822.png" alt="image-20220504160812822" style="zoom:150%;" />

<img src="assets/image-20220504161159602.png" alt="image-20220504161159602" style="zoom:150%;" />

<img src="assets/image-20220504161231300.png" alt="image-20220504161231300" style="zoom:150%;" />

<img src="assets/image-20220504161251568.png" alt="image-20220504161251568" style="zoom:150%;" />





# Redis缓存穿透和雪崩

## 缓存穿透(查不到)

>**概念**

缓存穿透的概念很简单，用户想要查询一个数据，发现redis内存数据库没有，也就是缓存没有命中，于是向持久层数据库查询。发现也没有，于是本次查询失败。当用户很多的时候，缓存都没有命中（秒杀！），于是都去请求了持久层数据库。这会给持久层数据库造成很大的压力，这时候就相当于出现了缓存穿透。

>**解决方案**

<img src="assets/image-20220504162231928.png" alt="image-20220504162231928" style="zoom:150%;" />

<img src="assets/image-20220504162256838.png" alt="image-20220504162256838" style="zoom:150%;" />

但是这种方法会存在两个问题：

1、如果空值能够被缓存起来，这就意味着缓存需要更多是的空间存储更多的键，因为这当中可能会有很多的空值的键；

2、即使对空值设置了过期时间，还是会存在缓存层和存储层的数据会有一段时间窗口的不一致，这对于需要保持一致性的业务会有影响。



## 缓存击穿（量太大，缓存过期）

<img src="assets/image-20220504162812321.png" alt="image-20220504162812321" style="zoom:150%;" />

<img src="assets/image-20220504162949348.png" alt="image-20220504162949348" style="zoom:150%;" />

## 缓存雪崩

<img src="assets/image-20220504163121203.png" alt="image-20220504163121203" style="zoom:150%;" />

<img src="assets/image-20220504163227377.png" alt="image-20220504163227377" style="zoom:150%;" />

<img src="assets/image-20220504163240026.png" alt="image-20220504163240026" style="zoom:150%;" />
