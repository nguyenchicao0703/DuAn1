package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class ProductCategory implements Serializable {
    public int id;
    public String name;
    public String description;

    public ProductCategory() {}

    public ProductCategory(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
