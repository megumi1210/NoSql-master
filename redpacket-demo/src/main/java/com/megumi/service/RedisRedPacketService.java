package com.megumi.service;

/**
 * @author chenj
 */
public interface RedisRedPacketService {
    /**
     * 保存 redis 抢红包列表
     * @param rePackedId  抢红包编号
     * @param unitAmount   红包金额
     */
     void saveUserRedPacketByRedis(Long rePackedId, Double unitAmount);
}
