package com.fortunekidew.pewaad.activities.gifts;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fortunekidew.pewaad.BuildConfig;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.profile.ProfileActivity;
import com.fortunekidew.pewaad.api.APIContributor;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.payments.EditPayments;
import com.fortunekidew.pewaad.models.wishlists.ContributorsModel;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.ui.recyclerview.InsetDividerDecoration;
import com.fortunekidew.pewaad.ui.recyclerview.SlideInItemAnimator;
import com.fortunekidew.pewaad.ui.widget.AuthorTextView;
import com.fortunekidew.pewaad.ui.widget.BaselineGridTextView;
import com.fortunekidew.pewaad.ui.widget.ElasticDragDismissFrameLayout;
import com.fortunekidew.pewaad.ui.widget.ParallaxScrimageView;
import com.fortunekidew.pewaad.util.ColorUtils;
import com.fortunekidew.pewaad.util.TransitionUtils;
import com.fortunekidew.pewaad.util.ViewUtils;
import com.fortunekidew.pewaad.util.glide.CircleTransform;
import com.fortunekidew.pewaad.util.glide.GlideUtils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.fortunekidew.pewaad.app.EndPoints.ASSETS_BASE_URL;
import static com.fortunekidew.pewaad.app.EndPoints.BASE_URL;
import static com.fortunekidew.pewaad.util.AnimUtils.getFastOutSlowInInterpolator;

public class GiftDetailsActivity extends Activity implements LoadingData {

    public final static String RESULT_EXTRA_GIFT_ID = "RESULT_EXTRA_GIFT_ID";
    public final static String RESULT_EXTRA_GIFT_TITLE = "RESULT_EXTRA_GIFT_TITLE";
    public final static String RESULT_EXTRA_GIFT_DESC = "RESULT_EXTRA_GIFT_DESC";
    public final static String RESULT_EXTRA_GIFT_IMAGE = "RESULT_EXTRA_GIFT_IMAGE";
    public static final String RESULT_EXTRA_GIFT_PRICE = "RESULT_EXTRA_GIFT_PRICE";
    public static final String EXTRA_GIFT = "EXTRA_GIFT";
    private static final float SCRIM_ADJUSTMENT = 0.075f;

    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout draggableFrame;
    @BindView(R.id.cover)
    ParallaxScrimageView GiftCover;
    @BindView(R.id.gift_contributors)
    RecyclerView contributorsList;
    @BindView(R.id.back) ImageButton back;
    private ContributorAnimator contributorAnimator;
    @BindDimen(R.dimen.large_avatar_size) int largeAvatarSize;
    @BindDimen(R.dimen.z_card) int cardElevation;
    private View giftDescription;
    private View giftSpacer;
    private View title;
    private View description;
    private View giftProgress;
    private View contributedAmount;
    private View targetAmount;
    private LinearLayout shotActions;
    private Button contribute;
    private TextView ownerName;
    private ImageView ownerAvatar;
    private TextView giftTimeAgo;
    private View contributorFooter;
    private APIContributor api;
    private ContributorsAdapter adapter;
    private GiftsModel gift;

