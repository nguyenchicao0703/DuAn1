package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Customer implements Serializable {
    public int id;
    public int avatar_id;
    public String fullname;
    public String birthdate;
    public String emailAddress;
    public String postalAddress;
    public String password;

    public Customer() {}

    public Customer(int id, int avatar_id, String fullname, String birthdate, String emailAddress, String postalAddress, String password) {
        this.id = id;
        this.avatar_id = avatar_id;
        this.fullname = fullname;
        this.birthdate = birthdate;
        this.emailAddress = emailAddress;
        this.postalAddress = postalAddress;
        this.password = password;
    }
}
