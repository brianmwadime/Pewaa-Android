package com.fortunekidew.pewaad.presenters;

import android.os.Handler;

import com.fortunekidew.pewaad.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.services.apiServices.WishlistsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class GiftsPresenter implements Presenter {
    private final WishlistActivity view;
    private final Realm realm;
    private WishlistsService mWishlistsService;
    private static final int UPDATE_MINUTES = 2 * 60 * 1000;
    private String wishlistID;


    public GiftsPresenter(WishlistActivity wishlistActivity) {
        this.view = wishlistActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(view)) EventBus.getDefault().register(view);
        Handler handler = new Handler();
        APIService mApiService = APIService.with(view.getApplicationContext());

        if (view.getIntent().hasExtra(view.RESULT_EXTRA_WISHLIST_ID)) {
            wishlistID = view.getIntent().getExtras().getString(view.RESULT_EXTRA_WISHLIST_ID);
        }

        mWishlistsService = new WishlistsService(realm, view.getApplicationContext(), mApiService);
        mWishlistsService.getGifts(wishlistID).subscribe(view::ShowGifts, view::onErrorLoading, view::onHideLoading);
//        loadDataLocal();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(view);
        realm.close();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onRefresh() {
        APIService mApiService = APIService.with(view.getApplicationContext());

        if (view.getIntent().hasExtra("wishlistID")) {
            wishlistID = view.getIntent().getExtras().getString("wishlistID");
        }

        mWishlistsService = new WishlistsService(realm, view.getApplicationContext(), mApiService);
        mWishlistsService.getGifts(wishlistID).subscribe(view::ShowGifts, view::onErrorLoading, view::onHideLoading);
    }

    @Override
    public void onStop() {

    }

}
