package com.fortunekidew.pewaad.models.gifts;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class GiftResponse {
    private boolean success;
    private String message;
    private String name;
    private String id;
    private String wishlist_id;
    private String description;
    private String avatar;
    private double price;
    private double contributed;
    private String cashout_status;
    private String creator_id;
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

    public String getCashout_status() {
        return cashout_status;
    }

    public void setCashout_status(String cashout_status) {
        this.cashout_status = cashout_status;
    }

    public String getCreator_id() {
        return creator_id;
    }

    public void setCreator_id(String creator_id) {
        this.creator_id = creator_id;
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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getWishlistId() {
        return wishlist_id;
    }

    public void setWishlistId(String wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