    private CircleTransform circleTransform;
    private ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;
    private List<EditPayments> paymentList;
    private String giftID, giftTitle, giftImage, giftDesc;
    private double giftPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_TITLE)) {
                giftTitle = getIntent().getExtras().getString(RESULT_EXTRA_GIFT_TITLE);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_IMAGE)) {
                giftImage = getIntent().getExtras().getString(RESULT_EXTRA_GIFT_IMAGE);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_ID)) {
                giftID = getIntent().getExtras().getString(RESULT_EXTRA_GIFT_ID);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_PRICE)) {
                giftPrice = getIntent().getExtras().getDouble(RESULT_EXTRA_GIFT_PRICE);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_DESC)) {
                giftDesc = getIntent().getExtras().getString(RESULT_EXTRA_GIFT_DESC);
            }

            if (getIntent().hasExtra(EXTRA_GIFT)) {
                gift = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_GIFT));
            }
        }

        giftDescription = getLayoutInflater().inflate(R.layout.content_gift_description,
                contributorsList, false);

        giftSpacer = giftDescription.findViewById(R.id.gift_spacer);
        title = giftDescription.findViewById(R.id.gift_title);
        contributedAmount = giftDescription.findViewById(R.id.contributed_amount);
        giftProgress = giftDescription.findViewById(R.id.gift_progress);
        description = giftDescription.findViewById(R.id.gift_description);
        targetAmount = giftDescription.findViewById(R.id.target_amount);


        ownerName = (TextView) giftDescription.findViewById(R.id.owner_name);
        ownerAvatar = (ImageView) giftDescription.findViewById(R.id.owner_avatar);

        giftTimeAgo = (TextView) giftDescription.findViewById(R.id.time_ago);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResultAndFinish();
            }
        });

        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            @Override
            public void onDragDismissed() {
                setResultAndFinish();
            }
        };

        setupContributing();
        contributorsList.addOnScrollListener(scrollListener);
        contributorsList.setOnFlingListener(flingListener);
        bindGift(false);

    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            final int scrollY = giftDescription.getTop();
            GiftCover.setOffset(scrollY);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // as we animate the main image's elevation change when it 'pins' at it's min height
            // a fling can cause the title to go over the image before the animation has a chance to
            // run. In this case we short circuit the animation and just jump to state.
            GiftCover.setImmediatePin(newState == RecyclerView.SCROLL_STATE_SETTLING);
        }
    };

    private RecyclerView.OnFlingListener flingListener = new RecyclerView.OnFlingListener() {
        @Override
        public boolean onFling(int velocityX, int velocityY) {
            GiftCover.setImmediatePin(true);
            return false;
        }
    };


    public void addContribution(View view) {

    }

    private void bindGift(final boolean postponeEnterTransition) {
        final Resources res = getResources();

        // load the main image
        Glide.with(this)
                .load(EndPoints.ASSETS_BASE_URL + giftImage)
                .listener(shotLoadListener)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.IMMEDIATE)
                .into(GiftCover);

        if (!TextUtils.isEmpty(giftTitle)) {
            ((BaselineGridTextView) title).setText(giftTitle);
        }

        if (!TextUtils.isEmpty(giftDesc)) {
            ((BaselineGridTextView) description).setText(giftDesc);
        } else {
            description.setVisibility(View.GONE);
        }

        if (gift != null) {
            if (gift.getCreatorName() != null) {
                ownerName.setText(gift.getCreatorName().toLowerCase());
            } else {
                ownerName.setText("N/A");
            }

            ((TextView) targetAmount).setText(String.format(Locale.ENGLISH, "Target: %1$,.2f", gift.getPrice()));

            Glide.with(this)
                    .load(ASSETS_BASE_URL + gift.getCreatorAvatar())
                    .transform(circleTransform)
                    .placeholder(R.drawable.avatar_placeholder)
                    .override(largeAvatarSize, largeAvatarSize)
                    .into(ownerAvatar);
            View.OnClickListener playerClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent player = new Intent(GiftDetailsActivity.this, ProfileActivity.class);
//                    if (shot.user.shots_count > 0) { // legit user object
//                        player.putExtra(PlayerActivity.EXTRA_PLAYER, shot.user);
//                    } else {
//                        // search doesn't fully populate the user object,
//                        // in this case send the ID not the full user
//                        player.putExtra(PlayerActivity.EXTRA_PLAYER_NAME, shot.user.username);
//                        player.putExtra(PlayerActivity.EXTRA_PLAYER_ID, shot.user.id);
//                    }
//                    ActivityOptions options =
//                            ActivityOptions.makeSceneTransitionAnimation(GiftDetailsActivity.this,
//                                    playerAvatar, getString(R.string.transition_player_avatar));
//                    startActivity(player, options.toBundle());
                }
            };
