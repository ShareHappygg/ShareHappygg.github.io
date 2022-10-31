package com.hmdp.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hmdp.dto.Result;
import com.hmdp.dto.UserDTO;
import com.hmdp.entity.VoucherOrder;
import com.hmdp.mapper.VoucherOrderMapper;
import com.hmdp.service.ISeckillVoucherService;
import com.hmdp.service.IVoucherOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hmdp.service.IVoucherService;
import com.hmdp.utils.RedisConstants;
import com.hmdp.utils.RedisIdWorker;
import com.hmdp.utils.UserHolder;
import org.apache.ibatis.javassist.bytecode.analysis.Executor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2021-12-22
 */
@Service
public class VoucherOrderServiceImpl extends ServiceImpl<VoucherOrderMapper, VoucherOrder> implements IVoucherOrderService {

    @Autowired
    private IVoucherService voucherService;
    @Autowired
    private ISeckillVoucherService seckillVoucherService;
    @Autowired
    private RedisIdWorker redisIdWorker;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedissonClient redissonClient;




    private  static DefaultRedisScript<Long> defaultRedisScript =new DefaultRedisScript<>();

    static {
        defaultRedisScript.setResultType(Long.class);
        defaultRedisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("JudgeStock.lua")));
    }

    IVoucherOrderService proxy;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
    @PostConstruct
    public void  init()
    {
        executorService.submit(new VoucherHolder());
    }

    private class  VoucherHolder implements Runnable {
        @Override
        public void run() {
            while (true){
//                "xreadgroup g1 c1 count 1 block 2000 STREAMS  stream.orders > "
                try {
                    List<MapRecord<String, Object, Object>> list =
                            stringRedisTemplate.opsForStream().read(
                                    Consumer.from("g1", "c1"),
                                    StreamReadOptions.empty().count(1).block(Duration.ofSeconds(2)),
                                    StreamOffset.create(RedisConstants.MESSAGE_QUEUE_KEY, ReadOffset.lastConsumed()));
                    if (ObjectUtil.isEmpty(list))
                    {
                        continue;
                    }
                    MapRecord<String, Object, Object> record = list.get(0);
                    Map<Object, Object> values = record.getValue();
                    VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), false);
                    handlerOrder(voucherOrder);
                    stringRedisTemplate.opsForStream().acknowledge(RedisConstants.MESSAGE_QUEUE_KEY,"g1",record.getId());
                } catch (Exception e) {
                    handlerPendList();
                }


            }
        }
    }

    @Transactional
    public void createVoucherOrder(VoucherOrder voucherOrder)
    {
        Long userId = voucherOrder.getUserId();
        Long voucherId = voucherOrder.getVoucherId();

        QueryWrapper<VoucherOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId).eq("voucher_id",voucherId);
        int count= this.baseMapper.selectCount(queryWrapper);
        if (count>0)
        {
            log.error("一个用户只能使用一张优惠卷");
        }

//        final   LongAdder longAdder = new LongAdder();
//        longAdder.increment();
        boolean success = seckillVoucherService.update().setSql("stock=stock-1")
                .eq("voucher_id", voucherId).gt("stock",0).update();
        if (!success)
        {
            log.error("库存不足");
        }


        save(voucherOrder);
        log.debug("抢购成功+订单ID"+voucherOrder.getId());
    }

    private void handlerPendList() {
        while (true) {
//                "xreadgroup g1 c1 count 1 block 2000 STREAMS  stream.orders > "
            try {
                List<MapRecord<String, Object, Object>> list =
                        stringRedisTemplate.opsForStream().read(
                                Consumer.from("g1", "c1"),
                                StreamReadOptions.empty().count(1),
                                StreamOffset.create(RedisConstants.MESSAGE_QUEUE_KEY, ReadOffset.from("0")));
                if (ObjectUtil.isEmpty(list)) {
                    break;
                }
                MapRecord<String, Object, Object> record = list.get(0);
                Map<Object, Object> values = record.getValue();
                VoucherOrder voucherOrder = BeanUtil.fillBeanWithMap(values, new VoucherOrder(), false);
                handlerOrder(voucherOrder);
                stringRedisTemplate.opsForStream().acknowledge(RedisConstants.MESSAGE_QUEUE_KEY, "g1", record.getId());
            } catch (Exception e) {
                log.error("处理订单异常");

            }


        }
    }

    private void handlerOrder(VoucherOrder order) {
        Long userId = order.getUserId();
        RLock rLock = redissonClient.getLock(RedisConstants.LOCK_ORDER_KEY+userId);
        boolean success = rLock.tryLock();
        if (!success)
        {
            log.error("不允许重复下单");
            return;
        }
        try {
            proxy.createVoucherOrder(order);
        }finally {
            rLock.unlock();
        }

    }

    @Override
    public Result seckillVoucher(Long voucherId) {
        UserDTO user =UserHolder.getUser();
        //用户id
        Long userId = user.getId();
        //订单id
        long orderId =redisIdWorker.nextId("order");

        Long success = stringRedisTemplate.execute(defaultRedisScript, Collections.emptyList(), voucherId.toString(), userId.toString(),String.valueOf(orderId));
        if (success !=0)
        {
            return Result.fail(success!=1?"一个用户只能下一单":"库存不足");
        }
        VoucherOrder voucherOrder = new VoucherOrder();

        voucherOrder.setUserId(userId);
        voucherOrder.setId(orderId);
        voucherOrder.setVoucherId(voucherId);
        proxy =(IVoucherOrderService) AopContext.currentProxy();

        return  Result.ok("下单成功,订单号为"+orderId);
    }

