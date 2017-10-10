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
    public static final boolean DEBUGGING_MODE = BuildConfig.DEBUG; // BuildConfig.DEBUG;
    public static final boolean CRASH_LYTICS = !BuildConfig.DEBUG;//this is crashlytics if you have an account on fabric.io but don't forget to change the key on manifests file
    static final boolean ENABLE_CRASH_HANDLER = !BuildConfig.DEBUG; // this for the crash activity  you can turn on this so when user get a crash this activity will appear instead of stop the app
    // Wishlist Permissions
    public static final double GIFT_SERVICE_CHARGE = 0.035;
    public static final String WISHLIST_ADMINISTRATOR = "ADMINISTRATOR";
    public static final String WISHLIST_CONTRIBUTOR = "CONTRIBUTOR";
    public static final String ACCOUNT_TYPE = "com.fortunekidew.pewaad";//for this you have to set you package name here we use this add the app to account manager in user phone
    public static final String DATABASE_LOCAL_NAME = "Pewaa.realm";
    public static final String INVITE_MESSAGE_SMS = "Hi help me win 500/= by downloading this cool app Pewaa ";
    // SMS provider identification
    // It should match with your SMS gateway origin
    // You can use  WatsClone, TESTER  as sender ID
    // If you want custom sender Id, approve MSG91 to get one
    public static final String SMS_SENDER_NAME = "Verify";
    // special character to prefix the code. Make sure this character appears only once in the sms
    public static final String CODE_DELIMITER = ":";
    public static final String LABEL_TERMS_LINK = "http://www.pewaa.com/legal";
    /**
     * upload image or video constants
     */
    public static final int UPLOAD_PICTURE_REQUEST_CODE = 0x001;
    public static final int SELECT_PROFILE_PICTURE = 0x005;
    public static final int SELECT_PROFILE_CAMERA = 0x006;
    public static final int SELECT_GIFT_CAMERA = 0x007;
    public static final int PERMISSION_REQUEST_CODE = 0x009;
    public static final int CONTACTS_PERMISSION_REQUEST_CODE = 0x010;
    public static final int SELECT_COUNTRY = 0x011;

    /**
     * Contributor constants
     */
    public static final int STATUS_CONTRIBUTOR_ADDED = 0x011;
    public static final int STATUS_CONTRIBUTOR_ADDED_SUCCESS = 0x012;
    public static final int STATUS_SELECTED_CONTRIBUTORS_SUCCESS = 0x013;
    public static final int STATUS_REPORT_SUCCESS = 0x014;
    public static final int STATUS_REPORT_WISHLIST_SUCCESS = 0x015;
    public static final int STATUS_REPORT_CANCELLED = 0x16;

    public enum REPORT_TYPE { WISHLIST, GIFT }

    /**
     * for cache
     */
    public static final String GROUP = "gp";
    public static final String USER = "ur";
    public static final String PROFILE_PREVIEW = "prp";
    public static final String FULL_PROFILE = "fp";
    public static final String SETTINGS_PROFILE = "spr";
    public static final String EDIT_PROFILE = "epr";
    public static final String ROW_PROFILE = "rpr";
    public static final String PENDING_STATUS = "PENDING";
    public static final String EVENT_BUS_COUNTRY_CODE = "countryCode";
    public static final String EVENT_BUS_COUNTRY_NAME = "countryName";
    public static final String SOCKET_CASHOUT = "cashout_request";
    public static final String DATA_CACHED = "DATA_CACHED";
    /**
     * images size
     */
    public static final int NOTIFICATIONS_IMAGE_SIZE = 150;
    public static final int ROWS_IMAGE_SIZE = 90;
    public static final int PROFILE_PREVIEW_IMAGE_SIZE = 500;
    public static final int PROFILE_PREVIEW_BLUR_IMAGE_SIZE = 50;
    public static final int PROFILE_IMAGE_SIZE = 500;
    public static final int SETTINGS_IMAGE_SIZE = 100;
    public static final int EDIT_PROFILE_IMAGE_SIZE = 500;
    public static final int EDIT_PROFILE_SMALL_IMAGE_SIZE = 234;
    public static final int BLUR_RADIUS = 1;
    // Payments
    public static final String SOCKET_PAYMENT_COMPLETED = "payment_completed";
    public static final String SOCKET_IMAGE_PROFILE_UPDATED = "socket_profileImageUpdated";
    public static final String SOCKET_CONTRIBUTOR_ADDED = "added_contributor";
    public static final String SOCKET_GIFT_ADDED = "added_gift";
    static final String APP_KEY_SECRET = "7d3d3b6c2d3683bf25bbb51533ec6dac";// make sure this one is the same that you put on your server side (for security reasons)
    /**
     * for toast and snackbar
     */
    public static final int MESSAGE_COLOR_ERROR = R.color.colorOrangeLight;
    public static final int MESSAGE_COLOR_WARNING = R.color.colorOrange;
    public static final int MESSAGE_COLOR_SUCCESS = R.color.colorGreenDark;
    public static final int TEXT_COLOR = R.color.colorWhite;
    /**
     * those for EventBus tool
     */
    public static final String EVENT_BUS_CONTACTS_PERMISSION = "ContactsPermission";
    public static final String EVENT_BUS_UPDATE_CONTACTS_LIST = "updatedContactsList";
    public static final String EVENT_BUS_UPDATE_CONTACTS_LIST_THROWABLE = "updatedContactsListThrowable";
    public static final String EVENT_BUS_STOP_REFRESH = "stopRefresh";
    public static final String EVENT_BUS_START_REFRESH = "startRefresh";
    public static final String EVENT_BUS_IMAGE_PROFILE_PATH = "ImageProfilePath";
    public static final String EVENT_BUS_IMAGE_GIFT_PATH = "ImageGiftPath";
    public static final String EVENT_BUS_UPDATE_CURRENT_STATUS = "updateCurrentStatus";
    public static final String EVENT_BUS_UPDATE_STATUS = "updateStatus";
    public static final String EVENT_BUS_DELETE_STATUS = "deleteStatus";
    public static final String EVENT_BUS_USERNAME_PROFILE_UPDATED = "updateUserName";
    public static final String EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED = "mine_profileImageUpdated";
    public static final String EVENT_BUS_CONTRIBUTER_REMOVED = "contributor_archived";
    public static final String EVENT_BUS_ACTION_MODE_STARTED = "actionModeStarted";
    public static final String EVENT_BUS_ACTION_MODE_DESTORYED = "actionModeDestroyed";
    public static final String EVENT_BUS_WISHLIST_DELETED = "deleteWishlist";
    public static final String EVENT_BUS_NEW_WISHLIST = "new_wishlist";
    public static final String EVENT_BUS_EXIT_WISHLIST = "exitWishlist";
    public static final String EVENT_BUS_NEW_GIFT = "new_gift";
    public static final String EVENT_BUS_UPDATE_NAME = "updateName";
    public static final String EVENT_BUS_UPDATE_PROFILE_IMAGE = "updateImageProfile";
    public static final String EVENT_BUS_IMAGE_PATH = "Path";
    public static final String EVENT_BUS_UPDATE_GIFT = "update_gift";
}
