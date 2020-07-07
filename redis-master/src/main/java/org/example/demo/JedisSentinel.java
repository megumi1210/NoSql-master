package org.example.demo;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 集群搭建测试
 *  @author chenj
 */
public class JedisSentinel {

  public static void main(String[] args) {
    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxTotal(10);
    poolConfig.setMaxIdle(5);
    poolConfig.setMinIdle(5);

    // 哨兵信息
    Set<String> sentinels =
        new HashSet<>(
            Arrays.asList("master:26379", "slave01:26379", "slave02:26379", "slave03:26379"));
    // 创建连接池
    // mymaster 是我们配置给哨兵的服务名称
    // poolConfig 是连接池的配置
    // 123456 是连接服务器密码
    JedisSentinelPool pool = new JedisSentinelPool("mymaster", sentinels, poolConfig, "123456");
    //获取客户端
    Jedis jedis = pool.getResource();
    jedis.set("key1","value1");
    System.out.println(jedis.get("key1"));
  }
}
