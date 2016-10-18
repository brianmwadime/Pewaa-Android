package com.fortunekidew.pewaa.adapters.recyclerView.contacts;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
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
import com.fortunekidew.pewaa.activities.gifts.WishlistActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.profile.ProfilePreviewActivity;
import com.fortunekidew.pewaa.animations.AnimationsUtil;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.helpers.UtilsPhone;
import com.fortunekidew.pewaa.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaa.ui.RecyclerViewFastScroller;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.fortunekidew.pewaa.helpers.UtilsString.unescapeJava;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    protected final Activity mActivity;
    private List<ContactsModel> mContactsModel;
    private String SearchQuery;
    private static final int HEADER_ITEM = 1;
    private static final int BASIC_ITEM = 2;
    private int mPreviousPosition = 0;
    private boolean headerAdded = false;

    public ContactsAdapter(@NonNull Activity mActivity, List<ContactsModel> mContactsModel) {
        this.mActivity = mActivity;
        this.mContactsModel = mContactsModel;
    }


    public void setContacts(List<ContactsModel> contactsModelList) {
        this.mContactsModel = contactsModelList;
        notifyDataSetChanged();
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
        for (int i = mContactsModel.size() - 1; i >= 0; i--) {
            final ContactsModel model = mContactsModel.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ContactsModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ContactsModel model = newModels.get(i);
            if (!mContactsModel.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ContactsModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ContactsModel model = newModels.get(toPosition);
            final int fromPosition = mContactsModel.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private ContactsModel removeItem(int position) {
        final ContactsModel model = mContactsModel.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, ContactsModel model) {
        mContactsModel.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final ContactsModel model = mContactsModel.remove(fromPosition);
        mContactsModel.add(toPosition, model);
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
            itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_contacts, parent, false);
            return new ContactsViewHolder(itemView);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ContactsViewHolder) {
            final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            final ContactsModel contactsModel = this.mContactsModel.get(position - 1);
            try {

                contactsViewHolder.setUsername(contactsModel.getUsername(), contactsModel.getPhone());


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
                    String status = unescapeJava(contactsModel.getStatus());
                    if (status.length() > 18)
                        contactsViewHolder.setStatus(status.substring(0, 18) + "... " + "");

                    else
                        contactsViewHolder.setStatus(status);

                } else {
                    contactsViewHolder.setStatus(contactsModel.getPhone());
                }

                if (contactsModel.isLinked()) {
                    contactsViewHolder.hideInviteButton();
                } else {
                    contactsViewHolder.showInviteButton();
                }
                if (contactsModel.getImage() != null) {
                    contactsViewHolder.setUserImage(contactsModel.getImage(), String.valueOf(contactsModel.getId()), contactsModel.getUsername());
                } else {
                    contactsViewHolder.setNullUserImage(R.drawable.ic_user_holder_white_48dp);
                }
            } catch (Exception e) {
                AppHelper.LogCat("Contacts adapters Exception " + e.getMessage());
            }
            contactsViewHolder.setOnClickListener(view -> {
                if (view.getId() == R.id.wishlist_image) {

                    if (AppHelper.isAndroid5()) {
                        Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                        mIntent.putExtra("userID", contactsModel.getId());
                        mIntent.putExtra("isGroup", false);
                        mActivity.startActivity(mIntent);
                    } else {
                        Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                        mIntent.putExtra("userID", contactsModel.getId());
                        mIntent.putExtra("isGroup", false);
                        mActivity.startActivity(mIntent);
                        mActivity.overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
                    }
                } else {
                    if (contactsModel.isLinked()) {
                        Intent messagingIntent = new Intent(mActivity, WishlistActivity.class);
                        messagingIntent.putExtra("conversationID", 0);
                        messagingIntent.putExtra("recipientID", contactsModel.getId());
                        messagingIntent.putExtra("isGroup", false);
                        mActivity.startActivity(messagingIntent);
                        mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        String number = contactsModel.getPhone();
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", number);
                        smsIntent.putExtra("sms_body", AppConstants.INVITE_MESSAGE_SMS);
                        mActivity.startActivity(smsIntent);
                    }
                }

            });

            if (position > mPreviousPosition) {
                AnimationsUtil.animateY(holder, true);
            } else {
                AnimationsUtil.animateY(holder, false);
            }
            mPreviousPosition = position;

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
        return mContactsModel.size() > 0 ? mContactsModel.size() + 1 : 1;
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

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.adParentLyout)
        LinearLayout rootLayout;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.wishlist_image)
        ImageView userImage;
        @Bind(R.id.wishlist_name)
        TextView username;
        @Bind(R.id.status)
        EmojiconTextView status;
        @Bind(R.id.invite)
        TextView invite;

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

        void hideInviteButton() {
            invite.setVisibility(View.GONE);
        }

        void showInviteButton() {
            invite.setVisibility(View.VISIBLE);
        }

        void setUsername(String Username, String phone) {
            if (Username != null) {
                username.setText(Username);
            } else {
                String name = UtilsPhone.getContactName(mActivity, phone);
                if (name != null) {
                    username.setText(name);
                } else {
                    username.setText(phone);
                }

            }

        }

        void setStatus(String Status) {
            status.setText(Status);
        }


        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            userImage.setOnClickListener(listener);
        }

    }


}
