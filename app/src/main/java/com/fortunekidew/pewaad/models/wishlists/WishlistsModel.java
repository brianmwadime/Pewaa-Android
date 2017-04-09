package com.fortunekidew.pewaad.models.wishlists;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

import io.realm.RealmList;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class WishlistsModel implements Serializable {
    private String id;
    private String name;
    private String description;
    private String recipients;
    private String category;
    private String avatar;
    private String permissions;
    private RealmList<GiftsModel> gifts;

    /**
     * field of groups
     */
    @Expose
    private boolean createdOnline;

    public boolean getCreatedOnline() {
        return createdOnline;
    }

    public void setCreatedOnline(boolean createdOnline) {
        this.createdOnline = createdOnline;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPermissions() {
        return permissions;
    }

    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    public WishlistsModel() {

    }

    public RealmList<GiftsModel> getGifts() {
        return gifts;
    }

    public void setGifts(RealmList<GiftsModel> gifts) {
        this.gifts = gifts;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

    public String toString() {
        return getName() + "##" + getName() + "##" + getDescription() + "##" + getCategory() + "##" + getPermissions();
    }

}
