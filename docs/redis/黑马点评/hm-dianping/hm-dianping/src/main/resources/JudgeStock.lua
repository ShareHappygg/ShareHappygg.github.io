--优惠id--
local voucherId =ARGV[1]
--用户id--
local userId = ARGV[2];
--订单id--
local orderId = ARGV[3];
--库存key--
local stockKey = "seckill:stock:"..voucherId
--优惠卷key--
local voucherKey = "seckill:order:"..voucherId
--判断库存--
if (tonumber(redis.call('get',stockKey) ) <=0 )then
    return 1
end
--判断用户是否一人一单--
if (redis.call('sismember',voucherKey,userId) ==1) then
    return  2
end
--扣除库存--
redis.call('incrby',stockKey,-1)
--标记用户购买订单--
redis.call('sadd',voucherKey,userId)
--接受订单消息队列--
redis.call('xadd',"stream.orders",'*','userId',userId,'id',orderId,'voucherId',voucherId)
return 0