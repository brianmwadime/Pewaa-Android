package com.fortunekidew.pewaad.services;

import com.fortunekidew.pewaad.api.APIPush;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.SignUpPreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import retrofit2.Retrofit;


/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PewaaFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private APIPush mApiService;
    Retrofit retrofit;
    private SignUpPreferenceManager mPreferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();

        mPreferenceManager = new SignUpPreferenceManager(this);
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//        httpClient.addInterceptor(chain -> {
//            Request original = chain.request();
//
//            // Customize the request
//            Request request = original.newBuilder()
//                    .header("Accept", "application/json")
//                    .method(original.method(), original.body())
//                    .build();
//            // Customize or return the response
//            return chain.proceed(request);
//        });
//
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        if (BuildConfig.DEBUG) {
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        } else {
//            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//        }
//        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
//
//        retrofit = new Retrofit.Builder()
//                    .baseUrl(BASE_URL)
//                    .client(client)
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//
//        mApiService = retrofit.create(APIPush.class);

    }

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        AppHelper.LogCat("Refreshed token: " + refreshedToken);
//        registerDevice(refreshedToken);
        mPreferenceManager.setDeviceToken(refreshedToken);
        mPreferenceManager.setIsDeviceSaved(false);

    }

    public void saveToken(String token) {

    }



}
