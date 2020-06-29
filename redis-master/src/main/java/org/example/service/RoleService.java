package org.example.service;

import org.example.domain.Role;

import java.util.List;

/**
 * @author chenj
 */
public interface RoleService {

    Role  getRole(Long id);
    int  deleteRole(Long id);
    Role insertRole(Role role);
    Role  updateRole(Role role);
    List<Role>  findRoles(String roleName ,String note);
    List<Role> findAll();
}
