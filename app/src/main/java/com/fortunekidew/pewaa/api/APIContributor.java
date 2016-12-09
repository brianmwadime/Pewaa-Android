package com.fortunekidew.pewaa.api;

import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.models.wishlists.ContributorsModel;
import com.fortunekidew.pewaa.models.wishlists.ContributorsResponse;

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

public interface APIContributor {
    /**
     * method to get all gift contributors
     *
     * @return this is return value
     */
    @GET(EndPoints.GIFT_CONTRIBUTORS)
    Call<List<ContributorsModel>> contributors(@Path("id") String giftId); // , @Path("page") int page, @Path("count") int giftCount

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

    /**
     * method to get a contributor's information
     *
     * @param contributorId this is  parameter for  getContributor method
     * @return this is return value
     */
    @GET(EndPoints.CONTRIBUTOR)
    Observable<ContributorsModel> getContributor(@Path("contributorId") String contributorId);
}
