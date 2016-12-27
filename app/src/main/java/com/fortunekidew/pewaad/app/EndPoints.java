package com.fortunekidew.pewaad.app;

/**
 * Email : mwadime@fortunekidew.co.ke
 */
public class EndPoints {

    public static final String BASE_URL = "http://api.pewaa.com/v1/";

    public static final String ASSETS_BASE_URL = "http://api.pewaa.com/static/";
    /**
     * Chat server URLs
     */
    public static final String CHAT_SERVER_URL = "http://api.pewaa.com";

;
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
    public static final String WISHLIST_ITEMS = "wishlists/{wishlistId}/gifts";
    public static final String GET_WISHLIST = "wishlists/{wishlistId}";

    /**
     * Gifts
     */
    public static final String GIFTS = "gifts";
    public static final String GIFT = "gifts/{giftId}";
    public static final String GIFT_CONTRIBUTORS = "gifts/{id}/contributors";

    public static final String Payments = "payments/create";
    public static final String Payment = "payments/update/{trx_id}";
    public static final String PAYMENT_REQUEST = "payments/request";
    public static final String PAYMENT_CONFIRM = "payments/confirm/{trx_id}";

    /**
     * Contributors
     */
    public static final String CONTRIBUTORS = "contributors";
    public static final String CONTRIBUTOR = "contributors/{contributorId}";

    /**
     * Push Notifications
     */
    public static final String NOTIFICATIONS = "notifications";
    public static final String DEVICES = "devices";
    public static final String DEVICE = "devices/{deviceId}";

    /**
     * Groups
     */
    public static final String CREATE_GROUP = "Groups/createGroup";
    public static final String ADD_MEMBERS_TO_GROUP = "Groups/addMembersToGroup";
    public static final String REMOVE_MEMBER_FROM_GROUP = "Groups/removeMemberFromGroup";
    public static final String MAKE_MEMBER_AS_ADMIN = "Groups/makeMemberAdmin";
    public static final String GROUPS_lIST = "Groups/all";
    public static final String GROUP_MEMBERS_lIST = "GetGroupMembers/{groupID}";
    public static final String EXIT_GROUP = "ExitGroup/{groupID}";
    public static final String DELETE_GROUP = "DeleteGroup/{groupID}";
    public static final String GET_GROUP = "GetGroup/{groupID}";
    public static final String UPLOAD_GROUP_PROFILE_IMAGE = "uploadGroupImage";
    public static final String EDIT_GROUP_NAME = "EditGroupName";


    /**
     * Download and upload files
     */
    public static final String UPLOAD_MESSAGES_IMAGE = "uploadMessagesImage";
    public static final String UPLOAD_MESSAGES_VIDEO = "uploadMessagesVideo";
    public static final String UPLOAD_MESSAGES_AUDIO = "uploadMessagesAudio";
    public static final String UPLOAD_MESSAGES_DOCUMENT = "uploadMessagesDocument";


    /**
     * Contacts
     */
    public static final String GET_STATUS = "GetStatus";
    public static final String DELETE_ALL_STATUS = "DeleteAllStatus";
    public static final String DELETE_STATUS = "DeleteStatus/{statusID}";
    public static final String UPDATE_STATUS = "UpdateStatus/{statusID}";
    public static final String EDIT_STATUS = "EditStatus";

    /**
     * Admob
     */
    public static final String GET_ADS_INFORMATION = "GetAdmobInformation";
    public static final String GET_INTERSTITIAL_INFORMATION = "GetAdmobInterstitialInformation";
}
