package com.fortunekidew.pewaad.activities.main;

import android.accounts.AccountManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class IntroActivity extends AppIntro {
    //for account manager
    private String mOldAccountType;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        initializerView();
        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
//        addSlide(firstFragment);
//        addSlide(secondFragment);
//        addSlide(thirdFragment);
//        addSlide(fourthFragment);

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(AppIntroFragment.newInstance("Create wishlists", "You can easily add, edit items on your wishlist, attach an image to each item.", R.drawable.intro_edit_white, Color.parseColor("#5f003871")));

        addSlide(AppIntroFragment.newInstance("Add contributors to wishlist", "Invite your family and friends to your wishlists.", R.drawable.intro_contributors, Color.parseColor("#5f003871")));

        addSlide(AppIntroFragment.newInstance("Add gifts", "Identify gifts Add/upload them from your camera or gallery.", R.drawable.intro_gallery, Color.parseColor("#5f003871")));

        addSlide(AppIntroFragment.newInstance("Administrators", "Assign down to earth administrators and watch as buddies team up to changa.", R.drawable.intro_highfive, Color.parseColor("#5f003871")));

        addSlide(AppIntroFragment.newInstance("Let buddy’s contribute", "An amazing collaborative effort to your happiness.", R.drawable.intro_handshake, Color.parseColor("#5f003871")));

        addSlide(AppIntroFragment.newInstance("Check out!", "Check out and wait for money to be deposited into your M-Pesa account.", R.drawable.intro_checkout, Color.parseColor("#5f003871")));

        // OPTIONAL METHODS
        // Override bar/separator color.
        //setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#5f003871"));

        // Hide Skip/Done button.
        //showSkipButton(false);
        //setProgressButtonEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
//        setVibrate(true);
//        setVibrateIntensity(30);
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        /**
         * Checking if user already connected
         */
        if (getIntent().hasExtra(AccountManager.KEY_ACCOUNT_TYPE)) {
            mOldAccountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            AppHelper.LogCat("IntentData is not null IntroActivity " + mOldAccountType);
        } else {
            /**
             * Checking if user already connected
             */
            if (PreferenceManager.getToken(this) != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        Intent intent = new Intent(this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}
