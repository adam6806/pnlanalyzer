package com.github.adam6806.pnlanalyzer.services.impl;

import com.github.adam6806.pnlanalyzer.entities.Invite;
import com.github.adam6806.pnlanalyzer.entities.Role;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.repositories.UserInviteRepository;
import com.github.adam6806.pnlanalyzer.services.RoleService;
import com.github.adam6806.pnlanalyzer.services.SendGridEmailService;
import com.github.adam6806.pnlanalyzer.services.UserInviteService;
import com.github.adam6806.pnlanalyzer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service("userInviteService")
@Transactional
public class UserInviteServiceImpl implements UserInviteService {

    private final UserInviteRepository userInviteRepository;
    private final UserService userService;
    private final RoleService roleService;
    private final SendGridEmailService sendGridEmailService;

    @Autowired
    public UserInviteServiceImpl(UserInviteRepository userInviteRepository, UserService userService, RoleService roleService, SendGridEmailService sendGridEmailService) {
        this.userInviteRepository = userInviteRepository;
        this.userService = userService;
        this.roleService = roleService;
        this.sendGridEmailService = sendGridEmailService;
    }

    @Override
    public Invite findUserInviteById(String inviteId) {
        return userInviteRepository.getOne(UUID.fromString(inviteId));
    }

    @Override
    public Invite findUserInviteByEmail(String email) {
        return userInviteRepository.findInviteByEmail(email);
    }

    @Override
    public List<Invite> findAllUserInvites() {
        return userInviteRepository.findAll();
    }

    @Override
    public void registerUser(User user, String inviteId) {
        Invite invite = userInviteRepository.getOne(UUID.fromString(inviteId));
        Set<Role> roles = new HashSet<>(invite.getRoles());
        user.setRoles(roles);
        userService.saveNewUser(user);
        userInviteRepository.delete(invite);
    }

    @Override
    public void createAndSendUserInvite(Invite invite, User admin, String[] selectedRoles) {
        invite.setAdminFirstName(admin.getName());
        invite.setAdminLastName(admin.getLastName());
        Set<Role> newRoles = new HashSet<>();
        for (String selectedRole : selectedRoles) {
            Role role = roleService.findRoleByName(selectedRole);
            newRoles.add(role);
        }
        invite.setRoles(new HashSet<>(newRoles));
        Invite savedInvite = userInviteRepository.save(invite);
        sendGridEmailService.sendUserInvite(savedInvite);
    }

    @Override
    public void deleteUserInviteById(UUID userInviteId) {
        userInviteRepository.deleteById(userInviteId);
    }
}
