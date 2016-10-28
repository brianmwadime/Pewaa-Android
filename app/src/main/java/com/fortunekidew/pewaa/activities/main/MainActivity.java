package com.fortunekidew.pewaa.activities.main;

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

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.activities.search.SearchContactsActivity;
import com.fortunekidew.pewaa.activities.settings.SettingsActivity;
import com.fortunekidew.pewaa.activities.status.StatusActivity;
import com.fortunekidew.pewaa.activities.wishlists.AddWishlistsActivity;
import com.fortunekidew.pewaa.adapters.others.TabsAdapter;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.receivers.NetworkChangeListener;
import com.fortunekidew.pewaa.services.MainService;
import com.fortunekidew.pewaa.sync.AuthenticatorService;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Email : mwadime@fortunekidew.co.ke
 */
public class MainActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.main_activity)
    View mView;
    @Bind(R.id.tabs)
    TabLayout tabLayout;
    @Bind(R.id.app_bar)
    Toolbar toolbar;
//    @Bind(R.id.main_view)
//    LinearLayout MainView;
    @Bind(R.id.addWishlistFab)
    FloatingActionButton AddWishlistFab;
    @Bind(R.id.toolbar_progress_bar)
    ProgressBar toolbarProgressBar;
    // authority for sync adapter's content provider
    private static final String AUTHORITY = "com.android.contacts";
    // sync interval
    private static final long SYNC_INTERVAL = 10;
    // Account type and auth token type
    public static final String ACCOUNT_TYPE = "com.fortunekidew.pewaa";
    private Account mAccount;
//    InterstitialAd mInterstitialAd;

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

        loadCounter();
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
            case "MessagesCounter":
                loadCounter();
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
    @SuppressWarnings("unused")
    public void onEvent(NetworkChangeListener networkChangeListener) {
        if (!networkChangeListener.isUserIsConnected()) {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_not_available), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
        } else {
            AppHelper.Snackbar(this, mView, getString(R.string.connection_is_available), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
        }

    }

    /**
     * methdo to load number of unread gifts
     */
    private void loadCounter() {
//        Realm realm = Realm.getDefaultInstance();
//        List<WishlistsModel> wishlistsModel1 = realm.where(WishlistsModel.class)
//                .equalTo("Status", AppConstants.IS_WAITING)
//                .equalTo("RecipientID", PreferenceManager.getID(this))
//                .findAll();
//        if (wishlistsModel1.size() == 0) {
//            findViewById(R.id.counterTabMessages).setVisibility(View.GONE);
//        } else {
//            findViewById(R.id.counterTabMessages).setVisibility(View.VISIBLE);
//            ((TextView) findViewById(R.id.counterTabMessages)).setText(String.valueOf(wishlistsModel1.size()));
//
//        }
//        realm.close();
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