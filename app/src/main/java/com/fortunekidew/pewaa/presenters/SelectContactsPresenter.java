package com.fortunekidew.pewaa.presenters;


import com.fortunekidew.pewaa.activities.NewContactsActivity;
import com.fortunekidew.pewaa.activities.gifts.TransferMessageContactsActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SelectContactsPresenter implements Presenter {
    private NewContactsActivity newContactsActivity;
    private TransferMessageContactsActivity transferMessageContactsActivity;
    private Realm realm;
    private boolean selector;

    public SelectContactsPresenter(NewContactsActivity newContactsActivity) {
        this.newContactsActivity = newContactsActivity;
        this.realm = Realm.getDefaultInstance();
        selector = true;
    }

    public SelectContactsPresenter(TransferMessageContactsActivity transferMessageContactsActivity) {
        this.transferMessageContactsActivity = transferMessageContactsActivity;
        this.realm = Realm.getDefaultInstance();
        selector = false;
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (selector) {
            APIService mApiService = APIService.with(this.newContactsActivity);
            ContactsService mContactsService = new ContactsService(realm, this.newContactsActivity, mApiService);
            mContactsService.getLinkedContacts().subscribe(newContactsActivity::ShowContacts, throwable -> {
                AppHelper.LogCat("Error contacts selector " + throwable.getMessage());
            });

        } else {
            APIService mApiService = APIService.with(this.transferMessageContactsActivity);
            ContactsService mContactsService = new ContactsService(realm, this.transferMessageContactsActivity, mApiService);
            mContactsService.getLinkedContacts().subscribe(transferMessageContactsActivity::ShowContacts, throwable -> {
                AppHelper.LogCat("Error contacts selector " + throwable.getMessage());
            });
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }
}