package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.Role;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.repositories.RoleRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
import com.github.adam6806.pnlanalyzer.services.RoleService;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.transaction.Transactional;
import java.util.List;

@Controller
public class RoleController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;

    @Autowired
    public RoleController(UserRepository userRepository, RoleRepository roleRepository, RoleService roleService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
    }

    @RequestMapping(value = "/admin/edituserrole", method = RequestMethod.GET)
    public ModelAndView getUserManagement(@RequestParam Long userId) {
        User user = userRepository.getOne(userId);
        List<Role> allRoles = roleRepository.findAll();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("user", user);
        modelAndView.addObject("roles", allRoles);
        modelAndView.setViewName("admin/edituserrole");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/edituserrole", method = RequestMethod.POST)
    @Transactional
    public ModelAndView saveUserRoles(@RequestParam Long userId, @RequestParam String roleSelect, RedirectAttributes redirectAttributes) {
        User user = userRepository.getOne(userId);
        user.setRoles(roleService.getRolesForRole(roleSelect));
        userRepository.save(user);
        ModelAndView modelAndView = new ModelAndView();
        redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("User role updated successfully."));
        modelAndView.setViewName("redirect:usermanagement");
        return modelAndView;
    }
}
