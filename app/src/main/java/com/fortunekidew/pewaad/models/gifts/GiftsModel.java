package com.fortunekidew.pewaad.models.gifts;

import org.parceler.Parcel;

import java.util.Date;

import io.realm.GiftsModelRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

@Parcel(implementations = { GiftsModelRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { GiftsModel.class })
public class GiftsModel extends RealmObject {
    @PrimaryKey
    private String id;

    private String name;

    private String cashout_status;

    private long contributor_count;

    private double price;

    private double contribution_total;

    private String wishlist_id;

    private  String description;

    private  String avatar;

    private String code;

    private Date created_on;

    private Date updated_on;

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

    public String getCashout_status(){
        return cashout_status;
    }

    public void setCashout_status(String cashout_status) {
        this.cashout_status = cashout_status;
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
        return contribution_total;
    }

    public long getContributor_count() {
        return contributor_count;
    }

    public void setContributor_count(long contributor_count) {
        this.contributor_count = contributor_count;
    }

    public void setContributed(double contributed) {
        this.contribution_total = contributed;
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
