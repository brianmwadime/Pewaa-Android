package com.fortunekidew.pewaad.presenters;


import com.fortunekidew.pewaad.activities.search.SearchConversationsActivity;
import com.fortunekidew.pewaad.interfaces.Presenter;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class SearchConversationsPresenter implements Presenter {
    private SearchConversationsActivity mSearchConversationsActivity;
    private Realm realm;


    public SearchConversationsPresenter(SearchConversationsActivity mSearchConversationsActivity) {
        this.mSearchConversationsActivity = mSearchConversationsActivity;
        this.realm = Realm.getDefaultInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
//        WishlistsService mWishlistsService = new WishlistsService(realm);
//        mWishlistsService.getWishlists().subscribe(mSearchConversationsActivity::ShowConversation, mSearchConversationsActivity::onErrorLoading);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }
}