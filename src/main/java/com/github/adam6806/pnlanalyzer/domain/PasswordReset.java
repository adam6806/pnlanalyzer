package com.github.adam6806.pnlanalyzer.domain;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Entity
@Table(name = "pw_reset")
public class PasswordReset {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pw_reset_id")
    private UUID id;

    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}