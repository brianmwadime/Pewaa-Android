package com.fortunekidew.pewaa.presenters;


import android.os.Handler;

import com.fortunekidew.pewaa.activities.messages.MessagesActivity;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.helpers.notifications.NotificationsManager;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.models.messages.WishlistsModel;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;
import com.fortunekidew.pewaa.services.apiServices.MessagesService;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class MessagesPresenter implements Presenter {
    private final MessagesActivity view;
    private final Realm realm;
    private int ConversationID, GroupID;
    private String RecipientID;
    private Boolean isGroup;
    private MessagesService mMessagesService;

    public MessagesPresenter(MessagesActivity messagesActivity) {
        this.view = messagesActivity;
        this.realm = Realm.getDefaultInstance();
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(view)) EventBus.getDefault().register(view);

        if (view.getIntent().getExtras() != null) {
            if (view.getIntent().hasExtra("conversationID")) {
                ConversationID = view.getIntent().getExtras().getInt("conversationID");
            }
            if (view.getIntent().hasExtra("recipientID")) {
                RecipientID = view.getIntent().getExtras().getString("recipientID");
            }

            if (view.getIntent().hasExtra("groupID")) {
                GroupID = view.getIntent().getExtras().getInt("groupID");
            }

            if (view.getIntent().hasExtra("isGroup")) {
                isGroup = view.getIntent().getExtras().getBoolean("isGroup");
            }

        }

        APIService mApiService = APIService.with(view.getApplicationContext());
        mMessagesService = new MessagesService(realm);
        ContactsService mContactsService = new ContactsService(realm, view.getApplicationContext(), mApiService);

        if (isGroup) {
            mMessagesService.getContact(PreferenceManager.getID(view)).subscribe(view::updateContact, view::onErrorLoading);
            mMessagesService.getGroupInfo(GroupID).subscribe(view::updateGroupInfo, view::onErrorLoading);
            loadLocalGroupData();
            new Handler().postDelayed(this::updateConversationStatus, 500);
        } else {

            mMessagesService.getContact(PreferenceManager.getID(view)).subscribe(view::updateContact, view::onErrorLoading);
            try {
                mMessagesService.getContact(RecipientID).subscribe(view::updateContactRecipient, view::onErrorLoading);
            } catch (Exception e) {
                AppHelper.LogCat(" " + e.getMessage());
            }
            mContactsService.getContactInfo(RecipientID).subscribe(view::updateContactRecipient, view::onErrorLoading);
            loadLocalData();
            new Handler().postDelayed(this::updateConversationStatus, 500);
        }

    }

    public void updateConversationStatus() {
        try {
            realm.executeTransaction(realm1 -> {
                WishlistsModel wishlistsModel1 = realm1.where(WishlistsModel.class).equalTo("id", ConversationID).findFirst();
                if (wishlistsModel1 != null) {
                    wishlistsModel1.setStatus(AppConstants.IS_SEEN);
                    wishlistsModel1.setUnreadMessageCounter("0");
                    realm1.copyToRealmOrUpdate(wishlistsModel1);
                    EventBus.getDefault().post(new Pusher("MessagesCounter"));
                    EventBus.getDefault().post(new Pusher("messages_read", ConversationID));
                }
            });
        } catch (Exception e) {
            AppHelper.LogCat("There is no conversation unRead MessagesPresenter ");
        }
    }

    public void loadLocalGroupData() {
        if (NotificationsManager.getManager())
            NotificationsManager.cancelNotification(GroupID);
        mMessagesService.getConversation(ConversationID).subscribe(view::ShowMessages, view::onErrorLoading, view::onHideLoading);
    }

    public void loadLocalData() {
//        if (NotificationsManager.getManager())
//            NotificationsManager.cancelNotification(RecipientID);
        mMessagesService.getConversation(ConversationID, RecipientID, PreferenceManager.getID(view)).subscribe(view::ShowMessages, view::onErrorLoading);

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(view);
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onStop() {

    }

}
