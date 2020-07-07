package com.megumi.controller;

import com.megumi.domain.RedPacket;
import com.megumi.service.RedPacketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.sql.Timestamp;

/**
 * @author chenj
 */
@RestController
@RequestMapping("/redPacket")
public class RedPacketController {

    @Autowired
    private RedPacketService redPacketService = null;


    @RequestMapping("/get")
    public RedPacket getRedPacket(Long redPacketId){
        return  redPacketService.getRedPacket(redPacketId);
    }

    @RequestMapping("/insert")
    public boolean  insertRedPacket(@RequestBody RedPacket redPacket){
        long now = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(now);
        redPacket.setSendDate(timestamp);
         return   redPacketService.insertRedPacket(redPacket);
    }

}
