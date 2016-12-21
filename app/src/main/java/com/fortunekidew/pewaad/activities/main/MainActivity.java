package com.fortunekidew.pewaad.activities.main;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.SignUpPreferenceManager;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.receivers.NetworkChangeListener;
import com.fortunekidew.pewaad.services.MainService;
import com.fortunekidew.pewaad.sync.AuthenticatorService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Email : mwadime@fortunekidew.co.ke
 */
public class MainActivity extends AppCompatActivity {

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
    private static final String AUTHORITY = "com.android.contacts";
    // sync interval
    private static final long SYNC_INTERVAL = 10;
    // Account type and auth token type
    public static final String ACCOUNT_TYPE = "com.fortunekidew.pewaad";
    private Account mAccount;
    private APIService mApiService;
    private SignUpPreferenceManager mPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupAccountInstance();
        startPeriodicSync();
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initializerView();
        setupToolbar();
        Uri intentData = getIntent().getData();
        if (intentData != null) {
            Cursor cursor = managedQuery(intentData, null, null, null, null);
            if (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }
        } else {
            AppHelper.LogCat("IntentData is null MainActivity ");
        }

        new Handler().postDelayed(this::Permissions, 5000);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.search_conversations:
//                AppHelper.LaunchActivity(this, SearchConversationsActivity.class);
//                break;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        switch (tabLayout.getSelectedTabPosition()) {
            case 0:
                getMenuInflater().inflate(R.menu.wishlists_menu, menu);
                break;
            case 1:
                getMenuInflater().inflate(R.menu.contacts_menu, menu);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * method to setup toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
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
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter));
                        ((TextView) findViewById(R.id.title_tabs_messages)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        break;
                    case 1:
                        viewPager.setCurrentItem(1);
                        AddWishlistFab.hide();
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_contacts)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorWhite));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:

                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
                        ((TextView) findViewById(R.id.title_tabs_messages)).setTextColor(AppHelper.getColor(MainActivity.this, R.color.colorUnSelected));
                        break;
                    case 1:
                        findViewById(R.id.counterTabMessages).setBackground(AppHelper.getDrawable(MainActivity.this, R.drawable.bg_circle_tab_counter_unselected));
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
//        MainView.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MainView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
//        DisConnectFromServer();
    }

    /**
     * method to setup your account
     */
    private void setupAccountInstance() {
        AccountManager manager = AccountManager.get(this);
        if (AppHelper.checkPermission(this, Manifest.permission.GET_ACCOUNTS)) {
            AppHelper.LogCat("GET ACCOUNTS  permission already granted.");
        } else {
            AppHelper.LogCat("Please request GET ACCOUNTS permission.");
            AppHelper.requestPermission(this, Manifest.permission.GET_ACCOUNTS);
        }
        Account[] accounts = manager.getAccountsByType(AuthenticatorService.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            mAccount = accounts[0];
        }
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
            ContentResolver.setIsSyncable(mAccount, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(mAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, AUTHORITY, new Bundle(), SYNC_INTERVAL);

        }
    }

    private void Permissions() {
        if (AppHelper.checkPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            AppHelper.LogCat("Read contact data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            AppHelper.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }


        if (AppHelper.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            AppHelper.LogCat("Read contact data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            AppHelper.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case "startRefresh":
                toolbarProgressBar.setVisibility(View.VISIBLE);
                toolbarProgressBar.getIndeterminateDrawable().setColorFilter(AppHelper.getColor(this, R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                break;
            case "stopRefresh":
                toolbarProgressBar.setVisibility(View.GONE);
                break;
            case "startConversation":
                if (viewPager.getCurrentItem() == 1)
                    viewPager.setCurrentItem(0);
                break;
            case "actionModeStarted":
                tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorGrayDarker));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(AppHelper.getColor(this, R.color.colorGrayDarkerBar));
                }
                break;
            case "actionModeDestroyed":
                tabLayout.setBackgroundColor(AppHelper.getColor(this, R.color.colorPrimary));
                if (AppHelper.isAndroid5()) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(AppHelper.getColor(this, R.color.colorPrimaryDark));
                }
                break;
        }

    }

    /**
     * method of EventBus to show if there is internet connection or not
     *
     * @param networkChangeListener this is parameter of onEvent method
     */
    @Subscribe
    public void onEvent(NetworkChangeListener networkChangeListener) {
        if (!networkChangeListener.isUserIsConnected()) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        }

    }

    /**
     * method to disconnect from socket server
     */
    private void DisConnectFromServer() {

        try {
            JSONObject json = new JSONObject();
            try {
                json.put("connected", false);
                json.put("senderId", PreferenceManager.getID(this));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            MainService.mSocket.emit(AppConstants.SOCKET_IS_ONLINE, json);
        } catch (Exception e) {
            AppHelper.LogCat("User is offline  Exception MainActivity" + e.getMessage());
        }
    }

}