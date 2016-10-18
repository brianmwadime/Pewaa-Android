package com.fortunekidew.pewaa.adapters.recyclerView.groups;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.rockerhieu.emojicon.EmojiconTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaa.api.APIGroups;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.helpers.UtilsPhone;
import com.fortunekidew.pewaa.models.groups.GroupResponse;
import com.fortunekidew.pewaa.models.groups.MembersGroupModel;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.ui.RecyclerViewFastScroller;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaa.helpers.UtilsString.unescapeJava;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    protected Activity mActivity;
    private List<MembersGroupModel> mContactsModel;
    private APIService mApiService;
    private Realm realm;
    private boolean isAnAdmin;

    public GroupMembersAdapter(@NonNull Activity mActivity, APIService mApiService, boolean isAnAdmin) {
        this.mActivity = mActivity;
        this.mApiService = mApiService;
        this.realm = Realm.getDefaultInstance();
        this.isAnAdmin = isAnAdmin;
    }

    public void setContacts(List<MembersGroupModel> contactsModelList) {
        this.mContactsModel = contactsModelList;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_group_members, parent, false);
        return new ContactsViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
        final MembersGroupModel membersGroupModel = this.mContactsModel.get(position);
        try {

            if (membersGroupModel.getUserId() == PreferenceManager.getID(mActivity)) {
                contactsViewHolder.itemView.setEnabled(false);
            }
            if (membersGroupModel.getUsername() != null) {
                if (membersGroupModel.getUserId() == PreferenceManager.getID(mActivity)) {
                    contactsViewHolder.setUsername(mActivity.getString(R.string.you));
                } else {
                    contactsViewHolder.setUsername(membersGroupModel.getUsername());
                }

            } else {
                try {
                    if (membersGroupModel.getUserId() == PreferenceManager.getID(mActivity)) {
                        contactsViewHolder.setUsername(mActivity.getString(R.string.you));
                    } else {
                        String name = UtilsPhone.getContactName(mActivity, membersGroupModel.getPhone());
                        if (name != null) {
                            contactsViewHolder.setUsername(name);
                        } else {
                            contactsViewHolder.setUsername(membersGroupModel.getPhone());
                        }

                    }
                } catch (Exception e) {
                    AppHelper.LogCat(" " + e.getMessage());
                }

            }

            if (membersGroupModel.getStatus() != null) {
                contactsViewHolder.setStatus(membersGroupModel.getStatus());
            } else {
                contactsViewHolder.setStatus(membersGroupModel.getPhone());
            }
            if (membersGroupModel.getRole().equals("member")) {
                contactsViewHolder.hideAdmin();
            } else {
                contactsViewHolder.showAdmin();
            }
            if (membersGroupModel.getImage() != null) {
                contactsViewHolder.setUserImage(membersGroupModel.getImage(), String.valueOf(membersGroupModel.getUserId()), membersGroupModel.getUsername());
            } else {
                contactsViewHolder.setNullUserImage(R.drawable.ic_user_holder_white_48dp);
            }
        } catch (Exception e) {
            AppHelper.LogCat("Exception" + e.getMessage());
        }
        contactsViewHolder.setOnClickListener(view -> {

            if (isAnAdmin) {
                String TheName;
                    String name = UtilsPhone.getContactName(mActivity, membersGroupModel.getPhone());
                    if (name != null) {
                        TheName = name;
                    } else {
                        TheName = membersGroupModel.getPhone();
                    }

                CharSequence options[] = new CharSequence[]{mActivity.getString(R.string.message_group_option) + TheName + "", mActivity.getString(R.string.view_group_option) + TheName + "", mActivity.getString(R.string.make_admin_group_option), mActivity.getString(R.string.remove_group_option) + TheName + ""};

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            Intent messagingIntent = new Intent(mActivity, WishlistActivity.class);
                            messagingIntent.putExtra("conversationID", 0);
                            messagingIntent.putExtra("recipientID", membersGroupModel.getUserId());
                            messagingIntent.putExtra("isGroup", false);
                            mActivity.startActivity(messagingIntent);
                            mActivity.finish();
                            break;
                        case 1:
                            contactsViewHolder.viewContact(membersGroupModel.getPhone());
                            break;
                        case 2:
                            contactsViewHolder.MakeMemberAsAdmin(membersGroupModel.getUserId(), membersGroupModel.getGroupID());
                            break;
                        case 3:
                            AlertDialog.Builder builderDelete = new AlertDialog.Builder(mActivity);
                            builderDelete.setMessage(mActivity.getString(R.string.remove_from_group) + TheName + mActivity.getString(R.string.from_group))
                                    .setPositiveButton(mActivity.getString(R.string.ok), (dialog1, which1) -> {
                                        AppHelper.showDialog(mActivity, mActivity.getString(R.string.deleting_group));
                                        contactsViewHolder.RemoveMembersFromGroup(membersGroupModel.getUserId(), membersGroupModel.getGroupID());
                                    }).setNegativeButton(mActivity.getString(R.string.cancel), null).show();


                            break;
                    }

                });
                builder.show();

            }
            return true;
        });


    }


    @Override
    public int getItemCount() {
        if (mContactsModel != null) return mContactsModel.size();
        return 0;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        try {
            return Character.toString(mContactsModel.get(pos).getUsername().charAt(0));
        } catch (Exception e) {
            AppHelper.LogCat(e.getMessage());
            return e.getMessage();
        }

    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.wishlist_image)
        ImageView userImage;
        @Bind(R.id.wishlist_name)
        TextView username;
        @Bind(R.id.status)
        EmojiconTextView status;
        @Bind(R.id.admin)
        TextView admin;

        @Bind(R.id.member)
        TextView member;

        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void setUserImage(String ImageUrl, String userId, String name) {

            if (FilesManager.isFileImagesProfileExists(FilesManager.getProfileImage(userId, name))) {
                Glide.with(mActivity)
                        .load(FilesManager.getFileImageProfile(userId, name))
                        .asBitmap()
                        .transform(new CropCircleTransformation(mActivity))
                        .override(100, 100)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
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
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(target);

            }
        }

        void setNullUserImage(int drawable) {
            userImage.setPadding(2, 2, 2, 2);
            userImage.setImageResource(drawable);
        }

        void hideAdmin() {
            admin.setVisibility(View.GONE);
            member.setVisibility(View.VISIBLE);
        }

        void showAdmin() {
            admin.setVisibility(View.VISIBLE);
            member.setVisibility(View.GONE);
        }

        void setUsername(String Username) {
            username.setText(Username);
        }

        void setStatus(String Status) {
            String statu = unescapeJava(Status);
            if (statu.length() > 18)
                status.setText(statu.substring(0, 18) + "... " + "");
            else
                status.setText(statu);
        }


        void setOnClickListener(View.OnLongClickListener listener) {
            itemView.setOnLongClickListener(listener);
        }

        void viewContact(String phone) {
            long ContactID = UtilsPhone.getContactID(mActivity, phone);
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, ContactID));
                mActivity.startActivity(intent);
            } catch (Exception e) {
                AppHelper.LogCat("Error view contact  Exception" + e.getMessage());
            }
        }


        void MakeMemberAsAdmin(String id, int groupID) {
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, PreferenceManager.getToken(mActivity), EndPoints.BASE_URL);
            Call<GroupResponse> CreateGroupCall = mApiGroups.makeAdmin(groupID, id);
            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                            EventBus.getDefault().post(new Pusher("createGroup"));
                            EventBus.getDefault().post(new Pusher("addMember", String.valueOf(groupID)));
                        } else {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_make_member_as_admin), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                }
            });


        }

        void RemoveMembersFromGroup(String id, int groupID) {
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, PreferenceManager.getToken(mActivity), EndPoints.BASE_URL);
            Call<GroupResponse> CreateGroupCall = mApiGroups.removeMember(groupID, id);
            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        AppHelper.hideDialog();
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                            realm.executeTransaction(realm1 -> {
                                MembersGroupModel membersGroupModel = realm1.where(MembersGroupModel.class).equalTo("userId", id).equalTo("groupID", groupID).findFirst();
                                membersGroupModel.deleteFromRealm();
                            });
                            EventBus.getDefault().post(new Pusher("createGroup"));
                            EventBus.getDefault().post(new Pusher("addMember", String.valueOf(groupID)));
                        } else {
                            AppHelper.hideDialog();
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.body().getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.hideDialog();
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), response.message(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.hideDialog();
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.containerProfile), mActivity.getString(R.string.failed_to_remove_member), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                }
            });


        }

    }


}
