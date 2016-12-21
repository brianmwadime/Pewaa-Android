package com.fortunekidew.pewaad.api;

import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;

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
