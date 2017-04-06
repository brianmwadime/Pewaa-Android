package com.fortunekidew.pewaad.api;

import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.ContributorsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public interface APIPush {
    /**
     * method to create group
     *
     * @param deviceId this is the first parameter for  registerDevice method
     * @param platform  this is the thirded parameter for  registerDevice method
     * @return this is return value
     */
    @FormUrlEncoded
    @POST(EndPoints.DEVICES)
     Call<StatusResponse> registerDevice(@Field("device_id") String deviceId,
                                         @Field("platform") String platform);

}