//    //异步使用阻塞队列消费订单
//    BlockingQueue<VoucherOrder> orderTasks = new ArrayBlockingQueue<>(1024*1024);
//
//
//    IVoucherOrderService proxy;
//
//    ExecutorService executorService = Executors.newSingleThreadExecutor();
//    @PostConstruct
//    public void  init()
//    {
//        executorService.submit(new VoucherHolder());
//    }
//
//    private class  VoucherHolder implements Runnable {
//        @Override
//        public void run() {
//            while (true){
//                try {
//                    VoucherOrder order = orderTasks.take();
//                    handlerOrder(order);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    private void handlerOrder(VoucherOrder order) {
//        Long userId = order.getUserId();
//        RLock rLock = redissonClient.getLock(RedisConstants.LOCK_ORDER_KEY+userId);
//        boolean success = rLock.tryLock();
//        if (!success)
//        {
//            log.error("不允许重复下单");
//            return;
//        }
//        try {
//            proxy.createVoucherOrder(order);
//        }finally {
//            rLock.unlock();
//        }
//
//    }
//
//    ;
//
//   @Override
//    public Result seckillVoucher(Long voucherId) {
//        UserDTO user =UserHolder.getUser();
//        Long userId = user.getId();
//        Long success = stringRedisTemplate.execute(defaultRedisScript, Collections.emptyList(), voucherId.toString(), userId.toString());
//        if (success !=0)
//        {
//            return Result.fail(success!=1?"一个用户只能下一单":"库存不足");
//        }
//       VoucherOrder voucherOrder = new VoucherOrder();
//       long orderId =redisIdWorker.nextId("order");
//       voucherOrder.setUserId(userId);
//       voucherOrder.setId(orderId);
//       voucherOrder.setVoucherId(voucherId);
//       proxy =(IVoucherOrderService) AopContext.currentProxy();
//       orderTasks.add(voucherOrder);
//       return  Result.ok("下单成功,订单号为"+orderId);
//    }
//
//    @Transactional
//    public void createVoucherOrder(VoucherOrder voucherOrder)
//    {
//        Long userId = voucherOrder.getUserId();
//        Long voucherId = voucherOrder.getVoucherId();
//
//        QueryWrapper<VoucherOrder> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id",userId).eq("voucher_id",voucherId);
//        int count= this.baseMapper.selectCount(queryWrapper);
//        if (count>0)
//        {
//            log.error("一个用户只能使用一张优惠卷");
//        }
//
////        final   LongAdder longAdder = new LongAdder();
////        longAdder.increment();
//        boolean success = seckillVoucherService.update().setSql("stock=stock-1")
//                .eq("voucher_id", voucherId).gt("stock",0).update();
//        if (!success)
//        {
//            log.error("库存不足");
//        }
//
//
//        save(voucherOrder);
//        log.debug("抢购成功+订单ID"+voucherOrder.getId());
//    }

    //同步执行
//    @Override
//    public Result seckillVoucher(Long voucherId) {
//
//        Voucher voucher = voucherService.getById(voucherId);
//        if (ObjectUtil.isEmpty(voucher))
//        {
//            return Result.fail("优惠价不存在");
//        }
//
//        SeckillVoucher seckillVoucher = seckillVoucherService.getById(voucherId);
//        if (seckillVoucher.getBeginTime().isAfter(LocalDateTime.now()))
//        {
//            return  Result.fail("秒杀活动未开始");
//        }
//
//        if (seckillVoucher.getEndTime().isBefore(LocalDateTime.now()))
//        {
//            return  Result.fail("秒杀活动结束");
//        }
//        Integer stock = seckillVoucher.getStock();
//        if (stock<=0)
//        {
//            return  Result.fail("库存不足");
//        }
//
//        Long userId = UserHolder.getUser().getId();
//
//
//        boolean success = cacheClient.tryLock("order:" + userId.toString().intern(), RedisConstants.CACHE_NULL_TTL);
//        if (!success)
//        {
//            return Result.fail("一个只能下一单");
//        }
//        try {
//            //获取代理对象
//            IVoucherOrderService proxy =(IVoucherOrderService) AopContext.currentProxy();
//
//            return proxy.createVoucherOrder(userId, voucherId);
//        } finally {
//            cacheClient.unLock(("order:"+userId.toString().intern()));
//        }
//
//
////        //单机下可以保证ACID，但在集群下无法保证
////        synchronized (userId.toString().intern()){
////
////            //获取代理对象
////            IVoucherOrderService proxy =(IVoucherOrderService) AopContext.currentProxy();
////
////            return proxy.createVoucherOrder(userId, voucherId);
////        }
//
//
//
//    }

//    @Transactional
//    public Result createVoucherOrder(Long userId,Long voucherId)
//    {
//
//        QueryWrapper<VoucherOrder> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id",userId).eq("voucher_id",voucherId);
//        int count= this.baseMapper.selectCount(queryWrapper);
//        if (count>0)
//        {
//            return Result.fail("一个用户只能使用一张优惠卷");
//        }
//
////        final   LongAdder longAdder = new LongAdder();
////        longAdder.increment();
//        boolean success = seckillVoucherService.update().setSql("stock=stock-1")
//                .eq("voucher_id", voucherId).gt("stock",0).update();
//        if (!success)
//        {
//            return  Result.fail("库存不足");
//        }
//
//
//
//        VoucherOrder voucherOrder = new VoucherOrder();
//        long orderId =redisIdWorker.nextId("order");
//        voucherOrder.setUserId(userId);
//        voucherOrder.setId(orderId);
//        voucherOrder.setVoucherId(voucherId);
//        save(voucherOrder);
//        return Result.ok("抢购成功+订单ID"+orderId);
//    }


}

