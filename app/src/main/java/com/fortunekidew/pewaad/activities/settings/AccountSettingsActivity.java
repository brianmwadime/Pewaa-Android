package com.fortunekidew.pewaad.activities.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.helpers.AppHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class AccountSettingsActivity extends AppCompatActivity {
    @BindView(R.id.app_bar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

/*

    @SuppressWarnings("unused")
    @OnClick(R.id.change_number)
    public void launchChangeNumber() {
        AppHelper.LaunchActivity(this, ChangeNumberActivity.class);
    }

*/

    @SuppressWarnings("unused")
    @OnClick(R.id.delete_account)
    public void launchDeleteAccount() {
        AppHelper.LaunchActivity(this, DeleteAccountActivity.class);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        finish();
    }
}
