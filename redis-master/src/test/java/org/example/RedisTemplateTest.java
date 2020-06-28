package org.example;

import org.example.config.RootConfig;
import org.example.domain.Role;
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

  // 获取被封装的原始对象
  private Jedis jedis =
      (Jedis)
          stringToStringRedisTemplate.getConnectionFactory().getConnection().getNativeConnection();

  /** 测试用的案例 */
  private Role data = new Role(1L, "role_name_1", "note_1");

  /** 底层操作测试 */
  @Test
  public void test00() {
    // 清空数据库
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

    stringToStringRedisTemplate.opsForValue().set("hello", "开发者");
    System.out.println(stringToStringRedisTemplate.opsForValue().get("hello"));
  }

  /** 测试整数以及运算 */
  @Test
  public void test04() {
    stringToStringRedisTemplate.opsForValue().set("i", "9");
    System.out.println(stringToStringRedisTemplate.opsForValue().get("i"));
    // i--操作
    stringToStringRedisTemplate
        .getConnectionFactory()
        .getConnection()
        .decr(stringToJdkRedisTemplate.getKeySerializer().serialize("i"));
    System.out.println(stringToStringRedisTemplate.opsForValue().get("i"));
  }

  /**
   * map 数据结构操作
   *
   * @see org.springframework.data.redis.core.DefaultHashOperations
   */
  @Test
  public void test05() {
    Map<String, String> map = new HashMap<>();
    map.put("f1", "val1");
    map.put("f2", "val2");
    // hmset
    stringToStringRedisTemplate.opsForHash().putAll("hash", map);
    // hset
    stringToStringRedisTemplate.opsForHash().put("hash", "f3", "val3");
    // hgetall
    Map cache = stringToStringRedisTemplate.opsForHash().entries("hash");
    cache.forEach(
        (key, value) -> {
          System.out.println(key + " ->" + value);
        });

    // hget
    System.out.println(stringToStringRedisTemplate.opsForHash().get("hash", "f1"));
  }

  /**
   * 双向链表操作,其他数据结构操作类似，查看源码即可
   *
   * @see org.springframework.data.redis.core.DefaultListOperations
   */
  @Test
  public void test06() {
    // 删除链表以便反复测试
    stringToStringRedisTemplate.delete("list");

    stringToStringRedisTemplate.opsForList().leftPush("list", "node3");

    List<String> nodeList = new ArrayList<>();
    for (int i = 2; i >= 1; i--) {
      nodeList.add("node" + i);
    }

    stringToStringRedisTemplate.opsForList().leftPushAll("list", nodeList);
    stringToStringRedisTemplate.opsForList().rightPush("list", "node4");

    long size = stringToStringRedisTemplate.opsForList().size("list");
    System.out.println(stringToStringRedisTemplate.opsForList().range("list", 0, size));
  }

  /** spring 下使用开启事务 */
  @Test
  public void test07() {
    SessionCallback callback =
        new SessionCallback() {
          @Override
          public List execute(RedisOperations ops) throws DataAccessException {
            ops.multi();
            // 只是进入队列没有被执行
            ops.boundValueOps("key1").set("value1");
            String value1 = (String) ops.boundValueOps("key1").get();

            ops.boundValueOps("key2").set("value2");
            String value2 = (String) stringToStringRedisTemplate.opsForValue().get("key2");

            List list = ops.exec(); // 执行事务,将全部结果封装到 List 中

            return list;
          }
        };

    // 执行 reddis 命令
    List value = (List) stringToStringRedisTemplate.execute(callback);
    System.out.println(value);
  }


    /**
     *  事务测试2
     */
  @Test
  public void test08(){
      stringToStringRedisTemplate.getConnectionFactory().getConnection().flushDb();
      stringToStringRedisTemplate.opsForValue().set("key1","value1");
      //使用乐观锁监控,只是针对多线程安全有效，比如多个客户端对数据同时修改
      stringToStringRedisTemplate.watch("key1");

      SessionCallback callback = new SessionCallback() {
          @Override
          public Object execute(RedisOperations ops) throws DataAccessException {
                ops.multi();

                stringToStringRedisTemplate.opsForValue().set("key1","value2");
                ops.boundValueOps("key2").set("value2");

                ops.exec();

                String result = (String) stringToStringRedisTemplate.opsForValue().get("key2");
                System.out.println(result);
                return  result;
          }
      };

      stringToStringRedisTemplate.execute(callback);
      System.out.println(stringToStringRedisTemplate.opsForValue().get("key1"));

  }


    /**
     *  Spring 中使用流水线 pipeline
     */
  @Test
  public void test09(){
      jedis.flushDB();
      SessionCallback callback = new SessionCallback() {
          @Override
          public Object execute(RedisOperations ops) throws DataAccessException {
              for(int i = 0 ; i< 100000 ;i++){
                  int j = i+1;
                  ops.boundValueOps("pipeline_key_"+j).set("pipeline_value_"+j);
                  ops.boundValueOps("pipeline_key_"+j).get();
              }
              return  null;
          }
      };

      long start  =System.currentTimeMillis();
      stringToStringRedisTemplate.executePipelined(callback);
      long end = System.currentTimeMillis();

    System.out.println("耗时："+ (end -start) +"毫秒");
  }


    /**
     *  发布订阅配置
     */
  @Test
  public void test10(){
      String channel = "chat";
      stringToStringRedisTemplate.convertAndSend(channel,"hello");

      //先打开客户端  执行 subscribe chat ，在运行此代码会受到消息
  }


}
