package com.fortunekidew.pewaa.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.services.MainService;

import de.greenrobot.event.EventBus;

/**
 * Created by Abderrahim El imame on 8/18/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
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
