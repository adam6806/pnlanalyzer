package com.github.adam6806.pnlanalyzer.forms;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class PasswordUpdateForm {

    @NotEmpty(message = "*Please provide your old password")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$", message = "*Your password must be at least 8 characters long and contain a lower case letter, uppercase letter, and a number.")
    private String password;

    @NotEmpty(message = "*Please provide your new password.")
    private String password2;

    private Long userId;

    @AssertFalse(message = "*Password cannot be the same as your previous password.")
    private boolean isValid() {
        return password.equals(password2);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
