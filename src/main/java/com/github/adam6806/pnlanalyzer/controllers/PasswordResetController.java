package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.PasswordReset;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.forms.PasswordResetForm;
import com.github.adam6806.pnlanalyzer.repositories.PasswordResetRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
import com.github.adam6806.pnlanalyzer.services.SendGridEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.UUID;

@Controller
public class PasswordResetController {

    private final PasswordResetRepository passwordResetRepository;
    private final SendGridEmailService sendGridEmailService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetController(SendGridEmailService sendGridEmailService, UserRepository userRepository, PasswordResetRepository passwordResetRepository, PasswordEncoder passwordEncoder) {

        this.sendGridEmailService = sendGridEmailService;
        this.userRepository = userRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/passwordreset/request", method = RequestMethod.GET)
    public ModelAndView requestPasswordReset() {
        ModelAndView modelAndView = new ModelAndView();
        PasswordReset passwordReset = new PasswordReset();
        modelAndView.addObject("passwordReset", passwordReset);
        modelAndView.setViewName("passwordreset/request");
        return modelAndView;
    }

    @RequestMapping(value = "/passwordreset/request", method = RequestMethod.POST)
    public ModelAndView addPasswordReset(@Valid PasswordReset passwordReset, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        PasswordReset passwordResetExists = passwordResetRepository.findPasswordResetByEmail(passwordReset.getEmail());
        User user = userRepository.findByEmail(passwordReset.getEmail());
        if (passwordResetExists != null) {
            bindingResult
                    .rejectValue("email", "error.passwordReset",
                            "A password reset request already exists for the email provided. Please check your email. If you can't find the request contact asmith0935@gmail.com or any administrator.");
        } else if (user == null) {
            bindingResult
                    .rejectValue("email", "error.passwordReset",
                            "*This email does not match any users in the system. Please try again. If you believe this message is in error contact asmith0935@gmail.com or any administrator.");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("passwordreset/request");
        } else {
            passwordResetRepository.save(passwordReset);
            sendGridEmailService.sendPasswordReset(passwordReset, user);
            modelAndView.addObject("successMessage", "A password reset email has been sent to you. Please follow the instructions in the email to reset your password.");
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/passwordreset", method = RequestMethod.GET)
    public ModelAndView getPasswordResetForm(@PathParam("resetId") String resetId) {
        ModelAndView modelAndView = new ModelAndView();
        PasswordReset passwordReset = passwordResetRepository.getOne(UUID.fromString(resetId));
        if (passwordReset == null) {
            modelAndView.addObject("errorMessage", "The password reset link has expired. Please create a new password reset request.");
            modelAndView.setViewName("login");
        } else {
            PasswordResetForm passwordResetForm = new PasswordResetForm();
            passwordResetForm.setResetId(resetId);
            modelAndView.addObject("changePassword", passwordResetForm);
            modelAndView.setViewName("passwordreset");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/passwordreset", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid PasswordResetForm passwordResetForm, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        PasswordReset passwordReset = passwordResetRepository.getOne(UUID.fromString(passwordResetForm.getResetId()));
        User user = userRepository.findByEmail(passwordReset.getEmail());
        if (user == null) {
            bindingResult
                    .rejectValue("password", "error.passwordReset",
                            "*The email for this password reset request doesn't exist. If you believe this message is in error contact asmith0935@gmail.com or any administrator.");
        }
        if (passwordReset == null) {
            bindingResult
                    .rejectValue("password", "error.passwordReset",
                            "*The password reset request has expired. If you believe this message is in error contact asmith0935@gmail.com or any administrator.");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("passwordreset");
        } else {
            passwordResetRepository.delete(passwordReset);
            user.setPassword(passwordEncoder.encode(passwordResetForm.getPassword()));
            userRepository.save(user);
            modelAndView.addObject("successMessage", "Your password was successfully reset!");
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }
}
