package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Seller implements Serializable {
    public String __id;
    public String name;
    public String avatarUrl;
    public String emailAddress;
    public String phoneNumber;
    public String postalAddress;
    public String openingTime;
    public String closingTime;
    public String[] featuredImages;

    public Seller() {
    }

    /**
     * @param __id           Firebase seller unique ID
     * @param name           Seller display name
     * @param avatarUrl      Seller display avatar url
     * @param emailAddress   Seller contact email address
     * @param phoneNumber    Seller contact phone number
     * @param postalAddress  Seller postal address
     * @param openingTime    Seller opening time
     * @param closingTime    Seller closing time
     * @param featuredImages Seller featured images as list of urls
     */
    public Seller(String __id, String name, String avatarUrl, String emailAddress, String phoneNumber, String postalAddress, String openingTime, String closingTime, String[] featuredImages) {
        this.__id = __id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.postalAddress = postalAddress;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.featuredImages = featuredImages;
    }
}
