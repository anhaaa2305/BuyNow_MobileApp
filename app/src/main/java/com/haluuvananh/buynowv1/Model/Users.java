package com.haluuvananh.buynowv1.Model;

public class Users {
    private String name, phone, address, password;

    public Users(){

    }

    public Users(String name, String phone, String address, String password) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
