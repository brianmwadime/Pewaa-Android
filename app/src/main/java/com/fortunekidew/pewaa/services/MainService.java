package com.fortunekidew.pewaa.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.models.messages.WishlistsModel;
import com.fortunekidew.pewaa.models.messages.MessagesModel;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.receivers.MessagesReceiverBroadcast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.Sort;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by Abderrahim El imame on 6/21/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class MainService extends Service {


    public static Socket mSocket;
    private static String socketID;
    private static final long TIMEOUT = 20 * 1000;
    private static final int RETRY_ATTEMPT = 5;
    private MessagesReceiverBroadcast mChangeListener;
    private Intent mIntent;
    private Realm realm;
    private static Handler handler;


    /**
     * method to disconnect user form server
     */
    public static void disconnectSocket() {
        JSONObject json = new JSONObject();
        try {
            json.put("connected", false);
            json.put("senderId", PreferenceManager.getID(PewaaApplication.getAppContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json);

        JSONObject jsonConnected = new JSONObject();
        try {
            jsonConnected.put("connected", false);
            jsonConnected.put("connectedId", PreferenceManager.getID(PewaaApplication.getAppContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit(AppConstants.SOCKET_DISCONNECTED, jsonConnected);
        mSocket.off(Socket.EVENT_CONNECT);
        mSocket.off(Socket.EVENT_DISCONNECT);
        mSocket.off(AppConstants.SOCKET_IS_ONLINE);
        mSocket.off(AppConstants.SOCKET_NEW_MESSAGE);
        //

        mSocket.off(AppConstants.SOCKET_SAVE_NEW_MESSAGE);
        mSocket.off(AppConstants.SOCKET_IS_LAST_SEEN);
        mSocket.off(AppConstants.SOCKET_IS_MESSAGE_DELIVERED);
        mSocket.off(AppConstants.SOCKET_IS_MESSAGE_SEEN);
        mSocket.off(AppConstants.SOCKET_IS_STOP_TYPING);
        mSocket.off(AppConstants.SOCKET_IS_TYPING);
        mSocket.off(AppConstants.SOCKET_IS_MESSAGE_SENT);
        mSocket.off(AppConstants.SOCKET_USER_PING);
        mSocket.off(AppConstants.SOCKET_CONNECTED);
        mSocket.off(AppConstants.SOCKET_DISCONNECTED);
        //groups
        mSocket.off(AppConstants.SOCKET_USER_PING_GROUP);
        mSocket.off(AppConstants.SOCKET_IS_MESSAGE_GROUP_SEND);
        mSocket.off(AppConstants.SOCKET_NEW_MESSAGE_GROUP);
        mSocket.off(AppConstants.SOCKET_NEW_MESSAGE_GROUP_SERVER);
        mSocket.off(AppConstants.SOCKET_SAVE_NEW_MESSAGE_GROUP);
        mSocket.off(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING);
        mSocket.off(AppConstants.SOCKET_IS_MEMBER_TYPING);
        mSocket.off(AppConstants.SOCKET_IS_MESSAGE_GROUP_DELIVERED);
        mSocket.off(AppConstants.SOCKET_IS_MESSAGE_GROUP_SENT);
        mSocket.off(AppConstants.SOCKET_NEW_MESSAGE_SERVER);
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.close();
            mSocket = null;
        }

        AppHelper.LogCat("disconnect in service");
    }

    /**
     * method for server connection initialization
     */
    public static void connectToServer() {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.timeout = TIMEOUT; //set -1 to  disable it
        options.reconnection = true;
        options.reconnectionDelay = 0;
        options.reconnectionAttempts = RETRY_ATTEMPT;
        try {
            mSocket = IO.socket(EndPoints.CHAT_SERVER_URL, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        mSocket.connect();


        JSONObject jsonConnected = new JSONObject();
        try {
            jsonConnected.put("connected", false);
            jsonConnected.put("connectedId", PreferenceManager.getID(PewaaApplication.getAppContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mSocket.emit(AppConstants.SOCKET_DISCONNECTED, jsonConnected);

        JSONObject json = new JSONObject();
        try {
            json.put("connected", true);
            json.put("connectedId", PreferenceManager.getID(PewaaApplication.getAppContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(AppConstants.SOCKET_CONNECTED, json);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("connected", true);
            jsonObject.put("senderId", PreferenceManager.getID(PewaaApplication.getAppContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(AppConstants.SOCKET_IS_ONLINE, jsonObject);

        isUserConnected();
    }


    /**
     * method to reconnect sockets
     */
    private static void reconnect() {
        if (mSocket != null && mSocket.connected()) {
//            disconnectSocket();
//            connectToServer();
//            updateStatusDeliveredOffline();
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        realm = Realm.getDefaultInstance();
        handler = new Handler();
//        connectToServer();

//        mSocket.on(Socket.EVENT_CONNECT, args -> {
//            AppHelper.LogCat("You are connected to chat server with id " + PreferenceManager.getSocketID(PewaaApplication.getAppContext()));
//        });
//
//        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
//            AppHelper.LogCat("You are lost connection to chat server ");
//
//        });
//        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
//            AppHelper.LogCat("SOCKET TIMEOUT");
//            reconnect();
//        });
//        mSocket.on(Socket.EVENT_RECONNECT, args -> {
//            AppHelper.LogCat("Reconnect");
//            reconnect();
//        });
//        onReceivedNewMessage();
//        onReceivedNewMessageGroup();
//        sendPongToSender();
//        SenderMarkMessageAsDelivered();
//        SenderMarkMessageAsSeen();
//        MemberMarkMessageAsSent();
//        MemberMarkMessageAsDelivered();
//        mChangeListener = new MessagesReceiverBroadcast() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            protected void MessageReceived(Context context, Intent intent) {
//                String action = intent.getAction();
//                switch (action) {
//                    case "new_user_message_notification":
//                        String file = intent.getExtras().getString("file");
//                        String userphone = intent.getExtras().getString("phone");
//                        String messageBody = intent.getExtras().getString("message");
//                        String recipientId = intent.getExtras().getString("recipientID");
//                        String senderId = intent.getExtras().getString("senderId");
//                        int conversationID = intent.getExtras().getInt("conversationID");
//                        String userImage = intent.getExtras().getString("userImage");
//                        Intent messagingIntent = new Intent(PewaaApplication.getAppContext(), MessagesActivity.class);
//                        messagingIntent.putExtra("conversationID", conversationID);
//                        messagingIntent.putExtra("recipientID", recipientId);
//                        messagingIntent.putExtra("isGroup", false);
//                        messagingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        if (AppHelper.isActivityRunning(PewaaApplication.getAppContext(), "activities.messages.MessagesActivity")) {
//                            NotificationsModel notificationsModel = new NotificationsModel();
//                            notificationsModel.setConversationID(conversationID);
//                            notificationsModel.setFile(file);
//                            notificationsModel.setGroup(false);
//                            notificationsModel.setImage(userImage);
//                            notificationsModel.setPhone(userphone);
//                            notificationsModel.setMessage(messageBody);
//                            notificationsModel.setRecipientId(recipientId);
//                            notificationsModel.setSenderId(senderId);
//                            EventBus.getDefault().post(new Pusher("NewUserNotification", notificationsModel));
//                        } else {
//                            if (file != null) {
//                                NotificationsManager.showUserNotification(PewaaApplication.getAppContext(), messagingIntent, userphone, file, recipientId, userImage);
//                            } else {
//                                NotificationsManager.showUserNotification(PewaaApplication.getAppContext(), messagingIntent, userphone, messageBody, recipientId, userImage);
//                            }
//                        }
//
//
//                        break;
//                    case "new_group_message_notification":
//                        String File = intent.getExtras().getString("file");
//                        String userPhone = intent.getExtras().getString("senderPhone");
//                        String groupName = unescapeJava(intent.getExtras().getString("groupName"));
//                        String messageGroupBody = intent.getExtras().getString("message");
//                        int groupID = intent.getExtras().getInt("groupID");
//                        String groupImage = intent.getExtras().getString("groupImage");
//                        int conversationId = intent.getExtras().getInt("conversationID");
//                        String memberName;
//                        String name = UtilsPhone.getContactName(PewaaApplication.getAppContext(), userPhone);
//                        if (name != null) {
//                            memberName = name;
//                        } else {
//                            memberName = userPhone;
//                        }
//                        messagingIntent = new Intent(PewaaApplication.getAppContext(), MessagesActivity.class);
//                        messagingIntent.putExtra("conversationID", conversationId);
//                        messagingIntent.putExtra("groupID", groupID);
//                        messagingIntent.putExtra("isGroup", true);
//                        messagingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        if (AppHelper.isActivityRunning(PewaaApplication.getAppContext(), "activities.messages.MessagesActivity")) {
//                            NotificationsModel notificationsModel = new NotificationsModel();
//                            notificationsModel.setConversationID(conversationId);
//                            notificationsModel.setFile(File);
//                            notificationsModel.setGroup(true);
//                            notificationsModel.setImage(groupImage);
//                            notificationsModel.setPhone(userPhone);
//                            notificationsModel.setMessage(messageGroupBody);
//                            notificationsModel.setMemberName(memberName);
//                            notificationsModel.setGroupID(groupID);
//                            notificationsModel.setGroupName(groupName);
//                            EventBus.getDefault().post(new Pusher("NewGroupNotification", notificationsModel));
//                        } else {
//                            if (File != null) {
//                                NotificationsManager.showGroupNotification(PewaaApplication.getAppContext(), messagingIntent, groupName, memberName + " : " + File, groupID, groupImage);
//                            } else {
//                                NotificationsManager.showGroupNotification(PewaaApplication.getAppContext(), messagingIntent, groupName, memberName + " : " + messageGroupBody, groupID, groupImage);
//                            }
//                        }
//
//
//                        break;
//                }
//
//            }
//        };


//        getApplication().registerReceiver(mChangeListener, new IntentFilter("new_user_message_notification"));
//        getApplication().registerReceiver(mChangeListener, new IntentFilter("new_group_message_notification"));


    }

    /**
     * method to check if user is connected to server
     */
    private static void isUserConnected() {
        mSocket.on(AppConstants.SOCKET_CONNECTED, args -> {
            final JSONObject data = (JSONObject) args[0];
            try {
                String connectedId = data.getString("connectedId");
                String socketId = data.getString("socketId");
                boolean connected = data.getBoolean("connected");

                if (connectedId != PreferenceManager.getID(PewaaApplication.getAppContext())) {
                    if (connected) {
                        AppHelper.LogCat("User with id  --> " + connectedId + " is connected <---");

                        handler.postDelayed(() -> MainService.unSentMessages(connectedId), 2000);//// TODO: 9/4/16

                    } else {
                        AppHelper.LogCat("User with id  --> " + connectedId + " is disconnected  <---");
                    }

                } else {
                    PreferenceManager.setSocketID(socketId, PewaaApplication.getAppContext());
                    AppHelper.LogCat("You  are connected with socket id --> " + PreferenceManager.getSocketID(PewaaApplication.getAppContext()) + " <---");
                }

            } catch (JSONException e) {
                AppHelper.LogCat(e);
            }

        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // service finished
//        getApplicationContext().unregisterReceiver(mChangeListener);
//        disconnectSocket();
        realm.close();
        stopSelf();


    }


    /**
     * method to check  for unsent messages
     */
    public static void unSentMessages(String recpientId) {
        Realm realm = Realm.getDefaultInstance();

        List<MessagesModel> messagesModelsList = realm.where(MessagesModel.class)
                .equalTo("status", AppConstants.IS_WAITING)
                .equalTo("recipientID", recpientId)
                .equalTo("isGroup", false)
                .equalTo("isFileUpload", true)
                .equalTo("senderID", PreferenceManager.getID(PewaaApplication.getAppContext()))
                .findAllSorted("id", Sort.ASCENDING);
        AppHelper.LogCat("size " + messagesModelsList.size());
        for (MessagesModel messagesModel : messagesModelsList) {
            sendMessages(messagesModel);
        }
        realm.close();
    }

    /**
     * method to send unsentMessages
     *
     * @param messagesModel this i parameter for sendMessages method
     */
    public static void sendMessages(MessagesModel messagesModel) {
        final JSONObject message = new JSONObject();
        try {
            message.put("messageBody", messagesModel.getMessage());
            message.put("messageId", messagesModel.getId());
            message.put("recipientId", messagesModel.getRecipientID());
            message.put("senderId", messagesModel.getSenderID());
            message.put("senderName", messagesModel.getUsername());
            message.put("phone", messagesModel.getPhone());
            message.put("date", messagesModel.getDate());
            message.put("senderImage", "null");
            message.put("isGroup", messagesModel.isGroup());
            message.put("conversationId", messagesModel.getConversationID());
            message.put("image", messagesModel.getImageFile());
            message.put("video", messagesModel.getVideoFile());
            message.put("thumbnail", messagesModel.getVideoThumbnailFile());
            message.put("audio", messagesModel.getAudioFile());
            message.put("document", messagesModel.getDocumentFile());
            message.put("duration", messagesModel.getDuration());
            message.put("fileSize", messagesModel.getFileSize());
        } catch (JSONException e) {
            AppHelper.LogCat(e);
        }
        if (!messagesModel.isFileUpload()) return;
        mSocket.emit(AppConstants.SOCKET_NEW_MESSAGE, message);
    }

    /**
     * method to send unsentMessages who has files
     *
     * @param messagesModel this i parameter for sendMessages method
     */
    public static void sendMessagesFiles(MessagesModel messagesModel) {
        final JSONObject message = new JSONObject();
        try {
            message.put("messageBody", messagesModel.getMessage());
            message.put("messageId", messagesModel.getId());
            message.put("recipientId", messagesModel.getRecipientID());
            message.put("senderId", messagesModel.getSenderID());
            message.put("senderName", messagesModel.getUsername());
            message.put("phone", messagesModel.getPhone());
            message.put("date", messagesModel.getDate());
            message.put("senderImage", "null");
            message.put("isGroup", messagesModel.isGroup());
            message.put("conversationId", messagesModel.getConversationID());
            message.put("image", messagesModel.getImageFile());
            message.put("video", messagesModel.getVideoFile());
            message.put("thumbnail", messagesModel.getVideoThumbnailFile());
            message.put("audio", messagesModel.getAudioFile());
            message.put("document", messagesModel.getDocumentFile());
            message.put("duration", messagesModel.getDuration());
            message.put("fileSize", messagesModel.getFileSize());
        } catch (JSONException e) {
            AppHelper.LogCat(e);
        }
        if (!messagesModel.isFileUpload()) return;
        mSocket.emit(AppConstants.SOCKET_NEW_MESSAGE, message);
        mSocket.emit(AppConstants.SOCKET_SAVE_NEW_MESSAGE, message);
    }


    /**
     * method to  update status delivered when user was offline and come online
     * and he has a new messages (unread)
     */

    private static void updateStatusDeliveredOffline() {
        Realm realm = Realm.getDefaultInstance();
        List<MessagesModel> messagesModels = realm.where(MessagesModel.class)
                .notEqualTo("recipientID", PreferenceManager.getID(PewaaApplication.getAppContext()))
                .equalTo("status", AppConstants.IS_WAITING).findAll();

        for (MessagesModel messagesModel : messagesModels) {
            RecipientMarkMessageAsDelivered(messagesModel.getId());
        }
    }

    /**
     * method to mark messages as delivered by recipient
     *
     * @param messageId this is the  parameter for RecipientMarkMessageAsDelivered method
     */
    private static void RecipientMarkMessageAsDelivered(int messageId) {
        try {
            JSONObject json = new JSONObject();
            json.put("senderId", PreferenceManager.getID(PewaaApplication.getAppContext()));
            json.put("messageId", messageId);
            mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_DELIVERED, json);
        } catch (Exception e) {
            AppHelper.LogCat(e);
        }
        AppHelper.LogCat("--> Recipient mark message as  delivered <--");
    }

    /**
     * method to update status for a specific  message (as delivered by sender)
     */
    private void SenderMarkMessageAsDelivered() {

        mSocket.on(AppConstants.SOCKET_IS_MESSAGE_DELIVERED, args -> {

            JSONObject data = (JSONObject) args[0];
            try {
                String senderId = data.getString("senderId");
                if (senderId == PreferenceManager.getID(PewaaApplication.getAppContext()))
                    return;
                updateDeliveredStatus(data);
                AppHelper.LogCat("--> Sender mark message as  delivered: update status  <--");

            } catch (Exception e) {
                AppHelper.LogCat(e);
            }

        });
    }


    /**
     * method to update status for a specific  message (as delivered by sender) in realm database
     *
     * @param data this is parameter for  updateDeliveredStatus
     */
    private void updateDeliveredStatus(JSONObject data) {
        try {
            int messageId = data.getInt("messageId");
            String senderId = data.getString("senderId");
            if (senderId == PreferenceManager.getID(PewaaApplication.getAppContext())) return;
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(realm1 -> {
                MessagesModel messagesModel = realm1.where(MessagesModel.class).equalTo("id", messageId).equalTo("status", AppConstants.IS_SENT).findFirst();
                if (messagesModel != null) {
                    messagesModel.setStatus(AppConstants.IS_DELIVERED);
                    realm1.copyToRealmOrUpdate(messagesModel);
                    AppHelper.LogCat("Delivered successfully");
                    EventBus.getDefault().post(new Pusher("messages_delivered", messagesModel.getId()));
                } else {
                    AppHelper.LogCat("Delivered failed ");
                }
            });
            realm.close();
        } catch (JSONException e) {
            AppHelper.LogCat("Save data to realm delivered JSONException " + e.getMessage());
        }
    }

    /**
     * method to check if a group conversation exist
     *
     * @param groupID this is the first parameter for  checkIfGroupConversationExist method
     * @param realm   this is the second parameter for  checkIfGroupConversationExist  method
     * @return return value
     */
    private boolean checkIfGroupConversationExist(int groupID, Realm realm) {
        RealmQuery<WishlistsModel> query = realm.where(WishlistsModel.class).equalTo("groupID", groupID);
        return query.count() == 0 ? false : true;

    }

    /**
     * method to save the incoming message and mark him as waiting
     *
     * @param data this is the parameter for saveNewMessage method
     */
    private void saveNewMessageGroup(JSONObject data) {
        Realm realm = Realm.getDefaultInstance();

        try {

            String senderId = data.getString("senderId");
            String recipientId = data.getString("recipientId");
            String messageBody = data.getString("messageBody");
            String senderName = data.getString("senderName");
            String senderPhone = data.getString("phone");
            String groupImage = data.getString("GroupImage");
            String groupName = data.getString("GroupName");
            String dateTmp = data.getString("date");
            String video = data.getString("video");
            String thumbnail = data.getString("thumbnail");
            boolean isGroup = true;
            String image = data.getString("image");
            String audio = data.getString("audio");
            String document = data.getString("document");
            String duration = data.getString("duration");
            String fileSize = data.getString("fileSize");
            int groupID = data.getInt("groupID");

            if (senderId == PreferenceManager.getID(PewaaApplication.getAppContext())) return;

            if (!checkIfGroupConversationExist(groupID, realm)) {
                realm.executeTransaction(realm1 -> {
                    int lastConversationID = 1;
                    int UnreadMessageCounter = 0;
                    int lastID = 1;
                    try {
                        WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).findAll().last();
                        lastConversationID = wishlistsModel.getId() + 1;
                        UnreadMessageCounter++;

                        MessagesModel messagesModel1 = realm1.where(MessagesModel.class).findAll().last();
                        lastID = messagesModel1.getId() + 1;

                        AppHelper.LogCat("last ID message  MainService" + lastID);

                    } catch (Exception e) {
                        AppHelper.LogCat("last conversation  ID  if conversation id = 0 Exception MainService" + e.getMessage());
                        lastConversationID = 1;
                    }

                    WishlistsModel wishlistsModel = new WishlistsModel();
                    RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
                    MessagesModel messagesModel = null;
                    messagesModel = new MessagesModel();
                    messagesModel.setId(lastID);
                    messagesModel.setDate(dateTmp);
                    messagesModel.setSenderID(senderId);
                    messagesModel.setUsername(senderName);
                    messagesModel.setPhone(senderPhone);
                    messagesModel.setRecipientID("0");
                    messagesModel.setStatus(AppConstants.IS_WAITING);
                    messagesModel.setGroup(isGroup);
                    messagesModel.setGroupID(groupID);
                    messagesModel.setImageFile(image);
                    messagesModel.setVideoFile(video);
                    messagesModel.setAudioFile(audio);
                    messagesModel.setDocumentFile(document);
                    messagesModel.setVideoThumbnailFile(thumbnail);
                    messagesModel.setFileUpload(true);
                    messagesModel.setFileDownLoad(false);
                    messagesModel.setDuration(duration);
                    messagesModel.setFileSize(fileSize);
                    messagesModel.setConversationID(lastConversationID);
                    messagesModel.setMessage(messageBody);
                    messagesModelRealmList.add(messagesModel);
                    wishlistsModel.setLastMessageId(lastID);
                    wishlistsModel.setRecipientID(recipientId);
                    // wishlistsModel.setCreatorID(groupsModel1.getCreatorID());//// TODO: 7/23/16 khasni nzid les attributes li khasnii
                    wishlistsModel.setRecipientUsername(groupName);
                    wishlistsModel.setRecipientImage(groupImage);
                    wishlistsModel.setGroupID(groupID);
                    wishlistsModel.setMessageDate(dateTmp);
                    wishlistsModel.setId(lastConversationID);
                    wishlistsModel.setRecipientID("0");
                    wishlistsModel.setGroup(isGroup);
                    wishlistsModel.setMessages(messagesModelRealmList);
                    wishlistsModel.setLastMessage(messageBody);
                    wishlistsModel.setStatus(AppConstants.IS_WAITING);
                    wishlistsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
                    wishlistsModel.setCreatedOnline(true);
                    realm1.copyToRealmOrUpdate(wishlistsModel);

                    EventBus.getDefault().post(new Pusher("createGroup"));

                    String FileType = null;
                    if (!messagesModel.getImageFile().equals("null")) {
                        FileType = "Image";
                    } else if (!messagesModel.getVideoFile().equals("null")) {
                        FileType = "Video";
                    } else if (!messagesModel.getAudioFile().equals("null")) {
                        FileType = "Audio";
                    } else if (!messagesModel.getDocumentFile().equals("null")) {
                        FileType = "Document";
                    }


                    mIntent = new Intent("new_group_message_notification");
                    mIntent.putExtra("conversationID", lastConversationID);
                    mIntent.putExtra("recipientID", senderId);
                    mIntent.putExtra("groupID", groupID);
                    mIntent.putExtra("groupImage", groupImage);
                    mIntent.putExtra("username", senderName);
                    mIntent.putExtra("file", FileType);
                    mIntent.putExtra("senderPhone", senderPhone);
                    mIntent.putExtra("groupName", groupName);
                    mIntent.putExtra("message", messageBody);
                    sendBroadcast(mIntent);
                });

            } else {
                realm.executeTransaction(realm1 -> {
                    int lastID = 1;
                    int UnreadMessageCounter = 0;
                    try {
                        List<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).findAll();
                        lastID = messagesModel1.size();
                        lastID++;


                        WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).equalTo("groupID", groupID).findFirst();
                        UnreadMessageCounter = Integer.parseInt(wishlistsModel.getUnreadMessageCounter());
                        UnreadMessageCounter++;

                        RealmList<MessagesModel> messagesModelRealmList = wishlistsModel.getMessages();
                        MessagesModel messagesModel = new MessagesModel();
                        messagesModel.setId(lastID);
                        messagesModel.setDate(dateTmp);
                        messagesModel.setRecipientID("0");
                        messagesModel.setStatus(AppConstants.IS_WAITING);
                        messagesModel.setUsername(senderName);
                        messagesModel.setPhone(senderPhone);
                        messagesModel.setSenderID(senderId);
                        messagesModel.setGroup(isGroup);
                        messagesModel.setMessage(messageBody);
                        messagesModel.setImageFile(image);
                        messagesModel.setVideoFile(video);
                        messagesModel.setAudioFile(audio);
                        messagesModel.setDocumentFile(document);
                        messagesModel.setVideoThumbnailFile(thumbnail);
                        messagesModel.setFileUpload(true);
                        messagesModel.setFileDownLoad(false);
                        messagesModel.setFileSize(fileSize);
                        messagesModel.setDuration(duration);
                        messagesModel.setGroupID(groupID);
                        messagesModel.setConversationID(wishlistsModel.getId());
                        messagesModelRealmList.add(messagesModel);
                        wishlistsModel.setLastMessageId(lastID);
                        wishlistsModel.setRecipientID("0");
                        wishlistsModel.setMessages(messagesModelRealmList);
                        wishlistsModel.setLastMessage(messageBody);
                        wishlistsModel.setGroup(isGroup);
                        wishlistsModel.setCreatedOnline(true);
                        wishlistsModel.setStatus(AppConstants.IS_WAITING);
                        wishlistsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
                        realm1.copyToRealmOrUpdate(wishlistsModel);
                        EventBus.getDefault().post(new Pusher("new_message_group", messagesModel));


                        String FileType = null;
                        if (!messagesModel.getImageFile().equals("null")) {
                            FileType = "Image";
                        } else if (!messagesModel.getVideoFile().equals("null")) {
                            FileType = "Video";
                        } else if (!messagesModel.getAudioFile().equals("null")) {
                            FileType = "Audio";
                        } else if (!messagesModel.getDocumentFile().equals("null")) {
                            FileType = "Document";
                        }

                        mIntent = new Intent("new_group_message_notification");
                        mIntent.putExtra("conversationID", wishlistsModel.getId());
                        mIntent.putExtra("recipientID", senderId);
                        mIntent.putExtra("groupID", groupID);
                        mIntent.putExtra("groupImage", groupImage);
                        mIntent.putExtra("username", senderName);
                        mIntent.putExtra("file", FileType);
                        mIntent.putExtra("senderPhone", senderPhone);
                        mIntent.putExtra("groupName", groupName);
                        mIntent.putExtra("message", messageBody);
                        sendBroadcast(mIntent);
                    } catch (Exception e) {
                        AppHelper.LogCat("last conversation  ID group if conversation id = 0 Exception  MainService " + e.getMessage());
                    }


                });
            }
        } catch (JSONException e) {
            AppHelper.LogCat(e.getMessage());
        }
        realm.close();
    }

    /**
     * method to when user receive a new message from a group
     */
    private void onReceivedNewMessageGroup() {

        mSocket.on(AppConstants.SOCKET_NEW_MESSAGE_GROUP_SERVER, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String recipientId = data.getString("recipientId");
                String senderID = data.getString("senderId");

                data.put("pingedId", recipientId);
                data.put("pinged", false);

                if (recipientId == PreferenceManager.getID(PewaaApplication.getAppContext())) {
                    data.put("pingedId", recipientId);
                    data.put("pinged", true);
                }

                data.put("socketId", PreferenceManager.getSocketID(PewaaApplication.getAppContext()));
                mSocket.emit(AppConstants.SOCKET_USER_PING_GROUP, data);
            } catch (Exception e) {
                AppHelper.LogCat("New group message MainService " + e.getMessage());
            }
        });
        mSocket.on(AppConstants.SOCKET_USER_PINGED_GROUP, args -> {
            JSONObject dataString = (JSONObject) args[0];
            try {
                String recipientID = dataString.getString("recipientId");
                String senderId = dataString.getString("senderId");
                boolean pinged = dataString.getBoolean("pinged");
                String pingedID = dataString.getString("pingedId");
                AppHelper.LogCat("pinged " + pinged);
                AppHelper.LogCat("pinged id " + pingedID);
                if (pinged) {
                    AppHelper.LogCat("User connected MainService (group)" + dataString.getInt("recipientId"));
                    dataString.put("isSent", 1);
                    mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_GROUP_SEND, dataString);
                    //saveNewMessage(dataString); //TODO hana khasni nsawbha bach luser li st9bal lmessage itzad fles conversations b7al you left ,okhasni ndir wa7ed l qquery li nchecki biha wach kayn luser f lgroup b group id
                    if (recipientID == PreferenceManager.getID(PewaaApplication.getAppContext()))
                        saveNewMessageGroup(dataString);

                } else {
                    AppHelper.LogCat("User not  connected  MainService (group)" + dataString.getInt("recipientId"));
                    if (pingedID == senderId) return;
                    dataString.put("isSent", 0);
                    mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_GROUP_SEND, dataString);
                    return;
                }
            } catch (JSONException e) {
                AppHelper.LogCat("Group message received JSONException  MainService" + e.getMessage());
            }
        });

    }

    /**
     * method to when user receive a new message
     */
    private void onReceivedNewMessage() {

        mSocket.on(AppConstants.SOCKET_NEW_MESSAGE_SERVER, args -> {
            JSONObject data = (JSONObject) args[0];
            AppHelper.LogCat("new_message_server ");
            try {
                String recipientId = data.getString("recipientId");

                data.put("pingedId", PreferenceManager.getID(PewaaApplication.getAppContext()));
                data.put("pinged", false);

                if (recipientId == PreferenceManager.getID(PewaaApplication.getAppContext())) {
                    data.put("pingedId", recipientId);
                    data.put("pinged", true);
                }


                data.put("socketId", PreferenceManager.getSocketID(PewaaApplication.getAppContext()));
                mSocket.emit(AppConstants.SOCKET_USER_PING, data, (Ack) argObjects -> {

                    JSONObject dataString = (JSONObject) argObjects[0];
                    try {
                        int recipientID = dataString.getInt("recipientId");
                        boolean pinged = dataString.getBoolean("pinged");
                        int pingedID = dataString.getInt("pingedId");

                        if (pinged && pingedID == recipientID) {
                            AppHelper.LogCat("User  connected " + dataString.getInt("pingedId"));
                            mSocket.emit(AppConstants.SOCKET_IS_MESSAGE_SENT, dataString);
                            saveNewMessage(dataString);
                        } else {
                            AppHelper.LogCat("User not  connected " + dataString.getInt("pingedId"));

                        }
                    } catch (JSONException e) {
                        AppHelper.LogCat("User message received MainService JSONException" + e.getMessage());
                    }
                });
            } catch (Exception e) {
                AppHelper.LogCat("User received  new message  Exception MainService" + e.getMessage());
            }
        });

    }

    /**
     * method to send a confirmation that the recipient user is connected
     */
    private void sendPongToSender() {

        mSocket.on(AppConstants.SOCKET_IS_MESSAGE_SENT, args -> {
            JSONObject dataOn = (JSONObject) args[0];

            try {
                String SenderID = dataOn.getString("senderId");
                if (SenderID != PreferenceManager.getID(PewaaApplication.getAppContext()))
                    return;
                updateStatusAsSentBySender(dataOn, AppConstants.IS_SENT);
            } catch (JSONException e) {
                AppHelper.LogCat("Recipient is online  MainService" + e.getMessage());
            }

        });
    }

    /**
     * method to update status as seen by sender (if recipient have been seen the message)
     */
    private void SenderMarkMessageAsSeen() {
        mSocket.on(AppConstants.SOCKET_IS_MESSAGE_SEEN, args -> {
            JSONObject data = (JSONObject) args[0];
            updateSeenStatus(data);
        });

    }


    /**
     * method to get a conversation id by groupId
     *
     * @param groupId this is the first parameter for getConversationId method
     * @param realm   this is the second parameter for getConversationId method
     * @return conversation id
     */
    private int getConversationIdByGroupId(int groupId, Realm realm) {
        try {
            WishlistsModel wishlistsModelNew = realm.where(WishlistsModel.class)
                    .equalTo("groupID", groupId)
                    .findAll().first();
            return wishlistsModelNew.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id  (group) Exception MainService  " + e.getMessage());
            return 0;
        }
    }

    /**
     * method to update status as seen by sender (if recipient have been seen the message)
     */
    private void MemberMarkMessageAsSent() {
        mSocket.on(AppConstants.SOCKET_IS_MESSAGE_GROUP_SENT, args -> {
            JSONObject data = (JSONObject) args[0];
            updateGroupSentStatus(data);
        });

    }

    /**
     * method to update status as delivered by sender (if recipient have been seen the message)
     */
    private void MemberMarkMessageAsDelivered() {
        mSocket.on(AppConstants.SOCKET_IS_MESSAGE_GROUP_DELIVERED, args -> {
            JSONObject data = (JSONObject) args[0];
            updateGroupDeliveredStatus(data);
        });

    }

    /**
     * method to update status as delivered by sender
     *
     * @param data this is parameter for updateSeenStatus method
     */
    private void updateGroupDeliveredStatus(JSONObject data) {

        Realm realm = Realm.getDefaultInstance();
        try {
            int groupId = data.getInt("groupId");
            String senderId = data.getString("senderId");
            AppHelper.LogCat("groupId " + groupId);
            AppHelper.LogCat("sen hhh " + senderId);
            if (senderId != PreferenceManager.getID(PewaaApplication.getAppContext())) return;

            int ConversationID = getConversationIdByGroupId(groupId, realm);
            AppHelper.LogCat("conversation  id seen " + ConversationID);
            List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
                    .equalTo("conversationID", ConversationID)
                    .equalTo("isGroup", true)
                    .equalTo("status", AppConstants.IS_SENT)
                    .findAll();
            if (messagesModelsRealm.size() != 0) {
                for (MessagesModel messagesModel1 : messagesModelsRealm) {

                    realm.executeTransaction(realm1 -> {
                        MessagesModel messagesModel = realm1.where(MessagesModel.class)
                                .equalTo("groupID", groupId)
                                .equalTo("senderID", senderId)
                                .equalTo("id", messagesModel1.getId())
                                .equalTo("status", AppConstants.IS_SENT).findFirst();
                        if (messagesModel != null) {
                            messagesModel.setStatus(AppConstants.IS_DELIVERED);
                            realm1.copyToRealmOrUpdate(messagesModel);
                            AppHelper.LogCat("Delivered successfully MainService");

                            EventBus.getDefault().post(new Pusher("messages_delivered", messagesModel.getId()));
                            handler.postDelayed(() -> {
                                updateGroupSeenStatus(data);//todo 7al tr9i3i osafi
                            }, 2000);
                        } else {
                            AppHelper.LogCat("Seen  failed MainService ");
                        }
                    });

                }
            }

            realm.close();
        } catch (JSONException e) {
            AppHelper.LogCat("Save to realm seen MainService " + e.getMessage());
        }

    }

    /**
     * method to update status as seen by sender (group)
     *
     * @param data this is parameter for updateSeenStatus method  //todo khasni nzid ila chafha kol wa7ed ndar bli chafha
     */
    private void updateGroupSeenStatus(JSONObject data) {

        Realm realm = Realm.getDefaultInstance();
        try {
            int groupId = data.getInt("groupId");
            String senderId = data.getString("senderId");
            AppHelper.LogCat("groupId " + groupId);
            AppHelper.LogCat("sen " + senderId);
            if (senderId != PreferenceManager.getID(PewaaApplication.getAppContext())) return;

            int ConversationID = getConversationIdByGroupId(groupId, realm);
            AppHelper.LogCat("conversation  id seen " + ConversationID);
            List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
                    .equalTo("conversationID", ConversationID)
                    .equalTo("isGroup", true)
                    .equalTo("status", AppConstants.IS_DELIVERED)
                    .findAll();
            if (messagesModelsRealm.size() != 0) {
                for (MessagesModel messagesModel1 : messagesModelsRealm) {

                    realm.executeTransaction(realm1 -> {
                        MessagesModel messagesModel = realm1.where(MessagesModel.class)
                                .equalTo("groupID", groupId)
                                .equalTo("senderID", senderId)
                                .equalTo("id", messagesModel1.getId())
                                .equalTo("status", AppConstants.IS_DELIVERED).findFirst();
                        if (messagesModel != null) {
                            messagesModel.setStatus(AppConstants.IS_SEEN);
                            realm1.copyToRealmOrUpdate(messagesModel);
                            AppHelper.LogCat("seen successfully");
                            EventBus.getDefault().post(new Pusher("messages_seen", messagesModel.getId()));
                        } else {
                            AppHelper.LogCat("Seen  failed MainService (group)");
                        }
                    });
                }
            }

            realm.close();
        } catch (JSONException e) {
            AppHelper.LogCat("Save to realm seen " + e);
        }

    }


    /**
     * method to update status as sent by sender
     *
     * @param data this is parameter for updateSeenStatus method
     */
    private void updateGroupSentStatus(JSONObject data) {

        Realm realm = Realm.getDefaultInstance();
        try {
            int groupId = data.getInt("groupId");
            String senderId = data.getString("senderId");
            if (senderId != PreferenceManager.getID(PewaaApplication.getAppContext())) return;
            int ConversationID = getConversationIdByGroupId(groupId, realm);
            List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
                    .equalTo("conversationID", ConversationID)
                    .equalTo("isGroup", true)
                    .equalTo("status", AppConstants.IS_WAITING)
                    .findAll();
            if (messagesModelsRealm.size() != 0) {
                for (MessagesModel messagesModel1 : messagesModelsRealm) {

                    realm.executeTransaction(realm1 -> {
                        MessagesModel messagesModel = realm1.where(MessagesModel.class)
                                .equalTo("groupID", groupId)
                                .equalTo("senderID", senderId)
                                .equalTo("id", messagesModel1.getId())
                                .equalTo("status", AppConstants.IS_WAITING).findFirst();
                        if (messagesModel != null) {
                            messagesModel.setStatus(AppConstants.IS_SENT);
                            realm1.copyToRealmOrUpdate(messagesModel);
                            AppHelper.LogCat("Sent successfully MainService");
                            EventBus.getDefault().post(new Pusher("new_message_sent", messagesModel.getId()));
                        } else {
                            AppHelper.LogCat("Sent  failed  MainService");
                        }
                    });

                }
            }

            realm.close();
        } catch (JSONException e) {
            AppHelper.LogCat("Save to realm sent Exception MainService " + e.getMessage());
        }

    }

    /**
     * method to update status as seen by sender (if recipient have been seen the message)  in realm database
     *
     * @param data this is parameter for updateSeenStatus method
     */
    private void updateSeenStatus(JSONObject data) {

        Realm realm = Realm.getDefaultInstance();
        try {
            String recipientId = data.getString("recipientId");
            String senderId = data.getString("senderId");
            if (senderId == PreferenceManager.getID(PewaaApplication.getAppContext())) return;
            int ConversationID = getConversationId(senderId, recipientId, realm);
            List<MessagesModel> messagesModelsRealm = realm.where(MessagesModel.class)
                    .equalTo("conversationID", ConversationID)
                    .equalTo("isGroup", false)
                    .equalTo("status", AppConstants.IS_DELIVERED)
                    .findAll();
            if (messagesModelsRealm.size() != 0) {
                for (MessagesModel messagesModel1 : messagesModelsRealm) {

                    realm.executeTransaction(realm1 -> {
                        MessagesModel messagesModel = realm1.where(MessagesModel.class)
                                .equalTo("recipientID", senderId)
                                .equalTo("senderID", recipientId)
                                .equalTo("id", messagesModel1.getId())
                                .equalTo("status", AppConstants.IS_DELIVERED).findFirst();
                        if (messagesModel != null) {
                            messagesModel.setStatus(AppConstants.IS_SEEN);
                            realm1.copyToRealmOrUpdate(messagesModel);
                            AppHelper.LogCat("Seen successfully MainService");
                            EventBus.getDefault().post(new Pusher("messages_seen", messagesModel.getId()));
                        } else {
                            AppHelper.LogCat("Seen  failed  MainService");
                        }
                    });
                }
            }

            realm.close();
        } catch (JSONException e) {
            AppHelper.LogCat("Save to realm seen  Exception" + e.getMessage());
        }

    }

    /**
     * method to get a conversation id
     *
     * @param recipientId this is the first parameter for getConversationId method
     * @param senderId    this is the second parameter for getConversationId method
     * @param realm       this is the thirded parameter for getConversationId method
     * @return conversation id
     */
    private int getConversationId(String recipientId, String senderId, Realm realm) {
        try {
            WishlistsModel wishlistsModelNew = realm.where(WishlistsModel.class)
                    .beginGroup()
                    .equalTo("RecipientID", recipientId)
                    .or()
                    .equalTo("RecipientID", senderId)
                    .endGroup().findAll().first();
            return wishlistsModelNew.getId();
        } catch (Exception e) {
            AppHelper.LogCat("Conversation id Exception MainService" + e.getMessage());
            return 0;
        }
    }


    /**
     * method to update status for the send message by sender  (as sent message ) in realm  database
     *
     * @param data   this is the first parameter for updateStatusAsSentBySender method
     * @param isSent this is the second parameter for updateStatusAsSentBySender method
     */
    private void updateStatusAsSentBySender(JSONObject data, int isSent) {
        try {
            int messageId = data.getInt("messageId");

            try {
                Realm realm = Realm.getDefaultInstance();
                try {
                    realm.executeTransaction(realm1 -> {
                        MessagesModel messagesModel = realm1.where(MessagesModel.class).equalTo("id", messageId).findFirst();
                        messagesModel.setStatus(isSent);
                        realm1.copyToRealmOrUpdate(messagesModel);
                        EventBus.getDefault().post(new Pusher("new_message_sent", messagesModel.getId()));

                    });
                } catch (Exception e) {
                    AppHelper.LogCat(" Is sent messages Realm Error" + e.getMessage());
                }

                realm.close();

            } catch (Exception e) {
                AppHelper.LogCat("null object Exception MainService" + e.getMessage());
            }


        } catch (JSONException e) {
            AppHelper.LogCat("UpdateStatusAsSentBySender error  MainService" + e.getMessage());
        }
    }

    /**
     * method to save the incoming message and mark him as waiting
     *
     * @param data this is the parameter for saveNewMessage method
     */
    private void saveNewMessage(JSONObject data) {

        Realm realm = Realm.getDefaultInstance();

        try {
            String recipientId = data.getString("recipientId");
            String senderId = data.getString("senderId");
            int messageId = data.getInt("messageId");
            String phone = data.getString("phone");
            String messageBody = data.getString("messageBody");
            String senderName = data.getString("senderName");
            String senderImage = data.getString("senderImage");
            String date = data.getString("date");
            String video = data.getString("video");
            String thumbnail = data.getString("thumbnail");
            boolean isGroup = false;
            String image = data.getString("image");
            String audio = data.getString("audio");
            String document = data.getString("document");
            String duration = data.getString("duration");
            String fileSize = data.getString("fileSize");

            if (senderId == PreferenceManager.getID(PewaaApplication.getAppContext())) return;

            int conversationID = getConversationId(recipientId, senderId, realm);
            if (conversationID == 0) {
                realm.executeTransaction(realm1 -> {
                    int lastConversationID = 1;
                    int UnreadMessageCounter = 0;
                    int lastID = 1;
                    try {
                        WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).findAll().last();
                        lastConversationID = wishlistsModel.getId() + 1;
                        UnreadMessageCounter++;

                        MessagesModel messagesModel1 = realm1.where(MessagesModel.class).findAll().last();
                        lastID = messagesModel1.getId() + 1;


                    } catch (Exception e) {
                        AppHelper.LogCat("last conversation  ID   if conversation id = 0 Exception MainService" + e.getMessage());
                        lastConversationID = 1;
                    }
                    RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
                    MessagesModel messagesModel = new MessagesModel();
                    messagesModel.setId(lastID);
                    messagesModel.setUsername(senderName);
                    messagesModel.setRecipientID(recipientId);
                    messagesModel.setDate(date);
                    messagesModel.setStatus(AppConstants.IS_WAITING);
                    messagesModel.setGroup(isGroup);
                    messagesModel.setSenderID(senderId);
                    messagesModel.setFileUpload(true);
                    messagesModel.setFileDownLoad(false);
                    messagesModel.setDuration(duration);
                    messagesModel.setFileSize(fileSize);
                    messagesModel.setConversationID(lastConversationID);
                    messagesModel.setMessage(messageBody);
                    messagesModel.setImageFile(image);
                    messagesModel.setVideoFile(video);
                    messagesModel.setAudioFile(audio);
                    messagesModel.setDocumentFile(document);
                    messagesModel.setVideoThumbnailFile(thumbnail);
                    messagesModel.setPhone(phone);
                    messagesModelRealmList.add(messagesModel);
                    WishlistsModel wishlistsModel1 = new WishlistsModel();
                    wishlistsModel1.setRecipientID(senderId);
                    wishlistsModel1.setLastMessage(messageBody);
                    wishlistsModel1.setRecipientUsername(senderName);
                    if (!senderImage.equals("null"))
                        wishlistsModel1.setRecipientImage(senderImage);
                    else
                        wishlistsModel1.setRecipientImage(null);
                    wishlistsModel1.setMessageDate(date);
                    wishlistsModel1.setId(lastConversationID);
                    wishlistsModel1.setStatus(AppConstants.IS_WAITING);
                    wishlistsModel1.setRecipientPhone(phone);
                    wishlistsModel1.setGroup(isGroup);
                    wishlistsModel1.setMessages(messagesModelRealmList);
                    wishlistsModel1.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
                    wishlistsModel1.setLastMessageId(lastID);
                    wishlistsModel1.setCreatedOnline(true);
                    realm1.copyToRealmOrUpdate(wishlistsModel1);


                    String FileType = null;
                    if (!messagesModel.getImageFile().equals("null")) {
                        FileType = "Image";
                    } else if (!messagesModel.getVideoFile().equals("null")) {
                        FileType = "Video";
                    } else if (!messagesModel.getAudioFile().equals("null")) {
                        FileType = "Audio";
                    } else if (!messagesModel.getDocumentFile().equals("null")) {
                        FileType = "Document";
                    }

                    EventBus.getDefault().post(new Pusher("new_message", messagesModel));
                    mIntent = new Intent("new_user_message_notification");
                    mIntent.putExtra("conversationID", lastConversationID);
                    mIntent.putExtra("recipientID", senderId);
                    mIntent.putExtra("senderId", recipientId);
                    mIntent.putExtra("userImage", senderImage);
                    mIntent.putExtra("username", senderName);
                    mIntent.putExtra("file", FileType);
                    mIntent.putExtra("phone", phone);
                    mIntent.putExtra("messageId", messageId);
                    mIntent.putExtra("message", messageBody);
                    sendBroadcast(mIntent);

                });
            } else {

                realm.executeTransaction(realm1 -> {

                    int UnreadMessageCounter = 0;
                    try {
                        MessagesModel messagesModel1 = realm1.where(MessagesModel.class).findAll().last();
                        int lastID = messagesModel1.getId() + 1;

                        WishlistsModel wishlistsModel;
                        RealmQuery<WishlistsModel> conversationsModelRealmQuery = realm1.where(WishlistsModel.class).equalTo("id", conversationID);
                        wishlistsModel = conversationsModelRealmQuery.findAll().first();

                        UnreadMessageCounter = Integer.parseInt(wishlistsModel.getUnreadMessageCounter());
                        UnreadMessageCounter++;
                        MessagesModel messagesModel = new MessagesModel();
                        messagesModel.setId(lastID);
                        messagesModel.setUsername(senderName);
                        messagesModel.setRecipientID(recipientId);
                        messagesModel.setDate(date);
                        messagesModel.setStatus(AppConstants.IS_WAITING);
                        messagesModel.setGroup(isGroup);
                        messagesModel.setSenderID(senderId);
                        messagesModel.setFileUpload(true);
                        messagesModel.setFileDownLoad(false);
                        messagesModel.setFileSize(fileSize);
                        messagesModel.setDuration(duration);
                        messagesModel.setConversationID(conversationID);
                        messagesModel.setMessage(messageBody);
                        messagesModel.setImageFile(image);
                        messagesModel.setVideoFile(video);
                        messagesModel.setAudioFile(audio);
                        messagesModel.setDocumentFile(document);
                        messagesModel.setVideoThumbnailFile(thumbnail);
                        messagesModel.setPhone(phone);
                        wishlistsModel.getMessages().add(messagesModel);
                        wishlistsModel.setLastMessageId(lastID);
                        wishlistsModel.setRecipientID(senderId);
                        wishlistsModel.setLastMessage(messageBody);
                        wishlistsModel.setMessageDate(date);
                        wishlistsModel.setCreatedOnline(true);
                        wishlistsModel.setRecipientUsername(senderName);
                        if (!senderImage.equals("null"))
                            wishlistsModel.setRecipientImage(senderImage);
                        else
                            wishlistsModel.setRecipientImage(null);
                        wishlistsModel.setRecipientPhone(phone);
                        wishlistsModel.setGroup(isGroup);
                        wishlistsModel.setStatus(AppConstants.IS_WAITING);
                        wishlistsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
                        realm1.copyToRealmOrUpdate(wishlistsModel);


                        String FileType = null;
                        if (!messagesModel.getImageFile().equals("null")) {
                            FileType = "Image";
                        } else if (!messagesModel.getVideoFile().equals("null")) {
                            FileType = "Video";
                        } else if (!messagesModel.getAudioFile().equals("null")) {
                            FileType = "Audio";
                        } else if (!messagesModel.getDocumentFile().equals("null")) {
                            FileType = "Document";
                        }

                        EventBus.getDefault().post(new Pusher("new_message", messagesModel));


                        mIntent = new Intent("new_user_message_notification");
                        mIntent.putExtra("conversationID", conversationID);
                        mIntent.putExtra("recipientID", senderId);
                        mIntent.putExtra("senderId", recipientId);
                        mIntent.putExtra("userImage", senderImage);
                        mIntent.putExtra("username", senderName);
                        mIntent.putExtra("file", FileType);
                        mIntent.putExtra("phone", phone);
                        mIntent.putExtra("messageId", messageId);
                        mIntent.putExtra("message", messageBody);
                        sendBroadcast(mIntent);


                    } catch (Exception e) {
                        AppHelper.LogCat("Exception  last id messages send MainService" + e.getMessage());
                    }

                });

            }
            handler.postDelayed(() -> RecipientMarkMessageAsDelivered(messageId), 1500);
            realm.close();


        } catch (JSONException e) {
            AppHelper.LogCat("save message Exception MainService" + e.getMessage());
        }


    }
}