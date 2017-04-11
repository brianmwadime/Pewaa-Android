package com.fortunekidew.pewaad.interfaces;

import com.fortunekidew.pewaad.models.wishlists.MessagesModel;

/**
 * Created by Brian Mwakima on 10/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */

public interface DownloadCallbacks {
    void onUpdate(int percentage,String type);

    void onError(String type);

    void onFinish(String type, MessagesModel messagesModel);

}
