package com.fortunekidew.pewaad.models.wishlists;

import java.util.Date;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditGift {
    private String name;
    private String id;
    private String description;
    private String avatar;
    private String wishlist_id;
    private Date created_on;
    private double price;
    private double contributed;

    public Date getCreatedOn() {
        return created_on;
    }

    public void setCreatedOn(Date created_on) {
        this.created_on = created_on;
    }

    public double getContributed() {
        return contributed;
    }

    public void setContributed(double contributed) {
        this.contributed = contributed;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getWishlistId() {
        return wishlist_id;
    }

    public void setWishlistId(String wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