//            playerAvatar.setOnClickListener(playerClick);
//            playerName.setOnClickListener(playerClick);
            if (gift.getCreatedOn() != null) {
                giftTimeAgo.setText(DateUtils.getRelativeTimeSpanString(gift.getCreatedOn().getTime(),
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS).toString().toLowerCase());
            }
        } else {
            ownerName.setVisibility(View.GONE);
            ownerAvatar.setVisibility(View.GONE);
            giftTimeAgo.setVisibility(View.GONE);
        }

        ContributorAnimator contributorAnimator = new ContributorAnimator();
        contributorsList.setItemAnimator(contributorAnimator);
        adapter = new ContributorsAdapter(giftDescription, contributorFooter, 0,
                getResources().getInteger(R.integer.contributor_expand_collapse_duration));
        contributorsList.setAdapter(adapter);
        contributorsList.addItemDecoration(new InsetDividerDecoration(
                ContributorViewHolder.class,
                res.getDimensionPixelSize(R.dimen.divider_height),
                res.getDimensionPixelSize(R.dimen.keyline_1),
                ContextCompat.getColor(this, R.color.divider)));
        if (adapter.getItemCount() != 0) {
            loadContributors();
        }
    }

    private void loadContributors() {

        if (api == null) createApi(APIContributor.class);

        final Call<List<ContributorsModel>> contributorsCall =
                api.contributors(giftID);
        contributorsCall.enqueue(new Callback<List<ContributorsModel>>() {
            @Override
            public void onResponse(Call<List<ContributorsModel>> call, Response<List<ContributorsModel>> response) {
                final List<ContributorsModel> contributors = response.body();


                if (contributors != null && !contributors.isEmpty()) {
                    ((TextView)contributedAmount).setText(String.valueOf(0.0));
                    adapter.addContributors(contributors);
                    double amount = 0;
//                    contributors.forEach(contributor -> {
//                        amount += contributor.getAmount();
//                    });
                    for (int i=0; i < contributors.size(); i++) {
                        amount += contributors.get(i).getAmount();
                    }

                    ((TextView)contributedAmount).setText(String.valueOf(amount));
                    double progress =  ((amount/ gift.getPrice()) * 100);

                    ((ProgressBar) giftProgress).setProgress((int)progress);

                }
            }

            @Override public void onFailure(Call<List<ContributorsModel>> call, Throwable t) { }
        });
    }

    public void createApi(Class<APIContributor> serviceClass) {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(chain -> {
            Request original = chain.request();

            // Customize the request
            Request request = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("token", PreferenceManager.getToken(PewaaApplication.getAppContext()))
                    .method(original.method(), original.body())
                    .build();
            // Customize or return the response
            return chain.proceed(request);
        });

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client = httpClient.addInterceptor(interceptor).build();
        Retrofit builder = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        api = builder.create(serviceClass);
    }

    private void setupContributing() {

        contributorFooter = getLayoutInflater().inflate(R.layout.pewaa_contribute,
                contributorsList, false);
        contribute = (Button) contributorFooter.findViewById(R.id.contribute);

        contribute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contribute = new Intent();
                contribute.setClass(GiftDetailsActivity.this, ContributeActivity.class);
                contribute.putExtra(ContributeActivity.EXTRA_GIFT, Parcels.wrap(GiftsModel.class, gift));

                startActivity(contribute);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        draggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        draggableFrame.removeListener(chromeFader);
        super.onPause();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onErrorLoading(Throwable throwable) {

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResultAndFinish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResultAndFinish();
    }

    @Override
    public boolean onNavigateUp() {
        setResultAndFinish();
        return true;
    }

    /**
     * We run a transition to expand/collapse contributors. Scrolling the RecyclerView while this is
     * running causes issues, so we consume touch events while the transition runs.
     */
    private View.OnTouchListener touchEater = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    };

    private RequestListener shotLoadListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onResourceReady(GlideDrawable resource, String model,
                                       Target<GlideDrawable> target, boolean isFromMemoryCache,
                                       boolean isFirstResource) {
            final Bitmap bitmap = GlideUtils.getBitmap(resource);
            final int twentyFourDip = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, GiftDetailsActivity.this.getResources().getDisplayMetrics());
            Palette.from(bitmap)
                    .maximumColorCount(3)
                    .clearFilters() /* by default palette ignore certain hues
                        (e.g. pure black/white) but we don't want this. */
                    .setRegion(0, 0, bitmap.getWidth() - 1, twentyFourDip) /* - 1 to work around
                        https://code.google.com/p/android/issues/detail?id=191013 */
                    .generate(palette -> {
                        boolean isDark;
                        @ColorUtils.Lightness int lightness = ColorUtils.isDark(palette);
                        if (lightness == ColorUtils.LIGHTNESS_UNKNOWN) {
                            isDark = ColorUtils.isDark(bitmap, bitmap.getWidth() / 2, 0);
                        } else {
                            isDark = lightness == ColorUtils.IS_DARK;
                        }

                        if (!isDark) { // make back icon dark on light images
                            back.setColorFilter(ContextCompat.getColor(
                                    GiftDetailsActivity.this, R.color.dark_icon));
                        }

                        // color the status bar. Set a complementary dark color on L,
                        // light or dark color on M (with matching status bar icons)
                        int statusBarColor = getWindow().getStatusBarColor();
                        final Palette.Swatch topColor =
                                ColorUtils.getMostPopulousSwatch(palette);
                        if (topColor != null &&
                                (isDark || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
                            statusBarColor = ColorUtils.scrimify(topColor.getRgb(),
                                    isDark, SCRIM_ADJUSTMENT);
                            // set a light status bar on M+
                            if (!isDark && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ViewUtils.setLightStatusBar(GiftCover);
                            }
                        }

                        if (statusBarColor != getWindow().getStatusBarColor()) {
                            GiftCover.setScrimColor(statusBarColor);
                            ValueAnimator statusBarColorAnim = ValueAnimator.ofArgb(
                                    getWindow().getStatusBarColor(), statusBarColor);
                            statusBarColorAnim.addUpdateListener(animation -> getWindow().setStatusBarColor(
                                    (int) animation.getAnimatedValue()));
                            statusBarColorAnim.setDuration(1000L);
                            statusBarColorAnim.setInterpolator(
                                    getFastOutSlowInInterpolator(GiftDetailsActivity.this));
                            statusBarColorAnim.start();
                        }
                    });

            Palette.from(bitmap)
                    .clearFilters()
                    .generate(palette -> {
                        // color the ripple on the image spacer (default is grey)
                            giftSpacer.setBackground(
                                    ViewUtils.createRipple(palette, 0.25f, 0.5f,
                                            ContextCompat.getColor(GiftDetailsActivity.this, R.color.mid_grey),
                                            true));
                        //  slightly more opaque ripple on the pinned image to compensate
                        //  for the scrim
                        GiftCover.setForeground(
                                ViewUtils.createRipple(palette, 0.3f, 0.6f,
                                        ContextCompat.getColor(GiftDetailsActivity.this, R.color.mid_grey),
                                        true));
                    });

            // TODO should keep the background if the image contains transparency?!
            GiftCover.setBackground(null);
            return false;
        }

        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            return false;
        }
    };

    private void setResultAndFinish() {
        final Intent resultData = new Intent();
        resultData.putExtra(RESULT_EXTRA_GIFT_ID, giftID);
        setResult(RESULT_OK, resultData);
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /* package */ class ContributorsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EXPAND = 0x1;
        private static final int COLLAPSE = 0x2;
        private static final int COMMENT_LIKE = 0x3;
        private static final int REPLY = 0x4;

        private final List<ContributorsModel> contributors = new ArrayList<>();
        private final Transition expandCollapse;
        private final View description;
        private View footer;

        private boolean loading;
        private boolean noContributors;
        private int expandedContributorPosition = RecyclerView.NO_POSITION;

        ContributorsAdapter(
                @NonNull View description,
                @Nullable View footer,
                long commentCount,
                long expandDuration) {
            this.description = description;
            this.footer = footer;
            noContributors = commentCount == 0L;
            loading = !noContributors;
            expandCollapse = new AutoTransition();
            expandCollapse.setDuration(expandDuration);
            expandCollapse.setInterpolator(getFastOutSlowInInterpolator(GiftDetailsActivity.this));
            expandCollapse.addListener(new TransitionUtils.TransitionListenerAdapter() {
                @Override
                public void onTransitionStart(Transition transition) {
                    contributorsList.setOnTouchListener(touchEater);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
//                    contributorAnimator.setAnimateMoves(true);
                    contributorsList.setOnTouchListener(null);
                }
            });
        }

        void addContributors(List<ContributorsModel> newContributors) {
            hideLoadingIndicator();
            noContributors = false;
            contributors.addAll(newContributors);
            notifyItemRangeInserted(1, newContributors.size());
        }

        void removeCommentingFooter() {
            if (footer == null) return;
            int footerPos = getItemCount() - 1;
            footer = null;
            notifyItemRemoved(footerPos);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)  return R.layout.gift_title;
            if (position == 1) {
                if (loading)  return R.layout.loading;
                if (noContributors) return R.layout.pewaa_no_contributors;
            }
            if (footer != null) {
                int footerPos = (loading || noContributors) ? 2 : contributors.size() + 1;
                if (position == footerPos) return R.layout.pewaa_contribute;
            }
            return R.layout.row_contributor;
        }

        @Override
        public int getItemCount() {
            int count = 1; // description
            if (!contributors.isEmpty()) {
                count += contributors.size();
            } else {
                count++; // either loading or no contributors
            }
            if (footer != null) count++;
            return count;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case R.layout.gift_title:
                    return new SimpleViewHolder(description);
                case R.layout.row_contributor:
                    return createContributorHolder(parent, viewType);
                case R.layout.loading:
                case R.layout.pewaa_no_contributors:
                    return new SimpleViewHolder(
                            getLayoutInflater().inflate(viewType, parent, false));
                case R.layout.pewaa_contribute:
                    return new SimpleViewHolder(footer);
            }
            throw new IllegalArgumentException();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (getItemViewType(position)) {
                case R.layout.row_contributor:
                    bindContributor((ContributorViewHolder) holder, getContributor(position));
                    break;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position,
                                     List<Object> partialChangePayloads) {
            if (holder instanceof ContributorViewHolder) {
                bindPartialCommentChange(
                        (ContributorViewHolder) holder, position, partialChangePayloads);
            } else {
                onBindViewHolder(holder, position);
            }
        }

        private ContributorViewHolder createContributorHolder(ViewGroup parent, int viewType) {
            final ContributorViewHolder holder = new ContributorViewHolder(
                    getLayoutInflater().inflate(viewType, parent, false));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = holder.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) return;

                    final ContributorsModel contributor = getContributor(position);
                    TransitionManager.beginDelayedTransition(contributorsList, expandCollapse);
