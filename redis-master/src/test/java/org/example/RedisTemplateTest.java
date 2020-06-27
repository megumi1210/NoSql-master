package org.example;

import org.example.config.RootConfig;
import org.example.pojo.Role;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import org.springframework.data.redis.core.RedisTemplate;

public class RedisTemplateTest {

  private AnnotationConfigApplicationContext ctx =
      new AnnotationConfigApplicationContext(RootConfig.class);

  @Test
  @SuppressWarnings("all")
  public void test01() {
    RedisTemplate redisTemplate = ctx.getBean(RedisTemplate.class);
    Role role = new Role();
    role.setId(1L);
    role.setRoleName("role_name_1");
    role.setNote("note_1");
    redisTemplate.opsForValue().set("role_1", role);
    Role role1 = (Role) redisTemplate.opsForValue().get("role_1");
    System.out.println(role1.getRoleName());
  }
}
