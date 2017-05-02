package com.fortunekidew.pewaad.api;

import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.models.DefaultResponse;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;
import com.fortunekidew.pewaad.models.gifts.GiftsModel;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
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
     * method to create a wishlist
     *
     * @return this is return value
     */
    @POST(EndPoints.WISHLISTS_LIST)
    Call<StatusResponse> editWishlist(@Body EditWishlist editWishlist);

    /**
     * method to report a wishlist
     *
     * @return this is return value
     */
    @PUT(EndPoints.WISHLISTS_REPORT)
    Call<DefaultResponse> reportWishlist(@Path("id") String wishlistId, @Body EditWishlist editWishlist);

    /**
     * method to get group information
     *
     * @param wishlistId this is  parameter for  getWishlist method
     * @return this is return value
     */
    @GET(EndPoints.GET_WISHLIST)
    Observable<WishlistsModel> getWishlist(@Path("id") String wishlistId);

    /**
     * method to get group information
     *
     * @param wishlistId this is  parameter for  getWishlist method
     * @return this is return value
     */
    @GET(EndPoints.WISHLIST_ITEMS)
    Observable<List<GiftsModel>> getGifts(@Path("id") String wishlistId);

    @DELETE(EndPoints.WISHLIST_DELETE_CONTRIBUTOR)
    Observable<StatusResponse> removeContributor(@Path("wishlist_id") String wishlist_id, @Path("contributor_id") String contributor_id);
}
