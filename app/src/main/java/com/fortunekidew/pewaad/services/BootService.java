package com.fortunekidew.pewaad.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fortunekidew.pewaad.app.PewaaApplication;

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
        startService(new Intent(PewaaApplication.getAppContext(), MainService.class));
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
