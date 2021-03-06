package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.domain.PasswordReset;
import com.github.adam6806.pnlanalyzer.domain.User;

public interface PasswordResetService {

    PasswordReset findPasswordResetByEmail(String email);

    void createAndSendPasswordReset(PasswordReset passwordReset, User user);

    PasswordReset findPasswordResetById(String passwordResetId);

    void completePasswordReset(PasswordReset passwordReset, User user, String newPassword);
}
