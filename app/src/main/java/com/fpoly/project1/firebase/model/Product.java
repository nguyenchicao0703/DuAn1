package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Product implements Serializable {
    public int id;
    public String name;
    public String description;
    public float price;
    public int[] thumbnails;

    public Product() {}

    public Product(int id, String name, String description, float price, int[] thumbnails) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.thumbnails = thumbnails;
    }
}
