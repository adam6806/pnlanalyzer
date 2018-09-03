package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.Role;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.forms.PasswordUpdateForm;
import com.github.adam6806.pnlanalyzer.forms.ProfileUpdateForm;
import com.github.adam6806.pnlanalyzer.repositories.RoleRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
import com.github.adam6806.pnlanalyzer.services.UserServiceImpl;
import com.github.adam6806.pnlanalyzer.utility.Message;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserServiceImpl userService;

    @Autowired
    private UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, UserServiceImpl userService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userService = userService;
    }

    @RequestMapping(value = "/admin/usermanagement", method = RequestMethod.GET)
    public ModelAndView getUserManagement(@ModelAttribute Message message) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", userRepository.findAll());
        modelAndView.setViewName("admin/usermanagement");
        modelAndView.addObject("message", message);
        return modelAndView;
    }

    @RequestMapping(value = "/admin/usermanagement", method = RequestMethod.POST)
    public ModelAndView getUserManagement(@RequestParam Long userId, RedirectAttributes redirectAttributes) {
        userRepository.deleteById(userId);
        ModelAndView modelAndView = new ModelAndView();
        redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("User was deleted successfully."));
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
            userRepository.save(user);
            modelAndView.addObject("message", new Message().setSuccessMessage("Your profile was successfully updated."));
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
    public ModelAndView updatePassword(@Valid PasswordUpdateForm passwordUpdateForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {

        ModelAndView modelAndView = new ModelAndView();

        if (bindingResult.hasErrors()) {
            modelAndView.addObject("passwordUpdateForm", passwordUpdateForm);
            modelAndView.setViewName("profile/updatepassword");
        } else {
            User user = userRepository.getOne(passwordUpdateForm.getUserId());
            user.setPassword(passwordEncoder.encode(passwordUpdateForm.getPassword2()));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute(new Message().setSuccessMessage("Your profile was successfully updated."));
            modelAndView.setViewName("redirect:");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/admin/edituser", method = RequestMethod.GET)
    public ModelAndView editUserForm(@RequestParam Long userId) {
        ModelAndView modelAndView = new ModelAndView();
        User user = userRepository.getOne(userId);
        modelAndView.addObject("user", user);
        List<Role> allRoles = roleRepository.findAll();
        allRoles.sort(Comparator.comparing(Role::getRole));
        modelAndView.addObject("allRoles", new HashSet<>(allRoles));
        modelAndView.setViewName("admin/edituser");
        return modelAndView;
    }

    @RequestMapping(value = "/admin/edituser", method = RequestMethod.POST)
    public ModelAndView editUser(User user, @RequestParam String[] selectedRoles, RedirectAttributes redirectAttributes) {
        ModelAndView modelAndView = new ModelAndView();
        User existing = userRepository.getOne(user.getId());
        if (Strings.isNotBlank(user.getPassword())) {
            existing.setPassword(user.getPassword());
        }
        Set<Role> newRoles = new HashSet<>();
        for (String selectedRole : selectedRoles) {
            Role role = roleRepository.findByRole(selectedRole);
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
