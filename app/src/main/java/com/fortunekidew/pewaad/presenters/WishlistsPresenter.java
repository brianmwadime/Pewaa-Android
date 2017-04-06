package com.fortunekidew.pewaad.presenters;

import android.os.Handler;

import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.WishlistsFragment;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.services.apiServices.WishlistsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class WishlistsPresenter implements Presenter {
    private final WishlistsFragment wishlistsFragmentView;
    private final Realm realm;
    private WishlistsService mWishlistsService;
    private static final int UPDATE_MINUTES = 2 * 60 * 1000;


    public WishlistsPresenter(WishlistsFragment wishlistsFragment) {
        this.wishlistsFragmentView = wishlistsFragment;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        Handler handler = new Handler();
        APIService mApiService = APIService.with(wishlistsFragmentView.getActivity());

        if (!EventBus.getDefault().isRegistered(wishlistsFragmentView))
            EventBus.getDefault().register(wishlistsFragmentView);

        mWishlistsService = new WishlistsService(realm, wishlistsFragmentView.getActivity(), mApiService);
        mWishlistsService.getWishlists().subscribe(wishlistsFragmentView::ShowWishlist, wishlistsFragmentView::onErrorLoading, wishlistsFragmentView::onHideLoading);
    }

    public void updateWishlistList() {
        mWishlistsService.getWishlists().subscribe(wishlistsFragmentView::UpdateWishlist, wishlistsFragmentView::onErrorLoading, wishlistsFragmentView::onHideLoading);
    }
    private void loadDataLocal() {
        mWishlistsService.getWishlists().subscribe(wishlistsFragmentView::ShowWishlist, wishlistsFragmentView::onErrorLoading, wishlistsFragmentView::onHideLoading);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(wishlistsFragmentView);
        realm.close();
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onRefresh() {
        APIService mApiService = APIService.with(wishlistsFragmentView.getActivity());

        if (!EventBus.getDefault().isRegistered(wishlistsFragmentView))
            EventBus.getDefault().register(wishlistsFragmentView);

        mWishlistsService = new WishlistsService(realm, wishlistsFragmentView.getActivity(), mApiService);
        mWishlistsService.getWishlists().subscribe(wishlistsFragmentView::ShowWishlist, wishlistsFragmentView::onErrorLoading, wishlistsFragmentView::onHideLoading);
    }

    @Override
    public void onStop() {

    }

}
