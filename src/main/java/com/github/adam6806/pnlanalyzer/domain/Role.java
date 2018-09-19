package com.github.adam6806.pnlanalyzer.domain;

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

    @OrderBy
    @Column(name = "role", unique = true)
    private String role;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "roles")
    private Set<User> users;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            },
            mappedBy = "roles")
    private Set<Invite> invites;

    public long getId() {
        return id;
    }

    public Role setId(long id) {
        this.id = id;
        return this;
    }

    public String getRole() {
        return role;
    }

    public Set<User> getUsers() {
        return users;
    }

    public Role setUsers(Set<User> users) {
        this.users = users;
        return this;
    }

    public Set<Invite> getInvites() {
        return invites;
    }

    public Role setInvites(Set<Invite> invites) {
        this.invites = invites;
        return this;
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