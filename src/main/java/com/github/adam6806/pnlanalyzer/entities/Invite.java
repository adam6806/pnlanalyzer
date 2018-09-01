package com.github.adam6806.pnlanalyzer.entities;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "invite")
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "invite_id")
    private UUID id;

    @Column(name = "email")
    @Email(message = "*Please provide a valid Email")
    @NotEmpty(message = "*Please provide an email")
    private String email;

    @Column(name = "first_name")
    @NotEmpty(message = "*Please provide the first name")
    private String firstName;

    @Column(name = "last_name")
    @NotEmpty(message = "*Please provide the last name")
    private String lastName;

    @Column(name = "admin_first_name")
    private String adminFirstName;

    @Column(name = "admin_last_name")
    private String adminLastName;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(name = "invite_role", joinColumns = @JoinColumn(name = "invite_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public Invite setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public Invite setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Invite setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getAdminFirstName() {
        return adminFirstName;
    }

    public Invite setAdminFirstName(String adminFirstName) {
        this.adminFirstName = adminFirstName;
        return this;
    }

    public String getAdminLastName() {
        return adminLastName;
    }

    public Invite setAdminLastName(String adminLastName) {
        this.adminLastName = adminLastName;
        return this;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Invite setRoles(Set<Role> roles) {
        this.roles = roles;
        return this;
    }
}