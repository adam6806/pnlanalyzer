package com.github.adam6806.pnlanalyzer.security.role;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "role_id")
    private long id;
    @Column(name = "role")
    private String role;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

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