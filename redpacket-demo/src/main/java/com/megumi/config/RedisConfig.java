package com.megumi.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.redis.cache.RedisCacheManager;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * 使用 spring 对redis的封装
 * @author chenj
 * @see RedisTemplate 封装类
 */

@Configuration
//开启缓存
@EnableCaching
@SuppressWarnings("all")
public class RedisConfig {

    @Autowired
    private RedisProperties redisProperties = null;

    /**
     *  redis连接池属相配置
     * @return JedisPoolConfig
     */
    @Bean
    JedisPoolConfig poolConfig(){
        JedisPoolConfig  config = new JedisPoolConfig();
        config.setMaxIdle(Integer.parseInt(redisProperties.getMaxIdle()));
        config.setMaxTotal(Integer.parseInt(redisProperties.getMaxTotal()));
        config.setMaxWaitMillis(Long.parseLong(redisProperties.getMaxWaitMillis()));
        return  config;
    }


    /**
     *  redis 连接工厂的配置
     * @return 连接工厂
     */
    @Bean
    JedisConnectionFactory  connectionFactory(){
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisProperties.getHostName());
        factory.setPort(Integer.parseInt(redisProperties.getPort()));
        factory.setPassword(redisProperties.getPassword());
        factory.setPoolConfig(poolConfig());
        return  factory;
    }

    /**
     * @return java对象的序列化器
     */
    @Bean
    JdkSerializationRedisSerializer jdkSerializationRedisSerializer(){
        return  new JdkSerializationRedisSerializer();
    }

    /**
     * @return 字符串的序列化器
     */
    @Bean
    StringRedisSerializer  stringRedisSerializer(){
        return  new StringRedisSerializer();
    }


    /**
     *
     * @return string -> jdk对象 键值对序列化的 redisTemplate
     */
    @Bean(name="stringToJdkRedisTemplate")
    RedisTemplate  stringToJdkRedisTemplate(){
         RedisTemplate redisTemplate = new RedisTemplate();
         redisTemplate.setConnectionFactory(connectionFactory());
         redisTemplate.setKeySerializer(stringRedisSerializer());
         redisTemplate.setValueSerializer(jdkSerializationRedisSerializer());
         return  redisTemplate;
    }


    /**
     * @return  string -> string 键值对序列化的 redisTemplate
     */
    @Bean(name="stringToStringRedisTemplate")
    RedisTemplate  stringToStringRedisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setValueSerializer(stringRedisSerializer());
        return  redisTemplate;
    }








    @Bean(name ="redisCacheManager")
    CacheManager cacheManager(){
        RedisCacheManager cacheManager = new RedisCacheManager(stringToJdkRedisTemplate());
        //设置超时时间为 10 分钟，单位为秒
        cacheManager.setDefaultExpiration(600);
        //设置缓存名称
        List<String> cacheNames = new ArrayList<>();
        cacheNames.add("redisCacheManager");
        cacheManager.setCacheNames(cacheNames);
        return  cacheManager;
    }



}
