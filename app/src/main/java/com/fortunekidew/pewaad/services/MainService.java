package com.fortunekidew.pewaad.services;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.fortunekidew.pewaad.activities.gifts.GiftDetailsActivity;
import com.fortunekidew.pewaad.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.helpers.notifications.NotificationsManager;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.receivers.MessagesReceiverBroadcast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.net.URISyntaxException;

import io.realm.Realm;
import io.socket.client.IO;
import io.socket.client.Socket;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class MainService extends IntentService {

    public static Socket mSocket;
    private static String socketID;
    private static final long TIMEOUT = 20 * 1000;
    private static final int RETRY_ATTEMPT = 5;
    private MessagesReceiverBroadcast mChangeListener;
    private Intent mIntent;
    private Realm realm;
    private static Handler handler;
    private Context mContext;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public MainService() {
        super(AppConstants.TAG);
    }

    /**
     * method to get socketId for the connected user
     *
     * @return socketID
     */
    public static String getSocketID() {
        return socketID;
    }

    /**
     * method to set the socketId for the connected user
     *
     * @param socketID this the parameter
     */
    private static void setSocketID(String socketID) {
        MainService.socketID = socketID;
    }

    /**
     * method to disconnect user form server
     */
    public static void disconnectSocket() {

        if (mSocket != null) {

            mSocket.disconnect();
            mSocket.off(Socket.EVENT_CONNECT);
            mSocket.off(Socket.EVENT_DISCONNECT);
            mSocket.off(Socket.EVENT_CONNECT_TIMEOUT);
            mSocket.off(Socket.EVENT_RECONNECT);
            mSocket.off(AppConstants.SOCKET_IS_ONLINE);
            mSocket.off(AppConstants.SOCKET_IS_LAST_SEEN);
            mSocket.off(AppConstants.SOCKET_IS_STOP_TYPING);
            mSocket.off(AppConstants.SOCKET_IS_TYPING);
            mSocket.off(AppConstants.SOCKET_USER_PING);
            mSocket.off(AppConstants.SOCKET_CONNECTED);

            mSocket.close();
            mSocket = null;

        }

        AppHelper.LogCat("disconnect in service");
    }

    /**
     * method for server connection initialization
     */
    public void connectToServer(Context mContext) {
        IO.Options options = new IO.Options();
        options.forceNew = true;
        options.reconnection = true;
        options.timeout = TIMEOUT; //set -1 to  disable it
        options.reconnectionDelay = 0;
        options.reconnectionAttempts = RETRY_ATTEMPT;
        try {
            mSocket = IO.socket(EndPoints.CHAT_SERVER_URL, options);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }


        mSocket.on(Socket.EVENT_CONNECT, args -> {
            AppHelper.LogCat("You are connected to chat server with id " + getSocketID());
        });

        mSocket.on(Socket.EVENT_DISCONNECT, args -> {
            AppHelper.LogCat("You have lost connection to chat server ");

        });
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
            AppHelper.LogCat("SOCKET TIMEOUT");
            reconnect(mContext);
        });
        mSocket.on(Socket.EVENT_RECONNECT, args -> {
            AppHelper.LogCat("Reconnect");
            reconnect(mContext);
        });
        mSocket.connect();
        JSONObject json = new JSONObject();
        try {
            json.put("connected", true);
            json.put("connectedId", PreferenceManager.getID(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(AppConstants.SOCKET_CONNECTED, json);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("connected", true);
            jsonObject.put("senderId", PreferenceManager.getID(mContext));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mSocket.emit(AppConstants.SOCKET_IS_ONLINE, jsonObject);

        isUserConnected(mContext);
    }


    /**
     * method to reconnect sockets
     */
    private static void reconnect(Context mContext) {
        mContext.getApplicationContext().stopService(new Intent(mContext.getApplicationContext(), MainService.class));
        mContext.getApplicationContext().startService(new Intent(mContext.getApplicationContext(), MainService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        if (PreferenceManager.getToken(mContext) != null) {
            handler = new Handler();
            connectToServer(mContext);

            if (AppConstants.CRASH_LYTICS)
                PewaaApplication.setupCrashlytics();

            onCompletedPayment();
            onGiftAdded();
            onContributorAdded();

            mChangeListener = new MessagesReceiverBroadcast() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                protected void MessageReceived(Context context, Intent intent) {
                    String action = intent.getAction();
                    switch (action) {
                        case "new_user_message_notification":
                            String file = intent.getExtras().getString("file");
                            String userphone = intent.getExtras().getString("phone");
                            String messageBody = intent.getExtras().getString("message");
                            int recipientId = intent.getExtras().getInt("recipientID");
                            int senderId = intent.getExtras().getInt("senderId");
                            String userImage = intent.getExtras().getString("userImage");
                            Intent messagingIntent = new Intent(mContext, WishlistActivity.class);
                            messagingIntent.putExtra("conversationID", intent.getExtras().getInt("conversationID"));
                            messagingIntent.putExtra("recipientID", recipientId);
                            messagingIntent.putExtra("isGroup", false);
                            messagingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            AppHelper.LogCat("recipient " + recipientId);
                            AppHelper.LogCat("sender " + senderId);
                            if (file != null) {
//                            NotificationsManager.showUserNotification(mContext, messagingIntent, userphone, file, recipientId, userImage);
                            } else {
//                            NotificationsManager.showUserNotification(mContext, messagingIntent, userphone, messageBody, recipientId, userImage);
                            }

                            break;
                        case "new_group_message_notification":
                            String File = intent.getExtras().getString("file");
                            String userPhone = intent.getExtras().getString("senderPhone");
                            String groupName = unescapeJava(intent.getExtras().getString("groupName"));
                            String messageGroupBody = intent.getExtras().getString("message");
                            int groupID = intent.getExtras().getInt("groupID");
                            String groupImage = intent.getExtras().getString("groupImage");
                            String memberName;
                            String name = UtilsPhone.getContactName(mContext, userPhone);
                            if (name != null) {
                                memberName = name;
                            } else {
                                memberName = userPhone;
                            }
                            messagingIntent = new Intent(mContext, WishlistActivity.class);
                            messagingIntent.putExtra("conversationID", intent.getExtras().getInt("conversationID"));
                            messagingIntent.putExtra("groupID", intent.getExtras().getInt("groupID"));
                            messagingIntent.putExtra("isGroup", true);
                            messagingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            break;
                    }

                }
            };


            getApplication().registerReceiver(mChangeListener, new IntentFilter("new_wishlist_notification"));
            getApplication().registerReceiver(mChangeListener, new IntentFilter("new_gift_notification"));
        }
    }

    private void onContributorAdded() {
        mSocket.on(AppConstants.SOCKET_CONTRIBUTOR_ADDED, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                JSONObject wishlist = data.getJSONObject("wishlist");
                String userId = data.getString("user_id");

                if (userId.equals(PreferenceManager.getID(mContext))) {
                    Intent wishlistIntent = new Intent();
                    wishlistIntent.setClass(mContext, WishlistActivity.class);
                    wishlistIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_ID, data.getString("wishlist_id"));
                    wishlistIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_TITLE, wishlist.getString("name"));
                    wishlistIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_PERMISSION, data.getString("permissions"));

                    NotificationsManager.showWishlistNotification(mContext, wishlistIntent, "You have been added to Wishlist." + wishlist.getString("name"));
                }

            } catch (Exception e) {
                AppHelper.LogCat("User received Contributor Added Exception MainService" + e.getMessage());
            }
        });
    }

    private void onGiftAdded() {
        mSocket.on(AppConstants.SOCKET_GIFT_ADDED, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                JSONArray contributors = data.getJSONArray("contributors");

                for (int i=0; i < contributors.length(); i++) {
                    if (contributors.getString(i).equals(PreferenceManager.getID(mContext))) {
                        Intent giftIntent = new Intent();
                        giftIntent.setClass(mContext, GiftDetailsActivity.class);
                        giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_ID, data.getString("id"));
                        giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_IMAGE, data.getString("avatar"));
                        giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_TITLE, data.getString("name"));
                        giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_DESC, data.getString("description"));
                        giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_PRICE, data.getString("price"));

                        final GiftsModel giftModel = new GiftsModel();
                        giftModel.setAvatar(data.getString("avatar"));
                        giftModel.setId(data.getString("id"));
                        giftModel.setDescription(data.getString("description"));
                        giftModel.setPrice(data.getDouble("price"));
                        giftModel.setName(data.getString("name"));

                        giftIntent.putExtra(GiftDetailsActivity.EXTRA_GIFT, Parcels.wrap(GiftsModel.class, giftModel));

                        NotificationsManager.showGiftNotification(mContext, giftIntent, "New Gift Added to wishlist " + data.getString("wishlist_name"));
                    }
                }

            } catch (Exception e) {
                AppHelper.LogCat("User received Gift Added Exception MainService" + e.getMessage());
            }
        });
    }

    private void onCompletedPayment() {
        mSocket.on(AppConstants.SOCKET_PAYMENT_COMPLETED, args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String userId = data.getString("user_id");

                if (userId.equals(PreferenceManager.getID(mContext))) {
                    Intent giftIntent = new Intent();
                    giftIntent.setClass(mContext, GiftDetailsActivity.class);
                    giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_ID, data.getString("id"));
                    giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_IMAGE, data.getString("avatar"));
                    giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_TITLE, data.getString("name"));
                    giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_DESC, data.getString("description"));
                    giftIntent.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_PRICE, data.getString("price"));

                    final GiftsModel giftModel = new GiftsModel();
                    giftModel.setAvatar(data.getString("avatar"));
                    giftModel.setId(data.getString("id"));
                    giftModel.setDescription(data.getString("description"));
                    giftModel.setPrice(data.getDouble("price"));
                    giftModel.setName(data.getString("name"));
                    giftModel.setCreatorAvatar(data.getString("creator_avatar"));
                    giftModel.setCreatorPhone(data.getString("creator_phone"));
                    giftModel.setCreatorName(data.getString("creator_name"));

                    giftIntent.putExtra(GiftDetailsActivity.EXTRA_GIFT, Parcels.wrap(GiftsModel.class, giftModel));

                    NotificationsManager.showGiftNotification(mContext, giftIntent, "Your contribution of Kes " + data.getString("amount") + " for gift item - " + data.getString("name") + " completed with status " + data.getString("status"));
                }

            } catch (Exception e) {
                AppHelper.LogCat("User received Payment complete Exception MainService" + e.getMessage());
            }
        });
    }

    /**
     * method to check if user is connected to server
     */
    private static void isUserConnected(Context mContext) {
        mSocket.on(AppConstants.SOCKET_CONNECTED, args -> {
            final JSONObject data = (JSONObject) args[0];
            try {
                String connectedId = data.getString("connectedId");
                String socketId = data.getString("socketId");
                boolean connected = data.getBoolean("connected");
                if (connectedId != PreferenceManager.getID(mContext)) {
                    if (connected) {
                        AppHelper.LogCat("User with id  --> " + connectedId + " is connected --> " + getSocketID() + " <---");
//                        handler.postDelayed(() -> MainService.unSentMessages(connectedId), 1000);
                    } else {
                        AppHelper.LogCat("User with id  --> " + connectedId + " is disconnected --> " + getSocketID() + " <---");
                    }

                } else {
                    setSocketID(socketId);

                    AppHelper.LogCat("You are connected with socket id --> " + getSocketID() + " <---");
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
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // service finished
        getApplicationContext().unregisterReceiver(mChangeListener);
        disconnectSocket();
        stopSelf();


    }

}