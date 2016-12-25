package com.fortunekidew.pewaad.sync;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * Created by Abderrahim El imame on 01/03/2016.
 * Email : abderrahim.elimame@gmail.com
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

