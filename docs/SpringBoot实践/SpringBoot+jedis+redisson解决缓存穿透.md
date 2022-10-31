### SpringBoot+jedis+redisson解决缓存穿透

#### **缓存穿透**

**缓存穿透**：指的是访问一个的**不存在的数据**，发送请求的访问的不存在数据，所以每次的请求的都要访问的数据库，但是数 

据库也无此记录，我们没有将这次查询的 null 写入缓存，这**将导致这个不存在的数据每次 请求都要到存储层去查询**，失去了缓存的意义

**在流量大时**，可能 DB 就挂掉了，要是有人利用不存在的 key 频繁攻击我们的应用，这就是漏洞

**解决方法：**

* 缓存空对象
  * 优点：实现简单，维护方便
  * 缺点：
    * 额外的内存消耗
    * 可能造成短期的不一致
* 布隆过滤
  * 优点：内存占用较少，没有多余key
  * 缺点：
    * 实现复杂
    * 存在误判可能
    * 增强id的复杂度，避免容易猜测id规则
    * 做好数据的基础格式校验
    * 加强用户权限校验
    * 做热点参数的限流

#### 布隆过滤器介绍

#### **布隆过滤器是什么**

redis的布隆过滤器其实有点像我们之前学习过的**hyperloglog** [深入理解redis——新类型bitmap/hyperloglgo/GEO](https://segmentfault.com/a/1190000041401769) ，它也是不保存元素的一个集合，它也**不保存元素的具体内容**，但是**能判定这个元素是否在这个集合中存在**（hyperloglog是判定集合中存在的不重复元素的个数）。

1）**它是由一个初值都为零的bit数组和多个哈希函数构成，用来快速判断某个数据是否存在。**

2）**本质就是判断具体数据存不存在一个大的集合中。**

3）布隆过滤器是一种类似set的数据结构，只是统计**结果不太准确**

#### **布隆过滤器的特点**

1）一个元素如果在布隆过滤器里判定结果为**不存在，则一定不存在**
2）一个元素在布隆过滤器里判定结**果存在，则不一定存在**（原理会在下面解释）
3）布隆过滤器**可以添加元素，但是不能删除元素**，删除元素会导致**误判率增加。**
4）误判只会发生在过滤器**没有添加过的元素**，对于已**经添加过的元素**不会发生误判。

#### **布隆过滤器原理**

布隆过滤器使用了**多个Hash函数和一个初始值都为0的bit大型数组构成**。

add：
比如我们现在有一个对象obj1，**它先用多个hash函数得到多个不同的值**，**再拿数组长度进行对这多个值取模得到多个位置**，**将这几个位置置为1**，就完成了add操作。

query:
查询的时候，**只要多个哈希函数算出来的下标其中有一位是0就代表这个key不存在，如果都是1，可能是存在，则可能遇上了哈希冲突**（这就是为什么，布隆过滤器，无是一定无，有可能有）。

![image.png](https://segmentfault.com/img/bVcXSJb)

为什么布隆过滤器不能删除：
如果布隆过滤器删除了一个元素，就是将某个对象的多个下标置为了0，**就大概率会影响到别的元素**，**因为很可能多个元素共享了某一个下标**，所以删除元素会导致误判率增加。

**5.布隆过滤器优缺点**

优点：**高效地插入和查询，占用空间少**

缺点：**不能删除元素，存在误判。**

#### 技术要点

[jedis入门使用](https://blog.csdn.net/qq_27026603/article/details/81865604)

[jedis使用set方法](https://www.cnblogs.com/Springmoon-venn/p/10141915.html)

`有基础可以了解原理`

[布隆原理](https://zhuanlan.zhihu.com/p/140545941)

### 核心代码实践

##### `配置RedisConfig`

```java
package com.example.redisboomfilter.config;

import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.rsocket.context.RSocketServerBootstrap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ResourceBundle;

@Configuration
public class RedisConfig {

    private  static  final  String HOST;

    private  static  final  int PORT;

    private  static  final  int MAX_TOTAL;

    private  static  final  int MAX_IDEL;

    private  static  final  int MAX_WAITMILLIS;

    private  static  final String PASS_WORD;

    static {
        ResourceBundle rb = ResourceBundle.getBundle("jedis");
        HOST = rb.getString("host");
        PORT = Integer.parseInt(rb.getString("port"));;
        MAX_TOTAL =Integer.parseInt(rb.getString("maxtotal"));;
        MAX_IDEL = Integer.parseInt(rb.getString("maxidel"));
        MAX_WAITMILLIS = Integer.parseInt(rb.getString("maxwaitmillis"));
        PASS_WORD = rb.getString("password");
    }

    /**
     * 配置redis连接池
     * @return
     */
    @Bean
    public JedisPool jedisPool()
    {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(MAX_TOTAL);
        jedisPoolConfig.setMaxIdle(MAX_IDEL);
        jedisPoolConfig.setMaxWaitMillis(MAX_WAITMILLIS);
        jedisPoolConfig.setMaxTotal(MAX_WAITMILLIS);
        return new JedisPool(jedisPoolConfig,HOST,PORT,2000,PASS_WORD);
    }


    /**
     * 配置redis连接客户端
     * @return
     */
    @Bean
    public RedissonClient redissonClient(){
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+HOST+":"+PORT);
        config.useSingleServer().setPassword(PASS_WORD);
        return Redisson.create(config);

    }

    /**
     * 配置布隆过滤器
     * @return
     */
    @Bean
    public RBloomFilter<String> bloomFilter(){
        RBloomFilter<String> bloomFilter = redissonClient().getBloomFilter("bloom-filter");
        //var1布隆过滤器里预计要插入多少数据,var2误判率
        bloomFilter.tryInit(1000,0.03);
        return bloomFilter;
    }

}

```

##### `SpringBoot加载时将数据库放入布隆过滤器`

```java
@Component
@Slf4j
public class StartComponent implements InitializingBean {


    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisConfig redisConfig;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void afterPropertiesSet() throws Exception {

        RBloomFilter<String> stringRBloomFilter = redisConfig.bloomFilter();
        List<Product> productList = productMapper.selectList(null);
        for (Product product :productList){
            stringRBloomFilter.add(product.getPid());
        }
        log.info("完成布隆过滤器保存产品信息"+productList.size()+"条数据");
        Jedis jedis = jedisPool.getResource();
        for (Product product:productList)
        {
            String jsonStr = JSONUtil.toJsonStr(product);
            jedis.hset("product", product.getPid(), jsonStr);
        }
        log.info("完成redis布隆过滤器保存产品信息"+productList.size()+"条数据");
        Map<String, String> product = jedis.hgetAll("product");
        product.forEach((pid,json)-> System.out.println(pid+"----"+json));


    }
}
```

##### 服务层代码

```java
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper,Product> implements ProductService {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisConfig redisConfig;

    @Override
    public Product getProduct(String pid) {
        RBloomFilter<String> filter = redisConfig.bloomFilter();
        if (!filter.contains(pid))
        {
            return null;
        }
        Jedis jedis =jedisPool.getResource();
        String jsonStr = jedis.hget("product",pid);
        String jsonEmptyStr = jedis.hget("product",pid);
        if (!Strings.isBlank(jsonStr)||!Strings.isBlank(jsonEmptyStr))
        {
            Product product = JSONUtil.toBean(jsonStr, Product.class);
            log.info("查询了缓存");
            return  product;
        }
        Product product = baseMapper.selectOne(new QueryWrapper<Product>().eq("pid", pid));
        if (ObjectUtil.isEmpty(product))
        {
            jedis.set(pid+"_product","null","NX","EX",60);
        }else {
            String toJsonStr = JSONUtil.toJsonStr(product);
            jedis.hset("product",product.getPid(),toJsonStr);

        }
        log.info("查询了数据库");
        return  product;


    }
}
```

##### 控制层

```java
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @GetMapping("/get")
    public Result get(@RequestParam String pid)
    {
        Product product = productService.getProduct(pid);
        if (ObjectUtil.isEmpty(product))
        {
            return Result.fail("查询产品不存在");
        }
        return Result.success(product);
    }

}
```

##### postman测试

查询不存在产品

![image-20221026223320166](C:\Users\coder\AppData\Roaming\Typora\typora-user-images\image-20221026223320166.png)

查询存在产品

![image-20221026223342965](C:\Users\coder\AppData\Roaming\Typora\typora-user-images\image-20221026223342965.png)