//                    contributorAnimator.setAnimateMoves(false);

                    // collapse any currently expanded items
                    if (expandedContributorPosition != RecyclerView.NO_POSITION) {
                        notifyItemChanged(expandedContributorPosition, COLLAPSE);
                    }

                    // expand this item (if it wasn't already)
                    if (expandedContributorPosition != position) {
                        expandedContributorPosition = position;
                        notifyItemChanged(position, EXPAND);
//                        if (contributor.liked == null) {
//                            final Call<Like> liked = dribbblePrefs.getApi()
//                                    .likedComment(shot.id, contributor.id);
//                            liked.enqueue(new Callback<Like>() {
//                                @Override
//                                public void onResponse(Call<Like> call, Response<Like> response) {
//                                    contributor.liked = response.isSuccessful();
//                                    holder.likeHeart.setChecked(contributor.liked);
//                                    holder.likeHeart.jumpDrawablesToCurrentState();
//                                }
//
//                                @Override public void onFailure(Call<Like> call, Throwable t) { }
//                            });
//                        }
//                        if (enterComment != null && enterComment.hasFocus()) {
//                            enterComment.clearFocus();
//                            ImeUtils.hideIme(enterComment);
//                        }
                        holder.itemView.requestFocus();
                    } else {
                        expandedContributorPosition = RecyclerView.NO_POSITION;
                    }
                }
            });

            holder.avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    final int position = holder.getAdapterPosition();
