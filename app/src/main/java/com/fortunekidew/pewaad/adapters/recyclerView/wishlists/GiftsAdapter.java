package com.fortunekidew.pewaad.adapters.recyclerView.wishlists;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.SharedElementCallback;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.gifts.GiftDetailsActivity;
import com.fortunekidew.pewaad.api.APIContributor;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.ContributorsResponse;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.ui.widget.BadgedFourThreeImageView;
import com.fortunekidew.pewaad.util.ObservableColorMatrix;
import com.fortunekidew.pewaad.util.glide.PewaaTarget;
import com.google.gson.Gson;

import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.realm.Realm;
import io.realm.RealmList;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.util.AnimUtils.getFastOutSlowInInterpolator;

/**
 * Created by Brian Mwakima 10/04/2017.
 * Email : mwadime@fortunekidew.co.ke
 */
public class GiftsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int REQUEST_CODE_VIEW_GIFT = 5407;
    protected final Activity mActivity;
    private RealmList<GiftsModel> mGifts;
    private APIService mApiService;

    private String SearchQuery;
    private SparseBooleanArray selectedItems;
    private boolean isActivated = false;
    private final ColorDrawable[] giftLoadingPlaceholders;

    public GiftsAdapter(@NonNull Activity mActivity) {
        this.mActivity = mActivity;
        this.mGifts = new RealmList<>();
        this.selectedItems = new SparseBooleanArray();
        mApiService = new APIService(mActivity);

        // get the gift placeholder colors & badge color from the theme
        final TypedArray a = this.mActivity.obtainStyledAttributes(R.styleable.GiftFeed);
        final int loadingColorArrayId =
                a.getResourceId(R.styleable.GiftFeed_giftLoadingPlaceholderColors, 0);
        if (loadingColorArrayId != 0) {
            int[] placeholderColors = this.mActivity.getResources().getIntArray(loadingColorArrayId);
            giftLoadingPlaceholders = new ColorDrawable[placeholderColors.length];
            for (int i = 0; i < placeholderColors.length; i++) {
                giftLoadingPlaceholders[i] = new ColorDrawable(placeholderColors[i]);
            }
        } else {
            giftLoadingPlaceholders = new ColorDrawable[] { new ColorDrawable(Color.DKGRAY) };
        }
    }

    public void setGifts(RealmList<GiftsModel> wishlistsModelList) {
        this.mGifts = wishlistsModelList;
        notifyDataSetChanged();
    }

    // Methods for search start
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
        View itemView = LayoutInflater.from(mActivity).inflate(R.layout.row_gift, parent, false);
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
                if(GiftsModel.getContributed() == GiftsModel.getPrice() && GiftsModel.getCashout_status() == null){
                    GiftViewHolder.cashout.setVisibility(View.VISIBLE);
                } else {
                    GiftViewHolder.cashout.setVisibility(View.INVISIBLE);
                }

                GiftViewHolder.cashout.setOnClickListener(v -> {
                    saveContributor(GiftsModel.getName(), GiftsModel.getId(), String.valueOf(GiftsModel.getContributed()));
                });

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

                GiftViewHolder.setGiftDate(GiftsModel.getCreatedOn());
                GiftViewHolder.setGiftImage(GiftsModel.getAvatar(), position);
                GiftViewHolder.setOnClickListener(view -> {
                    Intent gift = new Intent();
                    gift.setClass(mActivity, GiftDetailsActivity.class);
                    gift.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_ID, GiftsModel.getId());
                    gift.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_IMAGE, GiftsModel.getAvatar());
                    gift.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_TITLE, GiftsModel.getName());
                    gift.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_DESC, GiftsModel.getDescription());
                    gift.putExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_PRICE, GiftsModel.getPrice());
                    gift.putExtra(GiftDetailsActivity.EXTRA_GIFT, Parcels.wrap(GiftsModel.class, GiftsModel));
                    ActivityOptions options =
                            ActivityOptions.makeSceneTransitionAnimation(mActivity,
                                    Pair.create(view, mActivity.getString(R.string.transition_gift_avatar)),
                                    Pair.create(view, mActivity.getString(R.string.transition_gift_background)));
                    mActivity.startActivityForResult(gift, REQUEST_CODE_VIEW_GIFT, options.toBundle());
                });
            } catch (Exception e) {
                AppHelper.LogCat("Gifts Adapter Exception " + e.getMessage());
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

    public int getItemPosition(final String itemId) {
        for (int position = 0; position < mGifts.size(); position++) {
            if (getItem(position).getId() == itemId) return position;
        }
        return RecyclerView.NO_POSITION;
    }

    class GiftViewHolder extends RecyclerView.ViewHolder {

        private final int[] NORMAL_IMAGE_SIZE = new int[] { 400, 300 };
        private final int[] TWO_X_IMAGE_SIZE = new int[] { 800, 600 };

        @BindView(R.id.gift_image)
        BadgedFourThreeImageView GiftImage;
        @BindView(R.id.gift_name)
        EmojiconTextView gift_name;
        @BindView(R.id.date_created)
        TextView GiftDate;
        @BindView(R.id.cashout_button)
        ImageButton cashout;

        @BindView(R.id.cardview)
        CardView GiftRow;

        GiftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }


        void setGiftImage(String ImageUrl, int position) {

            Glide.with(mActivity)
                    .load(EndPoints.ASSETS_BASE_URL + ImageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onResourceReady(GlideDrawable resource,
                                                       String model,
                                                       Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache,
                                                       boolean isFirstResource) {
//                            if (!shot.hasFadedIn) {
                                GiftImage.setHasTransientState(true);
                                final ObservableColorMatrix cm = new ObservableColorMatrix();
                                final ObjectAnimator saturation = ObjectAnimator.ofFloat(
                                        cm, ObservableColorMatrix.SATURATION, 0f, 1f);
                                saturation.addUpdateListener(valueAnimator -> {
                                    // just animating the color matrix does not invalidate the
                                    // drawable so need this update listener.  Also have to create a
                                    // new CMCF as the matrix is immutable :(
                                    GiftImage.setColorFilter(new ColorMatrixColorFilter(cm));
                                });
                                saturation.setDuration(2000L);
                                saturation.setInterpolator(getFastOutSlowInInterpolator(mActivity));
                                saturation.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        GiftImage.clearColorFilter();
                                        GiftImage.setHasTransientState(false);
                                    }
                                });
                                saturation.start();
//                                shot.hasFadedIn = true;
//                            }
                            return false;
                        }

                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable>
                                target, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .placeholder(giftLoadingPlaceholders[position % giftLoadingPlaceholders.length])
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .fitCenter()
                    .override(NORMAL_IMAGE_SIZE[0], NORMAL_IMAGE_SIZE[1])

                    .into(new PewaaTarget(GiftImage, false));
            // need both placeholder & background to prevent seeing through shot as it fades in
            GiftImage.setBackground(
                    giftLoadingPlaceholders[position % giftLoadingPlaceholders.length]);
            // need a unique transition name per shot, let's use it's url
            GiftImage.setTransitionName(EndPoints.ASSETS_BASE_URL + ImageUrl);

        }

        void setNullGiftImage(int drawable) {
            GiftImage.setPadding(2, 2, 2, 2);
            GiftImage.setImageResource(drawable);
        }


        void setGiftName(String gift) {

            if (gift.length() > 16)
                gift_name.setText(gift.substring(0, 16) + "... " + "");
            else
                gift_name.setText(gift);

        }

        void setGiftDate(Date creationDate) {
            GiftDate.setText(DateUtils.getRelativeTimeSpanString(creationDate.getTime(),
                    System.currentTimeMillis(),
                    DateUtils.SECOND_IN_MILLIS).toString().toLowerCase());
        }

        void setOnClickListener(View.OnClickListener listener) {
            itemView.setOnClickListener(listener);
            GiftImage.setOnClickListener(listener);
        }

    }

    public static SharedElementCallback createSharedElementReenterCallback(
            @NonNull Context context) {
        final String giftTransitionName = context.getString(R.string.transition_gift_avatar);
        final String giftBackgroundTransitionName =
                context.getString(R.string.transition_gift_background);
        return new SharedElementCallback() {

            /**
             * We're performing a slightly unusual shared element transition i.e. from one view
             * (image in the grid) to two views (the image & also the background of the details
             * view, to produce the expand effect). After changing orientation, the transition
             * system seems unable to map both shared elements (only seems to map the shot, not
             * the background) so in this situation we manually map the background to the
             * same view.
             */
            @Override
            public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                if (sharedElements.size() != names.size()) {
                    // couldn't map all shared elements
                    final View sharedGift = sharedElements.get(giftTransitionName);
                    if (sharedGift != null) {
                        // has shot so add shot background, mapped to same view
                        sharedElements.put(giftBackgroundTransitionName, sharedGift);
                    }
                }
            }
        };
    }


    private void saveContributor(String gift_name, String gift_id, String gift_amount) {

        APIContributor mApiContributor = mApiService.RootService(APIContributor.class, PreferenceManager.getToken(mActivity), EndPoints.BASE_URL);
//        mActivity.this.runOnUiThread(() -> showLoading());
        Call<ContributorsResponse> statusResponseCall = mApiContributor.cashOut(gift_id, gift_name, gift_amount);
        statusResponseCall.enqueue(new Callback<ContributorsResponse>() {
            @Override
            public void onResponse(Call<ContributorsResponse> call, Response<ContributorsResponse> response) {
                if (response.isSuccessful()) {

                } else {

                    try {
                        Gson gson = new Gson();
                        StatusResponse res = gson.fromJson(response.errorBody().string(), StatusResponse.class);
//                        Snackbar.make(mActivity, res.getMessage(), Snackbar.LENGTH_SHORT).show();



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<ContributorsResponse> call, Throwable t) {

            }
        });

    }
}
