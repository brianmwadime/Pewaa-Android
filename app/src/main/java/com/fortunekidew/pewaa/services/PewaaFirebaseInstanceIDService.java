package com.fortunekidew.pewaa.services;

import com.fortunekidew.pewaa.BuildConfig;
import com.fortunekidew.pewaa.api.APIPush;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.models.users.status.StatusResponse;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fortunekidew.pewaa.app.EndPoints.BASE_URL;


/**
 * Created by mwakima on 10/26/16.
 */

public class PewaaFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private APIPush mApiService;
    Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Customize the request
            Request request = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("token", PreferenceManager.getToken(PewaaApplication.getAppContext()))
                    .method(original.method(), original.body())
                    .build();
            // Customize or return the response
            return chain.proceed(request);
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();

        retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        mApiService = retrofit.create(APIPush.class);

    }

    @Override
    public void onTokenRefresh() {

        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        //Displaying token on logcat
        AppHelper.LogCat("Refreshed token: " + refreshedToken);
        registerDevice(refreshedToken);

    }

    private void registerDevice(String token) {
        Call<StatusResponse> call = mApiService.registerDevice(token, "ANDROID");
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                int statusCode = response.code();

            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                // Log error here since request failed
            }
        });

    }

}
