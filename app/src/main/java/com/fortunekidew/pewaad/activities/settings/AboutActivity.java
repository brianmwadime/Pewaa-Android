package com.fortunekidew.pewaad.activities.settings;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.helpers.AppHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class AboutActivity extends AppCompatActivity {

    @BindView(R.id.version)
    TextView version;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        PackageInfo packageinfo;
        try {
            packageinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String appVersion = packageinfo.versionName;
            version.setText(getString(R.string.app_version) + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            AppHelper.LogCat(" About NameNotFoundException " + e.getMessage());
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        finish();
    }

}
