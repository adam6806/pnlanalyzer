package com.github.adam6806.pnlanalyzer.repositories;

import com.github.adam6806.pnlanalyzer.entities.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserInviteRepository extends JpaRepository<Invite, UUID> {

    Invite findInviteByEmail(String email);
}
