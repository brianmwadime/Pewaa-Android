package com.fortunekidew.pewaa.activities.settings;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.profile.EditProfileActivity;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaa.presenters.SettingsPresenter;
import com.fortunekidew.pewaa.ui.CropSquareTransformation;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.fortunekidew.pewaa.helpers.UtilsString.unescapeJavaString;

/**
 * Created by Abderrahim El imame on 27/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SettingsActivity extends AppCompatActivity {
    @Bind(R.id.userAvatar)
    ImageView userAvatar;
    @Bind(R.id.user_status)
    EmojiconTextView userStatus;
    @Bind(R.id.userName)
    TextView userName;
    private ContactsModel mContactsModel;
    private SettingsPresenter mSettingsPresenter = new SettingsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setupToolbar();
        mSettingsPresenter.onCreate();
        EventBus.getDefault().register(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @SuppressWarnings("unused")
    @OnClick(R.id.settingsHead)
    public void launchEditProfile(View v) {
        if (AppHelper.isAndroid5()) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(userAvatar, "userAvatar"), new Pair<>(userName, "userName"), new Pair<>
                    (userStatus, "userStatus"));
            Intent mIntent = new Intent(this, EditProfileActivity.class);
            startActivity(mIntent, options.toBundle());
        } else {
            AppHelper.LaunchActivity(this, EditProfileActivity.class);
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.account_settings)
    public void launchAccountSettings() {
        AppHelper.LaunchActivity(this, AccountSettingsActivity.class);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.notifications_settings)
    public void launchNotificationSettings() {
        AppHelper.LaunchActivity(this, NotificationsSettingsActivity.class);
    }


    @SuppressWarnings("unused")
    @OnClick(R.id.about_help_settings)
    public void launchAboutSettings() {
        AppHelper.LaunchActivity(this, AboutActivity.class);
    }

    public void ShowContact(ContactsModel contactsModels) {
        mContactsModel = contactsModels;
        try {
            if (mContactsModel.getPhone() != null) {
                String status = unescapeJavaString(mContactsModel.getPhone());
                userStatus.setText(status);
            } else {
                userStatus.setText(getString(R.string.no_status));
            }
            if (mContactsModel.getName() != null) {
                userName.setText(mContactsModel.getName());
            } else {
                userName.setText(getString(R.string.no_username));
            }
            if (mContactsModel.getImage() != null) {
                if (FilesManager.isFileImagesProfileExists(FilesManager.getProfileImage(String.valueOf(mContactsModel.getId()), mContactsModel.getId()))) {
                    Picasso.with(this)
                            .load(FilesManager.getFileImageProfile(String.valueOf(mContactsModel.getId()), mContactsModel.getId()))
                            .transform(new CropSquareTransformation())
                            .resize(100, 100)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(userAvatar);
                } else {


                    Target target = new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            userAvatar.setImageBitmap(bitmap);
                            FilesManager.downloadFilesToDevice(SettingsActivity.this, mContactsModel.getImage(), String.valueOf(mContactsModel.getId()), mContactsModel.getUsername(), "profile");

                        }

                        @Override
                        public void onBitmapFailed(Drawable errorDrawable) {
                            userAvatar.setImageDrawable(errorDrawable);
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            userAvatar.setImageDrawable(placeHolderDrawable);
                        }
                    };


                    Picasso.with(this)
                            .load(EndPoints.ASSETS_BASE_URL + mContactsModel.getImage())
                            .transform(new CropSquareTransformation())
                            .resize(100, 100)
                            .networkPolicy(NetworkPolicy.NO_CACHE)
                            .memoryPolicy(MemoryPolicy.NO_CACHE)
                            .into(target);

                }
            } else {
                userAvatar.setPadding(2, 2, 2, 2);
                userAvatar.setImageResource(R.drawable.ic_user_holder_white_48dp);


            }
        } catch (Exception e) {
            AppHelper.LogCat("" + e);
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSettingsPresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        if (pusher.getAction().equals("updateName") || pusher.getAction().equals("updateCurrentStatus")) {
            mSettingsPresenter.onCreate();
        } else if (pusher.getAction().equals("updateImageProfile")) {
            mSettingsPresenter.onCreate();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        finish();


    }
}
