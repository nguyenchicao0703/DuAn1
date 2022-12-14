package com.fpoly.project1.firebase.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Customer {
    public String __id;
    public String gid;
    public String fid;
    public String avatarUrl;
    public String fullName;
    public String birthDate;
    public String emailAddress;
    public String phoneNumber;
    public String postalAddress;
    public List<String> favoriteIds;

    public Customer() {
    }

    /**
     * @param __id          Firebase unique ID
     * @param gid           Google account unique ID
     * @param fid           Facebook account unique ID
     * @param avatarUrl     Account public avatar url, use Glide to load the image
     * @param fullName      Account display name
     * @param birthDate     Account birth date, optionally set by user via account settings screen
     * @param emailAddress  Account email address
     * @param phoneNumber   Account phone number
     * @param postalAddress Account postal address, set by user via account creation screen
     * @param favoriteIds   Account list of favorite products (by product ID)
     */
    public Customer(String __id, String gid, String fid, String avatarUrl, String fullName, String birthDate, String emailAddress, String phoneNumber, String postalAddress, List<String> favoriteIds) {
        this.__id = __id;
        this.gid = gid;
        this.fid = fid;
        this.avatarUrl = avatarUrl;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.postalAddress = postalAddress;
        this.favoriteIds = favoriteIds;
    }
}
