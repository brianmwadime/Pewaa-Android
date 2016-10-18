package com.fortunekidew.pewaa.services.apiServices;

import android.content.Context;

import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.api.APIWishlists;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.models.users.status.StatusResponse;
import com.fortunekidew.pewaa.models.wishlists.EditWishlist;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;
import com.fortunekidew.pewaa.models.wishlists.WishlistsModel;

import java.util.List;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Brian Mwakima on 20/04/2016.
 * Email : mwadime@fortunekidew.co.ke
 */
public class WishlistsService {
    private APIWishlists mApiWishlists;
    private Realm realm;
    private Context mContext;
    private APIService mApiService;

    public WishlistsService(Realm realm, Context context, APIService mApiService) {
        this.mContext = context;
        this.realm = realm;
        this.mApiService = mApiService;
    }

    /**
     * method to initialize the api wishlists
     * @return return value
     */
    private APIWishlists initializeApiWishlists() {
        if (mApiWishlists == null) {
            mApiWishlists = this.mApiService.RootService(APIWishlists.class, PreferenceManager.getToken(PewaaApplication.getAppContext()), EndPoints.BASE_URL);
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
     * method to get single wishlist information
     * @param wishlistID this is parameter for  getGroupWishlist method
     * @return return value
     */
    public Observable<WishlistsModel> getGroupInfo(String wishlistID) {
        return initializeApiWishlists().getWishlist(wishlistID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(this::copyOrUpdateWishlist);
    }

    /**
     * method to get wishlist information from local
     * @param wishlistID this is parameter for getWishlist method
     * @return return value
     */
    public Observable<WishlistsModel> getWishlist(String wishlistID) {
        return realm.where(WishlistsModel.class).equalTo("id", wishlistID).findFirst().asObservable();
    }

    /**
     * method to copy or update wishlists list
     * @param wishlists this is parameter for copyOrUpdateWishlists method
     * @return return value
     */
    private List<WishlistsModel> copyOrUpdateWishlists(List<WishlistsModel> wishlists) {
        realm.beginTransaction();
        List<WishlistsModel> realmWishlists = realm.copyToRealmOrUpdate(wishlists);
        realm.commitTransaction();
        return realmWishlists;
    }

    /**
     * method to copy or update wishlists list
     * @param wishlists this is parameter for copyOrUpdateWishlists method
     * @return return value
     */
    private List<GiftsModel> copyOrUpdateGifts(List<GiftsModel> gifts) {
        realm.beginTransaction();
        List<GiftsModel> realmGifts = realm.copyToRealmOrUpdate(gifts);
        realm.commitTransaction();
        return realmGifts;
    }

    /**
     * methid to copy or update a single wishlist
     * @param wishlist this is parameter for copyOrUpdateWishlist method
     * @return return value
     */
    private WishlistsModel copyOrUpdateWishlist(WishlistsModel wishlist) {
        realm.beginTransaction();
        WishlistsModel wishlistsModel = realm.where(WishlistsModel.class).equalTo("id",wishlist.getId()).findFirst();
        WishlistsModel realmWishlists = realm.copyToRealmOrUpdate(wishlistsModel);
        realm.commitTransaction();
        return realmWishlists;
    }

    /**
     * method to create/edit wishlist
     *
     * @param name this is the first parameter for editWishlist method
     * @param description  this is the second parameter for editWishlist method
     * @return return  value
     */
    public Observable<StatusResponse> editWishlist(String name, String description) {
        EditWishlist editWishlist = new EditWishlist();
        editWishlist.setName(name);
        editWishlist.setDescription(description);
        return initializeApiWishlists().editWishlist(editWishlist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(statusResponse -> statusResponse);
    }
}
