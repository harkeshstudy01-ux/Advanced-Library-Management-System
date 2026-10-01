package com.library.controller;

import com.library.model.User;
import com.library.service.AuthenticationService;

public class LoginController {

    private final AuthenticationService authenticationService;

    public LoginController() {
        this.authenticationService = new AuthenticationService();
    }

    public User login(String username, String password) {
        return authenticationService.login(username, password);
    }

    public boolean usernameExists(String username) {
        return authenticationService.usernameExists(username);
    }
}