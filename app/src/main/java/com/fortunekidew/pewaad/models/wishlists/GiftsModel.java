package com.fortunekidew.pewaad.models.wishlists;

import org.parceler.Parcel;

import java.util.Date;

import io.realm.GiftsModelRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Brian Mwakima on 10/6/16.
 */

@Parcel(implementations = { GiftsModelRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { GiftsModel.class })
public class GiftsModel extends RealmObject {
    @PrimaryKey
    public String id;

    public String name;

    public double price;

    public double contributed;

    public String wishlist_id;

    public  String description;

    public  String avatar;

    public String code;

    public Date created_on;

    public Date updated_on;

    private String creator_name;

    private String creator_avatar;

    private String creator_phone;

    public Date getUpdatedOn() {
        return updated_on;
    }

    public void setUpdatedOn(Date updated_on) {
        this.updated_on = updated_on;
    }

    public Date getCreatedOn() {
        return created_on;
    }

    public void setCreatedOn(Date created_on) {
        this.created_on = created_on;
    }

    public String getWishlistId() {
        return wishlist_id;
    }

    public void setWishlistId(String wishlist_id) {
        this.wishlist_id = wishlist_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCreatorName() {
        return creator_name;
    }

    public void setCreatorName(String name) {
        this.creator_name = name;
    }

    public String getCreatorAvatar() {
        return creator_avatar;
    }

    public void setCreatorAvatar(String avatar) {
        this.creator_avatar = avatar;
    }

    public String getCreatorPhone() {
        return creator_phone;
    }

    public void setCreatorPhone(String phone) {
        this.creator_phone = phone;
    }
}
