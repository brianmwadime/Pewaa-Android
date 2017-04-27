package com.fortunekidew.pewaad.adapters.recyclerView.wishlists;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.gifts.WishlistActivity;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class WishlistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    protected final Activity mActivity;
    private List<WishlistsModel> mWishlists;
    private List<WishlistsModel> mWishlistsSearch;
    private String SearchQuery;
    private SparseBooleanArray selectedItems;
    private boolean isActivated = false;

    public WishlistsAdapter(@NonNull Activity mActivity) {
        this.mActivity = mActivity;
        this.mWishlists = new ArrayList<>();
        this.selectedItems = new SparseBooleanArray();
    }

    public void setWishlists(List<WishlistsModel> wishlistsModelList) {
        this.mWishlists = wishlistsModelList;
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
        for (int i = mWishlists.size() - 1; i >= 0; i--) {
            final WishlistsModel model = mWishlists.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<WishlistsModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final WishlistsModel model = newModels.get(i);
            if (!mWishlists.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<WishlistsModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final WishlistsModel model = newModels.get(toPosition);
            final int fromPosition = mWishlists.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private WishlistsModel removeItem(int position) {
        final WishlistsModel model = mWishlists.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, WishlistsModel model) {
        mWishlists.add(position, model);
        notifyItemInserted(position);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final WishlistsModel model = mWishlists.remove(fromPosition);
        mWishlists.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_wishlist, parent, false);
        return new WishlistViewHolder(itemView);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof WishlistViewHolder) {

            final WishlistViewHolder wishlistViewHolder = (WishlistViewHolder) holder;
            final WishlistsModel wishlistsModel = this.mWishlists.get(position);

            try {

                String Name = wishlistsModel.getName();

                wishlistViewHolder.wishlist_name.setTextColor(mActivity.getResources().getColor(R.color.colorBlack));

                SpannableString wishlistName = SpannableString.valueOf(Name);
                if (SearchQuery == null) {
                    wishlistViewHolder.wishlist_name.setText(wishlistName, TextView.BufferType.NORMAL);
                } else {
                    int index = TextUtils.indexOf(Name.toLowerCase(), SearchQuery.toLowerCase());
                    if (index >= 0) {
                        wishlistName.setSpan(new ForegroundColorSpan(AppHelper.getColor(mActivity, R.color.colorAccent)), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        wishlistName.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), index, index + SearchQuery.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }

                    wishlistViewHolder.wishlist_name.setText(wishlistName, TextView.BufferType.SPANNABLE);
                }

                switch (wishlistsModel.getCategory()) {
                    case "Wedding":
                        wishlistViewHolder.setWishlistImage(R.drawable.wedding, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_navy));
                    break;
                    case "House Warming":
                        wishlistViewHolder.setWishlistImage(R.drawable.house_warming, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_red));
                    break;
                    case "Baby Shower":
                        wishlistViewHolder.setWishlistImage(R.drawable.baby_shower, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_green));
                    break;
                    case "Birthday":
                        wishlistViewHolder.setWishlistImage(R.drawable.birthday, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_orange));
                    break;
                    case "Graduation":
                        wishlistViewHolder.setWishlistImage(R.drawable.graduation, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_yellow));
                    break;
                    case "Bridal Shower":
                        wishlistViewHolder.setWishlistImage(R.drawable.bridal_shower, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_pink));
                        break;
                    case "Retirement":
                        wishlistViewHolder.setWishlistImage(R.drawable.retirement, ContextCompat.getDrawable(mActivity, R.drawable.bg_circle_image_brown));
                        break;
                }

                wishlistViewHolder.setOnClickListener(view -> {

                    Intent messagingIntent = new Intent(mActivity, WishlistActivity.class);
                    messagingIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_ID, wishlistsModel.getId());
                    messagingIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_TITLE, wishlistsModel.getName());
                    messagingIntent.putExtra(WishlistActivity.RESULT_EXTRA_WISHLIST_PERMISSION, wishlistsModel.getPermissions());
                    mActivity.startActivity(messagingIntent);
                    mActivity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                });

                wishlistViewHolder.setCategory(wishlistsModel.getCategory());

            } catch (Exception e) {
                AppHelper.LogCat("Wishlists Adapter  Exception" + e.getMessage());
            }

        }

    }

    @Override
    public int getItemCount() {
        if (mWishlists != null) return mWishlists.size();
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


    public WishlistsModel getItem(int position) {
        return mWishlists.get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<WishlistsModel> results = new ArrayList<>();
                if (mWishlistsSearch == null) {
                    mWishlistsSearch = mWishlists;
                }
                if (charSequence != null) {
                    if (mWishlistsSearch != null && mWishlistsSearch.size() > 0) {
                        for (final WishlistsModel wishlist : mWishlistsSearch) {
                            if (wishlist.getName().toLowerCase().contains(charSequence.toString())) {
                                results.add(wishlist);
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
                mWishlists = (List<WishlistsModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class WishlistViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.wishlist_image)
        ImageView wishlistImage;
        @BindView(R.id.wishlist_name)
        EmojiconTextView wishlist_name;
        @BindView(R.id.category)
        TextView category;
        @BindView(R.id.date_created)
        TextView wishlistDate;

        @BindView(R.id.wishlist_row)
        LinearLayout WishlistRow;

        WishlistViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void setWishlistImage(int ImageUrl, Drawable color) {

            wishlistImage.setBackground(color);

            BitmapImageViewTarget target = new BitmapImageViewTarget(wishlistImage) {
                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    wishlistImage.setImageDrawable(placeholder);
                }

                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    wishlistImage.setImageBitmap(resource);

                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    wishlistImage.setImageDrawable(errorDrawable);
                }
            };

            Glide.with(mActivity)
                    .load(ImageUrl)
                    .asBitmap()
                    .transform(new CropCircleTransformation(mActivity))
                    .override(100, 100)
                    .into(target);
        }

        void setNullWishlistImage(int drawable) {
            wishlistImage.setPadding(2, 2, 2, 2);
            wishlistImage.setImageResource(drawable);
        }

        void setWishlist_name(String wishlist) {
            if (wishlist.length() > 16)
                wishlist_name.setText(wishlist.substring(0, 16) + "... " + "");
            else
                wishlist_name.setText(wishlist);
        }

        void setWishlistDate(String creationDate) {
            wishlistDate.setText(creationDate);
        }

        void setCategory(String Counter) {
            category.setText(Counter.toUpperCase());
        }

        void hideCategory() {
            category.setVisibility(View.GONE);
        }

        void showCategory() {
            category.setVisibility(View.VISIBLE);
        }

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            wishlistImage.setOnClickListener(listener);
        }
    }
}