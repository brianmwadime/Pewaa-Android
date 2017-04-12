package com.fortunekidew.pewaad.models.users;

import android.view.View;

import com.fortunekidew.pewaad.models.notifications.NotificationsModel;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.wishlists.EditGift;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;

import org.json.JSONObject;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class Pusher {
    private String action;
    private View view;
    private String data;
    private boolean bool;
    private ContactsModel contactsModel;
    private JSONObject jsonObject;
    private EditWishlist wishlistObject;
    private EditGift giftObject;
    private NotificationsModel notificationsModel;
    private int messageId;

    public Pusher(String action) {
        this.action = action;
    }

    public Pusher(String action, String data) {
        this.action = action;
        this.data = data;
    }

    public Pusher(String action, String data, Boolean bool) {
        this.action = action;
        this.data = data;
        this.bool = bool;
    }

    public Pusher(String action, ContactsModel contactsModel) {
        this.action = action;
        this.contactsModel = contactsModel;
    }


    public Pusher(String action, NotificationsModel notificationsModel) {
        this.action = action;
        this.notificationsModel = notificationsModel;
    }

    public Pusher(String action, JSONObject jsonObject) {
        this.action = action;
        this.jsonObject = jsonObject;
    }

    public Pusher(String action, EditWishlist wishlist) {
        this.action = action;
        this.wishlistObject = wishlist;
    }

    public Pusher(String action, EditGift gift) {
        this.action = action;
        this.giftObject = gift;
    }

    public Pusher(String itemIsActivated, View view) {
        this.action = itemIsActivated;
        this.view = view;
    }


    public Pusher(String action, int messageId) {
        this.action = action;
        this.messageId = messageId;
    }



    public NotificationsModel getNotificationsModel() {
        return notificationsModel;
    }

    public void setNotificationsModel(NotificationsModel notificationsModel) {
        this.notificationsModel = notificationsModel;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }


    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public boolean isBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


    public String getAction() {
        return action;
    }


    public ContactsModel getContactsModel() {
        return contactsModel;
    }

    public void setWishlistObject(EditWishlist wishlistObject) {
        this.wishlistObject = wishlistObject;
    }

    public EditGift getGiftObject() {
        return giftObject;
    }

    public void setGiftObject(EditGift giftObject) {
        this.giftObject = giftObject;
    }

    public EditWishlist getWishlistObject() {
        return wishlistObject;
    }

    public void setContactsModel(ContactsModel contactsModel) {
        this.contactsModel = contactsModel;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
