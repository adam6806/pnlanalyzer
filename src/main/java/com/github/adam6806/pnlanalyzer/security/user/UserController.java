package com.github.adam6806.pnlanalyzer.security.user;

import com.github.adam6806.pnlanalyzer.SendGridEmailService;
import com.github.adam6806.pnlanalyzer.security.role.RoleRepository;
import com.github.adam6806.pnlanalyzer.security.role.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserInviteRepository userInviteRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final SendGridEmailService sendGridEmailService;

    @Autowired
    private UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, UserInviteRepository userInviteRepository, RoleRepository roleRepository, RoleService roleService, SendGridEmailService sendGridEmailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userInviteRepository = userInviteRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.sendGridEmailService = sendGridEmailService;
    }

    @RequestMapping(value = "/admin/usermanagement", method = RequestMethod.GET)
    public ModelAndView getUserManagement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", userRepository.findAll());
        modelAndView.setViewName("admin/usermanagement");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/usermanagement", method = RequestMethod.POST)
    public ModelAndView getUserManagement(@RequestParam Long userId) {
        userRepository.deleteById(userId);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:usermanagement");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView getUserProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("profile");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(@RequestParam Long userId, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        User user = userRepository.getOne(userId);
        user.setName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        User save = userRepository.save(user);
        modelAndView.addObject("user", save);
        modelAndView.addObject("successMessage", "Your profile was successfully updated.");
        return modelAndView;
    }

    @RequestMapping(value = "/profile/updatepassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@RequestParam Long userId, @RequestParam String oldPassword, @RequestParam String newPassword) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("profile");
        User user = userRepository.getOne(userId);

        if (passwordEncoder.matches(oldPassword, user.getPassword())) {
            user.setPassword(passwordEncoder.encode(newPassword));
            User save = userRepository.save(user);
            modelAndView.addObject("user", save);
            modelAndView.addObject("successMessage", "Your password was successfully updated.");
        } else {
            modelAndView.addObject("errorMessage", "Your old password was incorrect.");
        }

        return modelAndView;
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
                            "An invite already exists for the email provided");
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
