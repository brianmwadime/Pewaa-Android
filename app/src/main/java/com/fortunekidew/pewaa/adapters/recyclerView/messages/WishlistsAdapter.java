package com.fortunekidew.pewaa.adapters.recyclerView.messages;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.messages.MessagesActivity;
import com.fortunekidew.pewaa.activities.profile.ProfilePreviewActivity;
import com.fortunekidew.pewaa.api.APIGroups;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.helpers.UtilsPhone;
import com.fortunekidew.pewaa.helpers.UtilsString;
import com.fortunekidew.pewaa.helpers.UtilsTime;
import com.fortunekidew.pewaa.helpers.images.ImageCompressionAsyncTask;
import com.fortunekidew.pewaa.models.groups.GroupResponse;
import com.fortunekidew.pewaa.models.messages.WishlistsModel;
import com.fortunekidew.pewaa.models.messages.MessagesModel;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaa.services.MainService;

import org.joda.time.DateTime;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.realm.Realm;
import io.realm.RealmList;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaa.helpers.UtilsString.unescapeJava;


/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class WishlistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final Activity mActivity;
    private RealmList<WishlistsModel> mConversations;
    private Realm realm;
    private APIService mApiService;
    private String SearchQuery;
    private SparseBooleanArray selectedItems;
    private boolean isActivated = false;
    private static final int HEADER_ITEM = 1;
    private static final int BASIC_ITEM = 2;
    private boolean headerAdded = false;


    public WishlistsAdapter(@NonNull Activity mActivity) {
        this.mActivity = mActivity;
        this.mConversations = new RealmList<>();
        this.realm = Realm.getDefaultInstance();
        this.mApiService = new APIService(mActivity);
        this.selectedItems = new SparseBooleanArray();
    }

    public WishlistsAdapter(@NonNull Activity mActivity, RealmList<WishlistsModel> mConversations) {
        this.mActivity = mActivity;
        this.mConversations = new RealmList<>();
        this.realm = Realm.getDefaultInstance();
        this.mApiService = new APIService(mActivity);
        this.selectedItems = new SparseBooleanArray();
        this.mConversations = mConversations;
    }

    public void setConversations(RealmList<WishlistsModel> wishlistsModelList) {
        this.mConversations = wishlistsModelList;
        notifyDataSetChanged();
    }


    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    public void animateTo(List<WishlistsModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<WishlistsModel> newModels) {
        for (int i = mConversations.size() - 1; i >= 0; i--) {
            final WishlistsModel model = mConversations.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<WishlistsModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final WishlistsModel model = newModels.get(i);
            if (!mConversations.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<WishlistsModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final WishlistsModel model = newModels.get(toPosition);
            final int fromPosition = mConversations.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private WishlistsModel removeItem(int position) {
        final WishlistsModel model = mConversations.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, WishlistsModel model) {
        mConversations.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final WishlistsModel model = mConversations.remove(fromPosition);
        mConversations.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    //Methods for search end

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return HEADER_ITEM;
        } else {
            return BASIC_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == HEADER_ITEM) {
            itemView = LayoutInflater.from(mActivity).inflate(R.layout.admob_banner_header, parent, false);
            return new HeaderViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_conversation, parent, false);
            return new ConversationViewHolder(itemView);
        }

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ConversationViewHolder) {

            final ConversationViewHolder conversationViewHolder = (ConversationViewHolder) holder;
            final WishlistsModel wishlistsModel = this.mConversations.get(position - 1);


            try {
                final MessagesModel messagesModel = realm.where(MessagesModel.class).equalTo("conversationID", wishlistsModel.getId()).findAll().last();
                String Username = null;


                try {


                    if (wishlistsModel.isGroup()) {
                        if (wishlistsModel.getRecipientUsername() != null) {
                            String usrename = unescapeJava(wishlistsModel.getRecipientUsername());
                            conversationViewHolder.setUsername(usrename);
                            Username = usrename;
                        }
                    } else {
                        String name = UtilsPhone.getContactName(mActivity, wishlistsModel.getRecipientPhone());
                        if (name != null) {
                            conversationViewHolder.setUsername(name);
                            Username = name;
                        } else {
                            conversationViewHolder.setUsername(wishlistsModel.getRecipientPhone());
                            Username = wishlistsModel.getRecipientPhone();
                        }

                    }


                } catch (Exception e) {
                    AppHelper.LogCat("Exception " + e.getMessage());
                }


                if (wishlistsModel.isGroup()) {
                    if (!wishlistsModel.getCreatedOnline()) {
                        conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorGray2));

                    } else {
                        conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                    }
                    if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("image");
                    } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("video");
                    } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("audio");
                    } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                        conversationViewHolder.setTypeFile("document");
                    } else {

                        conversationViewHolder.isFile.setVisibility(View.GONE);
                        conversationViewHolder.FileContent.setVisibility(View.GONE);
                        conversationViewHolder.lastMessage.setVisibility(View.VISIBLE);
                        switch (messagesModel.getMessage()) {
                            case "FK":
                                if (wishlistsModel.getCreatorID() == PreferenceManager.getID(mActivity)) {
                                    if (!wishlistsModel.getCreatedOnline()) {
                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.tap_to_create_group));
                                    } else {
                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_created_this_group));
                                    }

                                } else {
                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                    if (name != null) {
                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_created_this_group));
                                    } else {
                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_created_this_group));
                                    }
                                }


                                break;
                            case "LT":
                                if (wishlistsModel.getCreatorID() == PreferenceManager.getID(mActivity)) {
                                    conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_left));
                                } else {
                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                    if (name != null) {
                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_left));
                                    } else {
                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_left));
                                    }


                                }

                                break;
                            default:

                                conversationViewHolder.isFile.setVisibility(View.GONE);
                                conversationViewHolder.FileContent.setVisibility(View.GONE);
                                conversationViewHolder.lastMessage.setVisibility(View.VISIBLE);
                                conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                break;
                        }
                    }

                    if (messagesModel.getDate() != null) {
                        DateTime messageDate = UtilsTime.getCorrectDate(messagesModel.getDate());
                        String finalDate = UtilsTime.convertDateToString(mActivity, messageDate);
                        conversationViewHolder.setMessageDate(finalDate);
                    }

                    if (wishlistsModel.getRecipientImage() != null) {

                        if (wishlistsModel.getCreatedOnline())
                            conversationViewHolder.setGroupImage(wishlistsModel.getRecipientImage(), String.valueOf(wishlistsModel.getGroupID()), wishlistsModel.getRecipientUsername());
                        else
                            conversationViewHolder.setGroupImageOffline(wishlistsModel.getRecipientImage());
                    } else {
                        conversationViewHolder.setNullGroupImage(R.drawable.ic_group_holder_wihte_48dp);
                    }
                    if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                        conversationViewHolder.showSent(messagesModel.getStatus());
                    } else {
                        conversationViewHolder.hideSent();
                    }
                    if (wishlistsModel.getStatus() == AppConstants.IS_WAITING && !wishlistsModel.getUnreadMessageCounter().equals("0")) {
                        conversationViewHolder.ChangeStatusUnread();
                        conversationViewHolder.showCounter();
                        conversationViewHolder.setCounter(wishlistsModel.getUnreadMessageCounter());
                        EventBus.getDefault().post(new Pusher("MessagesCounter"));
                    } else {
                        conversationViewHolder.ChangeStatusRead();
                        conversationViewHolder.hideCounter();
                        EventBus.getDefault().post(new Pusher("MessagesCounter"));
                    }

                    MainService.mSocket.on(AppConstants.SOCKET_IS_MEMBER_TYPING, args -> mActivity.runOnUiThread(() -> {

                        JSONObject data = (JSONObject) args[0];
                        try {

                            String senderID = data.getString("senderId");
                            int groupId = data.getInt("groupId");
                            ContactsModel contactsModel = realm.where(ContactsModel.class).equalTo("id", senderID).findFirst();
                            String finalName;
                            if (contactsModel.getUsername() != null) {
                                finalName = unescapeJava(contactsModel.getUsername());
                            } else {
                                String name = UtilsPhone.getContactName(mActivity, contactsModel.getPhone());
                                if (name != null) {
                                    finalName = name;
                                } else {
                                    finalName = contactsModel.getPhone();
                                }

                            }
                            if (groupId == wishlistsModel.getGroupID()) {
                                if (senderID == PreferenceManager.getID(mActivity)) return;
                                conversationViewHolder.lastMessage.setTextColor(mActivity.getResources().getColor(R.color.colorBlueLight));
                                conversationViewHolder.lastMessage.setText(finalName + " " + mActivity.getString(R.string.isTyping));
                            }

                        } catch (Exception e) {
                            AppHelper.LogCat(e);
                        }
                    }));

                    MainService.mSocket.on(AppConstants.SOCKET_IS_MEMBER_STOP_TYPING, args -> mActivity.runOnUiThread(() -> {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String senderID = data.getString("senderId");
                            if (senderID == PreferenceManager.getID(mActivity)) return;
                            if (wishlistsModel.isGroup()) {
                                if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                                    conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                    conversationViewHolder.setTypeFile("image");
                                } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                                    conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                    conversationViewHolder.setTypeFile("video");
                                } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                                    conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                    conversationViewHolder.setTypeFile("audio");
                                } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                                    conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                    conversationViewHolder.setTypeFile("document");
                                } else {

                                    conversationViewHolder.isFile.setVisibility(View.GONE);
                                    conversationViewHolder.FileContent.setVisibility(View.GONE);
                                    switch (messagesModel.getMessage()) {
                                        case "FK":
                                            if (wishlistsModel.getCreatorID() == PreferenceManager.getID(mActivity)) {
                                                if (!wishlistsModel.getCreatedOnline()) {
                                                    conversationViewHolder.setLastMessage(mActivity.getString(R.string.tap_to_create_group));
                                                } else {
                                                    conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_created_this_group));
                                                }

                                            } else {
                                                String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                                if (name != null) {
                                                    conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_created_this_group));
                                                } else {
                                                    conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_created_this_group));
                                                }
                                            }


                                            break;
                                        case "LT":
                                            if (wishlistsModel.getCreatorID() == PreferenceManager.getID(mActivity)) {
                                                conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_left));
                                            } else {
                                                String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                                if (name != null) {
                                                    conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_left));
                                                } else {
                                                    conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_left));
                                                }


                                            }

                                            break;
                                        default:
                                            conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                            break;
                                    }
                                }
                            } else {

                                conversationViewHolder.isFile.setVisibility(View.GONE);
                                conversationViewHolder.FileContent.setVisibility(View.GONE);
                                if (messagesModel.getMessage() != null) {
                                    conversationViewHolder.setLastMessage(messagesModel.getMessage());

                                } else {
                                    conversationViewHolder.setLastMessage(wishlistsModel.getLastMessage());

                                }
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat("ex member stop typing " + e.getMessage());
                        }

                    }));
                } else {

                    if (!wishlistsModel.getCreatedOnline()) {
                        conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                    } else {
                        conversationViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
                        if (messagesModel.getMessage() != null) {
                            if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("image");
                            } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("video");
                            } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("audio");
                            } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                                conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                conversationViewHolder.setTypeFile("document");
                            } else {

                                conversationViewHolder.isFile.setVisibility(View.GONE);
                                conversationViewHolder.FileContent.setVisibility(View.GONE);
                                conversationViewHolder.setLastMessage(messagesModel.getMessage());
                            }

                        } else {

                            conversationViewHolder.setLastMessage(wishlistsModel.getLastMessage());
                        }
                        if (messagesModel.getDate() != null) {
                            DateTime messageDate = UtilsTime.getCorrectDate(messagesModel.getDate());
                            String finalDate = UtilsTime.convertDateToString(mActivity, messageDate);
                            conversationViewHolder.setMessageDate(finalDate);
                        } else {
                            DateTime messageDate = UtilsTime.getCorrectDate(messagesModel.getDate());
                            String finalDate = UtilsTime.convertDateToString(mActivity, messageDate);
                            conversationViewHolder.setMessageDate(finalDate);
                        }
                    }


                    if (messagesModel.getSenderID() == PreferenceManager.getID(mActivity)) {
                        conversationViewHolder.showSent(messagesModel.getStatus());
                    } else {
                        conversationViewHolder.hideSent();
                    }

                    MainService.mSocket.on(AppConstants.SOCKET_IS_TYPING, args -> mActivity.runOnUiThread(() -> {

                        JSONObject data = (JSONObject) args[0];
                        try {

                            String senderID = data.getString("senderId");
                            String recipientID = data.getString("recipientId");
                            if (senderID == messagesModel.getSenderID() && recipientID == messagesModel.getRecipientID() && !messagesModel.isGroup()) {
                                conversationViewHolder.lastMessage.setTextColor(mActivity.getResources().getColor(R.color.colorBlueLight));
                                conversationViewHolder.lastMessage.setText(mActivity.getString(R.string.isTyping));
                            }

                        } catch (Exception e) {
                            AppHelper.LogCat(e);
                        }
                    }));

                    MainService.mSocket.on(AppConstants.SOCKET_IS_STOP_TYPING, args -> mActivity.runOnUiThread(() -> {
                        try {
                            JSONObject data = (JSONObject) args[0];
                            String senderID = data.getString("senderId");
                            if (senderID != PreferenceManager.getID(mActivity)) {
                                if (wishlistsModel.isGroup()) {
                                    if (messagesModel.getImageFile() != null && !messagesModel.getImageFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("image");
                                    } else if (messagesModel.getVideoFile() != null && !messagesModel.getVideoFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("video");
                                    } else if (messagesModel.getAudioFile() != null && !messagesModel.getAudioFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("audio");
                                    } else if (messagesModel.getDocumentFile() != null && !messagesModel.getDocumentFile().equals("null")) {
                                        conversationViewHolder.lastMessage.setVisibility(View.GONE);
                                        conversationViewHolder.setTypeFile("document");
                                    } else {

                                        conversationViewHolder.isFile.setVisibility(View.GONE);
                                        conversationViewHolder.FileContent.setVisibility(View.GONE);
                                        switch (messagesModel.getMessage()) {
                                            case "FK":
                                                if (wishlistsModel.getCreatorID() == PreferenceManager.getID(mActivity)) {
                                                    if (!wishlistsModel.getCreatedOnline()) {
                                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.tap_to_create_group));
                                                    } else {
                                                        conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_created_this_group));
                                                    }

                                                } else {
                                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                                    if (name != null) {
                                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_created_this_group));
                                                    } else {
                                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_created_this_group));
                                                    }
                                                }


                                                break;
                                            case "LT":
                                                if (wishlistsModel.getCreatorID() == PreferenceManager.getID(mActivity)) {
                                                    conversationViewHolder.setLastMessage(mActivity.getString(R.string.you_left));
                                                } else {
                                                    String name = UtilsPhone.getContactName(mActivity, messagesModel.getPhone());
                                                    if (name != null) {
                                                        conversationViewHolder.setLastMessage("" + name + mActivity.getString(R.string.he_left));
                                                    } else {
                                                        conversationViewHolder.setLastMessage("" + messagesModel.getPhone() + mActivity.getString(R.string.he_left));
                                                    }


                                                }

                                                break;
                                            default:
                                                conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                                break;
                                        }
                                    }
                                } else {

                                    conversationViewHolder.isFile.setVisibility(View.GONE);
                                    conversationViewHolder.FileContent.setVisibility(View.GONE);
                                    if (messagesModel.getMessage() != null) {
                                        conversationViewHolder.setLastMessage(messagesModel.getMessage());
                                    } else {
                                        conversationViewHolder.setLastMessage(wishlistsModel.getLastMessage());
                                    }
                                }
                            }

                        } catch (Exception e) {
                            AppHelper.LogCat("stop typing");
                        }
                    }));

                    if (wishlistsModel.getStatus() == AppConstants.IS_WAITING && !wishlistsModel.getUnreadMessageCounter().equals("0")) {
                        conversationViewHolder.ChangeStatusUnread();
                        conversationViewHolder.showCounter();
                        conversationViewHolder.setCounter(wishlistsModel.getUnreadMessageCounter());
                        EventBus.getDefault().post(new Pusher("MessagesCounter"));
                    } else {
                        conversationViewHolder.ChangeStatusRead();
                        conversationViewHolder.hideCounter();
                        EventBus.getDefault().post(new Pusher("MessagesCounter"));
                    }
                    if (wishlistsModel.getRecipientImage() != null) {
                        conversationViewHolder.setUserImage(wishlistsModel.getRecipientImage(), String.valueOf(wishlistsModel.getRecipientID()), wishlistsModel.getRecipientUsername());
                    } else {
                        conversationViewHolder.setNullUserImage(R.drawable.ic_user_holder_white_48dp);
                    }

                }


                SpannableString recipientUsername = SpannableString.valueOf(Username);
                if (SearchQuery == null) {
                    conversationViewHolder.username.setText(recipientUsername, TextView.BufferType.NORMAL);
                } else {
                    int index = TextUtils.indexOf(Username.toLowerCase(), SearchQuery.toLowerCase());
                    if (index >= 0) {
                        recipientUsername.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorAccent)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        recipientUsername.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    conversationViewHolder.username.setText(recipientUsername, TextView.BufferType.SPANNABLE);
                }

                conversationViewHolder.setOnClickListener(view -> {
                    if (!isActivated) {
                        if (wishlistsModel.isGroup()) {
                            if (!wishlistsModel.getCreatedOnline()) {
                                try {
                                    StringBuilder ids = new StringBuilder();
                                    for (int x = 0; x <= PreferenceManager.getMembers(mActivity).size() - 1; x++) {
                                        ids.append(PreferenceManager.getMembers(mActivity).get(x).getUserId());
                                        ids.append(",");
                                    }
                                    String id = UtilsString.removelastString(ids.toString());
                                    // create RequestBody instance from file
                                    RequestBody requestIds =
                                            RequestBody.create(MediaType.parse("multipart/form-data"), id);
                                    conversationViewHolder.getProgressBarGroup();
                                    ImageCompressionAsyncTask imageCompression = new ImageCompressionAsyncTask() {
                                        @Override
                                        protected void onPostExecute(byte[] imageBytes) {
                                            // image here is compressed & ready to be sent to the server
                                            // create RequestBody instance from file
                                            RequestBody requestFile = RequestBody.create(MediaType.parse("image*//**//**//**//*"), imageBytes);
                                            // create RequestBody instance from file
                                            RequestBody requestName = RequestBody.create(MediaType.parse("multipart/form-data"), wishlistsModel.getRecipientUsername());
                                            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, PreferenceManager.getToken(mActivity), EndPoints.BASE_URL);
                                            Call<GroupResponse> CreateGroupCall = mApiGroups.createGroup(PreferenceManager.getID(mActivity), requestName, requestFile, requestIds, wishlistsModel.getMessageDate());
                                            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                                                                        @Override
                                                                        public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                                                                            if (response.isSuccessful()) {
                                                                                if (response.body().isSuccess()) {
                                                                                    conversationViewHolder.setProgressBarGroup();
                                                                                    Realm realm = Realm.getDefaultInstance();
                                                                                    realm.executeTransaction(realm1 -> {
                                                                                        WishlistsModel wishlistsModel1 = realm1.where(WishlistsModel.class).equalTo("id", wishlistsModel.getId()).findFirst();
                                                                                        wishlistsModel1.setCreatedOnline(true);
                                                                                        wishlistsModel1.setGroupID(response.body().getGroupID());
                                                                                        wishlistsModel1.setRecipientImage(response.body().getGroupImage());
                                                                                        realm1.copyToRealmOrUpdate(wishlistsModel1);
                                                                                        if (!FilesManager.isFileImagesGroupExists(FilesManager.getGroupImage(String.valueOf(response.body().getGroupID()), wishlistsModel.getRecipientUsername()))) {
                                                                                            FilesManager.downloadFilesToDevice(mActivity, response.body().getGroupImage(), String.valueOf(response.body().getGroupID()), wishlistsModel.getRecipientUsername(), "group");
                                                                                        }
                                                                                        EventBus.getDefault().post(new Pusher("createGroup"));
                                                                                        PreferenceManager.clearMembers(mActivity);
                                                                                    });
                                                                                    realm.close();
                                                                                    AppHelper.Snackbar(mActivity, conversationViewHolder.itemView, response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                                                                                } else {
                                                                                    conversationViewHolder.setProgressBarGroup();
                                                                                    AppHelper.Snackbar(mActivity, conversationViewHolder.itemView, response.body().getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                                                                                }
                                                                            } else {
                                                                                conversationViewHolder.setProgressBarGroup();
                                                                                AppHelper.Snackbar(mActivity, conversationViewHolder.itemView, response.body().getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure
                                                                                (Call<GroupResponse> call, Throwable t) {
                                                                            conversationViewHolder.setProgressBarGroup();
                                                                            AppHelper.LogCat("Failed create group " + t.getMessage());
                                                                            AppHelper.Snackbar(mActivity, conversationViewHolder.itemView, mActivity.getString(R.string.create_group_failed), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                                                                        }
                                                                    }

                                            );
                                        }
                                    };
                                    imageCompression.execute(wishlistsModel.getRecipientImage());
                                } catch (Exception e) {
                                    AppHelper.LogCat("execption  ids " + e.getMessage());
                                }
                            } else {
                                if (view.getId() == R.id.user_image) {
                                    if (AppHelper.isAndroid5()) {
                                        Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                        mIntent.putExtra("conversationID", wishlistsModel.getId());
                                        mIntent.putExtra("groupID", wishlistsModel.getGroupID());
                                        mIntent.putExtra("isGroup", wishlistsModel.isGroup());
                                        mIntent.putExtra("userID", messagesModel.getRecipientID());
                                        mActivity.startActivity(mIntent);
                                    } else {
                                        Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                        mIntent.putExtra("conversationID", wishlistsModel.getId());
                                        mIntent.putExtra("groupID", wishlistsModel.getGroupID());
                                        mIntent.putExtra("isGroup", wishlistsModel.isGroup());
                                        mIntent.putExtra("userID", messagesModel.getRecipientID());
                                        mActivity.startActivity(mIntent);
                                        mActivity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                    }
                                } else {


                                    Intent messagingIntent = new Intent(mActivity, MessagesActivity.class);
                                    messagingIntent.putExtra("conversationID", wishlistsModel.getId());
                                    messagingIntent.putExtra("groupID", wishlistsModel.getGroupID());
                                    messagingIntent.putExtra("isGroup", true);
                                    messagingIntent.putExtra("recipientID", wishlistsModel.getRecipientID());
                                    mActivity.startActivity(messagingIntent);
                                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                }
                            }

                        } else {
                            if (view.getId() == R.id.user_image) {

                                if (AppHelper.isAndroid5()) {
                                    Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                    mIntent.putExtra("userID", wishlistsModel.getRecipientID());
                                    mIntent.putExtra("isGroup", false);
                                    mActivity.startActivity(mIntent);
                                } else {
                                    Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                                    mIntent.putExtra("userID", wishlistsModel.getRecipientID());
                                    mIntent.putExtra("isGroup", false);
                                    mActivity.startActivity(mIntent);
                                    mActivity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                                }
                            } else {

                                Intent messagingIntent = new Intent(mActivity, MessagesActivity.class);
                                messagingIntent.putExtra("conversationID", wishlistsModel.getId());
                                messagingIntent.putExtra("recipientID", wishlistsModel.getRecipientID());
                                messagingIntent.putExtra("isGroup", false);
                                mActivity.startActivity(messagingIntent);
                                mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        }
                    } else {
                        if (wishlistsModel.isGroup()) {
                            AppHelper.LogCat("This is a group you cannot delete this conversation now");
                        } else {
                            EventBus.getDefault().post(new Pusher("ItemIsActivated", view));
                        }

                    }


                });
            } catch (
                    Exception e
                    )

            {
                AppHelper.LogCat("Conversations Adapter  Exception" + e.getMessage());
            }

            holder.itemView.setActivated(selectedItems.get(position, false));

            if (holder.itemView.isActivated()) {

                final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_for_button_animtion_enter);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        conversationViewHolder.selectIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                conversationViewHolder.selectIcon.startAnimation(animation);
            } else {


                final Animation animation = AnimationUtils.loadAnimation(mActivity, R.anim.scale_for_button_animtion_exit);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        conversationViewHolder.selectIcon.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                conversationViewHolder.selectIcon.startAnimation(animation);
            }


        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            if (PreferenceManager.ShowBannerAds(mActivity)) {
                headerHolder.rootLayout.setVisibility(View.VISIBLE);

                if (PreferenceManager.getUnitBannerAdsID(mActivity) != null) {
                    if (!headerAdded) {
                        AdView mAdView = new AdView(mActivity);
                        mAdView.setAdSize(AdSize.BANNER);
                        mAdView.setAdUnitId(PreferenceManager.getUnitBannerAdsID(mActivity));
                        AdRequest adRequest = new AdRequest.Builder()
                                .build();
                        if (mAdView.getAdSize() != null || mAdView.getAdUnitId() != null)
                            mAdView.loadAd(adRequest);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        headerHolder.rootLayout.addView(mAdView, params);
                        headerAdded = true;
                    }


                }
            } else {
                headerHolder.rootLayout.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public int getItemCount() {
        return mConversations.size() > 0 ? mConversations.size() + 1 : 1;
    }


    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {

            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
            if (!isActivated)
                isActivated = true;

        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        if (isActivated)
            isActivated = false;
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }


    public WishlistsModel getItem(int position) {
        return mConversations.get(position - 1);
    }

    public void updateConversations(int conversationId) {
        for (int i = 0; i < mConversations.size(); i++) {
            WishlistsModel model = mConversations.get(i);
            if (conversationId == model.getId()) {
                Realm realm = Realm.getDefaultInstance();
                mConversations.remove(i);
                notifyItemRemoved(i);
                WishlistsModel wishlistsModel = realm.where(WishlistsModel.class).equalTo("id", conversationId).findFirst();
                mConversations.add(i, wishlistsModel);
                notifyItemInserted(i);
                realm.close();
                break;
            }
        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.adParentLyout)
        LinearLayout rootLayout;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class ConversationViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.user_image)
        ImageView userImage;
        @Bind(R.id.username)
        EmojiconTextView username;
        @Bind(R.id.last_message)
        EmojiconTextView lastMessage;
        @Bind(R.id.counter)
        TextView counter;
        @Bind(R.id.date_message)
        TextView messageDate;
        @Bind(R.id.status_messages)
        ImageView status_messages;
        @Bind(R.id.file_types)
        ImageView isFile;
        @Bind(R.id.file_types_text)
        TextView FileContent;

        @Bind(R.id.create_group_pro_bar)
        ProgressBar progressBarGroup;

        @Bind(R.id.conversation_row)
        LinearLayout ConversationRow;


        @Bind(R.id.select_icon)
        LinearLayout selectIcon;

        ConversationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void getProgressBarGroup() {
            progressBarGroup.setVisibility(View.VISIBLE);
        }

        void setProgressBarGroup() {
            progressBarGroup.setVisibility(View.GONE);
        }

        @SuppressLint("SetTextI18n")
        void setTypeFile(String type) {
            isFile.setVisibility(View.VISIBLE);
            FileContent.setVisibility(View.VISIBLE);
            switch (type) {
                case "image":
                    isFile.setImageResource(R.drawable.ic_photo_camera_gray_24dp);
                    FileContent.setText("Image");
                    break;
                case "video":
                    isFile.setImageResource(R.drawable.ic_videocam_gray_24dp);
                    FileContent.setText("Video");
                    break;
                case "audio":
                    isFile.setImageResource(R.drawable.ic_headset_gray_24dp);
                    FileContent.setText("Audio");
                    break;
                case "document":
                    isFile.setImageResource(R.drawable.ic_document_file_gray_24dp);
                    FileContent.setText("Document");
                    break;
            }

        }

        void setGroupImageOffline(String ImageUrl) {
            Glide.with(mActivity)
                    .load(ImageUrl)
                    .asBitmap()
                    .transform(new CropCircleTransformation(mActivity))
                    .override(100, 100)
                    .into(userImage);
        }

        void setGroupImage(String ImageUrl, String userId, String name) {


            if (FilesManager.isFileImagesGroupExists(FilesManager.getGroupImage(userId, name))) {
                Glide.with(mActivity)
                        .load(FilesManager.getFileImageGroup(userId, name))
                        .asBitmap()
                        .transform(new CropCircleTransformation(mActivity))
                        .override(100, 100)
                        .into(userImage);
            } else {


                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        userImage.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        userImage.setImageBitmap(resource);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        userImage.setImageDrawable(errorDrawable);
                    }


                };

                Glide.with(mActivity)
                        .load(EndPoints.BASE_URL + ImageUrl)
                        .asBitmap()
                        .transform(new CropCircleTransformation(mActivity))
                        .override(100, 100)
                        .into(target);

            }

        }

        void setUserImage(String ImageUrl, String userId, String name) {


            if (FilesManager.isFileImagesProfileExists(FilesManager.getProfileImage(userId, name))) {
                Glide.with(mActivity)
                        .load(FilesManager.getFileImageProfile(userId, name))
                        .asBitmap()
                        .transform(new CropCircleTransformation(mActivity))
                        .override(100, 100)
                        .into(userImage);
            } else {


                BitmapImageViewTarget target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        userImage.setImageDrawable(placeholder);
                    }

                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        userImage.setImageBitmap(resource);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        userImage.setImageDrawable(errorDrawable);
                    }


                };

                Glide.with(mActivity)
                        .load(EndPoints.BASE_URL + ImageUrl)
                        .asBitmap()
                        .transform(new CropCircleTransformation(mActivity))
                        .override(100, 100)
                        .into(target);


            }

        }

        void setNullUserImage(int drawable) {
            userImage.setPadding(2, 2, 2, 2);
            userImage.setImageResource(drawable);
        }

        void setNullGroupImage(int drawable) {
            userImage.setPadding(4, 4, 4, 4);
            userImage.setImageResource(drawable);
        }


        void setUsername(String user) {

            if (user.length() > 16)
                username.setText(user.substring(0, 16) + "... " + "");
            else
                username.setText(user);

        }

        void setLastMessage(String LastMessage) {
            lastMessage.setVisibility(View.VISIBLE);
            lastMessage.setTextColor(mActivity.getResources().getColor(R.color.colorGray2));
            String last = unescapeJava(LastMessage);
            if (last.length() > 18)
                lastMessage.setText(last.substring(0, 18) + "... " + "");
            else
                lastMessage.setText(last);

        }

        void setMessageDate(String MessageDate) {
            messageDate.setText(MessageDate);
        }

        void hideSent() {
            status_messages.setVisibility(View.GONE);
        }

        void showSent(int status) {
            status_messages.setVisibility(View.VISIBLE);
            switch (status) {
                case AppConstants.IS_WAITING:
                    status_messages.setImageResource(R.drawable.ic_access_time_gray_24dp);
                    break;
                case AppConstants.IS_SENT:
                    status_messages.setImageResource(R.drawable.ic_done_gray_24dp);
                    break;
                case AppConstants.IS_DELIVERED:
                    status_messages.setImageResource(R.drawable.ic_done_all_gray_24dp);
                    break;
                case AppConstants.IS_SEEN:
                    status_messages.setImageResource(R.drawable.ic_done_all_blue_24dp);
                    break;

            }

        }

        void setCounter(String Counter) {
            counter.setText(Counter.toUpperCase());
        }

        void hideCounter() {
            counter.setVisibility(View.GONE);
        }


        void showCounter() {
            counter.setVisibility(View.VISIBLE);
        }

        void ChangeStatusUnread() {
            messageDate.setTypeface(null, Typeface.BOLD);
            username.setTypeface(null, Typeface.BOLD);
            messageDate.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGreenLight));
        }

        void ChangeStatusRead() {
            messageDate.setTypeface(null, Typeface.NORMAL);
            username.setTypeface(null, Typeface.BOLD);
            messageDate.setTextColor(ContextCompat.getColor(mActivity, R.color.colorGray2));
        }

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            userImage.setOnClickListener(listener);
        }

    }

}
