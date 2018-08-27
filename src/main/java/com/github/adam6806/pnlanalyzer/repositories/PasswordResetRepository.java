package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.entities.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PasswordResetRepository extends JpaRepository<PasswordReset, UUID> {

    PasswordReset findPasswordResetByEmail(String email);
}