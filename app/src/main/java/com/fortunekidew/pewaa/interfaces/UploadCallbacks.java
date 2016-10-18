package com.fortunekidew.pewaa.interfaces;

import com.fortunekidew.pewaa.models.wishlists.MessagesModel;

/**
 * Created by Abderrahim El imame on 7/28/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public interface UploadCallbacks {
    void onUpdate(int percentage,String type);

    void onError(String type);

    void onFinish(String type, MessagesModel messagesModel);

}
