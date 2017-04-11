package com.fortunekidew.pewaad.interfaces;

/**
 * Created by Brian Mwakima on 10/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */

public interface LoadingData {

    void onShowLoading();

    void onHideLoading();

    void onErrorLoading(Throwable throwable);
}
