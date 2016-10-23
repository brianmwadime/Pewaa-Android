package com.fortunekidew.pewaa.activities.gifts;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.wishlists.PaymentsAdapter;
import com.fortunekidew.pewaa.animations.AnimationsUtil;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.payments.EditPayments;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GiftDetailsActivity extends Activity implements LoadingData {

    public final static String RESULT_EXTRA_GIFT_ID = "RESULT_EXTRA_GIFT_ID";
    public final static String RESULT_EXTRA_GIFT_TITLE = "RESULT_EXTRA_GIFT_TITLE";
    public final static String RESULT_EXTRA_GIFT_DESC = "RESULT_EXTRA_GIFT_DESC";
    public final static String RESULT_EXTRA_GIFT_IMAGE = "RESULT_EXTRA_GIFT_IMAGE";
    public static final String RESULT_EXTRA_GIFT_PRICE = "RESULT_EXTRA_GIFT_PRICE";

    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.containerGiftDetails)
    CoordinatorLayout containerGift;
    @Bind(R.id.cover)
    ImageView GiftCover;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.group_container)
    RelativeLayout GroupTitleContainer;
    @Bind(R.id.gift_title)
    TextView mGiftTitle;
    @Bind(R.id.gift_description)
    TextView mGiftDescirption;
    private PaymentsAdapter adapter;
    private List<EditPayments> paymentList;
    private String giftID, giftTitle, giftImage, giftDesc;
    private float giftPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        ButterKnife.bind(this);

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
                giftPrice = getIntent().getExtras().getFloat(RESULT_EXTRA_GIFT_PRICE);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_DESC)) {
                giftDesc = getIntent().getExtras().getString(RESULT_EXTRA_GIFT_DESC);
            }
        }

        initializerView();

        initCollapsingToolbar();

        prepareAlbums();

    }

    private void initializerView() {
        setActionBar(toolbar);
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        if (AppHelper.isAndroid5()) {
            getActionBar().setTitle(giftTitle);
        }


        mGiftDescirption.setText(giftDesc);
        mGiftTitle.setText(giftTitle);

        Glide.with(this)
            .load(EndPoints.ASSETS_BASE_URL + giftImage)
            .placeholder(R.drawable.cover)

            .into(GiftCover);

        paymentList = new ArrayList<>();
        adapter = new PaymentsAdapter(this, paymentList);

//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(PewaaApplication.getAppContext(), 2);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {

        AnimationsUtil.expandToolbar(containerGift, appBarLayout);


        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    getActionBar().setTitle("");

                    isShow = true;
                } else if (isShow) {
                    getActionBar().setTitle(giftTitle);

                    isShow = false;
                }
            }
        });
    }

    /**
     * Adding few albums for testing
     */
    private void prepareAlbums() {
        int[] covers = new int[]{
                R.drawable.album1,
                R.drawable.album2,
                R.drawable.album3,
                R.drawable.album4,
                R.drawable.album5,
                R.drawable.album6,
                R.drawable.album7,
                R.drawable.album8,
                R.drawable.album9,
                R.drawable.album10,
                R.drawable.album11};

        EditPayments a = new EditPayments("True Romance", 13, covers[0]);
        paymentList.add(a);

        a = new EditPayments("Xscpae", 8, covers[1]);
        paymentList.add(a);

        a = new EditPayments("Maroon 5", 11, covers[2]);
        paymentList.add(a);

        a = new EditPayments("Born to Die", 12, covers[3]);
        paymentList.add(a);

        a = new EditPayments("Honeymoon", 14, covers[4]);
        paymentList.add(a);

        a = new EditPayments("I Need a Doctor", 1, covers[5]);
        paymentList.add(a);

        a = new EditPayments("Loud", 11, covers[6]);
        paymentList.add(a);

        a = new EditPayments("Legend", 14, covers[7]);
        paymentList.add(a);

        a = new EditPayments("Hello", 11, covers[8]);
        paymentList.add(a);

        a = new EditPayments("Greatest Hits", 17, covers[9]);
        paymentList.add(a);

        adapter.notifyDataSetChanged();
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
            if (AppHelper.isAndroid5()) {
                Transition enterTrans = new Fade();
                getWindow().setEnterTransition(enterTrans);
                enterTrans.setDuration(300);
            }
            finish();
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

    private void setResultAndFinish() {
        final Intent resultData = new Intent();
        resultData.putExtra(RESULT_EXTRA_GIFT_ID, giftID);
        setResult(RESULT_OK, resultData);
        finishAfterTransition();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mProfilePresenter.onDestroy();
    }
}
