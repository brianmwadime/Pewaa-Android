package com.fortunekidew.pewaad.activities.main;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.helpers.PermissionHandler;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.CountriesAdapter;
import com.fortunekidew.pewaad.adapters.recyclerView.TextWatcherAdapter;
import com.fortunekidew.pewaad.api.APIAuthentication;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.SignUpPreferenceManager;
import com.fortunekidew.pewaad.models.CountriesModel;
import com.fortunekidew.pewaad.models.JoinModel;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.services.SMSVerificationService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;

import retrofit2.Response;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class WelcomeActivity extends AccountAuthenticatorActivity implements View.OnClickListener {
    @BindView(R.id.numberPhone) TextInputEditText phoneNumberWrapper;
    @BindView(R.id.inputOtpWrapper) TextInputEditText inputOtpWrapper;
    @BindView(R.id.btn_request_sms) FloatingActionButton btnNext;
    @BindView(R.id.btn_verify_otp) TextView btnVerifyOtp;
    @BindView(R.id.viewPagerVertical) ViewPager viewPager;
    @BindView(R.id.TimeCount) TextView textViewShowTime;
    @BindView(R.id.Resend) TextView Resend;
    @BindView(R.id.progressbar) ProgressBar mProgressBar;
    @BindView(R.id.code) TextView code;
    @BindView(R.id.btn_change_number) TextView EditBtn;
    @BindView(R.id.toolbar) LinearLayout toolbar;
    @BindView(R.id.CounrtriesList) RecyclerView CountriesList;
    @BindView(R.id.txtEditMobile) TextView txtEditMobile;
    @BindView(R.id.search_input) TextInputEditText searchInput;
    @BindView(R.id.clear_btn_search_view) ImageView clearBtn;

    //for account manager
    private String mOldAccountType;

    private final String DEFAULT_COUNTRY = Locale.getDefault().getCountry();
    private CountriesAdapter mCountriesAdapter;
    private String Code;
    private String Country;
    private CountDownTimer countDownTimer;
    private long totalTimeCountInMilliseconds;
    private long seconds, ResumeSeconds;
    private SignUpPreferenceManager mSignUpPreferenceManager;
    public static final String PARAM_AUTH_TOKEN_TYPE = "auth.token";
    LocalBroadcastManager mLocalBroadcastManager;
    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(getPackageName() + "closeWelcomeActivity")) {
                finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        ButterKnife.bind(this);
        initializerView();
        EventBus.getDefault().register(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(getPackageName() + "closeWelcomeActivity");
        mLocalBroadcastManager.registerReceiver(mBroadcastReceiver, mIntentFilter);

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        /**
         * Checking if user already connected
         */
        if (getIntent().hasExtra(AccountManager.KEY_ACCOUNT_TYPE)) {
            mOldAccountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);
            AppHelper.LogCat("IntentData is not null WelcomeActivity " + mOldAccountType);
            CreateSyncAccount();
        } else {
            AppHelper.LogCat("IntentData is null WelcomeActivity ");
            /**
             * Checking if user already connected
             */

            if (PreferenceManager.getToken(this) != null) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        }

        initializerSearchView(searchInput, clearBtn);
        clearBtn.setOnClickListener(v -> clearSearchView());
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        CountriesList.setLayoutManager(mLinearLayoutManager);
        mCountriesAdapter = new CountriesAdapter(this);
        CountriesList.setAdapter(mCountriesAdapter);
        Gson gson = new Gson();
        final List<CountriesModel> list = gson.fromJson(AppHelper.loadJSONFromAsset(this), new TypeToken<List<CountriesModel>>() {
        }.getType());
        mCountriesAdapter.setCountries(list);
        Code = "" + list.get(1).getDial_code();
        Country = "" + list.get(1).getName();
        code.setText(Code);
        toolbar.setBackgroundColor(AppHelper.getColor(this, R.color.colorPrimary));
        btnNext.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);
        Resend.setOnClickListener(this);
        EditBtn.setOnClickListener(this);
        mSignUpPreferenceManager = new SignUpPreferenceManager(this);
        ViewPagerAdapter adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);

        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (mSignUpPreferenceManager.isWaitingForSms()) {
            viewPager.setCurrentItem(1);
            resumeTimer();
        }

        if (viewPager.getCurrentItem() == 1) {

            if (PermissionHandler.checkPermission(this, Manifest.permission.RECEIVE_SMS)) {
                AppHelper.LogCat("RECEIVE SMS permission already granted.");
            } else {
                AppHelper.LogCat("Please request RECEIVE SMS permission.");
                PermissionHandler.requestPermission(this, Manifest.permission.RECEIVE_SMS);
            }
            if (PermissionHandler.checkPermission(this, Manifest.permission.READ_SMS)) {
                AppHelper.LogCat("READ SMS permission already granted.");
            } else {
                AppHelper.LogCat("Please request READ SMS permission.");
                PermissionHandler.requestPermission(this, Manifest.permission.READ_SMS);
            }

        }
    }

    /**
     * Create a new  account for the sync adapter
     */
    public Account CreateSyncAccount() {
        if (mOldAccountType != null) {
            // Create the account type and default account
            Account newAccount = new Account(getString(R.string.app_name), mOldAccountType);
            // Get an instance of the Android account manager
            AccountManager accountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);

            if (!accountManager.addAccountExplicitly(newAccount, null, null)) {
                AppHelper.CustomToast(this, getString(R.string.app_name) + getString(R.string.account_added_already));
                finish();

            }
            return newAccount;
        } else {
            return null;
        }

    }

    /**
     * method to clear/reset search view content
     */
    public void clearSearchView() {
        if (searchInput.getText() != null) {
            searchInput.setText("");
            Gson gson = new Gson();
            final List<CountriesModel> list = gson.fromJson(AppHelper.loadJSONFromAsset(this), new TypeToken<List<CountriesModel>>() {
            }.getType());
            mCountriesAdapter.setCountries(list);
        }
    }

    /**
     * method to initial the search view
     */
    public void initializerSearchView(TextInputEditText searchInput, ImageView clearSearchBtn) {

        final Context context = this;
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });
        searchInput.addTextChangedListener(new TextWatcherAdapter() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clearSearchBtn.setVisibility(View.GONE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCountriesAdapter.setString(s.toString());
                Search(s.toString().trim());
                clearSearchBtn.setVisibility(View.VISIBLE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    clearSearchBtn.setVisibility(View.GONE);
                    Gson gson = new Gson();
                    final List<CountriesModel> list = gson.fromJson(AppHelper.loadJSONFromAsset(WelcomeActivity.this), new TypeToken<List<CountriesModel>>() {
                    }.getType());
                    mCountriesAdapter.setCountries(list);
                }
            }
        });
    }

    /**
     * method to start searching
     *
     * @param string this is parameter of Search method
     */
    public void Search(String string) {

        final List<CountriesModel> filteredModelList;
        filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {
            mCountriesAdapter.animateTo(filteredModelList);
            CountriesList.scrollToPosition(0);
        }
    }

    /**
     * method to filter the list
     *
     * @param query this is parameter of FilterList method
     * @return this for what method return
     */
    private List<CountriesModel> FilterList(String query) {
        query = query.toLowerCase();
        List<CountriesModel> countriesModelList = mCountriesAdapter.getCountries();
        final List<CountriesModel> filteredModelList = new ArrayList<>();
        for (CountriesModel countriesModel : countriesModelList) {
            final String name = countriesModel.getName().toLowerCase();
            if (name.contains(query)) {
                filteredModelList.add(countriesModel);
            }
        }
        return filteredModelList;
    }

    /**
     * method to validate user information
     */
    private void validateInformation() {
        String mobile = null;
        try {
            mobile = phoneNumberWrapper.getText().toString().trim();
        } catch (Exception e) {
            AppHelper.LogCat(" number mobile is null Exception WelcomeActivity " + e.getMessage());
        }
        if (mobile != null) {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber phNumberProto = null;
            Code = Code.replace("+", "");
            String countryCode = phoneUtil.getRegionCodeForCountryCode(Integer.parseInt(Code));
            try {
                phNumberProto = phoneUtil.parse(mobile, countryCode);
            } catch (NumberParseException e) {
                AppHelper.LogCat("number  error  NumberParseException  WelcomeActivity" + e.getMessage());
                phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
            }
            if (phNumberProto != null) {
                boolean isValid = phoneUtil.isValidNumber(phNumberProto);
                if (isValid) {
                    String internationalFormat = phoneUtil.format(phNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
                    mSignUpPreferenceManager.setMobileNumber(internationalFormat);
                    requestForSMS(internationalFormat, Country);
                } else {
                    phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
                }
            }
        } else {
            phoneNumberWrapper.setError(getString(R.string.enter_a_val_number));
        }
    }

    /**
     * method to resend a request for SMS
     *
     * @param mobile this is parameter of ResendRequestForSMS method
     */
    private void ResendRequestForSMS(String mobile) {

        APIAuthentication mAPIAuthentication = APIService.RootService(APIAuthentication.class, EndPoints.BASE_URL);
        Call<JoinModel> ResendModelCall = mAPIAuthentication.resend(mobile);
        ResendModelCall.enqueue(new Callback<JoinModel>() {
            @Override
            public void onResponse(Call<JoinModel> call, Response<JoinModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        Resend.setVisibility(View.GONE);
                        mProgressBar.setVisibility(View.VISIBLE);
                        textViewShowTime.setVisibility(View.VISIBLE);
                        setTimer();
                        startTimer();
                        mSignUpPreferenceManager.setIsWaitingForSms(true);
                        viewPager.setCurrentItem(1);
                        txtEditMobile.setText(mSignUpPreferenceManager.getMobileNumber());
                    } else {
                        AppHelper.CustomToast(WelcomeActivity.this, response.body().getMessage());
                    }
                } else {
                    AppHelper.CustomToast(WelcomeActivity.this, response.message());
                }
            }

            @Override
            public void onFailure(Call<JoinModel> call, Throwable t) {
                AppHelper.CustomToast(WelcomeActivity.this, t.getMessage());
            }
        });
    }

    /**
     * method to send an SMS request to provider
     *
     * @param mobile  this the first parameter of  requestForSMS method
     * @param country this the second parameter of requestForSMS  method
     */
    private void requestForSMS(String mobile, String country) {
        APIAuthentication mAPIAuthentication = APIService.RootService(APIAuthentication.class, EndPoints.BASE_URL);
        Call<JoinModel> JoinModelCall = mAPIAuthentication.join(mobile, country);
        AppHelper.showDialog(this, getString(R.string.sms_verification));
        JoinModelCall.enqueue(new Callback<JoinModel>() {
            @Override
            public void onResponse(Call<JoinModel> call, Response<JoinModel> response) {
                if (response.isSuccessful()) {
                    if (response.body().isSuccess()) {
                        AppHelper.hideDialog();
                        String accountType = AppConstants.ACCOUNT_TYPE;
                        AccountManager accountManager = AccountManager.get(WelcomeActivity.this);

                        // This is the magic that add the account to the Android Account Manager
                        final Account account = new Account(getResources().getString(R.string.app_name), accountType);
                        accountManager.addAccountExplicitly(account, PreferenceManager.getMobileNumber(WelcomeActivity.this), null);

                        // Now we tell our caller, could be the Android Account Manager or even our own application
                        // that the process was successful
                        final Intent intent = new Intent();
                        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, getResources().getString(R.string.app_name));
                        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
                        setAccountAuthenticatorResult(intent.getExtras());
                        setResult(RESULT_OK, intent);

                        if (!response.body().isSmsVerification()) {
                            PreferenceManager.setIsWaitingForSms(WelcomeActivity.this, false);
                            txtEditMobile.setText(PreferenceManager.getMobileNumber(WelcomeActivity.this));
                            smsVerification(response.body().getCode());
                        } else {
                            setTimer();
                            startTimer();
                            PreferenceManager.setIsWaitingForSms(WelcomeActivity.this, true);
                            viewPager.setCurrentItem(1);
                            txtEditMobile.setText(PreferenceManager.getMobileNumber(WelcomeActivity.this));
                        }

                    } else {
                        AppHelper.hideDialog();
                        AppHelper.CustomToast(WelcomeActivity.this, response.body().getMessage());
                    }
                } else {
                    AppHelper.hideDialog();
                    AppHelper.CustomToast(WelcomeActivity.this, response.message());
                }
            }

            @Override
            public void onFailure(Call<JoinModel> call, Throwable t) {
                AppHelper.hideDialog();
                AppHelper.LogCat("Failed to create your account " + t.getMessage());
                AppHelper.CustomToast(WelcomeActivity.this, getString(R.string.unexpected_reponse_from_server));
                hideKeyboard();
            }
        });

    }

    /**
     * this if you disabled verification by sms
     * @param code
     */
    private void smsVerification(String code) {
        if (!code.isEmpty()) {
            Intent otpIntent = new Intent(getApplicationContext(), SMSVerificationService.class);
            otpIntent.putExtra("code", code);
            startService(otpIntent);
        } else {
            AppHelper.CustomToast(this, getString(R.string.please_enter_your_ver_code));
        }
    }

    /**
     * Hide keyboard from phoneEdit field
     */
    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(phoneNumberWrapper.getWindowToken(), 0);
    }

    /**
     * method to verify the code received by user then activating the user
     */
    private void verificationOfCode() {
        String code = inputOtpWrapper.getText().toString().trim();
        if (!code.isEmpty()) {
            Intent otpIntent = new Intent(getApplicationContext(), SMSVerificationService.class);
            otpIntent.putExtra("code", code);
            startService(otpIntent);
        } else {
            AppHelper.CustomToast(this, getString(R.string.please_enter_your_ver_code));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_sms:
                validateInformation();
                break;

            case R.id.btn_verify_otp:
                verificationOfCode();
                break;

            case R.id.btn_change_number:
                viewPager.setCurrentItem(0);
                stopTimer();
                PreferenceManager.setID(String.valueOf(0), this);
                PreferenceManager.setToken(null, this);
                mSignUpPreferenceManager.setIsWaitingForSms(false);
                break;

            case R.id.Resend:
                viewPager.setCurrentItem(1);
                ResendRequestForSMS(mSignUpPreferenceManager.getMobileNumber());
                break;
        }
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.numberPhone_layout;
                    break;
                case 1:
                    resId = R.id.layout_verification;
                    break;
            }
            return findViewById(resId);
        }
    }

    private void setTimer() {
        int time = 4;
        mProgressBar.setMax(60 * time);
        totalTimeCountInMilliseconds = 60 * time * 1000;

    }

    private void startTimer() {
        countDownTimer = new WhatsCloneCounter(totalTimeCountInMilliseconds, 500).start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void resumeTimer() {
        countDownTimer = new WhatsCloneCounter(ResumeSeconds, 500).start();
    }


    public class WhatsCloneCounter extends CountDownTimer {
        public WhatsCloneCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long leftTimeInMilliseconds) {
            ResumeSeconds = leftTimeInMilliseconds;
            seconds = leftTimeInMilliseconds / 1000;
            mProgressBar.setProgress((int) (leftTimeInMilliseconds / 1000));
            textViewShowTime.setText(String.format("%02d", seconds / 60) + ":" + String.format("%02d", seconds % 60));
        }

        @Override
        public void onFinish() {
            textViewShowTime.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            Resend.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @Subscribe
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case "countryCode":
                Code = "" + pusher.getData();
                code.setText(Code);
                break;
            case "countryName":
                Country = "" + pusher.getData();
                break;
        }
    }

}
