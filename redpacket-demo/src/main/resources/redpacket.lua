-- ..为连接两个字符串
--缓存抢红包信息列表key
local listKey ='red_packet_list_'..KEYS[1]
--当前被抢红包的key
local redPacket = 'red_packet_'..KEYS[1]
--获取当前红包的库存
local stock = tonumber(redis.call('hget',redPacket,'stock'))
--没有库存，返回0
if stock <= 0 then
    return 0
end
--库存减 1
stock = stock -1
--保存当前库存
redis.call('hset',redPacket,'stock',tostring(stock))
--往链表中加入当前红包信息
redis.call('rpush',listKey,ARGV[1])
--如果是最后一个红包，则返回2，表示抢红包已经结束，需要将列表中的数据
-- 保存到数据库中
if stock == 0 then
     return 2
end
--如果不是最后一个红包表示抢红包已经成功
return 1