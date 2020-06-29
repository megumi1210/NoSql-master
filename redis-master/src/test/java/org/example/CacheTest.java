package org.example;

import org.example.config.RootConfig;
import org.example.domain.Role;
import org.example.service.RoleService;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


/**
 * redis 结合数据库使用注解 cache 测试
 * 使用缓存测试案例
 */
public class CacheTest {

    private AnnotationConfigApplicationContext cxt =
                         new AnnotationConfigApplicationContext(RootConfig.class);

    RoleService roleService = cxt.getBean(RoleService.class);

    /** 测试用的案例 */
    private Role data = new Role(1L, "role_name_1", "note_1");

    @Test
    public void test01(){
        roleService.insertRole(data);
        Role role = roleService.getRole(data.getId());
        role.setNote("role_note_1_update");
        roleService.updateRole(role);
    }
}
