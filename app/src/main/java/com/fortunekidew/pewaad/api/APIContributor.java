package com.fortunekidew.pewaad.api;

import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.gifts.GiftResponse;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.contribute.ContributorsModel;
import com.fortunekidew.pewaad.models.contribute.ContributorsResponse;

import java.util.ArrayList;
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

public interface APIContributor {
    /**
     * method to get all gift contributors
     *
     * @return this is return value
     */
    @GET(EndPoints.GIFT_CONTRIBUTORS)
    Call<List<ContributorsModel>> contributors(@Path("id") String giftId);

    @FormUrlEncoded
    @POST(EndPoints.GIFT_CASHOUT)
    Call<GiftResponse> cashOut(@Path("id") String giftId,
                               @Field("gift_name") String giftName,
                               @Field("gift_amount") String giftAmount);

    /**
     * method to add contributor
     *
     * @param contributorId this is the first parameter for  editContributor method
     * @param wishlistId this is the first parameter for  editContributor method
     * @param permissions  this is the thirded parameter for  editContributor method
     * @return this is return value
     */
    @FormUrlEncoded
    @POST(EndPoints.CONTRIBUTORS)
     Call<ContributorsResponse> editContributor(@Field("user_id") String contributorId,
                                                @Field("wishlist_id") String wishlistId,
                                                @Field("permissions") String permissions);

    @FormUrlEncoded
    @POST(EndPoints.SEND_CONTRIBUTORS)
    Call<StatusResponse> addContributors(@Field("contributors[]") ArrayList<String> listString, @Field("wishlist") String wishlist);

    /**
     * method to get a contributor's information
     *
     * @param contributorId this is  parameter for  getContributor method
     * @return this is return value
     */
    @GET(EndPoints.CONTRIBUTOR)
    Observable<ContributorsModel> getContributor(@Path("contributorId") String contributorId);
}
