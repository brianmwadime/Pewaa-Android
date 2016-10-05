package com.fortunekidew.pewaa.app;

/**
 * Email : mwadime@fortunekidew.co.ke
 */
public class EndPoints {

    public static final String BASE_URL = "http://api.pewaa.com/v1/";
    /**
     * Chat server URLs
     */
    public static final String CHAT_SERVER_URL = "http://91.234.194.82:9000";

;
    /**
     * Authentication
     */
    public static final String JOIN = "users/join";
    public static final String RESEND_REQUEST_SMS = "users/resend";
    public static final String VERIFY_USER = "users/verify";
    public static final String DELETE_ACCOUNT = "users/deleteAccount";
    public static final String SEND_CONTACTS = "users/sendContacts";


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
    public static final String GET_CONTACT = "GetContact/{userID}";
    public static final String GET_STATUS = "GetStatus";
    public static final String DELETE_ALL_STATUS = "DeleteAllStatus";
    public static final String DELETE_STATUS = "DeleteStatus/{statusID}";
    public static final String UPDATE_STATUS = "UpdateStatus/{statusID}";
    public static final String EDIT_STATUS = "EditStatus";
    public static final String EDIT_NAME = "EditName";
    public static final String UPLOAD_PROFILE_IMAGE = "uploadImage";


    /**
     * Admob
     */
    public static final String GET_ADS_INFORMATION = "GetAdmobInformation";
    public static final String GET_INTERSTITIAL_INFORMATION = "GetAdmobInterstitialInformation";
}
