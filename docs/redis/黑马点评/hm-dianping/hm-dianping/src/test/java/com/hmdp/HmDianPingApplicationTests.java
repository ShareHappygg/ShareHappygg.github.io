package com.hmdp;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Shop;
import com.hmdp.entity.User;
import com.hmdp.service.impl.ShopServiceImpl;
import com.hmdp.service.impl.UserServiceImpl;
import com.hmdp.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;

@SpringBootTest
class HmDianPingApplicationTests {

    @Autowired
    private ShopServiceImpl shopService;

    @Autowired
    private RedisIdWorker redisIdWorker;

    @Autowired
    private  UserServiceImpl userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Test
    public  void  test()
    {
        shopService.saveRedisShop(1L,20L, TimeUnit.MINUTES);
    }

    private  static  final ExecutorService EXECUTOR_SERVICE =  Executors.newFixedThreadPool(500);


    @Test
    public  void testId() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(300);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    long id = redisIdWorker.nextId("order");
                    System.out.println("id =" + id);

                }
                countDownLatch.countDown();
            }
        };
        long begin = System.currentTimeMillis();
        for (int i = 0; i < 300; i++) {
            EXECUTOR_SERVICE.submit(runnable);
        }
        countDownLatch.await();
        System.out.println(System.currentTimeMillis()-begin);
    }

    @Test
    public  void testToken()
    {
        long phone =13688669001l;

        for (int i = 0; i <1000 ; i++) {
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            String s = String.valueOf(phone + i);
            queryWrapper.eq("phone",s);
            User user = userService.getBaseMapper().selectOne(queryWrapper);
            if (ObjectUtil.isEmpty(user))
            {
                user= createUserByPhone(s);
            }
            String token = UUID.randomUUID().toString();
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user,userDTO);
            Map<String, Object> hashMap = BeanUtil.beanToMap(userDTO);
            stringRedisTemplate.expire(LOGIN_USER_KEY+token,LOGIN_USER_TTL,TimeUnit.SECONDS);
            stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY+token,hashMap);
            System.out.println(token);
        }


    }

    private User createUserByPhone(String phone) {
        User user = new User();
        user.setNickName("user_"+ RandomUtil.randomString(10));
        user.setPhone(phone);
        userService.save(user);
        return  user;
    }


    @Test
    void loadShopData()
    {
        List<Shop> list = shopService.list();

        Map<Long, List<Shop>> hashMap = list.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        for (Map.Entry<Long,List<Shop>> entry:hashMap.entrySet()) {
            Long typeId = entry.getKey();
            List<Shop> value = entry.getValue();
            List<RedisGeoCommands.GeoLocation<String>> locations = new ArrayList<>();
            for (Shop shop : value)
            {
                locations.add(new RedisGeoCommands.GeoLocation<>(shop.getId().toString(),new Point(shop.getX(),shop.getY())));
            }
            stringRedisTemplate.opsForGeo().add("shop:geo:"+typeId,locations);
        }
    }

}
