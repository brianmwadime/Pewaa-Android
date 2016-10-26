package com.fortunekidew.pewaa.presenters;


import com.fortunekidew.pewaa.activities.profile.ProfilePreviewActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016. Email : abderrahim.elimame@gmail.com
 */
public class ProfilePreviewPresenter implements Presenter {
    private final ProfilePreviewActivity view;
    private final Realm realm;

    public ProfilePreviewPresenter(ProfilePreviewActivity profilePreviewActivity) {
        this.view = profilePreviewActivity;
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
        if (view.getIntent().hasExtra("userID")) {
            String userID = view.getIntent().getExtras().getString("userID");

            try {
                mContactsService.getContact(userID).subscribe(view::ShowContact, view::onErrorLoading);
            } catch (Exception e) {
                AppHelper.LogCat("Here getContact profile preview" + e.getMessage());
            }
            mContactsService.getContactInfo(userID).subscribe(view::ShowContact, view::onErrorLoading);


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