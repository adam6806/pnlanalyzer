package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.entities.User;

public interface UserService {
    User findUserByEmail(String email);

    void saveUser(User user);
}