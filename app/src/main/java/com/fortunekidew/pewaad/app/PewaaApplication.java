package com.fortunekidew.pewaad.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.fortunekidew.pewaad.BuildConfig;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.ExceptionHandler;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.NetworkListener;
import com.fortunekidew.pewaad.receivers.NetworkChangeListener;
import com.fortunekidew.pewaad.services.BootService;
import com.fortunekidew.pewaad.services.MainService;
import com.orhanobut.logger.Logger;

import java.net.URISyntaxException;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PewaaApplication extends Application {
    static PewaaApplication mInstance;
    public static final long TIMEOUT = 20 * 1000;
    private static final long RECONNECT_DEALY = 100;
    private static final int RETRY_ATTEMPT = 10;
    private static Context AppContext;
    private Socket mSocket = null;

    {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.timeout = TIMEOUT; //set -1 to  disable it
        options.reconnection = true;
        options.reconnectionDelay = RECONNECT_DEALY;
        options.reconnectionAttempts = RETRY_ATTEMPT;
        options.query = "token=" + AppConstants.APP_KEY_SECRET;
        try {
            mSocket = IO.socket(EndPoints.CHAT_SERVER_URL, options);
        } catch (URISyntaxException e) {
            AppHelper.LogCat(e);
            Crashlytics.logException(e);
        }

    }

    public static void setupCrashlytics() {
        Crashlytics crashlyticsKit = new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build();
        Fabric.with(mInstance, crashlyticsKit, new Crashlytics());
        Crashlytics.setUserEmail(PreferenceManager.getPhone(getInstance()));
        Crashlytics.setUserName(PreferenceManager.getPhone(getInstance()));
        Crashlytics.setUserIdentifier(String.valueOf(PreferenceManager.getID(getInstance())));
    }

    public Socket getSocket() {
        return mSocket;
    }

    public void reConnectSocket() {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.timeout = TIMEOUT; //set -1 to  disable it
        options.reconnection = true;
        options.reconnectionDelay = RECONNECT_DEALY;
        options.reconnectionAttempts = RETRY_ATTEMPT;
        options.query = "token=" + AppConstants.APP_KEY_SECRET;
        try {
            mSocket = IO.socket(EndPoints.CHAT_SERVER_URL, options);
        } catch (URISyntaxException e) {
            AppHelper.LogCat(e);
            Crashlytics.logException(e);
        }

    }

    public static synchronized PewaaApplication getInstance() {
        return mInstance;
    }

    public void setmInstance(PewaaApplication mInstance) {
        PewaaApplication.mInstance = mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setmInstance(this);
//        MultiDex.install(this);
        ButterKnife.setDebug(BuildConfig.DEBUG);
        Realm.init(this);
        startService(new Intent(this, BootService.class));

        if (AppConstants.DEBUGGING_MODE)
            Logger.init(AppConstants.TAG).hideThreadInfo();

        if (AppConstants.ENABLE_CRASH_HANDLER)
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
    }


    public static Context getAppContext() {
        return AppContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void setConnectivityListener(NetworkListener listener) {
        NetworkChangeListener.networkListener = listener;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    private static RealmConfiguration getRealmDatabaseConfiguration() {
        return new RealmConfiguration.Builder().name(getInstance().getString(R.string.app_name) + PreferenceManager.getToken(getInstance()) + ".realm").deleteRealmIfMigrationNeeded().build();
    }

    public static Realm getRealmDatabaseInstance() {
        return Realm.getInstance(getRealmDatabaseConfiguration());
    }

    public static boolean DeleteRealmDatabaseInstance() {
        return Realm.deleteRealm(getRealmDatabaseConfiguration());
    }

}
