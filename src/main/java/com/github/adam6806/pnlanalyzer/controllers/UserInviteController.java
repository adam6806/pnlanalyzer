package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.Invite;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.repositories.RoleRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserInviteRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
import com.github.adam6806.pnlanalyzer.services.RoleService;
import com.github.adam6806.pnlanalyzer.services.SendGridEmailService;
import com.github.adam6806.pnlanalyzer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
public class UserInviteController {

    private final UserInviteRepository userInviteRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final SendGridEmailService sendGridEmailService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public UserInviteController(UserInviteRepository userInviteRepository, RoleRepository roleRepository, RoleService roleService, SendGridEmailService sendGridEmailService, UserRepository userRepository, UserService userService) {
        this.userInviteRepository = userInviteRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.sendGridEmailService = sendGridEmailService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/admin/invite", method = RequestMethod.GET)
    public ModelAndView getInvites() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("invites", userInviteRepository.findAll());
        modelAndView.addObject("roles", roleRepository.findAll());
        modelAndView.setViewName("admin/invite");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite/add", method = RequestMethod.GET)
    public ModelAndView addInvite() {
        ModelAndView modelAndView = new ModelAndView();
        Invite invite = new Invite();
        modelAndView.addObject("invite", invite);
        modelAndView.addObject("roles", roleRepository.findAll());
        modelAndView.setViewName("admin/invite/add");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite/add", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid Invite invite, BindingResult bindingResult, @RequestParam String roleSelect) {
        ModelAndView modelAndView = new ModelAndView();
        Invite inviteExists = userInviteRepository.findInviteByEmail(invite.getEmail());
        if (inviteExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "*An invite already exists for the email provided");
        }
        User userExists = userService.findUserByEmail(invite.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "*There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("roles", roleRepository.findAll());
            modelAndView.setViewName("/admin/invite/add");
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User admin = userRepository.findByEmail(auth.getName());
            invite.setAdminFirstName(admin.getName());
            invite.setAdminLastName(admin.getLastName());
            invite.setRoles(roleService.getRolesForRole(roleSelect));
            Invite savedInvite = userInviteRepository.save(invite);
            sendGridEmailService.sendUserInvite(savedInvite);
            List<Invite> all = userInviteRepository.findAll();
            modelAndView.addObject("invites", all);
            modelAndView.setViewName("/admin/invite");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite/delete", method = RequestMethod.POST)
    public ModelAndView getUserManagement(@RequestParam UUID inviteId) {
        userInviteRepository.deleteById(inviteId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:");
        return modelAndView;
    }
}
