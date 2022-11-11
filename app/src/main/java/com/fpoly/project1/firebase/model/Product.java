package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Product implements Serializable {
    public String __id;
    public String sid;
    public String name;
    public String description;
    public String[] thumbnails;
    public float price;

    public Product() {}

    /**
     *
     * @param __id Firebase product unique ID
     * @param sid Seller ID which owns this product
     * @param name Product display name
     * @param description Product display description
     * @param price Product price
     * @param thumbnails Product thumbnails
     */
    public Product(String __id, String sid, String name, String description, float price, String[] thumbnails) {
        this.__id = __id;
        this.sid = sid;
        this.name = name;
        this.description = description;
        this.price = price;
        this.thumbnails = thumbnails;
    }
}
