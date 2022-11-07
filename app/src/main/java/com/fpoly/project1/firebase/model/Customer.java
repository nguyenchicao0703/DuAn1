package com.fpoly.project1.firebase.model;

import android.net.Uri;

import java.io.Serializable;

public class Customer implements Serializable {
    public int id;
    public String gid;
    public Uri avatar_url;
    public String fullname;
    public String birthdate;
    public String emailAddress;
    public String postalAddress;

    public Customer() {}

    public Customer(int id, String gid, Uri avatar_url, String fullname, String birthdate, String emailAddress, String postalAddress) {
        this.id = id;
        this.gid = gid;
        this.avatar_url = avatar_url;
        this.fullname = fullname;
        this.birthdate = birthdate;
        this.emailAddress = emailAddress;
        this.postalAddress = postalAddress;
    }
}
