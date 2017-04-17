package com.fortunekidew.pewaad.helpers;

import android.annotation.TargetApi;
import android.os.Build;
import android.telephony.PhoneNumberFormattingTextWatcher;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PhoneNumberWatcher extends PhoneNumberFormattingTextWatcher {


    @SuppressWarnings("unused")
    public PhoneNumberWatcher() {
        super();
    }

    //TODO solve it! support for android kitkat
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PhoneNumberWatcher(String countryCode) {
        super(countryCode);
    }
}