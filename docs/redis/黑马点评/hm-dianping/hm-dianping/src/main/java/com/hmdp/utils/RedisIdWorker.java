package com.hmdp.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author 屈燃希
 * @date 2022/10/18 15:35
 * @content
 */
@Component
public class RedisIdWorker {

    private  final  static  long BEGIN_TIMESTAMP = 1640995200L;

    private  final  static int COUNT_BITS =32;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public long  nextId(String keyPrefix)
    {
        LocalDateTime now = LocalDateTime.now();
        long nowSecond = now.toEpochSecond(ZoneOffset.UTC);
        long timestamp = nowSecond-BEGIN_TIMESTAMP;

        String date = now.format(DateTimeFormatter.ofPattern("yyyy:MM:dd"));
        long count = stringRedisTemplate.opsForValue().increment("icr" + keyPrefix + ":" + date);

       return timestamp<<COUNT_BITS|count;

    }
}
