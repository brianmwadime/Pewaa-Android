package com.fortunekidew.pewaad.activities.gifts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.contributors.NewContributorActivity;
import com.fortunekidew.pewaad.activities.contributors.AssignContributor;
import com.fortunekidew.pewaad.adapters.recyclerView.wishlists.GiftsAdapter;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.presenters.GiftsPresenter;
import com.fortunekidew.pewaad.ui.recyclerview.DividerItemDecoration;
import com.fortunekidew.pewaad.util.AnimUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmList;

import static com.fortunekidew.pewaad.app.AppConstants.STATUS_CONTRIBUTOR_ADDED;
import static com.fortunekidew.pewaad.app.AppConstants.STATUS_CONTRIBUTOR_ADDED_SUCCESS;


/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

@SuppressLint("SetTextI18n")
public class WishlistActivity extends Activity implements LoadingData {

    public final static String RESULT_EXTRA_WISHLIST_ID = "RESULT_EXTRA_WISHLIST_ID";
    public final static String RESULT_EXTRA_WISHLIST_TITLE = "RESULT_EXTRA_WISHLIST_TITLE";
    public final static String RESULT_EXTRA_WISHLIST_PERMISSION = "RESULT_EXTRA_WISHLIST_PERMISSION";
    @BindView(R.id.swipeContainer)
    SwipeRefreshLayout SwipeToRefresh;
    @BindView(R.id.GiftsList)
    RecyclerView GiftsList;
    @BindView(android.R.id.empty)
    LinearLayout EmptyGiftslists;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.addGiftFab)
    FloatingActionButton AddGift;
    @BindView(R.id.loading)
    ProgressBar loading;
    public Intent mIntent = null;
    private GiftsAdapter mGiftsAdapter;
    public Context context;
    private GiftsPresenter mGiftsPresenter = new GiftsPresenter(this);
    private String wishlistID, wishlistTitle, wishlistPermission;

    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifts);
        ButterKnife.bind(this);

        setActionBar(toolbar);
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        realm = PewaaApplication.getRealmDatabaseInstance();

        EventBus.getDefault().register(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_EXTRA_WISHLIST_ID)) {
                wishlistID = getIntent().getExtras().getString(RESULT_EXTRA_WISHLIST_ID);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_WISHLIST_TITLE)) {
                wishlistTitle = getIntent().getExtras().getString(RESULT_EXTRA_WISHLIST_TITLE);
            }

            if (getIntent().hasExtra(RESULT_EXTRA_WISHLIST_PERMISSION)) {
                wishlistPermission = getIntent().getExtras().getString(RESULT_EXTRA_WISHLIST_PERMISSION);
            }
        }

        if (savedInstanceState == null) {
            animateToolbar();
        }

        mGiftsPresenter.onCreate();
        initializerView();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mGiftsPresenter.onRefresh();
            }
        });

    }

    /**
     * method initialize the view
     */
    public void initializerView() {
        getActionBar().setTitle(wishlistTitle);

        if (wishlistPermission.equals(AppConstants.WISHLIST_CONTRIBUTOR)) AddGift.setVisibility(View.GONE);

        setExitSharedElementCallback(GiftsAdapter.createSharedElementReenterCallback(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGiftsAdapter = new GiftsAdapter(this, GiftsList);
        GiftsList.setLayoutManager(layoutManager);
        Drawable dividerDrawable = ContextCompat.getDrawable(this, R.drawable.recycler_divider);
        GiftsList.addItemDecoration(new DividerItemDecoration(dividerDrawable));
        GiftsList.setAdapter(mGiftsAdapter);
        GiftsList.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.wishlist_menu, menu);

        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                if (AppHelper.isAndroid5()) {
                    Transition enterTrans = new Fade();
                    getWindow().setEnterTransition(enterTrans);
                    enterTrans.setDuration(300);
                }
                finish();
            break;
            case R.id.delete_contributor:
                deleteContributor(wishlistID, PreferenceManager.getID(this));
                break;
            case R.id.add_contributor:
                Intent intent = new Intent(this, NewContributorActivity.class);
                intent.putExtra(NewContributorActivity.RESULT_EXTRA_WISHLIST_ID, wishlistID);
                startActivityForResult(intent, STATUS_CONTRIBUTOR_ADDED);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteContributor(String wishlist_id, String contributor_id) {
        mGiftsPresenter.removeContributor(wishlist_id, contributor_id);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(wishlistPermission.equals(AppConstants.WISHLIST_CONTRIBUTOR)) {
            menu.findItem(R.id.add_contributor).setVisible(false);
            menu.findItem(R.id.delete_contributor).setVisible(true);
        } else {
            menu.findItem(R.id.delete_contributor).setVisible(false);
            menu.findItem(R.id.add_contributor).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
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
    protected void onResume() {
        super.onResume();
    }

    /**
     * method to show all user gifts
     *
     * @param giftsModels this is parameter for ShowGifts method
     */
    public void ShowGifts(List<GiftsModel> giftsModels) {
        SwipeToRefresh.setRefreshing(false);
        if (giftsModels.size() != 0) {
            RealmList<GiftsModel> mGiftsList = new RealmList<GiftsModel>();
            for (GiftsModel giftsModel : giftsModels) {
                mGiftsList.add(giftsModel);
            }
            mGiftsAdapter.setGifts(mGiftsList);
        } else {
            GiftsList.setVisibility(View.GONE);
            EmptyGiftslists.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.addGiftFab)
    public void addGift(View view) {
        Intent wishlistIntent = new Intent(this, AddGiftsActivity.class);
        wishlistIntent.putExtra(AddGiftsActivity.RESULT_EXTRA_GIFT_ID, wishlistID);
        this.startActivity(wishlistIntent);
        this.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGiftsPresenter.onDestroy();
        EventBus.getDefault().unregister(this);
        realm.close();
    }

    @Override
    public void onShowLoading() {
        checkEmptyState();
    }



    @Override
    public void onHideLoading() {
        loading.setVisibility(View.GONE);
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        SwipeToRefresh.setRefreshing(false);
        loading.setVisibility(View.GONE);
        GiftsList.setVisibility(View.GONE);
        EmptyGiftslists.setVisibility(View.VISIBLE);
    }


    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @Subscribe
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case AppConstants.EVENT_BUS_NEW_GIFT:
                GiftsModel newGift = new GiftsModel();
                newGift.setName(pusher.getGiftObject().getName());
                newGift.setDescription(pusher.getGiftObject().getDescription());
                newGift.setId(pusher.getGiftObject().getId());
                newGift.setAvatar(pusher.getGiftObject().getAvatar());
                newGift.setCreatedOn(pusher.getGiftObject().getCreatedOn());
                newGift.setDescription(pusher.getGiftObject().getDescription());
                newGift.setPrice(pusher.getGiftObject().getPrice());

                mGiftsAdapter.addItem(0, newGift);

                break;
        case AppConstants.EVENT_BUS_EXIT_WISHLIST:
                 finish();
                break;
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK
                || !data.hasExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_ID)) return;

        // When reentering, if the shared element is no longer on screen (e.g. after an
        // orientation change) then scroll it into view.
        final String sharedShotId = data.getStringExtra(GiftDetailsActivity.RESULT_EXTRA_GIFT_ID);
        if (!sharedShotId.isEmpty()                                             // returning from a shot
                && mGiftsAdapter.getItemCount() > 0                           // grid populated
                ) {    // view not attached
            final int position = mGiftsAdapter.getItemPosition(sharedShotId);
            if (position == RecyclerView.NO_POSITION) return;

            // delay the transition until our shared element is on-screen i.e. has been laid out
            postponeEnterTransition();
            GiftsList.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int l, int t, int r, int b,
                                           int oL, int oT, int oR, int oB) {
                    GiftsList.removeOnLayoutChangeListener(this);
                    startPostponedEnterTransition();
                }
            });
            GiftsList.scrollToPosition(position);
            toolbar.setTranslationZ(-1f);
        }
    }

    // Call Back method  to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // check if the request code is same as what is passed  here it is 2
        if(requestCode == STATUS_CONTRIBUTOR_ADDED)
        {
            if(null != data)
            {
                String contributorId = data.getStringExtra("EXTRA_CONTRIBUTOR_ID");

                Intent intent = new Intent(this, AssignContributor.class);
                intent.putExtra(AssignContributor.EXTRA_WISHLIST_ID, wishlistID);
                intent.putExtra(AssignContributor.EXTRA_CONTRIBUTOR_ID, contributorId);
//                FabTransform.addExtras(intent,
//                        ContextCompat.getColor(this, R.color.accent), R.drawable.ic_add_dark);
//                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, fab,
//                        getString(R.string.transition_contributor_add));
                startActivityForResult(intent, STATUS_CONTRIBUTOR_ADDED_SUCCESS);
            }
        }
    }

    private void animateToolbar() {
        // this is gross but toolbar doesn't expose it's children to animate them :(
        View t = toolbar.getChildAt(0);
        if (t != null && t instanceof TextView) {
            TextView title = (TextView) t;

            // fade in and space out the title.  Animating the letterSpacing performs horribly so
            // fake it by setting the desired letterSpacing then animating the scaleX ¯\_(ツ)_/¯
            title.setAlpha(0f);
            title.setScaleX(0.8f);

            title.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .setStartDelay(300)
                    .setDuration(900)
                    .setInterpolator(AnimUtils.getFastOutSlowInInterpolator(this));
        }
        View amv = toolbar.getChildAt(1);
        if (amv != null & amv instanceof ActionMenuView) {
            ActionMenuView actions = (ActionMenuView) amv;
            popAnim(actions.getChildAt(0), 500, 200); // filter
            popAnim(actions.getChildAt(1), 700, 200); // overflow
        }
    }

    private void popAnim(View v, int startDelay, int duration) {
        if (v != null) {
            v.setAlpha(0f);
            v.setScaleX(0f);
            v.setScaleY(0f);

            v.animate()
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setStartDelay(startDelay)
                    .setDuration(duration)
                    .setInterpolator(AnimationUtils.loadInterpolator(this,
                            android.R.interpolator.overshoot));
        }
    }

    private void checkEmptyState() {
        if (mGiftsAdapter.getItemCount() == 0) {
            // if grid is empty check whether we're loading or if no filters are selected
            loading.setVisibility(View.VISIBLE);
            toolbar.setTranslationZ(0f);
        } else {
            loading.setVisibility(View.GONE);
        }
    }
}

