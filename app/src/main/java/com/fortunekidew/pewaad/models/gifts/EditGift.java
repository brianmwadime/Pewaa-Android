package com.fortunekidew.pewaad.models.gifts;

import java.util.Date;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
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
    private String cashout_status;
    private long contributor_count;
    private String creator_name;
    private String creator_avatar;
    private String creator_phone;
    private Boolean flagged;
    private String flagged_description;

    public void setFlagged_description(String flagged_description) {
        this.flagged_description = flagged_description;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public String getFlagged_description() {
        return flagged_description;
    }

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

    public String getCashout_status() {
        return cashout_status;
    }

    public void setCashout_status(String cashout_status) {
        this.cashout_status = cashout_status;
    }

    public long getContributor_count() {
        return contributor_count;
    }

    public void setContributor_count(long contributor_count) {
        this.contributor_count = contributor_count;
    }

    public String getCreator_name() {
        return creator_name;
    }

    public void setCreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    public String getCreator_avatar() {
        return creator_avatar;
    }

    public void setCreator_avatar(String creator_avatar) {
        this.creator_avatar = creator_avatar;
    }

    public String getCreator_phone() {
        return creator_phone;
    }

    public void setCreator_phone(String creator_phone) {
        this.creator_phone = creator_phone;
    }
}
