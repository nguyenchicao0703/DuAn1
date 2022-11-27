package com.fpoly.project1.firebase.model;

import java.io.Serializable;
import java.util.List;

public class Product implements Serializable {
    public String __id;
    public String sellerId;
    public String categoryId;
    public String name;
    public String description;
    public String price;
    public List<String> thumbnails;

    public Product() {
    }

    /**
     * @param __id        Firebase product unique ID
     * @param sellerId    Seller ID which owns this product
     * @param categoryId  Category ID which this belongs to
     * @param name        Product display name
     * @param description Product display description
     * @param price       Product price
     * @param thumbnails  Product thumbnails
     */
    public Product(String __id, String sellerId, String categoryId, String name, String description, String price, List<String> thumbnails) {
        this.__id = __id;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.thumbnails = thumbnails;
    }
}
