package org.example.config;

import org.example.domain.RedisMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * 使用 spring 对redis的封装
 * @author chenj
 * @see org.springframework.data.redis.core.RedisTemplate 封装类
 */

@Configuration
@SuppressWarnings("all")
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

    /**
     *  发布订阅的消息监听容器类配置
     */
    @Bean
    RedisMessageListenerContainer messageListenerContainer(){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());

        ThreadPoolTaskScheduler taskExecutor= new ThreadPoolTaskScheduler();
        taskExecutor.setPoolSize(3);
        taskExecutor.initialize();
        container.setTaskExecutor(taskExecutor);


        Map<MessageListener, Collection<? extends Topic>> map = new HashMap<>(8);
        List<Topic> topics = new ArrayList<>();
        topics.add(new ChannelTopic("chat"));

         MessageListener messageListener = messageListener();

        map.put(messageListener,topics);
        container.setMessageListeners(map);
        return  container;
    }

    /**
     *  消息监听器的配置
     * @return 消息监听器
     */
    @Bean
    MessageListener messageListener(){
        RedisMessageListener messageListener = new RedisMessageListener();
        messageListener.setRedisTemplate(stringToStringRedisTemplate());
        return  messageListener;
    }


    /**
     * 配置线程池
     * @return 线程池
     */
    //@Bean
    TaskExecutor taskExecutor(){
        ThreadPoolTaskScheduler taskExecutor= new ThreadPoolTaskScheduler();
        taskExecutor.setPoolSize(3);
        taskExecutor.initialize();
        return  taskExecutor;
    }




}
