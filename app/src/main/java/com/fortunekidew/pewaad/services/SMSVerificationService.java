package com.fortunekidew.pewaad.services;

import android.app.IntentService;
import android.content.Intent;

import com.fortunekidew.pewaad.activities.main.MainActivity;
import com.fortunekidew.pewaad.api.APIAuthentication;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.models.JoinModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class SMSVerificationService extends IntentService {


    public SMSVerificationService() {
        super(SMSVerificationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String code = intent.getStringExtra("code");
            verifyUser(code);
        }
    }

    private void verifyUser(String code) {
        APIAuthentication mAPIAuthentication = APIService.RootService(APIAuthentication.class, EndPoints.BASE_URL);
        Call<JoinModel> VerifyUser = mAPIAuthentication.verifyUser(code);
        VerifyUser.enqueue(new Callback<JoinModel>() {
            @Override
            public void onResponse(Call<JoinModel> call, Response<JoinModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        AppHelper.CustomToast(getApplicationContext(), response.body().getMessage());
                        PreferenceManager.setID(response.body().getUserID(), SMSVerificationService.this);
                        PreferenceManager.setToken(response.body().getToken(), SMSVerificationService.this);
                        PreferenceManager.setNumber(response.body().getMobile(), SMSVerificationService.this);
                        Intent intent = new Intent(SMSVerificationService.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                    } else {
                        AppHelper.CustomToast(getApplicationContext(), response.body().getMessage());
                    }
                } else {
                    AppHelper.CustomToast(getApplicationContext(), response.message());
                }
            }

            @Override
            public void onFailure(Call<JoinModel> call, Throwable t) {
                AppHelper.LogCat("SMS verification failure  SMSVerificationService" + t.getMessage());
                AppHelper.CustomToast(getApplicationContext(), t.getMessage());

            }
        });

    }
}
