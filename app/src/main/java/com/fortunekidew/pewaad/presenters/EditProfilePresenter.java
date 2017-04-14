package com.fortunekidew.pewaad.presenters;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.profile.EditProfileActivity;
import com.fortunekidew.pewaad.activities.profile.EditUsernameActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.BottomSheetEditProfile;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import io.realm.Realm;

import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_IMAGE_PATH;
import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_UPDATE_NAME;

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
    public File photoFile;
    public Uri uri;

    public EditProfilePresenter(EditProfileActivity editProfileActivity) {
        this.view = editProfileActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }

    public EditProfilePresenter() {
        this.realm = PewaaApplication.getRealmDatabaseInstance();
    }

    public EditProfilePresenter(EditUsernameActivity editUsernameActivity, boolean b) {
        this.isEditUsername = b;
        this.editUsernameActivity = editUsernameActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
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
            this.mContactsService = new ContactsService(editUsernameActivity, mApiService);
        }
    }

    public void loadData() {
        try {
            mContactsService.getContact(PreferenceManager.getID(view)).subscribe(view::ShowContact, view::onErrorLoading);
            mContactsService.getContactInfo(PreferenceManager.getID(view)).subscribe(view::ShowContact, view::onErrorLoading);
        } catch (Exception e) {
            AppHelper.LogCat(" EditProfileActivity Exception " + e.getMessage());
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
            switch (requestCode) {
                case AppConstants.SELECT_PROFILE_PICTURE:
                    imagePath = FilesManager.getPath(bottomSheetEditProfile.getActivity(), data.getData());
                    break;
                case AppConstants.SELECT_PROFILE_CAMERA:
                    if (photoFile != null) {
                        imagePath = photoFile.getAbsolutePath();
                    }
                    break;
            }
        }

        if (imagePath != null) {
            EventBus.getDefault().post(new Pusher(EVENT_BUS_IMAGE_PATH, imagePath));
        } else {
            AppHelper.LogCat("imagePath is null");
        }
    }


    public void EditCurrentName(String name) {
        mContactsService.editUsername(name).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.Snackbar(editUsernameActivity.getBaseContext(), editUsernameActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher(EVENT_BUS_UPDATE_NAME));
                editUsernameActivity.finish();
            } else {
                AppHelper.Snackbar(editUsernameActivity.getBaseContext(), editUsernameActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, AppHelper::LogCat);

    }

}