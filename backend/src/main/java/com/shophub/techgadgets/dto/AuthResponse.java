package com.shophub.techgadgets.dto;

public class AuthResponse {
    private Integer id;
    private String token;
    private String email;
    private String fullName;
    private String phone;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(Integer id, String token, String email, String fullName, String phone, String role) {
        this.id = id;
        this.token = token;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
