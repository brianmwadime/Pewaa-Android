package com.fortunekidew.pewaad.services.apiServices;

import android.content.Context;

import com.fortunekidew.pewaad.api.APIContact;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.SyncContacts;
import com.fortunekidew.pewaad.models.users.status.EditStatus;
import com.fortunekidew.pewaad.models.users.status.StatusModel;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class ContactsService {
    private APIContact mApiContact;
    private Context mContext;
    private Realm realm;
    private APIService mApiService;

    public ContactsService(Realm realm, Context context, APIService mApiService) {
        this.mContext = context;
        this.realm = realm;
        this.mApiService = mApiService;

    }

    public ContactsService(Context context, APIService mApiService) {
        this.mContext = context;
        this.mApiService = mApiService;

    }

    /**
     * method to initialize the api contact
     *
     * @return return value
     */
    private APIContact initializeApiContact() {
        if (mApiContact == null) {
            mApiContact = this.mApiService.RootService(APIContact.class, PreferenceManager.getToken(PewaaApplication.getInstance()), EndPoints.BASE_URL);
        }
        return mApiContact;
    }

    /**
     * method to get general user information
     *
     * @param userID this is parameter  getContact for method
     * @return return value
     */
    public Observable<ContactsModel> getContact(String userID) {
        return realm.where(ContactsModel.class).equalTo("id", userID).findFirst().asObservable();
    }

    /**
     * method to get all contacts
     *
     * @return return value
     */
    public Observable<RealmResults<ContactsModel>> getAllContacts() {
        return realm.where(ContactsModel.class).notEqualTo("id", PreferenceManager.getID(PewaaApplication.getInstance())).equalTo("Exist", true).findAllSorted("Linked", Sort.DESCENDING, "username", Sort.ASCENDING).asObservable();
    }

    /**
     * method to get linked contacts
     *
     * @return return value
     */
    public Observable<RealmResults<ContactsModel>> getLinkedContacts() {
        return realm.where(ContactsModel.class).notEqualTo("id", PreferenceManager.getID(PewaaApplication.getInstance())).equalTo("Exist", true).equalTo("Linked", true).findAllSorted("username", Sort.ASCENDING).asObservable();
    }

    /**
     * method to update(syncing) contacts
     *
     * @param ListString this is parameter for  updateContacts method
     * @return return value
     */
    public Observable<List<ContactsModel>> updateContacts(SyncContacts ListString) {
        return initializeApiContact().contacts(ListString)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::copyOrUpdateContacts);
    }

    /**
     * method to get user information from the server
     *
     * @param userID this is parameter for getContactInfo method
     * @return return  value
     */
    public Observable<ContactsModel> getContactInfo(String userID) {
        return initializeApiContact().contact(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::copyOrUpdateContactInfo);
    }

    /**
     * method to get user status from server
     *
     * @return return value
     */
    public Observable<List<StatusModel>> getUserStatus() {
        return initializeApiContact().status()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::copyOrUpdateStatus);
    }

    /**
     * method to delete user status
     *
     * @param statusID this is parameter for deleteStatus method
     * @return return  value
     */
    public Observable<StatusResponse> deleteStatus(int statusID) {
        return initializeApiContact().deleteStatus(statusID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }

    /**
     * method to delete all user status
     *
     * @return return value
     */
    public Observable<StatusResponse> deleteAllStatus() {
        return initializeApiContact().deleteAllStatus()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }

    /**
     * method to update user status
     *
     * @param statusID this is parameter for updateStatus method
     * @return return  value
     */
    public Observable<StatusResponse> updateStatus(int statusID) {
        return initializeApiContact().updateStatus(statusID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }

    /**
     * method to edit user status
     *
     * @param newStatus this is the first parameter for editStatus method
     * @param statusID  this is the second parameter for editStatus method
     * @return return  value
     */
    public Observable<StatusResponse> editStatus(String newStatus, int statusID) {
        EditStatus editStatus = new EditStatus();
        editStatus.setNewStatus(newStatus);
        editStatus.setStatusID(statusID);
        return initializeApiContact().editStatus(editStatus)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }

    /**
     * method to edit username
     *
     * @param newName this is parameter for editUsername method
     * @return return  value
     */
    public Observable<StatusResponse> editUsername(String newName) {
        return initializeApiContact().editUsername(newName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }

    /**
     * method to get all status
     *
     * @return return value
     */
    public Observable<RealmResults<StatusModel>> getAllStatus() {
        return realm.where(StatusModel.class).equalTo("userID", PreferenceManager.getID(PewaaApplication.getInstance())).findAllSorted("id", Sort.DESCENDING).asObservable();
    }

    /**
     * method to get current status fron local
     *
     * @return return value
     */
    public Observable<StatusModel> getCurrentStatusFromLocal() {
        return realm.where(StatusModel.class).equalTo("userID", PreferenceManager.getID(PewaaApplication.getInstance())).equalTo("current", 1).findFirst().asObservable();
    }

    /**
     * method to delete user status
     *
     * @param phone this is parameter for deleteStatus method
     * @return return  value
     */
    public Observable<StatusResponse> deleteAccount(String phone) {
        return initializeApiContact().deleteAccount(phone)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }


    /**
     * method to copy or update user status
     *
     * @param statusModels this is parameter for copyOrUpdateStatus method
     * @return return  value
     */
    private List<StatusModel> copyOrUpdateStatus(List<StatusModel> statusModels) {
        realm.beginTransaction();
        List<StatusModel> statusModels1 = realm.copyToRealmOrUpdate(statusModels);
        realm.commitTransaction();
        return statusModels1;
    }

    /**
     * method to copy or update contacts list
     *
     * @param contacts this is parameter for copyOrUpdateContacts method
     * @return return  value
     */
    private List<ContactsModel> copyOrUpdateContacts(List<ContactsModel> contacts) {

        realm.beginTransaction();
        List<ContactsModel> realmContacts = realm.copyToRealmOrUpdate(contacts);
        realm.commitTransaction();
        return realmContacts;
    }

    /**
     * method to copy or update user information
     *
     * @param contactsModel this is parameter for copyOrUpdateContactInfo method
     * @return return  value
     */
    private ContactsModel copyOrUpdateContactInfo(ContactsModel contactsModel) {
        ContactsModel realmContact;
        if (UtilsPhone.checkIfContactExist(PewaaApplication.getInstance(), contactsModel.getPhone())) {
            realm.beginTransaction();
            contactsModel.setExist(true);
            realmContact = realm.copyToRealmOrUpdate(contactsModel);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            contactsModel.setExist(false);
            realmContact = realm.copyToRealmOrUpdate(contactsModel);
            realm.commitTransaction();

        }
        return realmContact;
    }

}