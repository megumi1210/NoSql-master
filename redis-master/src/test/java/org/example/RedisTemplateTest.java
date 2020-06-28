package org.example;

import org.example.config.RootConfig;
import org.example.pojo.Role;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("all")
public class RedisTemplateTest {

  private AnnotationConfigApplicationContext ctx =
      new AnnotationConfigApplicationContext(RootConfig.class);

  // string -> jdk 对象的键值对 redisTemplate
  private RedisTemplate stringToStringRedisTemplate =
      (RedisTemplate) ctx.getBean("stringToStringRedisTemplate");

  // string -> string  键值对 redisTemplate
  private RedisTemplate stringToJdkRedisTemplate =
      (RedisTemplate) ctx.getBean("stringToJdkRedisTemplate");

  //获取被封装的原始对象
  private Jedis jedis = (Jedis) stringToStringRedisTemplate.getConnectionFactory().getConnection().
                                             getNativeConnection();

  /** 测试用的案例 */
  private Role data = new Role(1L, "role_name_1", "note_1");


  /**
   *  底层操作测试
   */
  @Test
  public void test00(){
       //清空数据库
        jedis.flushDB();
  }


  /** test01 testo2 使用 string -> jdk 序列化器，测试时若 不相同时 需要调整 */
  @Test
  public void test01() {
    // set 和 get 可能来自同一个连接池的不同连接
    stringToJdkRedisTemplate.opsForValue().set("role_1", data);
    Role role = (Role) stringToJdkRedisTemplate.opsForValue().get("role_1");
    System.out.println(role.getRoleName());
  }

  @Test
  public void test02() {

    // 把多个命令同时放到一个连接中去执行
    SessionCallback callback =
        new SessionCallback() {
          @Override
          public Role execute(RedisOperations ops) throws DataAccessException {
            ops.boundValueOps("role_1").set(data);
            return (Role) ops.boundValueOps("role_1").get();
          }
        };

    Role savedRole = (Role) stringToJdkRedisTemplate.execute(callback);
    System.out.println(savedRole.getRoleName());
  }

  /** 测试字符串 以及中文 */
  @Test
  public void test03() {
    stringToStringRedisTemplate.opsForValue().set("key1", "value1");
    stringToStringRedisTemplate.opsForValue().set("key2", "value2");

    System.out.println(stringToStringRedisTemplate.opsForValue().get("key1"));
    System.out.println(stringToStringRedisTemplate.opsForValue().get("key2"));

    stringToStringRedisTemplate.opsForValue().set("hello","开发者");
    System.out.println(stringToStringRedisTemplate.opsForValue().get("hello"));
  }

  /**
   * 测试整数以及运算
   */
  @Test
   public void test04(){
      stringToStringRedisTemplate.opsForValue().set("i","9");
      System.out.println(stringToStringRedisTemplate.opsForValue().get("i"));
      // i--操作
      stringToStringRedisTemplate.getConnectionFactory().getConnection().decr(
              stringToJdkRedisTemplate.getKeySerializer().serialize("i"));
      System.out.println(stringToStringRedisTemplate.opsForValue().get("i"));
   }


  /**
   *  map 数据结构操作
   * @see org.springframework.data.redis.core.DefaultHashOperations
   */
  @Test
   public void test05(){
    Map<String,String> map = new HashMap<>();
    map.put("f1","val1");
    map.put("f2","val2");
    //hmset
    stringToStringRedisTemplate.opsForHash().putAll("hash",map);
    //hset
    stringToStringRedisTemplate.opsForHash().put("hash","f3","val3");
    // hgetall
    Map cache = stringToStringRedisTemplate.opsForHash().entries("hash");
    cache.forEach(
        (key, value) -> {
          System.out.println(key +" ->" + value);
        });

    // hget
    System.out.println(stringToStringRedisTemplate.opsForHash().get("hash","f1"));
   }


    /**
     *  双向链表操作,其他数据结构操作类似，查看源码即可
     * @see org.springframework.data.redis.core.DefaultListOperations
     */
    @Test
   public void test06(){
        //删除链表以便反复测试
        stringToStringRedisTemplate.delete("list");

        stringToStringRedisTemplate.opsForList().leftPush("list","node3");

        List<String> nodeList = new ArrayList<>();
        for(int i = 2 ;i>=1 ;i--){
            nodeList.add("node"+i);
        }

        stringToStringRedisTemplate.opsForList().leftPushAll("list",nodeList);
        stringToStringRedisTemplate.opsForList().rightPush("list","node4");

        long size = stringToStringRedisTemplate.opsForList().size("list");
        System.out.println(stringToStringRedisTemplate.opsForList().range("list",0,size));
    }







}