//                    if (position == RecyclerView.NO_POSITION) return;
//
//                    final Comment contributor = getContributor(position);
//                    final Intent player = new Intent(GiftDetailsActivity.this, PlayerActivity.class);
//                    player.putExtra(PlayerActivity.EXTRA_PLAYER, comment.user);
//                    ActivityOptions options =
//                            ActivityOptions.makeSceneTransitionAnimation(DribbbleShot.this,
//                                    Pair.create(holder.itemView,
//                                            getString(R.string.transition_player_background)),
//                                    Pair.create((View) holder.avatar,
//                                            getString(R.string.transition_player_avatar)));
//                    startActivity(player, options.toBundle());
                }
            });

//            holder.reply.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final int position = holder.getAdapterPosition();
//                    if (position == RecyclerView.NO_POSITION) return;
//
//                    final Comment comment = getComment(position);
//                    enterComment.setText("@" + comment.user.username + " ");
//                    enterComment.setSelection(enterComment.getText().length());
//
//                    // collapse the comment and scroll the reply box (in the footer) into view
//                    expandedContributorPosition = RecyclerView.NO_POSITION;
//                    notifyItemChanged(position, REPLY);
//                    holder.reply.jumpDrawablesToCurrentState();
//                    enterComment.requestFocus();
//                    contributorsList.smoothScrollToPosition(getItemCount() - 1);
//                }
//            });

