package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
