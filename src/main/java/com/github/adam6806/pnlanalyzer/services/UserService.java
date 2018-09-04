package com.github.adam6806.pnlanalyzer.services;

import com.github.adam6806.pnlanalyzer.entities.User;

import java.util.List;

public interface UserService {
    User findUserByEmail(String email);

    User saveNewUser(User user);

    User saveUser(User user);

    List<User> findAllUsers();

    void deleteUserById(Long id);

    User findUserById(Long id);

    boolean passwordMatches(User user, String password);

    User updatePassword(User user, String password);

    void updateUser(User existingUser, User newUserDetails, String[] roles);
}