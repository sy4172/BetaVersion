package com.example.betaversion;

public class User {
    private String password;
    private String email;
    private String phone;

    public User () {}
    public User (String password, String email, String phone){
        this.password = password;
        this.email = email;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
