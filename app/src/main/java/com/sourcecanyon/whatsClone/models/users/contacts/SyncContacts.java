package com.sourcecanyon.whatsClone.models.users.contacts;

import java.util.List;

/**
 * Created by Abderrahim El imame on 27/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SyncContacts {

    private String userID;
    private List<ContactsModel> contactsModelList;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public List<ContactsModel> getContactsModelList() {
        return contactsModelList;
    }

    public void setContactsModelList(List<ContactsModel> contactsModelList) {
        this.contactsModelList = contactsModelList;
    }
}
