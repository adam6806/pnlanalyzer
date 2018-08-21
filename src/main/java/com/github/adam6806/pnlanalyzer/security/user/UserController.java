package com.github.adam6806.pnlanalyzer.security.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserController {

    private final UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
