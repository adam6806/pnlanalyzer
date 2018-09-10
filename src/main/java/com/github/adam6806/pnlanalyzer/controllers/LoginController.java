package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.domain.Invite;
import com.github.adam6806.pnlanalyzer.domain.User;
import com.github.adam6806.pnlanalyzer.services.UserInviteService;
import com.github.adam6806.pnlanalyzer.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.websocket.server.PathParam;

@Controller
public class LoginController {

    private final UserServiceImpl userService;
    private final UserInviteService userInviteService;

    @Autowired
    public LoginController(UserServiceImpl userService, UserInviteService userInviteService) {
        this.userService = userService;
        this.userInviteService = userInviteService;
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
        Invite invite = userInviteService.findUserInviteById(inviteId);
        User user = new User()
                .setName(invite.getFirstName())
                .setLastName(invite.getLastName())
                .setEmail(invite.getEmail());
        modelAndView.addObject("user", user);
        modelAndView.addObject("inviteId", inviteId);
        modelAndView.setViewName("invite");
        return modelAndView;
    }

    @RequestMapping(value = "/invite/accept", method = RequestMethod.POST)
    @Transactional
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
            userInviteService.registerUser(user, inviteId);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }


}