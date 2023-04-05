package com.kurata.booking.data.model;

public class User {
    private String id;
    private String email;
    private String fullName;
    private String avatar;
    private String mobile;
    private String address;
    private Boolean status;
    private String role;

    public User() {

    }

    public User(String id, String email, String fullName, String avatar, String mobile, String address, Boolean status, String role) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.avatar = avatar;
        this.mobile = mobile;
        this.address = address;
        this.status = status;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getfullName() {
        return fullName;
    }

    public void setfullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
