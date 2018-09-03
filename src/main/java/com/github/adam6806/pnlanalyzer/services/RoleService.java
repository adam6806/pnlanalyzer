package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.entities.Role;

import java.util.List;

public interface RoleService {

    List<Role> findAllRoles();

    Role findRoleByName(String name);
}
