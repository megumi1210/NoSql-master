package org.example.demo;

import org.example.util.JedisUtil;
import redis.clients.jedis.Jedis;

/** redis 执行脚本案例
 *  如果需要对象类的操作，可以使用Spring
 */
public class JedisLua {

  public static void main(String[] args) {
    Jedis jedis = JedisUtil.getJedis();
    // 执行简单的脚本
    String helloJava = (String) jedis.eval("return 'hello java'");
    System.out.println(helloJava);
    // 执行带参数的脚本 采用主要的调用格式 redis.call(command,[key..],[args..]) keyNum 对应的key  对应的value
    jedis.eval("redis.call('set',KEYS[1], ARGV[1])", 1, "lua-key", "lua-value");
    String luaKey = jedis.get("lua-key");
    System.out.println(luaKey);
    // 缓存脚本
    String sha1 = jedis.scriptLoad("redis.call('set',KEYS[1],ARGV[1])");
    // 通过标识执行脚本
    jedis.evalsha(sha1, 1, "sha-key", "sha-value");
    // 获取执行后脚本后的数据
    String shaVal = jedis.get("sha-key");
    System.out.println(shaVal);
    // 关闭连接
    JedisUtil.close(jedis);
  }
}
