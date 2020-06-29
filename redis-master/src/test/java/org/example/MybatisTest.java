package org.example;

import org.apache.ibatis.session.SqlSessionFactory;
import org.example.config.RootConfig;
import org.example.dao.RoleMapper;
import org.example.domain.Role;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MybatisTest {

  private AnnotationConfigApplicationContext ctx =
      new AnnotationConfigApplicationContext(RootConfig.class);

  RoleMapper roleMapper = ctx.getBean(RoleMapper.class);

  /** 测试用的案例 */
  private Role data = new Role(1L, "role_name_1", "note_1");

  @Test
  public void test00() {
    SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) ctx.getBean("sqlSessionFactory");
    RoleMapper roleMapper = sqlSessionFactory.openSession().getMapper(RoleMapper.class);
    roleMapper.insertRole(data);
  }

  @Test
  public void test01() {
    System.out.println(roleMapper.insertRole(data));
  }

  @Test
  public void test02(){
    System.out.println(roleMapper.findAll());
    System.out.println(roleMapper.findRoles("role",null));
  }
}
