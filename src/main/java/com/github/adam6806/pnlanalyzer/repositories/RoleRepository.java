package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);

}