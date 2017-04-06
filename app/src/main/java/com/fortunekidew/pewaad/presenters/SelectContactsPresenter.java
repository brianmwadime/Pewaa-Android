package com.fortunekidew.pewaad.presenters;


import com.fortunekidew.pewaad.activities.NewContactsActivity;
import com.fortunekidew.pewaad.activities.gifts.TransferMessageContactsActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

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
        this.realm = PewaaApplication.getRealmDatabaseInstance();
        selector = true;
    }

    public SelectContactsPresenter(TransferMessageContactsActivity transferMessageContactsActivity) {
        this.transferMessageContactsActivity = transferMessageContactsActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
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