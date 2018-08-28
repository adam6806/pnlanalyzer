package com.github.adam6806.pnlanalyzer.forms;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

public class ProfileUpdateForm {

    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;

    @NotEmpty(message = "*Please provide your first name")
    private String firstName;

    @NotEmpty(message = "*Please provide your last name")
    private String lastName;

    private Long userId;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
