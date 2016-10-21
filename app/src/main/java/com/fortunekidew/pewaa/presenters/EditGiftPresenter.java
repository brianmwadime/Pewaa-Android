package com.fortunekidew.pewaa.presenters;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;

import com.fortunekidew.pewaa.activities.gifts.AddGiftsActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.services.apiServices.WishlistsService;

import java.io.File;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditGiftPresenter implements Presenter {
    private AddGiftsActivity view;
    private Realm realm;
    private WishlistsService mWishlistsService;

    public EditGiftPresenter(AddGiftsActivity addGiftsActivity) {
        this.view = addGiftsActivity;
        this.realm = Realm.getDefaultInstance();

    }

    public EditGiftPresenter() {
        this.realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        APIService mApiService;
        mApiService = APIService.with(view);
        mWishlistsService = new WishlistsService(realm, view, mApiService);
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


    public void onActivityResult(BottomSheetEditGift bottomSheetEditGift, int requestCode, int resultCode, Intent data) {
        String imagePath = null;
        if (resultCode == Activity.RESULT_OK) {
            if (AppHelper.checkPermission(bottomSheetEditGift.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read contact data permission already granted.");
            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                AppHelper.requestPermission(bottomSheetEditGift.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            }


            if (AppHelper.checkPermission(bottomSheetEditGift.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read contact data permission already granted.");
            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                AppHelper.requestPermission(bottomSheetEditGift.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            switch (requestCode) {
                case AppConstants.SELECT_PROFILE_PICTURE:
                    imagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());
                    break;
                case AppConstants.SELECT_PROFILE_CAMERA:
                    if (data.getData() != null) {
                        imagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());
                    } else {
                        try {
                            String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                    .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                    .ImageColumns.MIME_TYPE};
                            final Cursor cursor = PewaaApplication.getAppContext().getContentResolver()
                                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns
                                            .DATE_TAKEN + " DESC");

                            if (cursor != null && cursor.moveToFirst()) {
                                String imageLocation = cursor.getString(1);
                                cursor.close();
                                File imageFile = new File(imageLocation);
                                if (imageFile.exists()) {
                                    imagePath = imageFile.getPath();
                                }
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat("error" + e);
                        }
                    }
                    break;
            }
        }

        if (imagePath != null) {
            EventBus.getDefault().post(new Pusher("GiftImagePath", imagePath));
        } else {
            AppHelper.LogCat("imagePath is null");
        }
    }
}