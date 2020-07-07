package com.megumi.dao;


import com.megumi.domain.UserRedPacket;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author chenj
 */
@Mapper
public interface UserRedPacketDao {

    /**
     *   插入抢红包信息
     * @param userRedPacket 抢红包信息
     * @return 影响记录数
     */
    int grabRedPacket(UserRedPacket userRedPacket);
}
