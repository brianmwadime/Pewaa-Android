package com.fortunekidew.pewaad.services;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.gifts.GiftDetailsActivity;
import com.fortunekidew.pewaad.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaad.activities.main.MainActivity;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.notifications.NotificationsManager;
import com.fortunekidew.pewaad.models.gifts.GiftsModel;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.json.JSONObject;
import org.parceler.Parcels;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class PewaaFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        switch (remoteMessage.getNotification().getBody()) {
             case AppConstants.SOCKET_CONTRIBUTOR_ADDED:
                 try {
                     JSONObject data = new JSONObject(remoteMessage.getData());
                     JSONObject wishlist = new JSONObject(data.getString("wishlist"));

                     Intent wishlistIntent = new Intent();
                     wishlistIntent.setClass(PewaaApplication.getAppContext(), WishlistActivity.class);
                     wishlistIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_ID, wishlist.getString("id"));
                     wishlistIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_TITLE, wishlist.getString("name"));
                     wishlistIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_PERMISSION, data.getString("permissions"));

                     NotificationsManager.showWishlistNotification(PewaaApplication.getAppContext(), wishlistIntent, "You have been added to " + wishlist.getString("name") + " as an " + data.getString("permissions"));

                 } catch (Exception e) {
                     AppHelper.LogCat("User received Contributor Added Exception on Notifications " + e.getMessage());
                 }
            break;
            case AppConstants.SOCKET_GIFT_ADDED:

                try {
                    JSONObject data = new JSONObject(remoteMessage.getData());
                    Intent giftIntent = new Intent();
                    giftIntent.setClass(PewaaApplication.getAppContext(), GiftDetailsActivity.class);
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

                    NotificationsManager.showGiftNotification(PewaaApplication.getAppContext(), giftIntent, "New Gift Added to wishlist " + data.getString("wishlist_name"));


                } catch (Exception e) {
                    AppHelper.LogCat("User received Gift Added Exception on Notifications " + e.getMessage());
                }
                break;
            case AppConstants.SOCKET_PAYMENT_COMPLETED:
                try {
                    JSONObject data = new JSONObject(remoteMessage.getData());
                    Intent giftIntent = new Intent();
                    giftIntent.setClass(PewaaApplication.getAppContext(), GiftDetailsActivity.class);
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

                    NotificationsManager.showGiftNotification(PewaaApplication.getAppContext(), giftIntent, "Your contribution of Kes " + data.getString("amount") + " for gift item - " + data.getString("name") + " completed with status " + data.getString("status"));

                } catch (Exception e) {
                    AppHelper.LogCat("User received Cash out request Exception on Notifications " + e.getMessage());
                }
                break;
            case AppConstants.SOCKET_CASHOUT:
                try {
                    JSONObject data = new JSONObject(remoteMessage.getData());
                    NotificationsManager.showCashoutNotification(PewaaApplication.getAppContext(), data.getString("message"));

                } catch (Exception e) {
                    AppHelper.LogCat("Cash out request Exception on Notifications " + e.getMessage());
                }

                break;
        }

    }

    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Pewaa")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
