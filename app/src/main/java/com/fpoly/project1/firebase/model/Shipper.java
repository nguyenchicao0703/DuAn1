package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Shipper implements Serializable {
    public int id;
    public int avatar_id;
    public String name;
    public String birthdate;
    public String emailAddress;
    public String phoneNumber;
    public String postalAddress;
    public String licenseNumber;
    public String cre_username;
    public String cre_password;

    public Shipper() {}

    public Shipper(int id, int avatar_id, String name, String birthdate, String emailAddress, String phoneNumber, String postalAddress, String licenseNumber, String cre_username, String cre_password) {
        this.id = id;
        this.avatar_id = avatar_id;
        this.name = name;
        this.birthdate = birthdate;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.postalAddress = postalAddress;
        this.licenseNumber = licenseNumber;
        this.cre_username = cre_username;
        this.cre_password = cre_password;
    }
}
