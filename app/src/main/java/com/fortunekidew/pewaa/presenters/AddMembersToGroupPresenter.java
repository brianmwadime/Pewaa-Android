package com.fortunekidew.pewaa.presenters;

import com.fortunekidew.pewaa.activities.groups.AddMembersToGroupActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 26/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class AddMembersToGroupPresenter implements Presenter {
    private final AddMembersToGroupActivity view;
    private final Realm realm;


    public AddMembersToGroupPresenter(AddMembersToGroupActivity addMembersToGroupActivity) {
        this.view = addMembersToGroupActivity;
        this.realm = Realm.getDefaultInstance();

    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {

        APIService mApiService = APIService.with(view);
        ContactsService mContactsService = new ContactsService(realm, view, mApiService);
        mContactsService.getLinkedContacts().subscribe(view::ShowContacts,throwable -> AppHelper.LogCat("AddMembersToGroupPresenter "+throwable.getMessage()));

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