//            holder.likeHeart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (dribbblePrefs.isLoggedIn()) {
//                        final int position = holder.getAdapterPosition();
//                        if (position == RecyclerView.NO_POSITION) return;
//
//                        final Comment comment = getComment(position);
//                        if (comment.liked == null || !comment.liked) {
//                            comment.liked = true;
//                            comment.likes_count++;
//                            holder.likesCount.setText(String.valueOf(comment.likes_count));
//                            notifyItemChanged(position, COMMENT_LIKE);
//                            final Call<Like> likeCommentCall =
//                                    dribbblePrefs.getApi().likeComment(shot.id, comment.id);
//                            likeCommentCall.enqueue(new Callback<Like>() {
//                                @Override
//                                public void onResponse(Call<Like> call, Response<Like> response) { }
//
//                                @Override
//                                public void onFailure(Call<Like> call, Throwable t) { }
//                            });
//                        } else {
//                            comment.liked = false;
//                            comment.likes_count--;
//                            holder.likesCount.setText(String.valueOf(comment.likes_count));
//                            notifyItemChanged(position, COMMENT_LIKE);
//                            final Call<Void> unlikeCommentCall =
//                                    dribbblePrefs.getApi().unlikeComment(shot.id, comment.id);
//                            unlikeCommentCall.enqueue(new Callback<Void>() {
//                                @Override
//                                public void onResponse(Call<Void> call, Response<Void> response) { }
//
//                                @Override
//                                public void onFailure(Call<Void> call, Throwable t) { }
//                            });
//                        }
//                    } else {
//                        holder.likeHeart.setChecked(false);
//                        startActivityForResult(new Intent(DribbbleShot.this,
//                                DribbbleLogin.class), RC_LOGIN_LIKE);
//                    }
//                }
//            });

