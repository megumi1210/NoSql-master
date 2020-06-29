package org.example.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.domain.Role;

import java.util.List;

/**
 * @author chenj
 */
@Mapper
public interface RoleMapper {

    int insertRole(Role role);
    int deleteRole(Long id);
    int updateRole(Role role);
    Role getRole(Long id);
    List<Role> findAll();
    List<Role> findRoles(@Param("roleName") String roleName ,@Param("note") String note );
}
