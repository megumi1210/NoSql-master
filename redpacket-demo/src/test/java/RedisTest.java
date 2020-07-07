import com.megumi.config.RootConfig;

import com.megumi.service.RedPacketService;
import com.megumi.service.impl.RedPacketServiceImpl;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

import java.util.List;

@SuppressWarnings("all")
public class RedisTest {
  AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(RootConfig.class);

  RedisTemplate redisTemplate = (RedisTemplate) ctx.getBean("stringToStringRedisTemplate");

  Jedis jedis = (Jedis) redisTemplate.getConnectionFactory().getConnection().getNativeConnection();

  RedPacketService service = new RedPacketServiceImpl();

  /** 需要注释掉 EnableWebMVC 才能测试 */
  @Test
  public void test01() {

    redisTemplate.opsForValue().set("key1", "value1");
    System.out.println(redisTemplate.opsForValue().get("key1"));
  }

  @Test
  public void test02(){

    BoundListOperations ops  =redisTemplate.boundListOps("red_packet_list_1");
    List list  = ops.range(0,1000);
    System.out.println(list);
  }
}
