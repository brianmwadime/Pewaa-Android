package com.fortunekidew.pewaad.sync;


import android.app.Service;
import android.content.Intent;

import android.os.IBinder;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class AuthenticatorService extends Service {

    private Authenticator mAuthenticator;

    public static final String ACCOUNT_TYPE = "com.fortunekidew.pewaad";

    public AuthenticatorService() {

    }


    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new Authenticator(this);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}

