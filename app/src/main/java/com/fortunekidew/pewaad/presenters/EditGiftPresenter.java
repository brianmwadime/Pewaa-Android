package com.fortunekidew.pewaad.presenters;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.fortunekidew.pewaad.activities.gifts.AddGiftsActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.services.apiServices.WishlistsService;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import io.realm.Realm;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class EditGiftPresenter implements Presenter {
    private AddGiftsActivity view;
    private BottomSheetEditGift bottomSheetEditGift;
    private Realm realm;
    private WishlistsService mWishlistsService;
    APIService mApiService;
    public File photoFile;
    public Uri uri;

    public EditGiftPresenter(AddGiftsActivity addGiftsActivity) {
        this.view = addGiftsActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }

    public EditGiftPresenter(BottomSheetEditGift bottomSheetEditGift) {
        this.bottomSheetEditGift = bottomSheetEditGift;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }

    public EditGiftPresenter() {
        this.realm = PewaaApplication.getRealmDatabaseInstance();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {

        if (view != null) {
            this.mApiService = APIService.with(view);
            this.mWishlistsService = new WishlistsService(view, this.mApiService);
        } else if (bottomSheetEditGift != null) {
            this.mApiService = APIService.with(bottomSheetEditGift.getActivity());
            this.mWishlistsService = new WishlistsService(bottomSheetEditGift.getActivity(), this.mApiService);
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String imagePath = null;
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case AppConstants.SELECT_PROFILE_PICTURE:
                    imagePath = FilesManager.getPath(bottomSheetEditGift.getActivity(), data.getData());
                    break;
                case AppConstants.SELECT_GIFT_CAMERA:
                        if (photoFile != null) {
                            imagePath = photoFile.getAbsolutePath();
                        }
                    break;
                }


                if (imagePath != null) {
                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_IMAGE_GIFT_PATH, imagePath));
                } else {
                    AppHelper.LogCat("imagePath is null");
                }

        }
    }


}