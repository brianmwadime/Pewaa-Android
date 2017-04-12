package com.fortunekidew.pewaad.presenters;


import com.fortunekidew.pewaad.activities.contributors.NewContributorActivity;
import com.fortunekidew.pewaad.activities.wishlists.ListContributors;
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
    private NewContributorActivity newContributorActivity;
    private ListContributors newListContributor;

    private Realm realm;
    private boolean selector;

    public SelectContactsPresenter(NewContributorActivity newContributorActivity) {
        this.newContributorActivity = newContributorActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
        selector = true;
    }

    public SelectContactsPresenter(ListContributors newListContributor) {
        this.newListContributor = newListContributor;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
        selector = false;
    }



    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (selector) {
            APIService mApiService = APIService.with(this.newContributorActivity);
            ContactsService mContactsService = new ContactsService(realm, this.newContributorActivity, mApiService);
            mContactsService.getLinkedContacts().subscribe(newContributorActivity::ShowContacts, throwable -> {
                AppHelper.LogCat("Error contacts selector " + throwable.getMessage());
            });

        } else {
            APIService mApiService = APIService.with(this.newListContributor);
            ContactsService mContactsService = new ContactsService(realm, this.newListContributor, mApiService);
            mContactsService.getLinkedContacts().subscribe(newListContributor::ShowContacts, throwable -> {
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