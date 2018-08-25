package com.github.adam6806.pnlanalyzer.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(auth.getName());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("userName", user.getName() + " " + user.getLastName());
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
}
