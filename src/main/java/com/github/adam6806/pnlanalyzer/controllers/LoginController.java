package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.Invite;
import com.github.adam6806.pnlanalyzer.entities.Role;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.repositories.UserInviteRepository;
import com.github.adam6806.pnlanalyzer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Controller
public class LoginController {

    private final UserService userService;
    private final UserInviteRepository userInviteRepository;

    @Autowired
    public LoginController(UserService userService, UserInviteRepository userInviteRepository) {
        this.userService = userService;
        this.userInviteRepository = userInviteRepository;
    }

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/home", method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("adminMessage", "Content Available Only for Users with Admin Role");
        modelAndView.setViewName("admin/home");
        return modelAndView;
    }

    @RequestMapping(value = "/invite", method = RequestMethod.GET)
    public ModelAndView invite(@PathParam("inviteId") String inviteId) {
        ModelAndView modelAndView = new ModelAndView();
        Invite invite = userInviteRepository.getOne(UUID.fromString(inviteId));
        User user = new User();
        user.setName(invite.getFirstName());
        user.setLastName(invite.getLastName());
        user.setEmail(invite.getEmail());
        modelAndView.addObject("user", user);
        modelAndView.addObject("inviteId", inviteId);
        modelAndView.setViewName("invite");
        return modelAndView;
    }

    @RequestMapping(value = "/invite/accept", method = RequestMethod.POST)
    public ModelAndView createUserFromInvite(@Valid User user, BindingResult bindingResult, @RequestParam String inviteId) {
        ModelAndView modelAndView = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult
                    .rejectValue("email", "error.user",
                            "There is already a user registered with the email provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("inviteId", inviteId);
            modelAndView.setViewName("invite");
        } else {
            Invite invite = userInviteRepository.getOne(UUID.fromString(inviteId));
            Set<Role> roles = new HashSet<>(invite.getRoles());
            user.setRoles(roles);
            userService.saveUser(user);
            userInviteRepository.delete(invite);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }


}