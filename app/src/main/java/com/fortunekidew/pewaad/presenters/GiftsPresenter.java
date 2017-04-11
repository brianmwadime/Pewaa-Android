package com.fortunekidew.pewaad.presenters;

import android.os.Handler;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.services.apiServices.WishlistsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;

import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_CONTRIBUTER_REMOVED;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
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

        mWishlistsService = new WishlistsService(view.getApplicationContext(), mApiService);
        mWishlistsService.getGifts(wishlistID).subscribe(view::ShowGifts, view::onErrorLoading, view::onHideLoading);
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

        mWishlistsService = new WishlistsService(view.getApplicationContext(), mApiService);
        mWishlistsService.getGifts(wishlistID).subscribe(view::ShowGifts, view::onErrorLoading, view::onHideLoading);
    }

    @Override
    public void onStop() {

    }

    public void removeContributor(String wishlist_id, String contributor_id) {
        mWishlistsService.removeContributor(wishlist_id, contributor_id).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.wishlist_activity), "You have been removed successfully.", AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_EXIT_WISHLIST));
                 view.finish();
            } else {
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.wishlist_activity), "Something went wrong. Please try again later.", AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, AppHelper::LogCat);

    }

}
