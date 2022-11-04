package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Seller implements Serializable {
    public int id;
    public int avatar_id;
    public String name;
    public String emailAddress;
    public String phoneNumber;
    public String postalAddress;
    public String openingTime;
    public String closingTime;
    public String password;

    public Seller() {}

    public Seller(int id, int avatar_id, String name, String emailAddress, String phoneNumber, String postalAddress, String openingTime, String closingTime, String password) {
        this.id = id;
        this.avatar_id = avatar_id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.postalAddress = postalAddress;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.password = password;
    }
}
