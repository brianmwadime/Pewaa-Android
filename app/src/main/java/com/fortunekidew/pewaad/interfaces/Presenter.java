package com.fortunekidew.pewaad.interfaces;

/**
 * Created by Brian Mwakima on 10/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */

public interface Presenter {
    void onStart();

    void onCreate();

    void onPause();

    void onResume();

    void onDestroy();

    void onLoadMore();

    void onRefresh();

    void onStop();
}