//            holder.likesCount.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    final int position = holder.getAdapterPosition();
//                    if (position == RecyclerView.NO_POSITION) return;
//
//                    final Comment comment = getComment(position);
//                    final Call<List<Like>> commentLikesCall =
//                            dribbblePrefs.getApi().getCommentLikes(shot.id, comment.id);
//                    commentLikesCall.enqueue(new Callback<List<Like>>() {
//                        @Override
//                        public void onResponse(Call<List<Like>> call,
//                                               Response<List<Like>> response) {
//                            // TODO something better than this.
//                            StringBuilder sb = new StringBuilder("Liked by:\n\n");
//                            for (Like like : response.body()) {
//                                if (like.user != null) {
//                                    sb.append("@");
//                                    sb.append(like.user.username);
//                                    sb.append("\n");
//                                }
//                            }
//                            Toast.makeText(getApplicationContext(), sb.toString(), Toast
//                                    .LENGTH_SHORT).show();
//                        }
//
//                        @Override
//                        public void onFailure(Call<List<Like>> call, Throwable t) { }
//                    });
//                }
//            });

            return holder;
        }

        private void bindContributor(ContributorViewHolder holder, ContributorsModel contributor) {
            final int position = holder.getAdapterPosition();
            final boolean isExpanded = position == expandedContributorPosition;
            Glide.with(GiftDetailsActivity.this)
                    .load(ASSETS_BASE_URL + contributor.getAvatar())
                    .transform(circleTransform)
                    .placeholder(R.drawable.avatar_placeholder)
                    .override(largeAvatarSize, largeAvatarSize)
                    .into(holder.avatar);

            if (contributor.getName() != null)
                holder.author.setText(contributor.getName().toLowerCase());

            holder.contributorBody.setText(String.format("Contributed %1$,.2f", contributor.getAmount()));

            holder.timeAgo.setText(contributor.getCreatedOn() == null ? "" :
                    DateUtils.getRelativeTimeSpanString(contributor.getCreatedOn().getTime(),
                            System.currentTimeMillis(),
                            DateUtils.SECOND_IN_MILLIS)
                            .toString().toLowerCase());
//            HtmlUtils.setTextWithNiceLinks(holder.contributorBody,
//                    contributor.getParsedBody(holder.contributorBody));
//            holder.likeHeart.setChecked(contributor.liked != null && contributor.liked.booleanValue());
//            holder.likeHeart.setEnabled(contributor.user.id != dribbblePrefs.getUserId());
//            holder.likesCount.setText(String.valueOf(contributor.likes_count));
            setExpanded(holder, isExpanded);
        }

        private void setExpanded(ContributorViewHolder holder, boolean isExpanded) {
            holder.itemView.setActivated(isExpanded);
//            holder.reply.setVisibility((isExpanded && allowComment) ? View.VISIBLE : View.GONE);
//            holder.likeHeart.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
//            holder.likesCount.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        private void bindPartialCommentChange(
                ContributorViewHolder holder, int position, List<Object> partialChangePayloads) {
            // for certain changes we don't need to rebind data, just update some view state
            if ((partialChangePayloads.contains(EXPAND)
                    || partialChangePayloads.contains(COLLAPSE))
                    || partialChangePayloads.contains(REPLY)) {
                setExpanded(holder, position == expandedContributorPosition);
            } else if (partialChangePayloads.contains(COMMENT_LIKE)) {
                return; // nothing to do
            } else {
                onBindViewHolder(holder, position);
            }
        }

        private ContributorsModel getContributor(int adapterPosition) {
            return contributors.get(adapterPosition - 1); // description
        }

        private void hideLoadingIndicator() {
            if (!loading) return;
            loading = false;
            notifyItemRemoved(1);
        }
    }

    /* package */ static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }
    }

    /* package */ static class ContributorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.contributor_avatar) ImageView avatar;
        @BindView(R.id.contributor_name) AuthorTextView author;
        @BindView(R.id.time_ago) TextView timeAgo;
        @BindView(R.id.player_bio) TextView contributorBody;

        ContributorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * A {@link RecyclerView.ItemAnimator} which allows disabling move animations. RecyclerView
     * does not like animating item height changes. {@link android.transition.ChangeBounds} allows
     * this but in order to simultaneously collapse one item and expand another, we need to run the
     * Transition on the entire RecyclerView. As such it attempts to move views around. This
     * custom item animator allows us to stop RecyclerView from trying to handle this for us while
     * the transition is running.
     */
    /* package */ static class ContributorAnimator extends SlideInItemAnimator {

        private boolean animateMoves = false;

        ContributorAnimator() {
            super();
        }

        void setAnimateMoves(boolean animateMoves) {
            this.animateMoves = animateMoves;
        }

        @Override
        public boolean animateMove(
                RecyclerView.ViewHolder holder, int fromX, int fromY, int toX, int toY) {
            if (!animateMoves) {
                dispatchMoveFinished(holder);
                return false;
            }
            return super.animateMove(holder, fromX, fromY, toX, toY);
        }
    }
}
