package com.fortunekidew.pewaad.services.apiServices;

import android.content.Context;

import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.api.APIWishlists;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class WishlistsService {
    private APIWishlists mApiWishlists;
    private Context mContext;
    private APIService mApiService;

    public WishlistsService(Context context, APIService mApiService) {
        this.mContext = context;
        this.mApiService = mApiService;

    }

    /**
     * method to initialize the api wishlists
     * @return return value
     */
    private APIWishlists initializeApiWishlists() {
        if (mApiWishlists == null) {
            mApiWishlists = this.mApiService.RootService(APIWishlists.class, PreferenceManager.getToken(PewaaApplication.getInstance()), EndPoints.BASE_URL);
        }
        return mApiWishlists;
    }

    /**
     * method to get wishlists list
     *
     * @return return value
     */
    public Observable<List<WishlistsModel>> getWishlists() {
        return initializeApiWishlists().wishlists()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::copyOrUpdateWishlists);
//        List<WishlistsModel> wishlists = realm.where(WishlistsModel.class).findAll();
//        return Observable.just(wishlists);
    }

    /**
     * method to get wishlist gifts list
     *
     * @return return value
     */
    public Observable<List<GiftsModel>> getGifts(String wishlistId) {
        return initializeApiWishlists().getGifts(wishlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::copyOrUpdateGifts);
    }

    /**
     * method to copy or update wishlists list
     * @param wishlists this is parameter for copyOrUpdateWishlists method
     * @return return value
     */
    private List<WishlistsModel> copyOrUpdateWishlists(List<WishlistsModel> wishlists) {
        return wishlists;
    }

    /**
     * method to copy or update wishlists list
     * @param gifts this is parameter for copyOrUpdateWishlists method
     * @return return value
     */
    private List<GiftsModel> copyOrUpdateGifts(List<GiftsModel> gifts) {
        return  gifts;
    }

    public Observable<StatusResponse> removeContributor(String wishlist_id, String contributor_id) {
        return initializeApiWishlists().removeContributor(wishlist_id, contributor_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }
}
