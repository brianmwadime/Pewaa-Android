package com.fortunekidew.pewaad.activities.main;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.search.SearchContactsActivity;
import com.fortunekidew.pewaad.activities.settings.SettingsActivity;
import com.fortunekidew.pewaad.activities.status.StatusActivity;
import com.fortunekidew.pewaad.activities.wishlists.AddWishlistsActivity;
import com.fortunekidew.pewaad.adapters.others.TabsAdapter;
import com.fortunekidew.pewaad.api.APIPush;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PermissionHandler;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.SignUpPreferenceManager;
import com.fortunekidew.pewaad.interfaces.NetworkListener;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_ACTION_MODE_DESTORYED;
import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_ACTION_MODE_STARTED;
import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_START_REFRESH;
import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_STOP_REFRESH;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class MainActivity extends AppCompatActivity implements NetworkListener {
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.main_activity)
    View mView;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.app_bar)
    Toolbar toolbar;
    @BindView(R.id.addWishlistFab)
    FloatingActionButton AddWishlistFab;
    @BindView(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    // authority for sync adapter's content provider
    private APIService mApiService;
    private SignUpPreferenceManager mPreferenceManager;
    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;//
    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;// 3600L
    private Account mAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Permissions();
        initializerView();
        setupToolbar();
        EventBus.getDefault().register(this);

        setupAccountInstance();
        startPeriodicSync();
        initRequestSync();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search_contacts:
                AppHelper.LaunchActivity(this, SearchContactsActivity.class);
                break;
            case R.id.settings:
                AppHelper.LaunchActivity(this, SettingsActivity.class);
                break;
            case R.id.status:
                AppHelper.LaunchActivity(this, StatusActivity.class);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * method to setup toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setHomeButtonEnabled(false);
        }
    }

    /**
     * method to initialize the view
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initializerView() {
        mApiService = new APIService(this);
        mPreferenceManager = new SignUpPreferenceManager(this);
        viewPager.setAdapter(new TabsAdapter(getSupportFragmentManager()));
        viewPager.setOffscreenPageLimit(1);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewPager.setCurrentItem(0);
        tabLayout.getTabAt(0).setCustomView(R.layout.custom_tab_wishlists);
        tabLayout.getTabAt(1).setCustomView(R.layout.custom_tab_contacts);
        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(this, R.color.colorUnSelected));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        viewPager.setCurrentItem(0);
                        AddWishlistFab.show();
                        findViewById(R.id.counterTabWishlists).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter));
                        ((TextView) findViewById(R.id.title_tabs_wishlists)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        AddWishlistFab.hide();
                        findViewById(R.id.counterTabWishlists).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        break;
                    default:
                        break;
                }

                final Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_for_button_animtion_enter);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        AddWishlistFab.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                AddWishlistFab.startAnimation(animation);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:

                        findViewById(R.id.counterTabWishlists).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_wishlists)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorUnSelected));
                        break;
                    case 1:
                        findViewById(R.id.counterTabWishlists).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorUnSelected));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if(!mPreferenceManager.isDeviceSaved()) {
            registerDevice(mPreferenceManager.getDeviceToken());
        }
    }

    @OnClick(R.id.addWishlistFab)
    public void addWishlist(View view) {
        AppHelper.LaunchActivity(this, AddWishlistsActivity.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PewaaApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void registerDevice(String token) {
        APIPush mApiPush = mApiService.RootService(APIPush.class, PreferenceManager.getToken(MainActivity.this), EndPoints.BASE_URL);
        Call<StatusResponse> call = mApiPush.registerDevice(token, "ANDROID");
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                mPreferenceManager.setIsDeviceSaved(true);
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                mPreferenceManager.setIsDeviceSaved(false);
            }
        });
    }

    /**
     * method to start synchronization
     */
    private void startPeriodicSync() {
        if (mAccount != null) {
            ContentResolver.setIsSyncable(mAccount, ContactsContract.AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(mAccount, ContactsContract.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, ContactsContract.AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);
        }
    }

    /**
     * method to start a new RequestSync
     */
    public void initRequestSync() {
        // Pass the settings flags by inserting them in a bundle
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(mAccount, ContactsContract.AUTHORITY, settingsBundle);
    }

    private void Permissions() {
        if (PermissionHandler.checkPermission(this, Manifest.permission.READ_CONTACTS)) {
            AppHelper.LogCat("Read contact data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            PermissionHandler.requestPermission(this, Manifest.permission.READ_CONTACTS);
        }
        if (PermissionHandler.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AppHelper.LogCat("Read storage data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read storage data permission.");
            PermissionHandler.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }

    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Subscribe
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case EVENT_BUS_START_REFRESH:
                toolbarProgressBar.setVisibility(View.VISIBLE);
                toolbarProgressBar.getIndeterminateDrawable().setColorFilter(AppHelper.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                break;
            case EVENT_BUS_STOP_REFRESH:
                toolbarProgressBar.setVisibility(View.GONE);
                break;
            case EVENT_BUS_ACTION_MODE_STARTED:
                tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorGrayDarker));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(AppHelper.getColor(this, R.color.colorGrayDarkerBar));
                }
                break;
            case EVENT_BUS_ACTION_MODE_DESTORYED:
                tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorPrimary));
                if (AppHelper.isAndroid5()) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(AppHelper.getColor(this, R.color.colorPrimaryDark));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
            EventBus.getDefault().post(new Pusher("ContactsPermission"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AppConstants.CONTACTS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                EventBus.getDefault().post(new Pusher("ContactsPermission"));
            }
        }
    }

    /**
     * method to setup your account
     */
    private void setupAccountInstance() {
        AccountManager manager = AccountManager.get(this);
        if (PermissionHandler.checkPermission(this, Manifest.permission.GET_ACCOUNTS)) {
            AppHelper.LogCat("GET ACCOUNTS  permission already granted.");
        } else {
            AppHelper.LogCat("Please request GET ACCOUNTS permission.");
            PermissionHandler.requestPermission(this, Manifest.permission.GET_ACCOUNTS);
        }
        Account[] accounts = manager.getAccountsByType(AppConstants.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            mAccount = accounts[0];
        }
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnecting, boolean isConnected) {
        if (!isConnecting && !isConnected) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_ERROR, AppConstants.TEXT_COLOR);
        } else if (isConnecting && isConnected) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, mView, getString(R.string.waiting_for_network), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

        }
    }

}