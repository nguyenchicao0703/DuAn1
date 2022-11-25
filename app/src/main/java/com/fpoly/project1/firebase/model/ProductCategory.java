package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class ProductCategory implements Serializable {
    public String __id;
    public String name;
    public String description;
    public String featuredThumbnail;

    public ProductCategory() {}

    /**
     *
     * @param __id Firebase Rating unique ID
     * @param name Rating display name
     * @param description Rating description
     * @param featuredThumbnail Rating featured thumbnail TODO: should use product ID or not
     */
    public ProductCategory(String __id, String name, String description, String featuredThumbnail) {
        this.__id = __id;
        this.name = name;
        this.description = description;
        this.featuredThumbnail = featuredThumbnail;
    }
}
