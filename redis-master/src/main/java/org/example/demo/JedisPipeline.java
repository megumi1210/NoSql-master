package org.example.demo;

import org.example.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

public class JedisPipeline {

  public static void main(String[] args) {
      Jedis jedis = JedisUtil.getJedis();
      //开启流水线
      Pipeline pipeline = jedis.pipelined();
      long start =  System.currentTimeMillis();
      //测试10万条数据的读写2个操作
      for(int  i = 0 ;i<100000;i++){
          int j = i+1;
          pipeline.set("pipeline_key_"+j,"pipeline_value_"+j);
          pipeline.get("pipeline_key_"+j);
      }

      pipeline.sync();  //同步但是不返回结果
      //pipeline.syncAndReturnAll();  //同步并将结果返回
      long end = System.currentTimeMillis();
      //计算耗时
      System.err.println("耗时:" + (end - start) + "毫秒");
  }
}
