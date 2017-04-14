package com.fortunekidew.pewaad.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.interfaces.NetworkListener;


import org.greenrobot.eventbus.EventBus;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class NetworkChangeListener extends BroadcastReceiver {
    private boolean userIsConnected;
    private boolean is_Connected = false;
    public static NetworkListener networkListener;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case "android.net.conn.CONNECTIVITY_CHANGE":
                if (isConnected()) {
                    AppHelper.LogCat("Connection is available");
                    setUserIsConnected(true);
                } else {
                    AppHelper.LogCat("Connection is not available");
                    setUserIsConnected(false);

                }

                break;
        }
        EventBus.getDefault().post(NetworkChangeListener.this);
    }

    public boolean isUserIsConnected() {
        return userIsConnected;
    }

    public void setUserIsConnected(boolean userIsConnected) {
        this.userIsConnected = userIsConnected;
    }

    private void isNetworkAvailable(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean is_Connecting = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (networkListener != null) {
            networkListener.onNetworkConnectionChanged(is_Connecting, is_Connected);
        }

        if (!is_Connecting && !is_Connected) {
            AppHelper.LogCat("Connection is not available");
        } else if (is_Connecting && is_Connected) {
            AppHelper.LogCat("Connection is available");
        } else {
            AppHelper.LogCat("Connection is available but waiting for network");
        }
    }
    /**
     * method to check if the user connection internet is available
     *
     * @return state of network
     */
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) PewaaApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
