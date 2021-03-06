package org.example.config;

import org.example.domain.RedisMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisSentinelConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;

/**
 * 使用 spring 对redis的封装
 * @author chenj
 * @see org.springframework.data.redis.core.RedisTemplate 封装类
 */

@Configuration
//开启缓存
@EnableCaching
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

    /**
     *  发布订阅的消息监听容器类配置
     */
    @Bean
    RedisMessageListenerContainer messageListenerContainer(){
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());

        container.setTaskExecutor(taskExecutor());


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
    @Bean
    TaskExecutor taskExecutor(){
        ThreadPoolTaskScheduler taskExecutor= new ThreadPoolTaskScheduler();
        taskExecutor.setPoolSize(3);
        taskExecutor.initialize();
        return  taskExecutor;
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

    /**
     *
     * @return 哨兵模式的配置
     */
    RedisSentinelConfiguration  sentinelConfig(){

        RedisSentinelConfiguration config = new RedisSentinelConfiguration();

         config.setMaster(redisProperties.getMaster());
         String sentinel = redisProperties.getSentinel();

         List<String> urls = Arrays.asList(sentinel.split(","));

         List<RedisNode> nodes = new ArrayList<>();

         for(String url : urls){
             String host = url.split(":")[0].trim();
             int port = Integer.parseInt(url.split(":")[1].trim());
             nodes.add(new RedisNode(host,port));
         }

         config.setSentinels(nodes);

         return  config;

    }


    /**
     *  sentinel 连接工厂的配置
     * @return 连接工厂
     */
    @Bean
    JedisConnectionFactory  sentinelConnectionFactory(){
        JedisConnectionFactory factory = new JedisConnectionFactory(sentinelConfig(),poolConfig());

        factory.setPassword(redisProperties.getPassword());
        return  factory;
    }


    /**
     * @return 配置了集群的 redisTemplate
     */
    @Bean(name="clusterTemplate")
    RedisTemplate  clusterRedisTemplate(){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(sentinelConnectionFactory());
        redisTemplate.setKeySerializer(stringRedisSerializer());
        redisTemplate.setValueSerializer(stringRedisSerializer());
        return  redisTemplate;
    }









}
