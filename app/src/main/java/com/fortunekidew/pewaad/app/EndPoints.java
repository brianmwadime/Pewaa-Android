package com.fortunekidew.pewaad.app;

/**
 * Created by Brian Mwakima 10/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */
public class EndPoints {

    public static final String BASE_URL = "https://api.pewaa.com/v1/";

    public static final String ASSETS_BASE_URL = "https://api.pewaa.com/static/";
    /**
     * Chat server URLs
     */
    public static final String CHAT_SERVER_URL = "https://api.pewaa.com";
    /**
     * Authentication & Profile
     */
    public static final String JOIN = "users/join";
    public static final String RESEND_REQUEST_SMS = "users/resend";
    public static final String VERIFY_USER = "users/verify";
    public static final String DELETE_ACCOUNT = "users/deleteAccount";
    public static final String SEND_CONTACTS = "users/sendContacts";
    public static final String UPLOAD_PROFILE_IMAGE = "users/avatar";
    public static final String GET_CONTACT = "users/{userID}";
    public static final String EDIT_NAME = "users/changeUsername";
    /**
     *  Wishlists
     */
    public static final String WISHLISTS_LIST = "wishlists";
    public static final String WISHLIST_ITEMS = "wishlists/{id}/gifts";
    public static final String GET_WISHLIST = "wishlists/{id}";
    public static final String WISHLISTS_REPORT = "wishlists/{id}/report";
    public static final String WISHLIST_DELETE_CONTRIBUTOR = "wishlists/{wishlist_id}/contributors/{contributor_id}";
    /**
     * Gifts
     */
    public static final String GIFTS = "gifts";
    public static final String GIFT = "gifts/{id}";
    public static final String GIFT_CASHOUT = "gifts/{id}/cashout";
    public static final String GIFT_CONTRIBUTORS = "gifts/{id}/contributors";
    public static final String GIFTS_REPORT = "gifts/{id}/report";
    public static final String Payments = "payments/create";
    public static final String Payment = "payments/update/{trx_id}";
    public static final String PAYMENT_REQUEST = "payments/request";
    public static final String PAYMENT_CONFIRM = "payments/confirm/{trx_id}";
    /**
     * Contributors
     */
    public static final String CONTRIBUTORS = "contributors";
    public static final String SEND_CONTRIBUTORS = "contributors/addContributors";
    public static final String CONTRIBUTOR = "contributors/{contributorId}";
    /**
     * Push Notifications
     */
    public static final String NOTIFICATIONS = "notifications";
    public static final String DEVICES = "devices";
    public static final String DEVICE = "devices/{id}";
    /**
     * Groups
     */
    public static final String EDIT_GROUP_NAME = "EditGroupName";
    /**
     * Contacts
     */
    public static final String GET_STATUS = "GetStatus";
    public static final String DELETE_ALL_STATUS = "DeleteAllStatus";
    public static final String DELETE_STATUS = "DeleteStatus/{statusID}";
    public static final String UPDATE_STATUS = "UpdateStatus/{statusID}";
    public static final String EDIT_STATUS = "EditStatus";

}
