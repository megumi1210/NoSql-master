package com.megumi.service.impl;

import com.megumi.dao.RedPacketDao;

import com.megumi.domain.RedPacket;
import com.megumi.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author chenj
 */
@Service
@SuppressWarnings("all")
public class RedPacketServiceImpl implements RedPacketService {

    private static  final String PREFIX ="red_packet_";

    @Autowired
    private RedPacketDao redPacketDao = null;

    @Autowired
    @Qualifier("stringToJdkRedisTemplate")
    private RedisTemplate redisTemplate = null;

    @Override
    @Transactional(isolation =  Isolation.READ_COMMITTED , propagation = Propagation.REQUIRED ,
       rollbackFor = Exception.class)
    public RedPacket getRedPacket(Long id) {
        return redPacketDao.getRedPacked(id);
    }

    @Override
    @Transactional(isolation =  Isolation.READ_COMMITTED , propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class)
    public int decreaseRedPacket(Long id) {
        return  redPacketDao.decreaseRedPacket(id);
    }

    @Override
    @Transactional(isolation =  Isolation.READ_COMMITTED , propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class)
    public boolean insertRedPacket(RedPacket redPacket) {
        boolean result = redPacketDao.insertRedPacket(redPacket) ==1;
        if(result) {
            long id = redPacket.getId();
            int stock = redPacket.getStock();
            double unit_amount = redPacket.getUnitAmount();

            redisTemplate.boundHashOps(PREFIX+id).put("stock",stock);
            redisTemplate.boundHashOps(PREFIX+id).put("unit_stock",unit_amount);
        }
        return  result;
    }
}
