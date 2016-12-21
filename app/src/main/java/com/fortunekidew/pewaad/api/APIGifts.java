package com.fortunekidew.pewaad.api;

import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.payments.ConfirmPaymentResponse;
import com.fortunekidew.pewaad.models.wishlists.GiftResponse;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
     * method to create group
     *
     * @param wishlistID this is the first parameter for  editGift method
     * @param name   this is the second parameter for  editGift method
     * @param image  this is the thirded parameter for  editGift method
     * @param description    this is the fourth  parameter for  editGift method
     * @param price   this is the fifth parameter for  editGift method
     * @return this is return value
     */
    @Multipart
    @POST(EndPoints.GIFTS)
     Call<GiftResponse> editGift(@Part("wishlist_id") RequestBody wishlistID,
                                @Part("name") RequestBody name,
                                @Part("image\"; filename=\"giftImage\" ") RequestBody image,
                                @Part("description") RequestBody description,
                                @Part("price") float price);

    /**
     * method to get a gift's information
     *
     * @param giftId this is  parameter for  getGift method
     * @return this is return value
     */
    @GET(EndPoints.GIFT)
    Observable<WishlistsModel> getGift(@Path("giftId") String giftId);

    @GET(EndPoints.GIFT_CONTRIBUTORS)
    Call<ConfirmPaymentResponse> getContributors(@Path("id") String giftId);
}
