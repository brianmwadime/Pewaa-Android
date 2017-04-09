package com.fortunekidew.pewaad.presenters;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;

import com.fortunekidew.pewaad.activities.gifts.AddGiftsActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.PermissionHandler;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.services.apiServices.WishlistsService;


import java.io.File;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditGiftPresenter implements Presenter {
    private AddGiftsActivity view;
    private BottomSheetEditGift bottomSheetEditGift;
    private Realm realm;
    private WishlistsService mWishlistsService;
    APIService mApiService;

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
            if (PermissionHandler.checkPermission(bottomSheetEditGift.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read storage data permission already granted.");

                switch (requestCode) {
                    case AppConstants.SELECT_PROFILE_PICTURE:
                        imagePath = FilesManager.getPath(bottomSheetEditGift.getActivity(), data.getData());
                        break;
                    case AppConstants.SELECT_PROFILE_CAMERA:
                        if (data.getData() != null) {
                            imagePath = FilesManager.getPath(bottomSheetEditGift.getActivity(), data.getData());
                        } else {
                            try {
                                String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                        .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                        .ImageColumns.MIME_TYPE};
                                final Cursor cursor = bottomSheetEditGift.getActivity().getContentResolver()
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


                if (imagePath != null) {
                    EventBus.getDefault().post(new Pusher("GiftImagePath", imagePath));
                } else {
                    AppHelper.LogCat("imagePath is null");
                }

            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                PermissionHandler.requestPermission(bottomSheetEditGift.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            }

        }
    }
}