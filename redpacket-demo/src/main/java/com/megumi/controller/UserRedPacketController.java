package com.megumi.controller;

import com.megumi.service.UserRedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chenj
 */
@RestController
public class UserRedPacketController {

    @Autowired
    private UserRedPacketService userRedPacketService = null;


    /**
     *  不使用 redis抢红包
     * @param redPacketId 红包id
     * @param userId  用户编号
     * @return 是否抢成功
     */
    @RequestMapping("/grabRedPacket")
    public  Map<String,Object>  grabRedPacket(Long redPacketId,Long userId){

        int result =  userRedPacketService.grabRedPacket(redPacketId,userId);
        boolean flag = result > 0;
        Map<String, Object> retMap = new HashMap<>(2);
        retMap.put("success",flag);
        retMap.put("message",flag ?"抢红包成功":"抢红包失败");
        return  retMap;
    }


    /**
     *  使用 redis抢红包
     * @param redPacketId 红包id
     * @param userId  用户编号
     * @return 是否抢成功
     */
    @RequestMapping("/grabRedPacketByRedis")
    public  Map<String,Object> grabRedPacketByRedis(Long  redPacketId, Long userId){
         Map<String ,Object> resultMap = new HashMap<>(2);
         Long result =  userRedPacketService.grabRedPacketByRedis(redPacketId,userId);
         boolean flag = result > 0;
         resultMap.put("result",flag);
         resultMap.put("message",flag? "抢红包成功":"抢红包失败");
         return resultMap;
    }



}
