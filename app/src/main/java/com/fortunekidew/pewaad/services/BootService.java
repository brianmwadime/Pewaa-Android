package com.fortunekidew.pewaad.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fortunekidew.pewaad.helpers.PreferenceManager;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class BootService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        new Handler().postDelayed(() -> {
            if (PreferenceManager.getToken(BootService.this) != null) {
                startService(new Intent(BootService.this, MainService.class));
            }
        }, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // service On start
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // service finished
        super.onDestroy();
    }
}
