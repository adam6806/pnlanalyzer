package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.entities.Invite;
import com.github.adam6806.pnlanalyzer.entities.User;

import java.util.List;
import java.util.UUID;

public interface UserInviteService {
    Invite findUserInviteById(String inviteId);

    Invite findUserInviteByEmail(String email);

    List<Invite> findAllUserInvites();

    void registerUser(User user, String inviteId);

    void createAndSendUserInvite(Invite invite, User admin, String[] selectedRoles);

    void deleteUserInviteById(UUID userInviteId);
}
