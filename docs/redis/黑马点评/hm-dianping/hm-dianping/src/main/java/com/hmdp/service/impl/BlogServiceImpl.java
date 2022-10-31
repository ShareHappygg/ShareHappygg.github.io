package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.dto.ScrollResult;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.Blog;
import com.hmdp.entity.Follow;
import com.hmdp.entity.User;
import com.hmdp.mapper.BlogMapper;
import com.hmdp.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IFollowService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.UserHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Autowired
    private  UserServiceImpl userService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private IFollowService followService;

    @Override
    public Result getBlogById(Long id) {
        Blog blog = getById(id);
        if (ObjectUtil.isEmpty(blog))
        {
            return  Result.fail("笔记不存在");
        }
        Long userId = blog.getUserId();
        //发表博文用户
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
        //当前用户
        UserDTO currentUser = UserHolder.getUser();
        if (currentUser !=null)
        {
            String key = RedisConstants.BLOG_LIKED_KEY+id;
            Double score = stringRedisTemplate.opsForZSet().score(key, currentUser.getId().toString());
            System.out.println(score);
            blog.setIsLike(score!=null);
        }

        return Result.ok(blog);
    }

    @Override
    public Result likeBlog(Long id) {
        Long userId = UserHolder.getUser().getId();
        String key = RedisConstants.BLOG_LIKED_KEY+id;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if (ObjectUtil.isEmpty(score))
        {
            System.out.println("点赞数+1");
            boolean isSuccess = this.update().setSql("liked=liked+1").eq("id", id).update();
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key,userId.toString(), System.currentTimeMillis());
            }

        }else {
            boolean isSuccess = this.update().setSql("liked=liked-1").eq("id", id).update();
            System.out.println("点赞数-1");
            stringRedisTemplate.opsForZSet().remove(key,userId.toString());
        }
        return Result.ok("点赞成功");

    }

    @Override
    public Result queryBlogLikes(Long id) {
        String key = RedisConstants.BLOG_LIKED_KEY+id;
        //查看点赞榜前5
        Set<String> range = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if (ObjectUtil.isEmpty(range))
        {
            return Result.ok();
        }
        List<Long> ids = range.stream().map(Long::valueOf).collect(Collectors.toList());
        String idsStr = StrUtil.join(",",ids);
        List<User> users = userService.query().in("id",ids).last("ORDER BY Field (id,"+idsStr+")").list();
        List<UserDTO> collect = users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtil.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());
        return  Result.ok(collect);
    }

    @Override
    public Result saveBlog(Blog blog) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        boolean success = save(blog);

        long timeMillis = System.currentTimeMillis();
        if (success)
        {
            List<Follow> followList = followService.getBaseMapper().selectList(new QueryWrapper<Follow>().eq("follow_user_id", blog.getUserId()));
            for (Follow follow :followList)
            {
                String key = RedisConstants.FEED_KEY+follow.getUserId();
                stringRedisTemplate.opsForZSet().add(key,blog.getId().toString(),timeMillis);
            }
        }
        // 返回id
        return Result.ok(blog.getId());
    }

    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {

        Long userId =  UserHolder.getUser().getId();

        String key = RedisConstants.FEED_KEY +userId;

        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate
                .opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        List<Long> blogIds =  new ArrayList<>();
        //最小时间
        long minTime = 0;
        //偏移量
        int os = 1;

        for (ZSetOperations.TypedTuple<String> tuple:typedTuples)
        {
            String blogId= tuple.getValue();
            blogIds.add(Long.valueOf(blogId));
            long time = tuple.getScore().longValue();
            if (time == minTime)
            {
                os++;

            }else {
                os =1;
            }
            minTime=time;
        }
        String idStr = StrUtil.join(",",blogIds);
        List<Blog> blogs = query().in("id",blogIds).last("Order by field (id,"+idStr+")").list();
        for (Blog blog :blogs)
        {
            //当前用户
            UserDTO currentUser = UserHolder.getUser();
            if (currentUser !=null)
            {
                String blogKey = RedisConstants.BLOG_LIKED_KEY+blog.getId();
                Double score = stringRedisTemplate.opsForZSet().score(blogKey, currentUser.getId().toString());
                System.out.println(score);
                blog.setIsLike(score!=null);
            }
        }
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setList(blogs);
        scrollResult.setMinTime(minTime);
        scrollResult.setOffset(os);
        return Result.ok(scrollResult);

    }
}
