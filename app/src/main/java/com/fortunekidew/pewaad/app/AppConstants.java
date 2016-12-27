package com.fortunekidew.pewaad.app;

import com.fortunekidew.pewaad.BuildConfig;
import com.fortunekidew.pewaad.R;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class AppConstants {

    /* debugging constants  for developer */
    public static final String TAG = "pewaa";
    public static final boolean DEBUGGING_MODE = BuildConfig.DEBUG;


    // Wishlist Permissions
    public static final String WISHLIST_ADMINISTRATOR = "ADMINISTRATOR";
    public static final String WISHLIST_CONTRIBUTOR = "CONTRIBUTOR";

    public static final int MESSAGE_COLOR_WARNING = R.color.colorOrangeLight;
    public static final int MESSAGE_COLOR_SUCCESS = R.color.colorGreenDark;
    public static final int TEXT_COLOR = R.color.colorWhite;
    public static final String DATABASE_LOCAL_NAME = "Pewaa.realm";
    public static final String INVITE_MESSAGE_SMS = "Hello checkout the Pewaa application https://play.google.com/apps/testing/com.fortunekidew.pewaad";


    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  WatsClone, TESTER  as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_SENDER_NAME = "Verify";
    // special character to prefix the code. Make sure this character appears only once in the sms
    public static final String CODE_DELIMITER = ":";

    /**
     * upload image or video constants
     */
    public static final int UPLOAD_PICTURE_REQUEST_CODE = 0x001;
    public static final int SELECT_PROFILE_PICTURE = 0x005;
    public static final int SELECT_PROFILE_CAMERA = 0x006;
    public static final int SELECT_MESSAGES_CAMERA = 0x007;
    public static final int PERMISSION_REQUEST_CODE = 0x009;

    /**
     * Contributor constants
     */

    //contributor constants:
    public static final int STATUS_CONTRIBUTOR_ADDED = 0x010;
    public static final int STATUS_CONTRIBUTOR_ADDED_SUCCESS = 0x011;


    //single user socket constants:

    public static final String SOCKET_USER_PING = "user_ping";
    public static final String SOCKET_IS_TYPING = "typing";
    public static final String SOCKET_IS_STOP_TYPING = "stop_typing";
    public static final String SOCKET_IS_ONLINE = "is_online";
    public static final String SOCKET_IS_LAST_SEEN = "last_seen";
    public static final String SOCKET_CONNECTED = "user_connect";
    public static final String SOCKET_DISCONNECTED = "user_disconnect";


    // Payments
    public static final String SOCKET_PAYMENT_COMPLETED = "payment_completed";

    public static final String SOCKET_CONTRIBUTOR_ADDED = "added_contributor";

    public static final String SOCKET_GIFT_ADDED = "added_gift";

}
