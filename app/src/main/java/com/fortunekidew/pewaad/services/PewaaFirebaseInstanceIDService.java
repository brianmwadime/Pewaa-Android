package com.fortunekidew.pewaad.services;

import com.fortunekidew.pewaad.api.APIPush;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.SignUpPreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Retrofit;


/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PewaaFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private SignUpPreferenceManager mPreferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferenceManager = new SignUpPreferenceManager(this);

    }

    @Override
    public void onTokenRefresh() {
        // Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Displaying token on logcat
        AppHelper.LogCat("Refreshed token: " + refreshedToken);
        mPreferenceManager.setDeviceToken(refreshedToken);
        mPreferenceManager.setIsDeviceSaved(false);

    }
}
