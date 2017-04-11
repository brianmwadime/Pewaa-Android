package com.fortunekidew.pewaad.activities.settings;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.gifts.GiftDetailsActivity;
import com.fortunekidew.pewaad.activities.profile.EditProfileActivity;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.Files.ImageLoader;
import com.fortunekidew.pewaad.helpers.Files.MemoryCache;
import com.fortunekidew.pewaad.helpers.images.PewaaImageLoader;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.presenters.SettingsPresenter;
import com.fortunekidew.pewaad.ui.CropSquareTransformation;
import com.fortunekidew.pewaad.util.glide.CircleTransform;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.rockerhieu.emojicon.EmojiconTextView;

import static com.fortunekidew.pewaad.app.EndPoints.ASSETS_BASE_URL;
import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJavaString;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class SettingsActivity extends AppCompatActivity {
    @BindView(R.id.userAvatar)
    ImageView userAvatar;
    @BindView(R.id.user_status)
    EmojiconTextView userStatus;
    @BindView(R.id.userName)
    TextView userName;
    @BindDimen(R.dimen.large_avatar_size) int largeAvatarSize;
    private CircleTransform circleTransform;
    private ContactsModel mContactsModel;
    private SettingsPresenter mSettingsPresenter;
    private MemoryCache memoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(this);
        setupToolbar();
        memoryCache = new MemoryCache();
        mSettingsPresenter = new SettingsPresenter(this);
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
            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.SETTINGS_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userAvatar);
            } else {
             /*   Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        userAvatar.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.SETTINGS_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), SettingsActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.SETTINGS_PROFILE);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.EDIT_PROFILE_IMAGE_URL + mContactsModel.getImage(), mContactsModel.getImage(), SettingsActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        userAvatar.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        userAvatar.setImageDrawable(placeHolderDrawable);
                    }
                };*/
                com.bumptech.glide.request.target.Target target = new BitmapImageViewTarget(userAvatar) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userAvatar.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ASSETS_BASE_URL + mContactsModel.getImage(), mContactsModel.getImage(), SettingsActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.SETTINGS_PROFILE);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ASSETS_BASE_URL + mContactsModel.getImage(), mContactsModel.getImage(), SettingsActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        userAvatar.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        userAvatar.setImageDrawable(placeholder);
                    }
                };
                PewaaImageLoader.loadCircleImage(this, EndPoints.ASSETS_BASE_URL + mContactsModel.getImage(), target, R.drawable.image_holder_ur_circle, AppConstants.SETTINGS_IMAGE_SIZE);

            }
        } catch (Exception e) {
            AppHelper.LogCat(e.getMessage());
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

    @Subscribe
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case "updateName":
            case "updateCurrentStatus":
            case "updateImageProfile":
                mSettingsPresenter.loadData();
                break;
            case AppConstants.EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED:
                mSettingsPresenter.loadData();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        finish();


    }
}
