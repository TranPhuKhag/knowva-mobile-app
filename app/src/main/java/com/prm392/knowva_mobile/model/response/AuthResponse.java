package com.prm392.knowva_mobile.model.response;

import com.prm392.knowva_mobile.model.User;

public class AuthResponse {
    private String token;
    private User user;
    // private String username;
    // private String email;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}