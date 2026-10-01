package com.library.service;

import com.library.model.User;
import com.library.repository.UserRepository;

import java.util.Optional;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public User login(String username, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(
                    "Username cannot be empty."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        Optional<User> userOptional =
                userRepository.findByUsername(username.trim());

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        User user = userOptional.get();

        // Check account status from the database.
        if (!user.isActive()) {

            throw new IllegalStateException(
                    "User account is "
                            + user.getAccountStatus()
                            + "."
            );
        }

        // Compare the entered password with the value
        // stored in the password_hash column.
        if (!password.equals(user.getPasswordHash())) {

            throw new IllegalArgumentException(
                    "Invalid username or password."
            );
        }

        return user;
    }

    public boolean usernameExists(String username) {

        if (username == null || username.isBlank()) {
            return false;
        }

        return userRepository.existsByUsername(
                username.trim()
        );
    }
}

