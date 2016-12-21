package com.fortunekidew.pewaad.presenters;

import com.fortunekidew.pewaad.activities.groups.AddNewMembersToGroupActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 26/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class AddNewMembersToGroupPresenter implements Presenter {
    private  AddNewMembersToGroupActivity view;
    private  Realm realm;


    public AddNewMembersToGroupPresenter(AddNewMembersToGroupActivity addMembersToGroupActivity) {
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
        mContactsService.getLinkedContacts().subscribe(view::ShowContacts,throwable -> AppHelper.LogCat("AddNewMembersToGroupPresenter "+throwable.getMessage()));
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