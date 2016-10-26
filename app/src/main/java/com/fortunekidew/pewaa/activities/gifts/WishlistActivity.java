package com.fortunekidew.pewaa.activities.gifts;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Fade;
import android.transition.Transition;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ActionMenuView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.NewContactsActivity;
import com.fortunekidew.pewaa.activities.contributors.AssignContributor;
import com.fortunekidew.pewaa.adapters.recyclerView.wishlists.GiftsAdapter;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;
import com.fortunekidew.pewaa.presenters.GiftsPresenter;
import com.fortunekidew.pewaa.ui.recyclerview.DividerItemDecoration;
import com.fortunekidew.pewaa.util.AnimUtils;

import java.util.List;
import java.util.Timer;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;

import static com.fortunekidew.pewaa.app.AppConstants.STATUS_CONTRIBUTOR_ADDED;
import static com.fortunekidew.pewaa.app.AppConstants.STATUS_CONTRIBUTOR_ADDED_SUCCESS;
import static com.fortunekidew.pewaa.services.MainService.mSocket;


/**
 * Created by Brian Mwakima on 05/03/2016.
 * Email : mwadime@fortunekidew.co.ke
 */

@SuppressLint("SetTextI18n")
public class WishlistActivity extends Activity implements LoadingData {

    @Bind(R.id.GiftsList)
    RecyclerView GiftsList;
    @Bind(android.R.id.empty)
    LinearLayout EmptyGiftslists;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.addGiftFab)
    FloatingActionButton AddGift;
    @Bind(R.id.loading)
    ProgressBar loading;
    public Intent mIntent = null;
    private GiftsAdapter mGiftsAdapter;
    public Context context;
    private GiftsPresenter mGiftsPresenter = new GiftsPresenter(this);
    private String wishlistID, wishlistTitle;

    private Timer TYPING_TIMER_LENGTH = new Timer();
    private boolean isTyping = false;
    private boolean isOpen;
    private Realm realm;
    private EventBus eventBus = EventBus.getDefault();
    private Animator.AnimatorListener mAnimatorListenerOpen, mAnimatorListenerClose;
    private GestureDetectorCompat gestureDetector;
    private ActionMode actionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gifts);
        ButterKnife.bind(this);

        setActionBar(toolbar);
        if (getActionBar() != null)
            getActionBar().setDisplayHomeAsUpEnabled(true);

        realm = Realm.getDefaultInstance();
        eventBus.register(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("wishlistID")) {
                wishlistID = getIntent().getExtras().getString("wishlistID");
            }

            if (getIntent().hasExtra("wishlistTitle")) {
                wishlistTitle = getIntent().getExtras().getString("wishlistTitle");
            }
        }

        if (savedInstanceState == null) {
            animateToolbar();
        }

        mGiftsPresenter.onCreate();
        initializerView();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    /**
     * method initialize the view
     */
    public void initializerView() {


        getActionBar().setTitle(wishlistTitle);

        setExitSharedElementCallback(GiftsAdapter.createSharedElementReenterCallback(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mGiftsAdapter = new GiftsAdapter(this, GiftsList, mSocket);
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
            case R.id.add_contributor:
                Intent intent = new Intent(this, NewContactsActivity.class);
                intent.putExtra(NewContactsActivity.RESULT_EXTRA_WISHLIST_ID, wishlistID);
                startActivityForResult(intent, STATUS_CONTRIBUTOR_ADDED);
                break;
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
    protected void onResume() {
        super.onResume();
    }

    /**
     * method to show all user gifts
     *
     * @param giftsModels this is parameter for ShowGifts method
     */
    public void ShowGifts(List<GiftsModel> giftsModels) {

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
        wishlistIntent.putExtra("wishlistID", wishlistID);
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
        eventBus.unregister(this);
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
        loading.setVisibility(View.GONE);
        GiftsList.setVisibility(View.GONE);
        EmptyGiftslists.setVisibility(View.VISIBLE);
    }


    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        int messageId = pusher.getMessageId();
        switch (pusher.getAction()) {
            case "new_gift":
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

