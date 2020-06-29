package org.example.service.impl;

import org.example.service.RedisTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义 redis 服务接口实现类
 * @author chenj
 */
@Service
@SuppressWarnings("all")
public class RedisTemplateServiceImpl implements RedisTemplateService {

    @Autowired
    @Qualifier("stringToStringRedisTemplate")
    private RedisTemplate redisTemplate = null;

    /**
     *  使用 SessionCallback 接口实现多个命令在一个Redis 连接中执行
     */
    @Override
    public void execMultiCommand() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations ops) throws DataAccessException {

                    ops.boundValueOps("key1").set("abc");
                    ops.boundHashOps("hash").put("hash-key-1","hash-value-1");
                    return  ops.boundValueOps("key1").get();
            }
        });
     System.err.println(obj);
    }

    /**
     *  使用 SessionCallback 接口实现事务在一个Redis 连接中执行
     */
    @Override
    public void execTransaction() {
         List list = (List) redisTemplate.execute(new SessionCallback() {
              @Override
              public Object execute(RedisOperations ops) throws DataAccessException {
                  //监控
                  ops.watch("key1");
                  //开启事务
                  ops.multi();
                  ops.boundValueOps("key1").set("abc");
                  ops.boundHashOps("hash").put("hash-key-1","hash-value-1");
                  ops.opsForValue().get("key1");

                  List result = ops.exec();
                  return  result;
              }
          });

    System.err.println(list);
    }


    /**
     * 执行流水线，将多个命令一次性发送给 Redis 服务器
     */
    @Override
    public void execPipeline() {
          List list =redisTemplate.executePipelined(new SessionCallback() {
              @Override
              public Object execute(RedisOperations ops) throws DataAccessException {
                  //流水线下，命令不会马上返回结果，结果是一次性执行后返回的
                   ops.opsForValue().set("key1","value1");
                   ops.opsForHash().put("hash","key-hash-1","value-hash-1");
                   ops.opsForValue().get("key1");
                   return  null;
              }
          });

       System.err.println(list);
    }
}
