package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Rating implements Serializable {
    public String __id;
    public String customerId;
    public String sellerId;
    public String shipperId;
    public String productId;
    public String comment;
    public String date;
    public float rating;

    public Rating() {}

    /**
     *
     * @param __id Firebase comment unique Id
     * @param customerId Comment customer owner ID
     * @param sellerId Comment target seller ID, if any
     * @param shipperId Comment target shipper ID, if any
     * @param productId Comment target product ID, if any
     * @param comment Comment text
     * @param date Comment date
     * @param rating Comment rating points 1.00 - 5.00
     */
    public Rating(String __id, String customerId, String sellerId, String shipperId, String productId, String comment, String date, float rating) {
        this.__id = __id;
        this.customerId = customerId;
        this.sellerId = sellerId;
        this.shipperId = shipperId;
        this.productId = productId;
        this.comment = comment;
        this.date = date;
        this.rating = rating;
    }
}
