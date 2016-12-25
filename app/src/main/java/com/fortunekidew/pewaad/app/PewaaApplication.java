package com.fortunekidew.pewaad.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.fortunekidew.pewaad.BuildConfig;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.services.MainService;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class PewaaApplication extends Application {
    private static Context AppContext;

    @Override
    public void onCreate() {
        super.onCreate();

        MultiDex.install(this);
        ButterKnife.setDebug(BuildConfig.DEBUG);
        AppContext = getApplicationContext();
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder(this)
                        .name(AppConstants.DATABASE_LOCAL_NAME)
                        .deleteRealmIfMigrationNeeded()
                        .build());
//        if (PreferenceManager.getToken(this) != null) {
//            startService(new Intent(this, MainService.class));
//        }
    }


    public static Context getAppContext() {
        return AppContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
