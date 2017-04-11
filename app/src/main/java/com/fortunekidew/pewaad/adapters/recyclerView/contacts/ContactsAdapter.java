package com.fortunekidew.pewaad.adapters.recyclerView.contacts;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Telephony;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.profile.ProfilePreviewActivity;
import com.fortunekidew.pewaad.animations.AnimationsUtil;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.Files.ImageLoader;
import com.fortunekidew.pewaad.helpers.Files.MemoryCache;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.helpers.images.PewaaImageLoader;
import com.fortunekidew.pewaad.models.users.contacts.PewaaContact;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJava;

/**
 * Created by Brian Mwakima 10/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    protected final Activity mActivity;
    private List<PewaaContact> mContactsModel;
    private List<PewaaContact> mContactsSearch;
    private String SearchQuery;
    private static final int HEADER_ITEM = 1;
    private static final int BASIC_ITEM = 2;
    private int mPreviousPosition = 0;
    private boolean headerAdded = false;
    private MemoryCache memoryCache;

    public ContactsAdapter(@NonNull Activity mActivity, List<PewaaContact> mContactsModel) {
        this.mActivity = mActivity;
        this.mContactsModel = mContactsModel;
        this.memoryCache = new MemoryCache();
    }

    public ContactsAdapter(@NonNull Activity mActivity) {
        this.mActivity = mActivity;
        this.mContactsModel = new ArrayList<>();
        this.memoryCache = new MemoryCache();
    }

    public void setContacts(List<PewaaContact> contacts) {
        this.mContactsModel = contacts;
        notifyDataSetChanged();
    }

    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    public void animateTo(List<PewaaContact> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<PewaaContact> newModels) {
        for (int i = mContactsModel.size() - 1; i >= 0; i--) {
            final PewaaContact model = mContactsModel.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<PewaaContact> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final PewaaContact model = newModels.get(i);
            if (!mContactsModel.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<PewaaContact> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final PewaaContact model = newModels.get(toPosition);
            final int fromPosition = mContactsModel.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private PewaaContact removeItem(int position) {
        final PewaaContact model = mContactsModel.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    private void addItem(int position, PewaaContact model) {
        mContactsModel.add(position, model);
        notifyItemInserted(position);
    }

    public void addItem(PewaaContact model) {
        mContactsModel.add(model);
        notifyItemInserted(mContactsModel.size() - 1);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final PewaaContact model = mContactsModel.remove(fromPosition);
        mContactsModel.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    //Methods for search end


    @Override
    public int getItemViewType(int position) {
            return BASIC_ITEM;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<PewaaContact> results = new ArrayList<>();
                if (mContactsSearch == null) {
                    mContactsSearch = mContactsModel;
                }
                if (charSequence != null) {
                    if (mContactsSearch != null && mContactsSearch.size() > 0) {
                        for (final PewaaContact contact : mContactsSearch) {

                            if (contact.getUsername() != null) {
                                if (contact.getUsername().toLowerCase().contains(charSequence.toString())) {
                                    results.add(contact);
                                }
                            }

                        }
                    }
                    oReturn.values = results;
                    oReturn.count = results.size();
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                if (filterResults.count > 0) {
//                    MainActivity.setResultsMessage(false);
                } else {
//                    MainActivity.setResultsMessage(true);
                }
                mContactsModel = (List<PewaaContact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_contacts, parent, false);
        return new ContactsViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {


        if (holder instanceof ContactsViewHolder) {
            final ContactsViewHolder contactsViewHolder = (ContactsViewHolder) holder;
            final PewaaContact contactsModel = this.mContactsModel.get(position);
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

                contactsViewHolder.setUserImage(contactsModel.getImage(), contactsModel.getId());

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
                    // TODO: Do something if Linked
                    } else {
                        String number = contactsModel.getPhone();
                        contactsViewHolder.setShareApp(mActivity, "From " + number, AppConstants.INVITE_MESSAGE_SMS + String.format(mActivity.getString(R.string.rate_helper_google_play_url), mActivity.getPackageName()));

                    }
                }

            });

            if (position > mPreviousPosition) {
                AnimationsUtil.animateY(holder, true);
            } else {
                AnimationsUtil.animateY(holder, false);
            }
            mPreviousPosition = position;
        }

    }


    @Override
    public int getItemCount() {
        if (mContactsModel != null) return mContactsModel.size();
        return 0;
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

        void setShareApp(Activity mActivity, String subject, String message) {

//            Uri bmpUri = Uri.parse("android.resource://" + mActivity.getPackageName() + "/mipmap/ic_launcher");
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
//            intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, message);
            mActivity.startActivity(intent);
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
