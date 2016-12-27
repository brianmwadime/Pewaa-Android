package com.fortunekidew.pewaad.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.services.MainService;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class NetworkChangeListener extends BroadcastReceiver {
    private boolean userIsConnected;

    @Override
    public void onReceive(Context mContext, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case "android.net.conn.CONNECTIVITY_CHANGE":
                if (isConnected()) {
                    AppHelper.LogCat("Connection is available");
                    setUserIsConnected(true);
                    mContext.stopService(new Intent(mContext, MainService.class));
                    mContext.startService(new Intent(mContext, MainService.class));
                } else {
                    AppHelper.LogCat("Connection is not available");
                    mContext.stopService(new Intent(mContext, MainService.class));
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


    /**
     * method to check if the user connection internet is available
     *
     * @return state of network
     */
    public static boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) PewaaApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

}
