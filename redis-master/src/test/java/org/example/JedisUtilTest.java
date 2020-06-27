package org.example;

import org.example.util.JedisUtil;
import org.junit.Test;
import redis.clients.jedis.Jedis;

public class JedisUtilTest {

    @Test
    public void test01(){
        Jedis  jedis = JedisUtil.getJedis();
        jedis.set("test","test");
    }
}
