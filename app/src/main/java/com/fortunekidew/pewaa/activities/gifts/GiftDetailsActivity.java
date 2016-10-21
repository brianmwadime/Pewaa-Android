package com.fortunekidew.pewaa.activities.gifts;

import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.wishlists.PaymentsAdapter;
import com.fortunekidew.pewaa.animations.AnimationsUtil;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.payments.EditPayments;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GiftDetailsActivity extends AppCompatActivity implements LoadingData {

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
    private String giftID, giftTitle;
    private float giftPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift);
        ButterKnife.bind(this);

        initializerView();

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("giftTitle")) {
                giftTitle = getIntent().getExtras().getString("giftTitle");
            }
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("giftID")) {
                giftID = getIntent().getExtras().getString("giftID");
            }
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("giftPrice")) {
                giftPrice = getIntent().getExtras().getFloat("giftPrice");
            }
        }

        initCollapsingToolbar();

        prepareAlbums();

        try {
            Glide.with(this).load(R.drawable.cover).into(GiftCover);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializerView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (AppHelper.isAndroid5()) {
            collapsingToolbar.setTransitionName(getString(R.string.gift_name_transition));
        }

        paymentList = new ArrayList<>();
        adapter = new PaymentsAdapter(this, paymentList);

//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(PewaaApplication.getAppContext(), 2);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        collapsingToolbar.setTitle(" ");
        AnimationsUtil.expandToolbar(containerGift, appBarLayout);


//        mGiftTitle.setText(giftTitle);

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
                    collapsingToolbar.setTitle(giftTitle);

                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");

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
        super.onBackPressed();
        if (AppHelper.isAndroid5()) {
            //Transition animation
            Transition enterTrans = new Fade();
            getWindow().setEnterTransition(enterTrans);
            enterTrans.setDuration(300);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mProfilePresenter.onDestroy();
    }
}
