package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.forms.PasswordUpdateForm;
import com.github.adam6806.pnlanalyzer.forms.ProfileUpdateForm;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
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

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        ProfileUpdateForm profileUpdateForm = new ProfileUpdateForm();
        profileUpdateForm.setEmail(user.getEmail());
        profileUpdateForm.setFirstName(user.getName());
        profileUpdateForm.setLastName(user.getLastName());
        profileUpdateForm.setUserId(user.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("profileUpdateForm", profileUpdateForm);
        modelAndView.setViewName("profile");
        return modelAndView;
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public ModelAndView updateProfile(@Valid ProfileUpdateForm profileUpdateForm, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("profile");
        } else {
            User user = userRepository.getOne(profileUpdateForm.getUserId());
            user.setName(profileUpdateForm.getFirstName());
            user.setLastName(profileUpdateForm.getLastName());
            user.setEmail(profileUpdateForm.getEmail());
            modelAndView.addObject("successMessage", "Your profile was successfully updated.");
        }
        modelAndView.addObject("profileUpdateForm", profileUpdateForm);
        modelAndView.setViewName("profile");
        return modelAndView;
    }

    @RequestMapping(value = "/profile/updatepassword", method = RequestMethod.GET)
    public ModelAndView getPasswordUpdateForm() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
        PasswordUpdateForm passwordUpdateForm = new PasswordUpdateForm();
        passwordUpdateForm.setUserId(user.getId());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("passwordUpdateForm", passwordUpdateForm);
        modelAndView.setViewName("profile/updatepassword");
        return modelAndView;
    }

    @RequestMapping(value = "/profile/updatepassword", method = RequestMethod.POST)
    public ModelAndView updatePassword(@Valid PasswordUpdateForm passwordUpdateForm, BindingResult bindingResult) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("passwordUpdateForm", passwordUpdateForm);
            modelAndView.setViewName("profile/updatepassword");
        } else {
            User user = userRepository.getOne(passwordUpdateForm.getUserId());
            user.setPassword(passwordEncoder.encode(passwordUpdateForm.getPassword2()));
            modelAndView.addObject("successMessage", "Your profile was successfully updated.");
            modelAndView.setViewName("redirect:");
        }
        return modelAndView;
    }

}
