package com.hmdp.utils;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.events.Event;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author 屈燃希
 * @date 2022/10/17 21:14
 * @content
 */
@Slf4j
@Component
public class CacheClient {

    private final StringRedisTemplate stringRedisTemplate;

    private  static ExecutorService CACHE_EXECUTOR = Executors.newFixedThreadPool(5);

    private  String uuid = UUID.randomUUID(true).toString()+"-";

    private  String KEY_PREFIX ="lock:";

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    private DefaultRedisScript<Long> redisScript;

    public void set(String key , Object value, Long time, TimeUnit timeUnit){

        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value),time,timeUnit);
    }

    public void  setWithLogicalExpire(String key,Object value, Long time,TimeUnit timeUnit){
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(time)));
        stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(redisData));
    }
    //缓存穿透
    public  <R,T> R queryWithPassThrough(String keyPrefix, T id, Class<R> type, Function<T,R> dbFallback,Long time, TimeUnit timeUnit)
    {
        String key =keyPrefix +id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isNotBlank(json))
        {
            return  JSONUtil.toBean(json,type);
        }

        if (json != null)
        {
            return  null;
        }
        R r = dbFallback.apply(id);
        if ( r== null)
        {
            stringRedisTemplate.opsForValue().set(key,"",RedisConstants.CACHE_NULL_TTL,TimeUnit.MINUTES);

            return  null;
        }

        this.set(key,r,time,timeUnit);
        return  r;

    }


    public  <R,T> R  queryLogicalExpire(String keyPrefix, T id, Class<R> type, Function<T,R> dbFallback,Long time,TimeUnit timeUnit)
    {
        String key =keyPrefix +id;
        String lockKey = RedisConstants.LOCK_SHOP_KEY+id;

        String json = stringRedisTemplate.opsForValue().get(key);
        if (StrUtil.isBlank(json))
        {
           return  null;
        }
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);

        R data = JSONUtil.toBean((JSONObject) redisData.getData(), type);

        if (redisData.getExpireTime().isAfter(LocalDateTime.now()))
        {
            return data;
        }

        if (tryLock(lockKey,RedisConstants.LOCK_SHOP_TTL))
        {
            CACHE_EXECUTOR.submit(()->{
                R dataById = dbFallback.apply(id);
                redisData.setData(dataById);
                redisData.setExpireTime(LocalDateTime.now().plusSeconds(time));
            });
            unLock(lockKey);
        }

        return  data;

    }

    public boolean tryLock(String key,Long timeOutSec)
    {
        long threadId = Thread.currentThread().getId() ;
        String value=uuid+threadId;
        String lockKey = KEY_PREFIX+key;
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, value, timeOutSec, TimeUnit.MINUTES);
        return BooleanUtil.isTrue(aBoolean);
    }

    public  void unLock(String key)
    {
         stringRedisTemplate.execute(redisScript, Collections.singletonList(KEY_PREFIX+key),uuid+Thread.currentThread().getId());
    }
}
