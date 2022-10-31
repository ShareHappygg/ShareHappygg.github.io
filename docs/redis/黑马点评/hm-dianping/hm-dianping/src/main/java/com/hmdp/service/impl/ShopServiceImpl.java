package com.hmdp.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmdp.dto.Result;
import com.hmdp.entity.Shop;
import com.hmdp.mapper.ShopMapper;
import com.hmdp.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.utils.CacheClient;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisData;
import com.hmdp.utils.SystemConstants;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisCommand;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CacheClient cacheClient;


    private  static ExecutorService CACHE_EXECUTOR = Executors.newFixedThreadPool(5);
    @Override
    public Result getShopById(Long id) {
        //解决缓存穿透
//        Shop shop = cacheClient.queryWithPassThrough(RedisConstants.CACHE_SHOP_KEY,id,Shop.class,this::getById,RedisConstants.CACHE_SHOP_TTL,TimeUnit.MINUTES);
//        //互斥锁
//        Shop shop = getShopMutex(id);
        //逻辑过期
//        Shop shop = getShopLogicalExpire(id);
        //逻辑过期
        Shop shop = cacheClient.queryLogicalExpire(RedisConstants.CACHE_SHOP_KEY,id,Shop.class,this::getById,RedisConstants.CACHE_SHOP_TTL,TimeUnit.MINUTES);

        if (shop == null){
            return  Result.fail("商铺不存在");
        }
        return  Result.ok(shop);
    }

    @Transactional
    @Override
    public void updateShop(Shop shop) {
        updateById(shop);
        stringRedisTemplate.delete(RedisConstants.CACHE_SHOP_KEY + shop.getId());

    }

    @Override
    public Result queryShopByType(Integer typeId, Integer current, Double x, Double y) {
        if (x==null||y==null){
            // 根据类型分页查询
            Page<Shop> page = query()
                    .eq("type_id", typeId)
                    .page(new Page<>(current, SystemConstants.DEFAULT_PAGE_SIZE));
            // 返回数据
            return Result.ok(page.getRecords());
        }

        int from =(current-1)*SystemConstants.DEFAULT_PAGE_SIZE;
        int end = current*SystemConstants.DEFAULT_PAGE_SIZE;


        //搜索商铺
        GeoResults<RedisGeoCommands.GeoLocation<String>> search = stringRedisTemplate.opsForGeo().search(
                RedisConstants.SHOP_GEO_KEY + typeId,
                GeoReference.fromCoordinate(x, y),
                new Distance(5000),
                RedisGeoCommands.GeoSearchCommandArgs.newGeoSearchArgs().includeDistance().limit(end));
        List<GeoResult<RedisGeoCommands.GeoLocation<String>>> list = search.getContent();



        HashMap<String,Distance> distanceHashMap = new HashMap<>();
        List<Long> ids = new ArrayList<>();
        if (ObjectUtil.isEmpty(list)||list.size()<=from)
        {
            return Result.ok(Collections.emptyList());
        }
        list.stream().skip(from).forEach(geoLocationGeoResult -> {
            RedisGeoCommands.GeoLocation<String> content = geoLocationGeoResult.getContent();
            String shopId = content.getName();
            ids.add(Long.valueOf(shopId));
            Distance distance = geoLocationGeoResult.getDistance();
            distanceHashMap.put(shopId,distance);
        });
        String idStr = StrUtil.join(",",ids);
        List<Shop> shopList = query().in("id",ids).last("order by field (id,"+idStr+")").list();
        for (Shop shop :shopList)
        {
            shop.setDistance(distanceHashMap.get(shop.getId().toString()).getValue());
        }
        return Result.ok(shopList);
    }

    //使用互斥锁解决缓存击穿
    public Shop getShopMutex(Long id)
    {
        String key =RedisConstants.CACHE_SHOP_KEY + id;
        String lockKey = RedisConstants.LOCK_SHOP_KEY+id;
        String shopJson= stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isNotBlank(shopJson))
        {
            Shop shop = JSONUtil.toBean(shopJson,Shop.class);
            return  shop;
        }

        if (shopJson !=null)
        {
            return null;
        }

        try {
            if (!tryLock(lockKey))
            {
                Thread.sleep(50);
                return  getShopMutex(id);

            }
            Shop shopById = getById(id);
            Thread.sleep(200);
            if (ObjectUtil.isEmpty(shopById))
            {
                stringRedisTemplate.opsForValue().set(key,"",RedisConstants.CACHE_NULL_TTL,TimeUnit.MINUTES);
                return  null;
            }
            stringRedisTemplate.opsForValue().set(key,JSONUtil.toJsonStr(shopById),RedisConstants.CACHE_SHOP_TTL, TimeUnit.MINUTES);
            return shopById;
        } catch (InterruptedException e) {
            throw  new RuntimeException(e);
        }finally {
            unLock(lockKey);
        }
    }

    //使用逻辑过期解决缓存击穿
    public  void saveRedisShop(Long id,Long expireTime,TimeUnit timeUnit)
    {
        Shop shop = getById(id);
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(timeUnit.toSeconds(expireTime)));
        stringRedisTemplate.opsForValue().set(RedisConstants.CACHE_SHOP_KEY+id,JSONUtil.toJsonStr(redisData));
    }


    public  Shop getShopLogicalExpire(Long id)
    {
        String key =RedisConstants.CACHE_SHOP_KEY + id;
        String lockKey = RedisConstants.LOCK_SHOP_KEY+id;
        String shopJson= stringRedisTemplate.opsForValue().get(key);

        if (StrUtil.isBlank(shopJson))
        {
            return null;
        }

        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        LocalDateTime expireTime = redisData.getExpireTime();
        Shop shop = (Shop) redisData.getData();
        if (expireTime.isAfter(LocalDateTime.now()))
        {
            return shop;
        }
        try {
            if (tryLock(lockKey))
            {
                CACHE_EXECUTOR.submit(()->{
                    saveRedisShop(id,20L,TimeUnit.SECONDS);
                });
                Thread.sleep(200);
                unLock(lockKey);
            }
            return shop;
        } catch (InterruptedException e) {
            throw  new RuntimeException(e);
        }finally {
            unLock(lockKey);
        }
    }

    public boolean tryLock(String key)
    {
        Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstants.LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(aBoolean);
    }

    public  boolean unLock(String key)
    {
       return stringRedisTemplate.delete(key);
    }
}
