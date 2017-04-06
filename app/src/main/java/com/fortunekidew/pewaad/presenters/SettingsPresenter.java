package com.fortunekidew.pewaad.presenters;


import com.fortunekidew.pewaad.activities.settings.SettingsActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016. Email : abderrahim.elimame@gmail.com
 */
public class SettingsPresenter implements Presenter {
    private final SettingsActivity view;
    private final Realm realm;
    ContactsService mContactsService;

    public SettingsPresenter(SettingsActivity settingsActivity) {
        this.view = settingsActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        APIService mApiService = APIService.with(view);
        mContactsService = new ContactsService(realm, view, mApiService);
        try {
            loadData();
        } catch (Exception e) {
            AppHelper.LogCat("get contact settings Activity " + e.getMessage());
        }
    }

    public void loadData() {
        mContactsService.getContact(PreferenceManager.getID(view)).subscribe(view::ShowContact, AppHelper::LogCat);
        mContactsService.getContactInfo(PreferenceManager.getID(view)).subscribe(view::ShowContact, AppHelper::LogCat);
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