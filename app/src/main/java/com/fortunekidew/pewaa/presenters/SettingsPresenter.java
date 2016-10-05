package com.fortunekidew.pewaa.presenters;


import com.fortunekidew.pewaa.activities.settings.SettingsActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016. Email : abderrahim.elimame@gmail.com
 */
public class SettingsPresenter implements Presenter {
    private final SettingsActivity view;
    private final Realm realm;

    public SettingsPresenter(SettingsActivity settingsActivity) {
        this.view = settingsActivity;
        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        APIService mApiService = APIService.with(view);
        ContactsService mContactsService = new ContactsService(realm, view, mApiService);
        try {
            mContactsService.getContact(PreferenceManager.getID(view)).subscribe(view::ShowContact, throwable -> AppHelper.LogCat(throwable.getMessage()));
            mContactsService.getContactInfo(PreferenceManager.getID(view)).subscribe(view::ShowContact, throwable -> AppHelper.LogCat(throwable.getMessage()));
        } catch (Exception e) {
            AppHelper.LogCat("get contact settings Activity " + e.getMessage());
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