package com.fpoly.project1.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Customer {
    public String id;
    public String gid;
    public String avatar_url;
    public String fullname;
    public String birthdate;
    public String emailAddress;
    public String postalAddress;

    public Customer() {}

    public Customer(String id, String gid, String avatar_url, String fullname, String birthdate, String emailAddress, String postalAddress) {
        this.id = id;
        this.gid = gid;
        this.avatar_url = avatar_url;
        this.fullname = fullname;
        this.birthdate = birthdate;
        this.emailAddress = emailAddress;
        this.postalAddress = postalAddress;
    }
}
