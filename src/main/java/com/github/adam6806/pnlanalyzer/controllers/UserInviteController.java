package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.domain.Invite;
import com.github.adam6806.pnlanalyzer.domain.User;
import com.github.adam6806.pnlanalyzer.services.RoleService;
import com.github.adam6806.pnlanalyzer.services.UserInviteService;
import com.github.adam6806.pnlanalyzer.services.UserService;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.UUID;

@Controller
public class UserInviteController {

    private final UserInviteService userInviteService;
    private final RoleService roleService;
    private final UserService userService;

    @Autowired
    public UserInviteController(UserInviteService userInviteService, RoleService roleService, UserService userService) {
        this.userInviteService = userInviteService;
        this.roleService = roleService;
        this.userService = userService;
    }

    @RequestMapping(value = "/admin/invite", method = RequestMethod.GET)
    public ModelAndView getInvites(@ModelAttribute Message message) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("invites", userInviteService.findAllUserInvites());
        modelAndView.addObject("roles", roleService.findAllRoles());
        modelAndView.addObject("message", message);
        modelAndView.setViewName("admin/invite");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite/add", method = RequestMethod.GET)
    public ModelAndView addInvite() {
        ModelAndView modelAndView = new ModelAndView();
        Invite invite = new Invite();
        modelAndView.addObject("invite", invite);
        modelAndView.addObject("allRoles", roleService.findAllRoles());
        modelAndView.setViewName("admin/invite/add");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite/add", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid Invite invite, BindingResult bindingResult, @RequestParam String[] selectedRoles, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        Invite inviteExists = userInviteService.findUserInviteByEmail(invite.getEmail());
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
            modelAndView.addObject("roles", roleService.findAllRoles());
            modelAndView.setViewName("admin/invite/add");
        } else {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User admin = userService.findUserByEmail(auth.getName());
            userInviteService.createAndSendUserInvite(invite, admin, selectedRoles);
            redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Invite has been created and sent."));
            modelAndView.setViewName("redirect:/admin/invite");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/invite/delete", method = RequestMethod.POST)
    public ModelAndView getUserManagement(@RequestParam UUID inviteId, RedirectAttributes redirectAttributes) {
        userInviteService.deleteUserInviteById(inviteId);
        ModelAndView modelAndView = new ModelAndView();
        redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Invite was deleted successfully."));
        modelAndView.setViewName("redirect:/admin/invite");
        return modelAndView;
    }
}
