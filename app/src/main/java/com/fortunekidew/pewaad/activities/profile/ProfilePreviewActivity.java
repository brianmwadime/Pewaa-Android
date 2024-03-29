package com.fortunekidew.pewaad.activities.profile;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatImageView;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaad.animations.AnimationsUtil;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.presenters.ProfilePreviewPresenter;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import jp.wasabeef.glide.transformations.BlurTransformation;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class ProfilePreviewActivity extends Activity {

    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    public static final String EXTRA_CONTRIBUTOR_ID = "EXTRA_CONTRIBUTOR_ID";
    @BindView(R.id.userProfileName)
    EmojiconTextView userProfileName;
    @BindView(R.id.ContactBtn)
    AppCompatImageView ContactBtn;
    @BindView(R.id.AboutBtn)
    AppCompatImageView AboutBtn;
    @BindView(R.id.CallBtn)
    AppCompatImageView CallBtn;
    @BindView(R.id.userProfilePicture)
    AppCompatImageView userProfilePicture;
    @BindView(R.id.actionProfileArea)
    LinearLayout actionProfileArea;
    @BindView(R.id.containerProfile)
    LinearLayout containerProfile;
    @BindView(R.id.containerProfileInfo)
    LinearLayout containerProfileInfo;

    public int userID;
    public int groupID;
    public int conversationID;
    private boolean isGroup;
    private long Duration = 500;
    private Intent mIntent;

    private ProfilePreviewPresenter mProfilePresenter = new ProfilePreviewPresenter(this);
    private ContactsModel contactsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppHelper.isAndroid5()) {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        // Make us non-modal, so that others can receive touch events.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        // but notify us that it happened.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

        setContentView(R.layout.activity_profile_perview);
        ButterKnife.bind(this);
        initializerView();
        setupProgressBar();
        if (getIntent().hasExtra(EXTRA_USER_ID)) {
            userID = getIntent().getExtras().getInt(EXTRA_USER_ID);
        }

        mProfilePresenter.onCreate();
        if (AppHelper.isAndroid5()) {
            containerProfileInfo.post(() -> AnimationsUtil.show(containerProfileInfo, Duration));
        }
        if (isGroup) {
            CallBtn.setVisibility(View.GONE);
        } else {
            CallBtn.setVisibility(View.VISIBLE);
        }

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        if (AppHelper.isAndroid5()) {
            userProfilePicture.setTransitionName(getString(R.string.user_image_transition));
            userProfileName.setTransitionName(getString(R.string.user_name_transition));
        }
//        ContactBtn.setOnClickListener(v -> {
//            if (isGroup) {
//                Intent messagingIntent = new Intent(this, WishlistActivity.class);
//                messagingIntent.putExtra("conversationID", conversationID);
//                messagingIntent.putExtra("groupID", groupID);
//                messagingIntent.putExtra("isGroup", true);
//                startActivity(messagingIntent);
//                finish();
//            } else {
//                Intent messagingIntent = new Intent(this, WishlistActivity.class);
//                messagingIntent.putExtra("conversationID", 0);
//                messagingIntent.putExtra("recipientID", userID);
//                messagingIntent.putExtra("isGroup", false);
//                startActivity(messagingIntent);
//                finish();
//            }
//        });
        AboutBtn.setOnClickListener(v -> {

//                if (AppHelper.isAndroid5()) {
//                    mIntent = new Intent(this, ProfileActivity.class);
//                    mIntent.putExtra("userID", userID);
//                    mIntent.putExtra("isGroup", false);
//                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, new Pair<>(userProfilePicture, getString(R.string.user_image_transition)), new Pair<>(userProfileName, getString(R.string.user_name_transition)));
//                    startActivity(mIntent, options.toBundle());
//                    finish();
//                } else {
//                    mIntent = new Intent(this, ProfileActivity.class);
//                    mIntent.putExtra("userID", userID);
//                    mIntent.putExtra("isGroup", false);
//                    startActivity(mIntent);
//                    finish();
//                }


        });
//        CallBtn.setOnClickListener(v -> {
//            if (!isGroup) {
//                callContact(contactsModel.getPhone());
//            }
//        });
        containerProfile.setOnClickListener(v -> {
            if (AppHelper.isAndroid5())
                containerProfileInfo.post(() -> AnimationsUtil.hide(this, containerProfileInfo, Duration));
            else
                finish();
        });
        containerProfileInfo.setOnClickListener(v -> {
            if (AppHelper.isAndroid5())
                containerProfileInfo.post(() -> AnimationsUtil.hide(this, containerProfileInfo, Duration));
            else
                finish();
        });

    }

    /**
     * method to setup the progressBar
     */
    private void setupProgressBar() {
        ProgressBar mProgress = (ProgressBar) findViewById(R.id.progress_bar);
        mProgress.getIndeterminateDrawable().setColorFilter(Color.parseColor("#0EC654"),
                PorterDuff.Mode.SRC_IN);
    }

    /**
     * method to call a user
     */
    private void callContact(String phone) {

        try {
            String uri = "tel:" + phone.trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));

            if (AppHelper.checkPermission(this, Manifest.permission.CALL_PHONE)) {
                AppHelper.LogCat("CALL PHONE  permission already granted.");
            } else {
                AppHelper.LogCat("Please request CALL PHONE permission.");
                AppHelper.requestPermission(this, Manifest.permission.CALL_PHONE);
            }
            startActivity(intent);
        } catch (Exception e) {
            AppHelper.LogCat("error view contact " + e.getMessage());
        }
    }

    /**
     * method to show user information
     *
     * @param contactsModels this is parameter for  ShowContact method
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void ShowContact(ContactsModel contactsModels) {
        contactsModel = contactsModels;

    }


    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat(throwable.getMessage());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProfilePresenter.onDestroy();
    }


    @Override
    public void onBackPressed() {
        if (AppHelper.isAndroid5())
            containerProfileInfo.post(() -> AnimationsUtil.hide(this, containerProfileInfo, Duration));
        else
            finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, finish the activity.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()) {
            if (AppHelper.isAndroid5())
                containerProfileInfo.post(() -> AnimationsUtil.hide(this, containerProfileInfo, Duration));
            else
                finish();
            return true;
        }
        return super.onTouchEvent(event);
    }
}
