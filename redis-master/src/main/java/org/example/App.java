package org.example;

import redis.clients.jedis.Jedis;

/**
 * redis 入门测试
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Jedis jedis = new Jedis("localhost", 6379);
        jedis.auth("123456");
        int i = 0; //记录操作数
        try{
            long start = System.currentTimeMillis();
            while(true){
                long end = System.currentTimeMillis();
                if(end - start >=1000){//当大于 1 秒时结束操作
                    break;
                }
                i++;
                jedis.set("test"+i ,i+"");
            }
        }finally{
            jedis.close();
        }
        System.out.println("redis 每秒操作:" + i +" 次");
    }
}
