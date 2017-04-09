package com.fortunekidew.pewaad.presenters;


import android.app.Activity;

import com.fortunekidew.pewaad.activities.search.SearchContactsActivity;
import com.fortunekidew.pewaad.activities.wishlists.ListContributors;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SearchContactsPresenter implements Presenter {
    private SearchContactsActivity mSearchContactsActivity;
    private Realm realm;
    private ContactsService mContactsService;


    public SearchContactsPresenter(SearchContactsActivity mSearchContactsActivity) {
        this.mSearchContactsActivity = mSearchContactsActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        APIService mApiService = APIService.with(this.mSearchContactsActivity);
        mContactsService = new ContactsService(realm, this.mSearchContactsActivity, mApiService);
        loadLocalData();
    }

    public void loadLocalData() {
        mContactsService.getAllContacts().subscribe(mSearchContactsActivity::ShowContacts, mSearchContactsActivity::onErrorLoading);
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