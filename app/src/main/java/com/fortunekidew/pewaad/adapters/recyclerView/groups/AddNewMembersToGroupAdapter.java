package com.fortunekidew.pewaad.adapters.recyclerView.groups;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIGroups;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.models.groups.GroupResponse;
import com.fortunekidew.pewaad.models.groups.MembersGroupModel;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.realm.Realm;
import io.realm.RealmQuery;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Abderrahim El imame on 11/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class AddNewMembersToGroupAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<ContactsModel> mContactsModels;
    private LayoutInflater mInflater;
    private Realm realm;
    private int groupID;
    private APIService mApiService;
    private String SearchQuery;

    public AddNewMembersToGroupAdapter(Activity mActivity, List<ContactsModel> mContactsModels, int groupID, APIService mApiService) {
        this.mActivity = mActivity;
        this.mContactsModels = mContactsModels;
        mInflater = LayoutInflater.from(mActivity);
        this.realm = Realm.getDefaultInstance();
        this.groupID = groupID;
        this.mApiService = mApiService;
    }

    public void setContacts(List<ContactsModel> mContactsModels) {
        this.mContactsModels = mContactsModels;
        notifyDataSetChanged();
    }


    public List<ContactsModel> getContacts() {
        return mContactsModels;
    }


    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    public void animateTo(List<ContactsModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ContactsModel> newModels) {
        for (int i = mContactsModels.size() - 1; i >= 0; i--) {
            final ContactsModel model = mContactsModels.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContactsModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContactsModel model = newModels.get(i);
            if (!mContactsModels.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContactsModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContactsModel model = newModels.get(toPosition);
            final int fromPosition = mContactsModels.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private ContactsModel removeItem(int position) {
        final ContactsModel model = mContactsModels.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, ContactsModel model) {
        mContactsModels.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ContactsModel model = mContactsModels.remove(fromPosition);
        mContactsModels.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    //Methods for search end

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.row_add_members_group, parent, false);
        return new ContactsViewHolder(view);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
        final ContactsModel contactsModel = this.mContactsModels.get(position);
        try {
            if (contactsViewHolder.checkIfMemberExist(contactsModel.getId(), groupID)) {
                contactsViewHolder.itemView.setEnabled(false);
                contactsViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorGray2));
            } else {
                contactsViewHolder.itemView.setEnabled(true);
                contactsViewHolder.username.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));
            }
            String Username;
            String name = UtilsPhone.getContactName(mActivity, contactsModel.getPhone());
            if (name != null) {
                Username = name;
            } else {
                Username = contactsModel.getPhone();
            }
            SpannableString recipientUsername = SpannableString.valueOf(Username);
            if (SearchQuery == null) {
                contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.NORMAL);
            } else {
                int index = TextUtils.indexOf(Username.toLowerCase(), SearchQuery.toLowerCase());
                if (index >= 0) {
                    recipientUsername.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorAccent)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    recipientUsername.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }

                contactsViewHolder.username.setText(recipientUsername, TextView.BufferType.SPANNABLE);
            }


            if (contactsModel.getStatus() != null) {
                contactsViewHolder.setStatus(contactsModel.getStatus());
            }

            if (contactsModel.getImage() != null) {
                contactsViewHolder.setUserImage(contactsModel.getImage(), String.valueOf(contactsModel.getId()), contactsModel.getUsername());
            } else {
                contactsViewHolder.setNullUserImage(R.drawable.ic_user_holder_white_48dp);
            }
        } catch (Exception e) {
            AppHelper.LogCat("Exception" + e.getMessage());
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mContactsModels != null) {
            return mContactsModels.size();
        } else {
            return 0;
        }
    }


    class ContactsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.wishlist_image)
        ImageView userImage;

        @BindView(R.id.wishlist_name)
        TextView username;

        @BindView(R.id.status)
        EmojiconTextView status;

        @BindView(R.id.select_icon)
        LinearLayout selectIcon;

        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        boolean checkIfMemberExist(String memberID, int groupID) {
            RealmQuery<MembersGroupModel> query = realm.where(MembersGroupModel.class).equalTo("userId", memberID).equalTo("groupID", groupID);
            return query.count() == 0 ? false : true;
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


        void setStatus(String Status) {
            String statu = unescapeJava(Status);
            status.setText(statu);
        }

        @Override
        public void onClick(View view) {
            ContactsModel membersGroupModel = mContactsModels.get(getAdapterPosition());
            String theName;
            String name = UtilsPhone.getContactName(mActivity, membersGroupModel.getPhone());
            if (name != null) {
                theName = name;
            } else {
                theName = membersGroupModel.getPhone();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage(mActivity.getString(R.string.add_to_group) + theName + mActivity.getString(R.string.member_to_group))
                    .setPositiveButton(mActivity.getString(R.string.add_new_member), (dialog, which) -> {
                        AddMembersToGroup(membersGroupModel.getId());
                    }).setNegativeButton(mActivity.getString(R.string.cancel), null).show();
        }


        private void AddMembersToGroup(String id) {
            APIGroups mApiGroups = mApiService.RootService(APIGroups.class, PreferenceManager.getToken(mActivity), EndPoints.BASE_URL);
            Call<GroupResponse> CreateGroupCall = mApiGroups.addMembers(groupID, id);
            AppHelper.showDialog(mActivity, mActivity.getString(R.string.adding_member));
            CreateGroupCall.enqueue(new Callback<GroupResponse>() {
                @Override
                public void onResponse(Call<GroupResponse> call, Response<GroupResponse> response) {
                    if (response.isSuccessful()) {
                        AppHelper.hideDialog();
                        if (response.body().isSuccess()) {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.ParentLayoutAddNewMembers), response.body().getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                            EventBus.getDefault().post(new Pusher("createGroup"));
                            EventBus.getDefault().post(new Pusher("addMember", String.valueOf(groupID)));
                            mActivity.finish();

                        } else {
                            AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.ParentLayoutAddNewMembers), response.body().getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                        }
                    } else {
                        AppHelper.hideDialog();
                        AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.ParentLayoutAddNewMembers), response.message(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

                    }
                }

                @Override
                public void onFailure(Call<GroupResponse> call, Throwable t) {
                    AppHelper.hideDialog();
                    AppHelper.Snackbar(mActivity, mActivity.findViewById(R.id.ParentLayoutAddNewMembers), mActivity.getString(R.string.failed_to_add_member_to_group), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                }
            });


        }
    }


}

