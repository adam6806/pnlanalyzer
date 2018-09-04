package com.github.adam6806.pnlanalyzer.forms;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class PasswordResetForm {

    @NotEmpty(message = "*Please provide your password")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])[\\w!@#$%^&*]{8,}$", message = "*Your password must be at least 8 characters long and contain a lower case letter, uppercase letter, and a number. Optionally it can also contain one of these special characters !@#$%^&*")
    private String password;

    @NotNull
    private String resetId;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getResetId() {
        return resetId;
    }

    public void setResetId(String resetId) {
        this.resetId = resetId;
    }
}