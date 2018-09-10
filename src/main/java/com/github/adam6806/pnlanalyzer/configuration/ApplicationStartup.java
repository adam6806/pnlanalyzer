package com.github.adam6806.pnlanalyzer.configuration;

import com.github.adam6806.pnlanalyzer.domain.Invite;
import com.github.adam6806.pnlanalyzer.domain.Role;
import com.github.adam6806.pnlanalyzer.domain.User;
import com.github.adam6806.pnlanalyzer.repositories.RoleRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserInviteRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
import com.github.adam6806.pnlanalyzer.services.SendGridEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final UserInviteRepository userInviteRepository;
    private final SendGridEmailService sendGridEmailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Autowired
    public ApplicationStartup(UserInviteRepository userInviteRepository, SendGridEmailService sendGridEmailService, RoleRepository roleRepository, UserRepository userRepository) {
        this.userInviteRepository = userInviteRepository;
        this.sendGridEmailService = sendGridEmailService;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * This event is executed as late as conceivably possible to indicate that
     * the application is ready to service requests.
     */
    @Override
    @Transactional
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        List<Role> all = roleRepository.findAll();
        if (all.isEmpty()) {
            all.add(new Role().setRole(Role.Roles.ROLE_ADMIN.name()));
            all.add(new Role().setRole(Role.Roles.ROLE_USER.name()));
            all = roleRepository.saveAll(all);
        }

        User admin = userRepository.findByEmail("asmith0935@gmail.com");
        Invite adminInvite = userInviteRepository.findInviteByEmail("asmith0935@gmail.com");
        if (admin == null && adminInvite == null) {
            Invite invite = new Invite().setAdminFirstName("Server").setAdminLastName("Initialized").setRoles(new HashSet<>(all)).setEmail("asmith0935@gmail.com").setFirstName("Adam").setLastName("Smith");
            Invite savedInvite = userInviteRepository.save(invite);
            sendGridEmailService.sendUserInvite(savedInvite);
        }
    }
}