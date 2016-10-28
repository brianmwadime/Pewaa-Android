package com.fortunekidew.pewaa.models.wishlists;

import com.google.gson.annotations.Expose;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class WishlistsModel extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private String description;
    private String recipients;
    private String category;
    private String avatar;
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
        avatar = avatar;
    }

    public String getRecipients() {
        return recipients;
    }

    public void setRecipients(String recipients) {
        this.recipients = recipients;
    }

}
