package com.fpoly.project1.firebase.model;

public class Shipper {
    public String __id;
    public String fullName;
    public String avatarUrl;
    public String birthDate;
    public String emailAddress;
    public String phoneNumber;
    public String postalAddress;
    public String licenseNumber;

    public Shipper() {}

    public Shipper(String __id, String fullName, String avatarUrl, String birthDate, String emailAddress, String phoneNumber, String postalAddress, String licenseNumber) {
        this.__id = __id;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.birthDate = birthDate;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.postalAddress = postalAddress;
        this.licenseNumber = licenseNumber;
    }
}
