package org.example.service.impl;

import org.example.dao.RoleMapper;
import org.example.domain.Role;
import org.example.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * redis 和 数据库整合案例
 * @author chenj
 */
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleMapper roleMapper = null;


    /**
     * 使用 @Cacheable 定义缓存策略
     *  当缓存中有值，则返回缓存数据，否则访问方法得到数据
     *  通过 value 引用缓存管理器 ，通过 key 定义键
     * @param id  角色编号
     * @return 角色
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED ,isolation = Isolation.READ_COMMITTED)
    @Cacheable(value="redisCacheManager",key="'redis_role_'+#id")
    public Role getRole(Long id) {
        return roleMapper.getRole(id);
    }


    /**
     * 使用@CachePut 则表示无论如何都会执行方法 ，最后将方法放入到缓存中
     * 使用在插入数据的地方，则表示保存到数据库后，会同期插入Redis 缓存中
     * @param role  角色对象
     * @return 角色对象（会回填主键）
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
    @CachePut(value="redisCacheManager",key="'reids_role_'+#role.id")
    public Role insertRole(Role role) {
        roleMapper.insertRole(role);
        return role;
    }


    /**
     *  使用 cachePut 表示更新数据库数据的同时，同步更新缓存
     * @param role 角色对象
     * @return 更新后的数据对象
     */
    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED ,propagation = Propagation.REQUIRED,
         rollbackFor = Exception.class)
    @CachePut(value = "redisCacheManager",key="'redis_role_'+#role.id")
    public Role updateRole(Role role) {
        roleMapper.updateRole(role);
        return  role;
    }



    @Override
    @Transactional(isolation =  Isolation.READ_COMMITTED , propagation = Propagation.REQUIRED,
         rollbackFor = Exception.class)
    @CacheEvict(value="redisCacheManager" ,key ="'redis_role_'+#id")
    public int deleteRole(Long id) {
        return  roleMapper.deleteRole(id);
    }

    @Override
    public List<Role> findRoles(String roleName, String note) {
        return roleMapper.findRoles(roleName, note);
    }

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }
}
