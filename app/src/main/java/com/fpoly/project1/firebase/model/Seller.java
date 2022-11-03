package com.fpoly.project1.firebase.model;

import java.util.ArrayList;

public class Seller {
    public int id;
    public String name;
    public String emailAddress;
    public String postalAddress;
    public String openingTime;
    public String closingTime;
    public ArrayList<Integer> rating;
    public String cre_username;
    public String cre_password;

    public Seller() {}

    public Seller(int id, String name, String emailAddress, String postalAddress, String openingTime, String closingTime, ArrayList<Integer> rating, String cre_username, String cre_password) {
        this.id = id;
        this.name = name;
        this.emailAddress = emailAddress;
        this.postalAddress = postalAddress;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.rating = rating;
        this.cre_username = cre_username;
        this.cre_password = cre_password;
    }
}
