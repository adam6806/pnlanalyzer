package com.github.adam6806.pnlanalyzer.security.user;

public interface UserService {
    User findUserByEmail(String email);

    void saveUser(User user);
}