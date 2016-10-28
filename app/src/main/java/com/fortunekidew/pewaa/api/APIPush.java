package com.fortunekidew.pewaa.api;

import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.models.users.status.StatusResponse;
import com.fortunekidew.pewaa.models.wishlists.ContributorsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by mwakima on 10/13/16.
 */

public interface APIPush {
    /**
     * method to get all devices
     *
     * @return this is return value
     */
    @GET(EndPoints.DEVICES)
    Observable<List<ContributorsModel>> devices();

    /**
     * method to create group
     *
     * @param deviceId this is the first parameter for  registerDevice method
     * @param userId this is the first parameter for  registerDevice method
     * @param platform  this is the thirded parameter for  registerDevice method
     * @return this is return value
     */
    @FormUrlEncoded
    @POST(EndPoints.DEVICES)
     Call<StatusResponse> registerDevice(@Field("device_id") String deviceId,
                                         @Field("platform") String platform);

    /**
     * method to get a device's information
     *
     * @param deviceId this is  parameter for  getDevice method
     * @return this is return value
     */
    @GET(EndPoints.DEVICE)
    Observable<ContributorsModel> getDevice(@Path("deviceId") String deviceId);
}
