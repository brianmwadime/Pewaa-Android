package com.fortunekidew.pewaad.presenters;


import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.profile.ProfileActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;
import com.fortunekidew.pewaad.models.wishlists.MessagesModel;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;
import com.fortunekidew.pewaad.services.apiServices.GroupsService;
import com.fortunekidew.pewaad.services.apiServices.MessagesService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTime;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ProfilePresenter implements Presenter {
    private final ProfileActivity view;
    private final Realm realm;
    private int groupID;
    private String userID;
    private GroupsService mGroupsService;
    private MessagesService mMessagesService;
    private APIService mApiService;

    public ProfilePresenter(ProfileActivity profileActivity) {
        this.view = profileActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(view)) EventBus.getDefault().register(view);
        mApiService = APIService.with(view);

        if (view.getIntent().hasExtra("userID")) {
            userID = view.getIntent().getExtras().getString("userID");
            loadContactData();
            try {
                loadUserMediaData();
            } catch (Exception e) {
                AppHelper.LogCat("Media Execption");
            }

        }
        if (view.getIntent().hasExtra("groupID")) {
            groupID = view.getIntent().getExtras().getInt("groupID");
            loadGroupData(groupID);
            try {
                loadGroupMediaData();
            } catch (Exception e) {
                AppHelper.LogCat("Media Execption");
            }
        }


    }


    private void loadUserMediaData() {
        mMessagesService = new MessagesService(realm);
        mMessagesService.getUserMedia(userID, PreferenceManager.getID(view)).subscribe(view::ShowMedia, view::onErrorLoading);
    }

    private void loadGroupMediaData() {
        mMessagesService = new MessagesService(realm);
        mMessagesService.getGroupMedia(groupID, PreferenceManager.getID(view)).subscribe(view::ShowMedia, view::onErrorLoading);
    }

    private void loadContactData() {
        ContactsService mContactsService = new ContactsService(realm, view, mApiService);
        try {
            mContactsService.getContact(userID).subscribe(view::ShowContact, view::onErrorLoading);
        } catch (Exception e) {
            AppHelper.LogCat("" + e.getMessage());
        }
        mContactsService.getContactInfo(userID).subscribe(view::ShowContact, view::onErrorLoading);

    }

    private void loadGroupData(int groupID) {
        mGroupsService = new GroupsService(realm, view, mApiService);
        mGroupsService.getGroup(groupID).subscribe(view::ShowGroup, view::onErrorLoading);
        mGroupsService.getGroupInfo(groupID).subscribe(view::ShowGroup, view::onErrorLoading);
        mGroupsService.getGroupMembers(groupID).subscribe(view::ShowGroupMembers, view::onErrorLoading);
        //mGroupsService.updateGroupMembers(groupID).subscribe(view::updateGroupMembers, view::onErrorLoading);
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

    public void onEventPush(Pusher pusher) {
        switch (pusher.getAction()) {
            case "addMember":
                mGroupsService.getGroupInfo(Integer.parseInt(pusher.getData()));
                loadGroupData(Integer.parseInt(pusher.getData()));
                break;
            case "exitThisGroup":
                mGroupsService.getGroupInfo(Integer.parseInt(pusher.getData()));
                loadGroupData(Integer.parseInt(pusher.getData()));
                break;
            case "updateGroupName":
                mGroupsService.getGroupInfo(Integer.parseInt(pusher.getData()));
                loadGroupData(Integer.parseInt(pusher.getData()));
                break;
        }
    }

    public void ExitGroup() {
        mGroupsService.ExitGroup(groupID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {

                realm.executeTransactionAsync(realm1 -> {
                    DateTime current = new DateTime();
                    String sendTime = String.valueOf(current);
                    WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).equalTo("groupID", groupID).findFirst();
                    ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(view)).findFirst();
                    int lastID = 1;
                    try {

                        List<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).findAll();
                        lastID = messagesModel1.size();
                        lastID++;

                        AppHelper.LogCat("last ID group message" + lastID);

                    } catch (Exception e) {
                        AppHelper.LogCat("last message  ID   0 Exception" + e.getMessage());
                    }

                    RealmList<GiftsModel> messagesModelRealmList = wishlistsModel.getGifts();
                    GiftsModel messagesModel = new GiftsModel();
//                    messagesModel.setId(lastID);
//                    messagesModel.setDate(sendTime);
//                    messagesModel.setSenderID(contactsModel.getId());
//                    messagesModel.setStatus(AppConstants.IS_WAITING);
//                    messagesModel.setUsername(contactsModel.getUsername());
//                    messagesModel.setGroup(true);
//                    messagesModel.setMessage("LT");
//                    messagesModel.setGroupID(groupID);
//                    messagesModel.setWishlistID(wishlistsModel.getId());
//                    messagesModelRealmList.add(messagesModel);

                    wishlistsModel.setCreatedOnline(true);
                    realm1.copyToRealmOrUpdate(wishlistsModel);
                }, () -> {
                    AppHelper.hideDialog();
                    EventBus.getDefault().post(new Pusher("exitGroup", statusResponse.getMessage()));
                    EventBus.getDefault().post(new Pusher("exitThisGroup", String.valueOf(groupID)));
                }, error -> AppHelper.LogCat("error while exiting group" + error.getMessage()));
            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view, view.findViewById(R.id.containerProfile), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, throwable -> {
            try {
                AppHelper.hideDialog();
                view.onErrorExiting();
            } catch (Exception e) {
                AppHelper.LogCat(e);
            }
        });

    }

    public void DeleteGroup() {
        mGroupsService.DeleteGroup(groupID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {

                realm.executeTransactionAsync(realm1 -> {
                    WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).equalTo("groupID", groupID).findFirst();
                    wishlistsModel.deleteFromRealm();
                }, () -> {
                    AppHelper.hideDialog();
                    EventBus.getDefault().post(new Pusher("deleteGroup", statusResponse.getMessage()));

                }, error -> AppHelper.LogCat("Error while deleting group" + error.getMessage()));
            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view, view.findViewById(R.id.containerProfile), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, throwable -> {
            try {
                AppHelper.hideDialog();
                view.onErrorDeleting();
            } catch (Exception e) {
                AppHelper.LogCat(e);
            }

        });
    }
}