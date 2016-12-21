package com.fortunekidew.pewaad.app;

import com.fortunekidew.pewaad.R;

/**
 * Email : mwadime@fortunekidew.co.ke
 */
public class AppConstants {

    /* debugging constants  for developer */
    public static final String TAG = "pewaa";
    public static final boolean DEBUGGING_MODE = false;


    public static final int MESSAGE_COLOR_WARNING = R.color.colorOrangeLight;
    public static final int MESSAGE_COLOR_SUCCESS = R.color.colorGreenDark;
    public static final int TEXT_COLOR = R.color.colorWhite;
    public static final String DATABASE_LOCAL_NAME = "Pewaa.realm";
    public static final String INVITE_MESSAGE_SMS = "Hello checkout the Pewaa application";



    // special character to prefix the code. Make sure this character appears only once in the sms
    public static final String CODE_DELIMITER = ":";

    /**
     * upload image or video constants
     */
    public static final int UPLOAD_PICTURE_REQUEST_CODE = 0x001;
    public static final int UPLOAD_VIDEO_REQUEST_CODE = 0x002;
    public static final int UPLOAD_AUDIO_REQUEST_CODE = 0x003;
    public static final int UPLOAD_DOCUMENT_REQUEST_CODE = 0x004;
    public static final int SELECT_PROFILE_PICTURE = 0x005;
    public static final int SELECT_PROFILE_CAMERA = 0x006;
    public static final int SELECT_MESSAGES_CAMERA = 0x007;
    public static final int SELECT_MESSAGES_RECORD_VIDEO = 0x008;
    public static final int PERMISSION_REQUEST_CODE = 0x009;

    /**
     * Contributor constants
     */

    //contributor constants:
    public static final int STATUS_CONTRIBUTOR_ADDED = 0x010;
    public static final int STATUS_CONTRIBUTOR_ADDED_SUCCESS = 0x011;


    //single user socket constants:
    public static final String SOCKET_IS_MESSAGE_SENT = "send_message";
    public static final String SOCKET_IS_MESSAGE_DELIVERED = "delivered";
    public static final String SOCKET_IS_MESSAGE_SEEN = "seen";
    public static final String SOCKET_NEW_MESSAGE = "new_message";
    public static final String SOCKET_NEW_MESSAGE_SERVER = "new_message_server";
    public static final String SOCKET_SAVE_NEW_MESSAGE = "save_new_message";
    public static final String SOCKET_USER_PING = "user_ping";
    public static final String SOCKET_IS_TYPING = "typing";
    public static final String SOCKET_IS_STOP_TYPING = "stop_typing";
    public static final String SOCKET_IS_ONLINE = "is_online";
    public static final String SOCKET_IS_LAST_SEEN = "last_seen";
    public static final String SOCKET_CONNECTED = "user_connect";
    public static final String SOCKET_DISCONNECTED = "user_disconnect";
    //group socket constants:
    public static final String SOCKET_SAVE_NEW_MESSAGE_GROUP = "save_group_message";
    public static final String SOCKET_NEW_MESSAGE_GROUP = "new_group_message";
    public static final String SOCKET_NEW_MESSAGE_GROUP_SERVER = "new_group_message_server";
    public static final String SOCKET_USER_PING_GROUP = "user_ping_group";
    public static final String SOCKET_USER_PINGED_GROUP = "user_pinged_group";
    public static final String SOCKET_IS_MESSAGE_GROUP_SEND = "send_group_message";
    public static final String SOCKET_IS_MESSAGE_GROUP_DELIVERED = "group_delivered";
    public static final String SOCKET_IS_MESSAGE_GROUP_SENT = "group_sent";
    public static final String SOCKET_IS_MEMBER_TYPING = "member_typing";
    public static final String SOCKET_IS_MEMBER_STOP_TYPING = "member_stop_typing";


    /**
     * Status constants
     */

    public static final int IS_WAITING = 0;
    public static final int IS_SENT = 1;
    public static final int IS_DELIVERED = 2;
    public static final int IS_SEEN = 3;
}
