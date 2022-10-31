package com.hmdp.service.impl;

import ch.qos.logback.core.util.TimeUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RadixUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.dto.LoginFormDTO;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.User;
import com.hmdp.mapper.UserMapper;
import com.hmdp.service.IUserService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RegexUtils;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor;

import javax.servlet.http.HttpSession;
import java.sql.Time;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static cn.hutool.core.date.DateUtil.now;
import static com.hmdp.utils.RedisConstants.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service

public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private StringRedisTemplate  redisTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        //校验手机号
        if (RegexUtils.isPhoneInvalid(phone))
        {
            return  Result.fail("手机号格式错误");
        }

        String code = RandomUtil.randomNumbers(6);
        //使用redis
        redisTemplate.opsForValue().setIfAbsent(LOGIN_CODE_KEY+phone,code,LOGIN_CODE_TTL, TimeUnit.MINUTES);
        redisTemplate.opsForValue().setIfAbsent(LOGIN_PHONE_KEY+phone,phone,LOGIN_PHONE_TTL, TimeUnit.MINUTES);
        //发送验证码
        log.debug("发送验证码："+code);
        //session共享
//        session.setAttribute("code",code);
//        session.setAttribute("phone",phone);
        return  Result.ok("发送成功");

    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
//        String phone = (String) session.getAttribute("phone");
        String phone = (String) redisTemplate.opsForValue().get(LOGIN_PHONE_KEY+loginForm.getPhone());
        if (!loginForm.getPhone().equals(phone))
        {
            return Result.fail("手机号错误");
        }
//        String code = (String) session.getAttribute("code");
        String code = (String) redisTemplate.opsForValue().get(LOGIN_CODE_KEY+phone);
        if (!loginForm.getCode().equals(code))
        {
            return Result.fail("验证码错误");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone",loginForm.getPhone());
        User user = baseMapper.selectOne(queryWrapper);
        if (ObjectUtil.isEmpty(user))
        {
           user= createUserByPhone(phone);
        }
        String token = UUID.randomUUID().toString();
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user,userDTO);
        Map<String, Object> hashMap = BeanUtil.beanToMap(userDTO);
        redisTemplate.expire(LOGIN_USER_KEY+token,LOGIN_USER_TTL,TimeUnit.SECONDS);
        redisTemplate.opsForHash().putAll(LOGIN_USER_KEY+token,hashMap);
        System.out.println(token);
        return Result.ok(token);
    }

    @Override
    public Result sign() {
        Long userId = UserHolder.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        String suffix = now.format(DateTimeFormatter.ofPattern(":yyyy:MM"));
        String key = USER_SIGN_KEY+userId+suffix;
        redisTemplate.opsForValue().setBit(key,now.getDayOfMonth()-1,true);
        return Result.ok("签到成功");
    }

    @Override
    public Result signCount() {
        Long userId = UserHolder.getUser().getId();
        LocalDateTime now = LocalDateTime.now();
        String suffix = now.format(DateTimeFormatter.ofPattern(":yyyy:MM"));
        String key = USER_SIGN_KEY+userId+suffix;
        List<Long> list = redisTemplate.opsForValue().bitField(key, BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.unsigned(now.getDayOfMonth())).valueAt(0));
        if (ObjectUtil.isEmpty(list))
        {
            return  Result.ok(0);
        }

        Long num = list.get(0);
        if (ObjectUtil.isEmpty(num))
        {
            return  Result.ok();
        }

        int signCount = 0;
        while (true)
        {
            if ((num&1)==0)
            {
                break;
            }
            else {
                signCount++;
            }
            num>>=1;
        }
        return Result.ok(signCount);

    }

    private User createUserByPhone(String phone) {
        User user = new User();
        user.setNickName("user_"+RandomUtil.randomString(10));
        user.setPhone(phone);
        save(user);
        return  user;
    }
}
