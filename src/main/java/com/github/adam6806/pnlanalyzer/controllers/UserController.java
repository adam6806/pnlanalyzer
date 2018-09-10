package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.domain.Role;
import com.github.adam6806.pnlanalyzer.domain.User;
import com.github.adam6806.pnlanalyzer.forms.PasswordUpdateForm;
import com.github.adam6806.pnlanalyzer.forms.ProfileUpdateForm;
import com.github.adam6806.pnlanalyzer.services.RoleService;
import com.github.adam6806.pnlanalyzer.services.UserService;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.apache.logging.log4j.util.Strings;
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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @RequestMapping(value = "/admin/usermanagement", method = RequestMethod.GET)
    public ModelAndView getUserManagement(@ModelAttribute Message message) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", userService.findAllUsers());
        modelAndView.setViewName("admin/usermanagement");
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value = "/admin/usermanagement", method = RequestMethod.POST)
    public ModelAndView getUserManagement(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        userService.deleteUserById(userId);
        ModelAndView modelAndView = new ModelAndView();
        redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("User was deleted successfully."));
        modelAndView.setViewName("redirect:usermanagement");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public ModelAndView getUserProfile(@ModelAttribute Message message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        ProfileUpdateForm profileUpdateForm = new ProfileUpdateForm();
        profileUpdateForm.setEmail(user.getEmail());
        profileUpdateForm.setFirstName(user.getName());
        profileUpdateForm.setLastName(user.getLastName());
        profileUpdateForm.setUserId(user.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", message);
        modelAndView.addObject("profileUpdateForm", profileUpdateForm);
        modelAndView.setViewName("profile");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(@Valid ProfileUpdateForm profileUpdateForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("profile");
        } else {
            User user = userService.findUserById(profileUpdateForm.getUserId());
            user.setName(profileUpdateForm.getFirstName());
            user.setLastName(profileUpdateForm.getLastName());
            user.setEmail(profileUpdateForm.getEmail());
            userService.saveUser(user);
            redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Your profile was successfully updated."));
        }
        modelAndView.setViewName("redirect:/profile");
        return modelAndView;
    }

    @RequestMapping(value = "/profile/updatepassword", method = RequestMethod.GET)
    public ModelAndView getPasswordUpdateForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm();
        passwordUpdateForm.setUserId(user.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("passwordUpdateForm", passwordUpdateForm);
        modelAndView.setViewName("profile/updatepassword");
        return modelAndView;
    }

    @RequestMapping(value = "/profile/updatepassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid PasswordUpdateForm passwordUpdateForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        ModelAndView modelAndView = new ModelAndView();

        User user = userService.findUserById(passwordUpdateForm.getUserId());
        if (!userService.passwordMatches(user, passwordUpdateForm.getPassword())) {
            bindingResult.rejectValue("password", "error.passwordUpdateForm", "Password does not match existing passwor.");
        }

        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("profile/updatepassword");
        } else {
            userService.updatePassword(user, passwordUpdateForm.getPassword2());
            redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Your profile was successfully updated."));
            modelAndView.setViewName("redirect:/profile");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/edituser", method = RequestMethod.GET)
    public ModelAndView editUserForm(@RequestParam Long userId) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userService.findUserById(userId);
        modelAndView.addObject("user", user);
        List<Role> allRoles = roleService.findAllRoles();
        allRoles.sort(Comparator.comparing(Role::getRole));
        modelAndView.addObject("allRoles", new HashSet<>(allRoles));
        modelAndView.setViewName("admin/edituser");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/edituser", method = RequestMethod.POST)
    public ModelAndView editUser(User user, @RequestParam String[] selectedRoles, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        User existing = userService.findUserById(user.getId());
        if (Strings.isNotBlank(user.getPassword())) {
            userService.updatePassword(existing, user.getPassword());
        }
        Set<Role> newRoles = new HashSet<>();
        for (String selectedRole : selectedRoles) {
            Role role = roleService.findRoleByName(selectedRole);
            newRoles.add(role);
        }
        existing.setRoles(newRoles);
        existing.setName(user.getName()).setLastName(user.getLastName()).setEmail(user.getEmail());
        userService.saveUser(existing);
        redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("User was successfully updated"));
        modelAndView.setViewName("redirect:usermanagement");
        return modelAndView;
    }

}
