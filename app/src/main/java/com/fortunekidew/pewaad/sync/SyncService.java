package com.fortunekidew.pewaad.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fortunekidew.pewaad.helpers.AppHelper;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class SyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter mSyncAdapter = null;

    @Override
    public void onCreate() {
        AppHelper.LogCat("Sync Service created.");
        synchronized (sSyncAdapterLock) {
            if (mSyncAdapter == null) {
                mSyncAdapter = new SyncAdapter(getApplicationContext(),
                        true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        AppHelper.LogCat("Sync Service binded.");
        return mSyncAdapter.getSyncAdapterBinder();
    }
}