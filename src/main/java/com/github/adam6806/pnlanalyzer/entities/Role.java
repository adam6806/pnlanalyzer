package com.github.adam6806.pnlanalyzer.entities;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "role")
public class Role {

    public Role setRole(String role) {
        this.role = role;
        return this;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private long id;

    @Column(name = "role")
    private String role;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public enum Roles {ROLE_ADMIN, ROLE_USER}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Role) {
            return ((Role) obj).getRole().equals(this.getRole());
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return role;
    }
}