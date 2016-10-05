package com.sourcecanyon.whatsClone.presenters;

import com.sourcecanyon.whatsClone.api.APIService;
import com.sourcecanyon.whatsClone.app.AppConstants;
import com.sourcecanyon.whatsClone.fragments.WishlistsFragment;
import com.sourcecanyon.whatsClone.helpers.AppHelper;
import com.sourcecanyon.whatsClone.helpers.Files.FilesManager;
import com.sourcecanyon.whatsClone.interfaces.Presenter;
import com.sourcecanyon.whatsClone.models.groups.GroupsModel;
import com.sourcecanyon.whatsClone.models.groups.MembersGroupModel;
import com.sourcecanyon.whatsClone.models.messages.WishlistsModel;
import com.sourcecanyon.whatsClone.models.messages.MessagesModel;
import com.sourcecanyon.whatsClone.models.users.Pusher;
import com.sourcecanyon.whatsClone.services.apiServices.ConversationsService;
import com.sourcecanyon.whatsClone.services.apiServices.GroupsService;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class WishlistsPresenter implements Presenter {
    private final WishlistsFragment wishlistsFragmentView;
    private final Realm realm;
    private ConversationsService mConversationsService;
    private GroupsService mGroupsService;


    public WishlistsPresenter(WishlistsFragment wishlistsFragment) {
        this.wishlistsFragmentView = wishlistsFragment;
        this.realm = Realm.getDefaultInstance();
    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(wishlistsFragmentView))
            EventBus.getDefault().register(wishlistsFragmentView);
        APIService mApiService = APIService.with(wishlistsFragmentView.getContext());
        mConversationsService = new ConversationsService(realm);
        mGroupsService = new GroupsService(realm, wishlistsFragmentView.getContext(), mApiService);
        loadDataLocal();


    }

    public void updateConversationList() {
        mGroupsService.updateGroups().subscribe(this::checkGroups, throwable -> AppHelper.LogCat("Groups list WishlistsPresenter " + throwable.getMessage()));
        mConversationsService.getConversations().subscribe(wishlistsFragmentView::UpdateConversation, wishlistsFragmentView::onErrorLoading, wishlistsFragmentView::onHideLoading);
    }
    private void loadDataLocal() {
        mGroupsService.updateGroups().subscribe(this::checkGroups, throwable -> AppHelper.LogCat("Groups list WishlistsPresenter " + throwable.getMessage()));
        mConversationsService.getConversations().subscribe(wishlistsFragmentView::ShowConversation, wishlistsFragmentView::onErrorLoading, wishlistsFragmentView::onHideLoading);
    }

    private void checkGroups(List<GroupsModel> groupsModels) {
        for (GroupsModel groupsModel1 : groupsModels) {
            if (!FilesManager.isFileImagesGroupExists(FilesManager.getGroupImage(String.valueOf(groupsModel1.getId()), groupsModel1.getGroupName()))) {
                FilesManager.downloadFilesToDevice(wishlistsFragmentView.getActivity(), groupsModel1.getGroupImage(), String.valueOf(groupsModel1.getId()), groupsModel1.getGroupName(), "group");
            }
            if (!mGroupsService.checkIfGroupConversationExist(groupsModel1.getId())) {
                realm.executeTransaction(realm1 -> {
                    int lastConversationID = 1;
                    int UnreadMessageCounter = 0;
                    int lastID = 1;
                    try {
                        WishlistsModel wishlistsModel = realm1.where(WishlistsModel.class).findAll().last();
                        lastConversationID = wishlistsModel.getId();
                        lastConversationID++;

                        UnreadMessageCounter = Integer.parseInt(wishlistsModel.getUnreadMessageCounter());
                        UnreadMessageCounter++;

                        List<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).findAll();
                        lastID = messagesModel1.size();
                        lastID++;

                        AppHelper.LogCat("last ID group message" + lastID);

                    } catch (Exception e) {
                        AppHelper.LogCat("last conversation  ID group if conversation id = 0 Exception" + e.getMessage());
                        lastConversationID = 1;
                    }
                    WishlistsModel wishlistsModel = new WishlistsModel();
                    RealmList<MessagesModel> messagesModelRealmList = new RealmList<MessagesModel>();
                    MessagesModel messagesModel = null;
                    for (MembersGroupModel membersGroupModel1 : groupsModel1.getMembers()) {
                        messagesModel = new MessagesModel();
                        messagesModel.setId(lastID);
                        messagesModel.setDate(groupsModel1.getCreatedDate());

                        messagesModel.setSenderID(groupsModel1.getCreatorID());
                        messagesModel.setRecipientID("0");
                        messagesModel.setStatus(AppConstants.IS_SEEN);
                        messagesModel.setUsername(groupsModel1.getCreator());
                        messagesModel.setGroup(true);
                        messagesModel.setPhone(membersGroupModel1.getPhone());
                        messagesModel.setImageFile("null");
                        messagesModel.setVideoFile("null");
                        messagesModel.setAudioFile("null");
                        messagesModel.setDocumentFile("null");
                        messagesModel.setVideoThumbnailFile("null");
                        messagesModel.setFileDownLoad(true);
                        messagesModel.setFileUpload(true);
                        messagesModel.setDuration("0");
                        messagesModel.setFileSize("0");
                        messagesModel.setGroupID(groupsModel1.getId());
                        messagesModel.setConversationID(lastConversationID);
                        if (!membersGroupModel1.isLeft())
                            messagesModel.setMessage("FK");
                        else
                            messagesModel.setMessage("LT");

                        if (!membersGroupModel1.isLeft())
                            wishlistsModel.setLastMessage("FK");
                        else
                            wishlistsModel.setLastMessage("LT");
                        messagesModelRealmList.add(messagesModel);
                    }

                    wishlistsModel.setLastMessageId(lastID);
                    wishlistsModel.setRecipientID("0");
                    wishlistsModel.setCreatorID(groupsModel1.getCreatorID());
                    wishlistsModel.setRecipientUsername(groupsModel1.getGroupName());
                    wishlistsModel.setRecipientImage(groupsModel1.getGroupImage());
                    wishlistsModel.setGroupID(groupsModel1.getId());
                    wishlistsModel.setMessageDate(groupsModel1.getCreatedDate());
                    wishlistsModel.setId(lastConversationID);
                    wishlistsModel.setGroup(true);
                    wishlistsModel.setMessages(messagesModelRealmList);
                    wishlistsModel.setStatus(AppConstants.IS_SEEN);
                    wishlistsModel.setUnreadMessageCounter(String.valueOf(UnreadMessageCounter));
                    wishlistsModel.setCreatedOnline(true);
                    realm1.copyToRealmOrUpdate(wishlistsModel);

                });
                EventBus.getDefault().post(new Pusher("createGroup"));
            }

        }
    }


    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(wishlistsFragmentView);
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
