package com.fpoly.project1.firebase.model;

import java.io.Serializable;

public class Rating implements Serializable {
    public int id;
    public int customer_id;
    public int seller_id;
    public int shipper_id;
    public int product_id;
    public String comment;
    public String date;
    public int rating;

    public Rating() {}

    public Rating(int id, int customer_id, int seller_id, int shipper_id, int product_id, String comment, String date, int rating) {
        this.id = id;
        this.customer_id = customer_id;
        this.seller_id = seller_id;
        this.shipper_id = shipper_id;
        this.product_id = product_id;
        this.comment = comment;
        this.date = date;
        this.rating = rating;
    }
}
