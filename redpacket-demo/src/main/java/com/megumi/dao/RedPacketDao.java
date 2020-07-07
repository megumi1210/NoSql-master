package com.megumi.dao;

import com.megumi.domain.RedPacket;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chenj
 */

@Mapper
public interface RedPacketDao {

    /**
     *  获取红包信息
     * @param id 红包 id
     * @return 红包的具体信息
     */
     RedPacket getRedPacked(Long id);


    /**
     *  扣减抢红包个数
     * @param id 红包 id
     * @return 更新的记录数
     */
     int decreaseRedPacket(Long id);


    /**
     *  插入红包的数据
     * @param redPacket 新发的红包
     * @return 更新的记录数
     */
    int insertRedPacket(RedPacket redPacket);


    /**
     *  查询全部数据
     * @return 全部数据
     */
    List<RedPacket>  findAll();
}
