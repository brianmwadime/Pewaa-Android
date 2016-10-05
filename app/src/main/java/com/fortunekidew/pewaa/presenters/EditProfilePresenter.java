package com.fortunekidew.pewaa.presenters;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.profile.EditProfileActivity;
import com.fortunekidew.pewaa.activities.profile.EditUsernameActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.fragments.BottomSheetEditProfile;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;

import java.io.File;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class EditProfilePresenter implements Presenter {
    private EditProfileActivity view;
    private EditUsernameActivity editUsernameActivity;
    private Realm realm;
    private ContactsService mContactsService;
    private boolean isEditUsername = false;

    public EditProfilePresenter(EditProfileActivity editProfileActivity) {
        this.view = editProfileActivity;
        this.realm = Realm.getDefaultInstance();

    }

    public EditProfilePresenter() {
        this.realm = Realm.getDefaultInstance();
    }

    public EditProfilePresenter(EditUsernameActivity editUsernameActivity, boolean b) {
        this.isEditUsername = b;
        this.editUsernameActivity = editUsernameActivity;
        this.realm = Realm.getDefaultInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void
    onCreate() {
        APIService mApiService;
        if (!isEditUsername){
            mApiService = APIService.with(view);
            mContactsService = new ContactsService(realm, view, mApiService);
            mContactsService.getContact(PreferenceManager.getID(view)).subscribe(view::ShowContact, view::onErrorLoading);
            mContactsService.getContactInfo(PreferenceManager.getID(view)).subscribe(view::ShowContact, view::onErrorLoading);
        }else {
            mApiService = APIService.with(editUsernameActivity);
            this.mContactsService = new ContactsService(this.realm, editUsernameActivity, mApiService);
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


    public void onActivityResult(BottomSheetEditProfile bottomSheetEditProfile, int requestCode, int resultCode, Intent data) {
        String imagePath = null;
        if (resultCode == Activity.RESULT_OK) {
            if (AppHelper.checkPermission(bottomSheetEditProfile.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read contact data permission already granted.");
            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                AppHelper.requestPermission(bottomSheetEditProfile.getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
            }


            if (AppHelper.checkPermission(bottomSheetEditProfile.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AppHelper.LogCat("Read contact data permission already granted.");
            } else {
                AppHelper.LogCat("Please request Read contact data permission.");
                AppHelper.requestPermission(bottomSheetEditProfile.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
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
            EventBus.getDefault().post(new Pusher("Path", imagePath));
        } else {
            AppHelper.LogCat("imagePath is null");
        }
    }


    public void EditCurrentName(String name) {
        mContactsService.editUsername(name).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.Snackbar(editUsernameActivity.getBaseContext(), editUsernameActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher("updateName"));
                editUsernameActivity.finish();
            } else {
                AppHelper.Snackbar(editUsernameActivity.getBaseContext(), editUsernameActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, AppHelper::LogCat);

    }

}