package com.github.adam6806.pnlanalyzer.security.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Set<Role> getRolesForRole(String roleRequest) {
        List<Role> allRoles = roleRepository.findAll();
        switch (roleRequest) {
            case "ROLE_USER":
                allRoles.removeIf(role -> role.getRole().equals("ROLE_ADMIN"));
                break;
            case "ROLE_GUEST":
                allRoles.removeIf(role -> role.getRole().equals("ROLE_USER"));
                allRoles.removeIf(role -> role.getRole().equals("ROLE_ADMIN"));
        }
        return new HashSet<>(allRoles);
    }
}
