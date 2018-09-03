package com.github.adam6806.pnlanalyzer.services.impl;

import com.github.adam6806.pnlanalyzer.entities.PasswordReset;
import com.github.adam6806.pnlanalyzer.entities.User;
import com.github.adam6806.pnlanalyzer.repositories.PasswordResetRepository;
import com.github.adam6806.pnlanalyzer.repositories.UserRepository;
import com.github.adam6806.pnlanalyzer.services.PasswordResetService;
import com.github.adam6806.pnlanalyzer.services.SendGridEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.UUID;

@Service("passwordResetService")
@Transactional
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetRepository passwordResetRepository;
    private final UserRepository userRepository;
    private final SendGridEmailService sendGridEmailService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetServiceImpl(PasswordResetRepository passwordResetRepository, SendGridEmailService sendGridEmailService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordResetRepository = passwordResetRepository;
        this.sendGridEmailService = sendGridEmailService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PasswordReset findPasswordResetByEmail(String email) {
        return passwordResetRepository.findPasswordResetByEmail(email);
    }

    @Override
    public void createAndSendPasswordReset(PasswordReset passwordReset, User user) {
        passwordResetRepository.save(passwordReset);
        sendGridEmailService.sendPasswordReset(passwordReset, user);
    }

    @Override
    public PasswordReset findPasswordResetById(String passwordResetId) {
        return passwordResetRepository.getOne(UUID.fromString(passwordResetId));
    }

    @Override
    public void completePasswordReset(PasswordReset passwordReset, User user, String newPassword) {
        passwordResetRepository.delete(passwordReset);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
