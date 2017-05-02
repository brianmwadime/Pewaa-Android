package com.fortunekidew.pewaad.models.users.contacts;

import java.util.List;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class PusherContacts {
    private String action;
    private List<ContactsModel> contactsModelList;
    private Throwable throwable;

    public PusherContacts(String action) {
        this.action = action;
    }

    public PusherContacts(String action, Throwable throwable) {
        this.action = action;
        this.throwable = throwable;
    }

    public PusherContacts(String action, List<ContactsModel> contactsModelList) {
        this.action = action;
        this.contactsModelList = contactsModelList;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public List<ContactsModel> getContactsModelList() {
        return contactsModelList;
    }

    public void setContactsModelList(List<ContactsModel> contactsModelList) {
        this.contactsModelList = contactsModelList;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}