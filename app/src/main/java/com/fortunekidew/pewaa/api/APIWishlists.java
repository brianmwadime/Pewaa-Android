package com.fortunekidew.pewaa.api;

import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.models.users.status.StatusResponse;
import com.fortunekidew.pewaa.models.wishlists.EditWishlist;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;
import com.fortunekidew.pewaa.models.wishlists.WishlistsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by mwakima on 10/12/16.
 */

public interface APIWishlists {

    /**
     * method to get all wishlists
     *
     * @return this is return value
     */
    @GET(EndPoints.WISHLISTS_LIST)
    Observable<List<WishlistsModel>> wishlists();

    /**
     * method to get all wishlist gifts
     *
     * @return this is return value
     */
    @POST(EndPoints.WISHLISTS_LIST)
    Call<StatusResponse> editWishlist(@Body EditWishlist editWishlist);

    /**
     * method to get group information
     *
     * @param wishlistId this is  parameter for  getWishlist method
     * @return this is return value
     */
    @GET(EndPoints.GET_WISHLIST)
    Observable<WishlistsModel> getWishlist(@Path("wishlistId") String wishlistId);

    /**
     * method to get group information
     *
     * @param wishlistId this is  parameter for  getWishlist method
     * @return this is return value
     */
    @GET(EndPoints.WISHLIST_ITEMS)
    Observable<List<GiftsModel>> getGifts(@Path("wishlistId") String wishlistId);
}
