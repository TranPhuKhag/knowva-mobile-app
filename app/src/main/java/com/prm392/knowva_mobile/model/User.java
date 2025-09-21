package com.prm392.knowva_mobile.model;

public class User {
    private String UserID;
    private String Username;
    private String Email;
    private String PhoneNumber;
    private String Address;
    private String Role;

    public User(String userID, String username, String email, String phoneNumber, String address, String role) {
        UserID = userID;
        Username = username;
        Email = email;
        PhoneNumber = phoneNumber;
        Address = address;
        Role = role;
    }

    public User() {
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getRole() {
        return Role;
    }

    public void setRole(String role) {
        Role = role;
    }
}
