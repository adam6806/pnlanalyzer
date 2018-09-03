package com.github.adam6806.pnlanalyzer.controllers;

import com.github.adam6806.pnlanalyzer.entities.PasswordReset;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.forms.PasswordResetForm;
import com.github.adam6806.pnlanalyzer.services.PasswordResetService;
import com.github.adam6806.pnlanalyzer.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

@Controller
public class PasswordResetController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @Autowired
    public PasswordResetController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
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
        PasswordReset passwordResetExists = passwordResetService.findPasswordResetByEmail(passwordReset.getEmail());
        User user = userService.findUserByEmail(passwordReset.getEmail());
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
            passwordResetService.createAndSendPasswordReset(passwordReset, user);
            modelAndView.addObject("successMessage", "A password reset email has been sent to you. Please follow the instructions in the email to reset your password.");
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/passwordreset", method = RequestMethod.GET)
    public ModelAndView getPasswordResetForm(@PathParam("resetId") String resetId) {
        ModelAndView modelAndView = new ModelAndView();
        PasswordReset passwordReset = passwordResetService.findPasswordResetById(resetId);
        if (passwordReset == null) {
            modelAndView.addObject("errorMessage", "The password reset link has expired. Please create a new password reset request.");
            modelAndView.setViewName("login");
        } else {
            PasswordResetForm passwordResetForm = new PasswordResetForm();
            passwordResetForm.setResetId(resetId);
            modelAndView.addObject("passwordResetForm", passwordResetForm);
            modelAndView.setViewName("passwordreset");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/passwordreset", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Valid PasswordResetForm passwordResetForm, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        PasswordReset passwordReset = passwordResetService.findPasswordResetById(passwordResetForm.getResetId());
        User user = userService.findUserByEmail(passwordReset.getEmail());
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
            modelAndView.addObject("resetId", passwordResetForm.getResetId());
            modelAndView.setViewName("passwordreset");
        } else {
            passwordResetService.completePasswordReset(passwordReset, user, passwordResetForm.getPassword());
            modelAndView.addObject("successMessage", "Your password was successfully reset!");
            modelAndView.setViewName("login");
        }
        return modelAndView;
    }
}
