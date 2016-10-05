package com.sourcecanyon.whatsClone.models.users;

import android.view.View;

import com.sourcecanyon.whatsClone.models.messages.MessagesModel;
import com.sourcecanyon.whatsClone.models.notifications.NotificationsModel;
import com.sourcecanyon.whatsClone.models.users.contacts.ContactsModel;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Abderrahim El imame on 04/05/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class Pusher {
    private String action;
    private View view;
    private String data;
    private boolean bool;
    private ContactsModel contactsModel;
    private List<MessagesModel> messagesModelList;
    private JSONObject jsonObject;
    private MessagesModel messagesModel;
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

    public Pusher(String action, List<MessagesModel> messagesModelList) {
        this.action = action;
        this.messagesModelList = messagesModelList;
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

    public Pusher(String action, MessagesModel messagesModel) {
        this.action = action;
        this.messagesModel = messagesModel;
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


    public MessagesModel getMessagesModel() {
        return messagesModel;
    }

    public void setMessagesModel(MessagesModel messagesModel) {
        this.messagesModel = messagesModel;
    }


    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }


    public List<MessagesModel> getMessagesModelList() {
        return messagesModelList;
    }

    public void setMessagesModelList(List<MessagesModel> messagesModelList) {
        this.messagesModelList = messagesModelList;
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

    public void setContactsModel(ContactsModel contactsModel) {
        this.contactsModel = contactsModel;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
