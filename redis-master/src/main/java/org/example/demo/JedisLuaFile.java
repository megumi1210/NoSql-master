package org.example.demo;

import org.example.util.FileUtil;
import org.example.util.JedisUtil;
import redis.clients.jedis.Jedis;

import java.io.InputStream;

/**
 * 使用 lua 文件拓展 redis
 * @author chenj
 */
public class JedisLuaFile {
  public static void main(String[] args) {
      InputStream is = JedisLuaFile.class.getClassLoader().getResourceAsStream("test.lua");
      assert is != null;
      byte[] bytes = FileUtil.getInputStreamToByte(is);
      Jedis jedis = JedisUtil.getJedis();
      //发送文件二进制给 Redis ,能够得到 sha1标识
      byte[] sha1= jedis.scriptLoad(bytes);

      Object object = jedis.evalsha(sha1,2,"key1".getBytes(),"key2".getBytes(),
                "2".getBytes(),"4".getBytes());
    System.out.println(object);
  }
}
