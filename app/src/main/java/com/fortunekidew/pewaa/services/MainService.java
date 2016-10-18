package com.fortunekidew.pewaa.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.models.wishlists.MessagesModel;
import com.fortunekidew.pewaa.receivers.MessagesReceiverBroadcast;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
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


    @Override
    public void onCreate() {
        super.onCreate();
        realm = Realm.getDefaultInstance();
        handler = new Handler();
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
        realm.close();
        stopSelf();


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
}