package com.megumi.service.impl;

import com.megumi.dao.RedPacketDao;
import com.megumi.dao.UserRedPacketDao;

import com.megumi.domain.RedPacket;
import com.megumi.domain.UserRedPacket;
import com.megumi.service.RedisRedPacketService;
import com.megumi.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

/** @author chenj */
@Service
@SuppressWarnings("all")
public class UserRedPacketServiceImpl implements UserRedPacketService {

  private static final int FAILED = 0;

  @Autowired private UserRedPacketDao userRedPacketDao = null;

  @Autowired private RedPacketDao redPacketDao = null;

  @Autowired
  @Qualifier("stringToJdkRedisTemplate")
  private RedisTemplate redisTemplate = null;

  @Autowired private RedisRedPacketService redisRedPacketService = null;

  // Lua脚本
  String script =
      "local listKey ='red_packet_list_'..KEYS[1] \n"
          + "local redPacket = 'red_packet_'..KEYS[1] \n"
          + "local stock = tonumber(redis.call('hget',redPacket,'stock')) \n"
          + "if stock <= 0 then \n"
          + "    return 0 \n"
          + "end \n"
          + "stock = stock -1 \n"
          + "redis.call('hset',redPacket,'stock',tostring(stock)) \n"
          + "redis.call('rpush',listKey,ARGV[1]) \n"
          + "if stock == 0 then \n"
          + "     return 2 \n"
          + "end \n"
          + "return  1 \n";

  // 在缓存 Lua 脚本后，使用该变量保存 Redis 返回的 32 位的 SHA1 编码，使用它去执行缓存的 Lua脚本
  String sha1 = null;

  /**
   * 普通抢红包
   *
   * @param redPacketId 红包编号
   * @param userId 抢红包用户编号
   * @return
   */
  @Override
  @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
  public int grabRedPacket(Long redPacketId, Long userId) {
    // 获取红包信息
    RedPacket redPacket = redPacketDao.getRedPacked(redPacketId);
    // 当前红包个数大于0
    if (redPacket.getStock() > 0) {
      redPacketDao.decreaseRedPacket(redPacketId);
      // 生成抢红包信息
      UserRedPacket userRedPacket = new UserRedPacket();
      userRedPacket.setRedPackedId(redPacketId);
      userRedPacket.setUserId(userId);
      userRedPacket.setAmount(redPacket.getUnitAmount());
      userRedPacket.setNote("抢红包 " + redPacketId);
      // 插入抢红包信息
      return userRedPacketDao.grabRedPacket(userRedPacket);
    }
    // 失败返回
    return FAILED;
  }

  /**
   * 通过redis 抢红包
   *
   * @param redPacketId 红包编号
   * @param userId 用户编号
   * @return 是否成功
   */
  @Override
  public Long grabRedPacketByRedis(Long redPacketId, Long userId) {

    // 当前抢红包用户和日期信息
    String args = userId + "-" + System.currentTimeMillis();
    Long result = null;
    // 获取底层Redis操作对象
    Jedis jedis =
        (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();

    try {
      // 如果脚本没有被加载过，那么进行加载，这样就会返回一个sha1编码
      if (sha1 == null) {
        sha1 = jedis.scriptLoad(script);
      }

      Object res = jedis.evalsha(sha1, 1, redPacketId + "", args);
      result = (Long) res;
      // 返回2时 为最后一个红包 ,此时将抢红包信息通过异步保存到数据库中
      if (result == 2) {

        // 获取单个小红包金额
        String unitAmountStr = jedis.hget("red_packet_" + redPacketId, "unit_amount");
        // 触发保存数据库操作
        Double unitAmount = Double.parseDouble(unitAmountStr);
        System.out.println("Thread name[ " + Thread.currentThread().getName() + " ]");
        redisRedPacketService.saveUserRedPacketByRedis(redPacketId, unitAmount);
      }
    } finally {
      // 确保 jedis 顺利关闭
      if (jedis != null && jedis.isConnected()) {
        jedis.close();
      }
    }
    return result;
  }
}
