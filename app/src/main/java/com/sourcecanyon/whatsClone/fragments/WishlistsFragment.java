package com.sourcecanyon.whatsClone.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sourcecanyon.whatsClone.R;
import com.sourcecanyon.whatsClone.adapters.recyclerView.messages.WishlistsAdapter;
import com.sourcecanyon.whatsClone.app.WhatsCloneApplication;
import com.sourcecanyon.whatsClone.helpers.AppHelper;
import com.sourcecanyon.whatsClone.helpers.PreferenceManager;
import com.sourcecanyon.whatsClone.interfaces.LoadingData;
import com.sourcecanyon.whatsClone.models.messages.WishlistsModel;
import com.sourcecanyon.whatsClone.models.messages.MessagesModel;
import com.sourcecanyon.whatsClone.models.users.Pusher;
import com.sourcecanyon.whatsClone.presenters.WishlistsPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Abderrahim El imame  on 20/01/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class WishlistsFragment extends Fragment implements LoadingData, RecyclerView.OnItemTouchListener, ActionMode.Callback {

    @Bind(R.id.WishlistsList)
    RecyclerView WishlistList;
    @Bind(R.id.empty)
    LinearLayout emptyWishlists;

    private WishlistsAdapter mWishlistsAdapter;
    private WishlistsPresenter mWishlistsPresenter = new WishlistsPresenter(this);
    private Realm realm;
    private GestureDetectorCompat gestureDetector;
    private ActionMode actionMode;
    private RealmList<WishlistsModel> mWishlists;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_conversations, container, false);
        ButterKnife.bind(this, mView);
        realm = Realm.getDefaultInstance();
        mWishlistsPresenter.onCreate();
        initializerView();
        return mView;
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        setHasOptionsMenu(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(WhatsCloneApplication.getAppContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWishlistsAdapter = new WishlistsAdapter(getActivity(), mWishlists);
        WishlistList.setLayoutManager(mLinearLayoutManager);
        WishlistList.setAdapter(mWishlistsAdapter);
        WishlistList.setItemAnimator(new DefaultItemAnimator());
        WishlistList.addOnItemTouchListener(this);
        gestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewBenOnGestureListener());

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }


    /**
     * method to toggle the selection
     *
     * @param position
     */
    private void ToggleSelection(int position) {
        mWishlistsAdapter.toggleSelection(position);
        String title = String.format("%s selected", mWishlistsAdapter.getSelectedItemCount());
        actionMode.setTitle(title);


    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.select_conversation_menu, menu);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        EventBus.getDefault().post(new Pusher("actionModeStarted"));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_conversations:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                builder.setMessage(R.string.alert_message_delete_conversation);

                builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
                    Realm realm = Realm.getDefaultInstance();
                    int currentPosition;
                    if (mWishlistsAdapter.getSelectedItemCount() != 0) {

                        AppHelper.showDialog(getActivity(), getString(R.string.deleting_chat));

                        for (int x = 0; x < mWishlistsAdapter.getSelectedItems().size(); x++) {
                            currentPosition = mWishlistsAdapter.getSelectedItems().get(x);
                            WishlistsModel wishlistsModel = mWishlistsAdapter.getItem(currentPosition);

                            int conversationID = getConversationId(wishlistsModel.getRecipientID(), PreferenceManager.getID(WhatsCloneApplication.getAppContext()), realm);


                            realm.executeTransactionAsync(realm1 -> {
                                RealmResults<MessagesModel> messagesModel1 = realm1.where(MessagesModel.class).equalTo("conversationID", conversationID).findAll();
                                messagesModel1.deleteAllFromRealm();
                            }, () -> {
                                AppHelper.LogCat("Message Deleted  successfully  WishlistsFragment");

                                RealmResults<MessagesModel> messagesModel1 = realm.where(MessagesModel.class).equalTo("conversationID", conversationID).findAll();
                                if (messagesModel1.size() == 0) {
                                    realm.executeTransactionAsync(realm1 -> {
                                        WishlistsModel wishlistsModel1 = realm1.where(WishlistsModel.class).equalTo("id", conversationID).findFirst();
                                        wishlistsModel1.deleteFromRealm();
                                    }, () -> {
                                        AppHelper.LogCat("Conversation deleted successfully WishlistsFragment");
                                        EventBus.getDefault().post(new Pusher("deleteConversation"));
                                        mWishlistsAdapter.notifyDataSetChanged();
                                    }, error -> {
                                        AppHelper.LogCat("Delete conversation failed  WishlistsFragment" + error.getMessage());

                                    });
                                } else {
                                    MessagesModel lastMessage = realm.where(MessagesModel.class).equalTo("conversationID", conversationID).findAll().last();
                                    realm.executeTransactionAsync(realm1 -> {
                                        WishlistsModel wishlistsModel1 = realm1.where(WishlistsModel.class).equalTo("id", conversationID).findFirst();
                                        wishlistsModel1.setLastMessage(lastMessage.getMessage());
                                        wishlistsModel1.setLastMessageId(lastMessage.getId());
                                        realm1.copyToRealmOrUpdate(wishlistsModel1);
                                    }, () -> {
                                        AppHelper.LogCat("Conversation deleted successfully WishlistsFragment ");
                                        EventBus.getDefault().post(new Pusher("deleteConversation"));
                                        mWishlistsAdapter.notifyDataSetChanged();
                                    }, error -> {
                                        AppHelper.LogCat("Delete conversation failed  WishlistsFragment" + error.getMessage());

                                    });
                                }
                            }, error -> {
                                AppHelper.LogCat("Delete message failed WishlistsFragment" + error.getMessage());

                            });


                        }
                        AppHelper.hideDialog();
                    }
                    if (actionMode != null) {
                        mWishlistsAdapter.clearSelections();
                        actionMode.finish();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }
                    realm.close();
                });


                builder.setNegativeButton(R.string.No, (dialog, whichButton) -> {

                });

                builder.show();
                return true;
            default:
                return false;
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

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
        mWishlistsAdapter.clearSelections();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        EventBus.getDefault().post(new Pusher("actionModeDestroyed"));
    }


    private class RecyclerViewBenOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = WishlistList.findChildViewUnder(e.getX(), e.getY());
            int currentPosition = WishlistList.getChildAdapterPosition(view);
            WishlistsModel wishlistsModel = mWishlistsAdapter.getItem(currentPosition);
            if (!wishlistsModel.isGroup()) {
                if (actionMode != null) {
                    return;
                }
                actionMode = getActivity().startActionMode(WishlistsFragment.this);
                ToggleSelection(currentPosition);
            }

            super.onLongPress(e);
        }

    }

    /**
     * method to show conversation list
     *
     * @param wishlistsModels this is parameter for  ShowConversation  method
     */
    public void ShowConversation(List<WishlistsModel> wishlistsModels) {

        if (wishlistsModels.size() != 0) {
            WishlistList.setVisibility(View.VISIBLE);
            emptyWishlists.setVisibility(View.GONE);
            RealmList<WishlistsModel> wishlistsModels1 = new RealmList<WishlistsModel>();
            for (WishlistsModel wishlistsModel : wishlistsModels) {
                wishlistsModels1.add(wishlistsModel);
            }
            mWishlists = wishlistsModels1;
        } else {
            WishlistList.setVisibility(View.GONE);
            emptyWishlists.setVisibility(View.VISIBLE);
        }
    }

    /**
     * method to show conversation list
     *
     * @param wishlistsModels this is parameter for  ShowConversation  method
     */
    public void UpdateConversation(List<WishlistsModel> wishlistsModels) {

        if (wishlistsModels.size() != 0) {
            WishlistList.setVisibility(View.VISIBLE);
            emptyWishlists.setVisibility(View.GONE);
            RealmList<WishlistsModel> wishlistsModels1 = new RealmList<WishlistsModel>();
            for (WishlistsModel wishlistsModel : wishlistsModels) {
                wishlistsModels1.add(wishlistsModel);
            }
            mWishlistsAdapter.setConversations(wishlistsModels1);
        } else {
            WishlistList.setVisibility(View.GONE);
            emptyWishlists.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mWishlistsPresenter.onDestroy();
        realm.close();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {
    }


    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        // mWishlistsPresenter.onEvent(pusher);
        int messageId = pusher.getMessageId();
        switch (pusher.getAction()) {
            case "ItemIsActivated":
                int idx = WishlistList.getChildAdapterPosition(pusher.getView());
                if (actionMode != null) {
                    ToggleSelection(idx);
                    return;
                }

                break;
            case "new_message_group":
            case "new_message":
                mWishlistsPresenter.updateConversationList();
                break;
            case "messages_read":
                mWishlistsPresenter.updateConversationList();
                break;
            case "new_message_sent":
            case "messages_seen":
            case "messages_delivered":
                mWishlistsPresenter.updateConversationList();
                break;
            case "deleteConversation":
                mWishlistsPresenter.updateConversationList();
                break;
            case "createGroup":
            case "deleteGroup":
            case "exitGroup":
                mWishlistsPresenter.updateConversationList();
                break;
        }
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Conversations Fragment " + throwable.getMessage());
    }


}
