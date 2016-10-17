package com.fortunekidew.pewaa.api;

import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;
import com.fortunekidew.pewaa.models.wishlists.WishlistsModel;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by mwakima on 10/13/16.
 */

public interface APIGifts {
    /**
     * method to get all gifts
     *
     * @return this is return value
     */
    @GET(EndPoints.GIFTS)
    Observable<List<GiftsModel>> gifts();

    /**
     * method to add a gift
     *
     * @return this is return value
     */
    @POST(EndPoints.GIFTS)
    Observable<List<GiftsModel>> addGift();

    /**
     * method to get a gift's information
     *
     * @param giftId this is  parameter for  getGift method
     * @return this is return value
     */
    @GET(EndPoints.GIFT)
    Observable<WishlistsModel> getGift(@Path("giftId") String giftId);
}
