package com.fortunekidew.pewaa.adapters.recyclerView.wishlists;

import android.annotation.TargetApi;
import android.app.Activity;
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
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.realm.Realm;
import io.realm.RealmList;
import io.socket.client.Socket;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class GiftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected final Activity mActivity;
    private RealmList<GiftsModel> mGifts;
    private Realm realm;
    private APIService mApiService;
    private String SearchQuery;
    private SparseBooleanArray selectedItems;
    private boolean isActivated = false;
    private RecyclerView wishlistList;
    private Socket mSocket;



    public GiftsAdapter(@NonNull Activity mActivity) {
        this.mActivity = mActivity;
        this.mGifts = new RealmList<>();
        this.realm = Realm.getDefaultInstance();
        this.mApiService = new APIService(mActivity);
        this.selectedItems = new SparseBooleanArray();
    }

    public GiftsAdapter(@NonNull Activity mActivity, RecyclerView wishlistList, Socket mSocket) {
        this.mActivity = mActivity;
        this.mGifts = new RealmList<>();
        this.wishlistList = wishlistList;
        this.realm = Realm.getDefaultInstance();
        this.mApiService = new APIService(mActivity);
        this.selectedItems = new SparseBooleanArray();
        this.mSocket = mSocket;
    }

    public void setGifts(RealmList<GiftsModel> wishlistsModelList) {
        this.mGifts = wishlistsModelList;
        notifyDataSetChanged();
    }


    //Methods for search start
    public void setString(String SearchQuery) {
        this.SearchQuery = SearchQuery;
        notifyDataSetChanged();
    }

    public void animateTo(List<GiftsModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<GiftsModel> newModels) {
        for (int i = mGifts.size() - 1; i >= 0; i--) {
            final GiftsModel model = mGifts.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<GiftsModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final GiftsModel model = newModels.get(i);
            if (!mGifts.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<GiftsModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final GiftsModel model = newModels.get(toPosition);
            final int fromPosition = mGifts.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private GiftsModel removeItem(int position) {
        final GiftsModel model = mGifts.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, GiftsModel model) {
        mGifts.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final GiftsModel model = mGifts.remove(fromPosition);
        mGifts.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_wishlist, parent, false);
        return new GiftsAdapter.GiftViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof GiftsAdapter.GiftViewHolder) {

            final GiftsAdapter.GiftViewHolder GiftViewHolder = (GiftsAdapter.GiftViewHolder) holder;
            final GiftsModel GiftsModel = this.mGifts.get(position);

            try {

                String Name = GiftsModel.getName();

                GiftViewHolder.gift_name.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));

                SpannableString wishlistName = SpannableString.valueOf(Name);
                if (SearchQuery == null) {
                    GiftViewHolder.gift_name.setText(wishlistName, TextView.BufferType.NORMAL);
                } else {
                    int index = TextUtils.indexOf(Name.toLowerCase(), SearchQuery.toLowerCase());
                    if (index >= 0) {
                        wishlistName.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorAccent)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        wishlistName.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    GiftViewHolder.gift_name.setText(wishlistName, TextView.BufferType.SPANNABLE);
                }

                GiftViewHolder.setGiftImage(GiftsModel.getAvatar());

                GiftViewHolder.setOnClickListener(view -> {

//                    Intent messagingIntent = new Intent(mActivity, WishlistActivity.class);
//                    messagingIntent.putExtra("wishlistID", GiftsModel.getId());
//                    mActivity.startActivity(messagingIntent);
//                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });
            } catch (Exception e) {
                AppHelper.LogCat("Gifts Adapter  Exception" + e.getMessage());
            }

        }

    }

    @Override
    public int getItemCount() {
        if (mGifts != null) return mGifts.size();
        return 0;
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


    public GiftsModel getItem(int position) {
        return mGifts.get(position);
    }

    class GiftViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.gift_image)
        ImageView GiftImage;
        @Bind(R.id.gift_name)
        EmojiconTextView gift_name;
        @Bind(R.id.counter)
        TextView counter;
        @Bind(R.id.date_created)
        TextView GiftDate;

        @Bind(R.id.gift_row)
        LinearLayout GiftRow;

        GiftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void setGiftImage(String ImageUrl) {

            BitmapImageViewTarget target = new BitmapImageViewTarget(GiftImage) {
                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    GiftImage.setImageDrawable(placeholder);
                }

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    GiftImage.setImageBitmap(resource);

                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    GiftImage.setImageDrawable(errorDrawable);
                }


            };

            Glide.with(mActivity)
                    .load(EndPoints.ASSETS_BASE_URL + ImageUrl)
                    .asBitmap()
                    .transform(new CropCircleTransformation(mActivity))
                    .override(100, 100)
                    .into(target);

        }

        void setNullGiftImage(int drawable) {
            GiftImage.setPadding(2, 2, 2, 2);
            GiftImage.setImageResource(drawable);
        }


        void setGift_name(String wishlist) {

            if (wishlist.length() > 16)
                gift_name.setText(wishlist.substring(0, 16) + "... " + "");
            else
                gift_name.setText(wishlist);

        }

        void setGiftDate(String creationDate) {
            GiftDate.setText(creationDate);
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

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            GiftImage.setOnClickListener(listener);
        }

    }
}
