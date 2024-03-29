package com.fortunekidew.pewaad.adapters.recyclerView;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.profile.ProfilePreviewActivity;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.Files.ImageLoader;
import com.fortunekidew.pewaad.helpers.Files.MemoryCache;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.helpers.images.PewaaImageLoader;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.ui.RecyclerViewFastScroller;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.fortunekidew.pewaad.app.AppConstants.STATUS_CONTRIBUTOR_ADDED;
import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class SelectContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements RecyclerViewFastScroller.BubbleTextGetter {
    private final Activity mActivity;
    private List<ContactsModel> mContactsModel;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 2;
    private MemoryCache memoryCache;

    public void setContacts(List<ContactsModel> contactsModelList) {
        this.mContactsModel = contactsModelList;
        notifyDataSetChanged();
    }

    public SelectContactsAdapter(@NonNull Activity mActivity, List<ContactsModel> mContactsModel) {
        this.mActivity = mActivity;
        this.mContactsModel = mContactsModel;
        this.memoryCache = new MemoryCache();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_contacts, parent, false);
        return new ContactsViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContactsViewHolder) {
            final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            final ContactsModel contactsModel = this.mContactsModel.get(position);
            try {
                contactsViewHolder.setUsername(contactsModel.getUsername(), contactsModel.getPhone());


                if (contactsModel.getStatus() != null) {
                    contactsViewHolder.setStatus(contactsModel.getStatus());
                } else {
                    contactsViewHolder.setStatus(contactsModel.getPhone());
                }

                if (contactsModel.isLinked()) {
                    contactsViewHolder.hideInviteButton();
                } else {
                    contactsViewHolder.showInviteButton();
                }

                contactsViewHolder.setUserImage(contactsModel.getImage(), contactsModel.getId());
            } catch (Exception e) {
                AppHelper.LogCat("" + e.getMessage());
            }
            contactsViewHolder.setOnClickListener(view -> {
                if (view.getId() == R.id.wishlist_image) {
                    Intent mIntent = new Intent(mActivity, ProfilePreviewActivity.class);
                    mIntent.putExtra(ProfilePreviewActivity.EXTRA_USER_ID, contactsModel.getId());
                    mActivity.startActivity(mIntent);
                } else {

                    Intent intentMessage = new Intent();

                    intentMessage.putExtra("EXTRA_CONTRIBUTOR_ID", contactsModel.getId());
                    mActivity.setResult(STATUS_CONTRIBUTOR_ADDED, intentMessage);
                    mActivity.finish();
                }

            });
        }

    }


    @Override
    public int getItemCount() {
        if (mContactsModel != null) return mContactsModel.size();
        return 0;
    }

    @Override
    public String getTextToShowInBubble(int pos) {
        return Character.toString(mContactsModel.get(pos).getUsername().charAt(0));
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wishlist_image)
        ImageView userImage;
        @BindView(R.id.wishlist_name)
        TextView username;
        @BindView(R.id.status)
        EmojiconTextView status;
        @BindView(R.id.invite)
        TextView invite;

        ContactsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setUserImage(String ImageUrl, String userId) {

            Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, ImageUrl, mActivity, userId, AppConstants.USER, AppConstants.ROW_PROFILE);
            if (bitmap != null) {
                ImageLoader.SetBitmapImage(bitmap, userImage);
            } else {

                Target target = new BitmapImageViewTarget(userImage) {
                    @Override
                    public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                        super.onResourceReady(bitmap, anim);
                        userImage.setImageBitmap(bitmap);
                        ImageLoader.DownloadImage(memoryCache, EndPoints.ASSETS_BASE_URL + ImageUrl, ImageUrl, mActivity, userId, AppConstants.USER, AppConstants.ROW_PROFILE);

                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        if (ImageUrl != null) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(mActivity.getContentResolver(), Uri.parse(ImageUrl));
                            } catch (IOException ex) {
                                // AppHelper.LogCat(e.getMessage());
                            }
                            if (bitmap != null) {
                                ImageLoader.SetBitmapImage(bitmap, userImage);
                            } else {
                                userImage.setImageDrawable(errorDrawable);
                            }
                        } else {
                            userImage.setImageDrawable(errorDrawable);
                        }
                    }

                    @Override
                    public void onLoadStarted(Drawable placeholder) {
                        super.onLoadStarted(placeholder);
                        userImage.setImageDrawable(placeholder);
                    }
                };

                PewaaImageLoader.loadCircleImage(mActivity, EndPoints.ASSETS_BASE_URL + ImageUrl, target, R.drawable.image_holder_ur_circle, AppConstants.ROWS_IMAGE_SIZE);
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
            String user = unescapeJava(Status);
            if (user.length() > 18)
                status.setText(user.substring(0, 18) + "... " + "");
            else
                status.setText(user);
        }


        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            userImage.setOnClickListener(listener);
        }

    }
}
