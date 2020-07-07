package com.megumi.service;

/**
 * @author chenj
 */
public interface UserRedPacketService {
    /**
     *   保存抢红包消息
     * @param redPacketId 红包编号
     * @param userId 抢红包用户编号
     * @return 影响记录数
     */
   int grabRedPacket(Long redPacketId, Long userId);


    /**
     *  通过 Redis 实现抢红包
     *
     * @param redPacketId 红包编号
     * @param userId 用户编号
     * @return
     * 0-没有库存，失败
     * 1-成功，且不是最后一个红包
     * 2-成功，且是最后一个红包
     */
      Long grabRedPacketByRedis(Long redPacketId, Long userId);
}
