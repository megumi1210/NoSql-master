package org.example.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Redis 连接池工具类,不使用 RedisTemplate
 * @author chenj
 * @see Jedis
 */
public final class JedisUtil {

    private static JedisPool jedisPool ;

    static {

        try{
            //读取配置文件
            InputStream is = JedisPool.class.getClassLoader().getResourceAsStream("jedis.properties");
            //创建 properties 对象
            Properties pro = new Properties();
            pro.load(is);
            //获取数据，设置到JedisPoolConfig中
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Integer.parseInt(pro.getProperty("maxTotal")));
            config.setMaxIdle(Integer.parseInt(pro.getProperty("maxIdle")));
            //初始化JedisPool
            jedisPool = new JedisPool(config,pro.getProperty("host"), Integer.parseInt(pro.getProperty("port")),
                    Integer.parseInt(pro.getProperty("timeOut")), pro.getProperty("password"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  static Jedis getJedis(){return  jedisPool.getResource();}

    /**
     * 关闭Jedis
     */
    public static void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
