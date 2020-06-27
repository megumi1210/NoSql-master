package org.example.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 使用 spring 对redis的封装
 * @author chenj
 * @see org.springframework.data.redis.core.RedisTemplate 封装类
 */

@Configuration
public class SpringRedisConfig {

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
     * @return l连接工厂
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

    @Bean
    @SuppressWarnings("all")
    RedisTemplate  redisTemplate(){
         RedisTemplate redisTemplate = new RedisTemplate();
         redisTemplate.setConnectionFactory(connectionFactory());
         redisTemplate.setKeySerializer(stringRedisSerializer());
         redisTemplate.setValueSerializer(jdkSerializationRedisSerializer());
         return  redisTemplate;
    }

}
