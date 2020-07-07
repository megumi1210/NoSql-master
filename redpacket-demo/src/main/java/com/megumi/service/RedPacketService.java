package com.megumi.service;


import com.megumi.domain.RedPacket;

/**
 * @author chenj
 */
public interface RedPacketService {
    /**
     *  获取红包信息
     * @param id 编号
     * @return 红包信息
     */
    public RedPacket getRedPacket(Long id);


    /**
     * 扣减红包
     * @param id 编号
     * @return 影响条数
     */
    int decreaseRedPacket(Long id);

    /**
     *  插入红包
     * @param redPacket 红包
     * @return 是否更新成功
     */
    boolean  insertRedPacket(RedPacket redPacket);
}
