package com.fortunekidew.pewaad.activities.gifts;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.wishlists.TransferMessageContactsAdapter;
import com.fortunekidew.pewaad.api.APIGifts;
import com.fortunekidew.pewaad.api.APIPayments;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.SignUpPreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.payments.ConfirmPaymentResponse;
import com.fortunekidew.pewaad.models.payments.EditPayments;
import com.fortunekidew.pewaad.models.payments.PaymentRequest;
import com.fortunekidew.pewaad.models.payments.PaymentResponse;
import com.fortunekidew.pewaad.models.payments.RequestPaymentResponse;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.wishlists.EditGift;
import com.fortunekidew.pewaad.models.wishlists.GiftResponse;
import com.fortunekidew.pewaad.models.wishlists.GiftsModel;
import com.fortunekidew.pewaad.presenters.EditGiftPresenter;
import com.fortunekidew.pewaad.ui.widget.BadgedFourThreeImageView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Brian Mwakima on 17/10/2016.
 * Email : mwadime@fortunekidew.co.ke
 */

@SuppressLint("SetTextI18n")
public class ContributeActivity extends AppCompatActivity implements LoadingData {

    public static final String EXTRA_GIFT = "EXTRA_GIFT";

    private APIService mApiService;
    @BindView(R.id.ContributeParentLayout)
    RelativeLayout contributeParentLayout;
    @BindView(R.id.amount)
    EditText EditAmount;

    private SignUpPreferenceManager mSignUpPreferenceManager;
    private GiftsModel gift;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribute);

        if (getIntent().hasExtra(EXTRA_GIFT)) {
            gift = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_GIFT));
        }

        ButterKnife.bind(this);
        initializeView();
        EventBus.getDefault().register(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    /**
     * method to initialize the view
     */
    private void initializeView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);


        EditAmount.setText(String.valueOf(gift.getPrice()));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_contributor));

        }

        mSignUpPreferenceManager = new SignUpPreferenceManager(this);
        mApiService = new APIService(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
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

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    private void setResultAndFinish() {
        finishAfterTransition();
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Wishlists " + throwable.getMessage());
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @Subscribe
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case "GiftImagePath":

                break;
        }
    }

    private int convertToDp(float value) {
        return (int) Math.ceil(1 * value);
    }

    @OnClick(R.id.contribute)
    public void contribute(View view) {

        if (EditAmount.getText().toString().isEmpty())
            return;

        // Hide keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(EditAmount.getWindowToken(), 0);


        double newPrice = Double.parseDouble(EditAmount.getText().toString().trim());

        String referenceID = UUID.fromString(gift.getId()).toString();

        APIPayments mAPIPayments = mApiService.RootService(APIPayments.class, PreferenceManager.getToken(ContributeActivity.this), EndPoints.BASE_URL);

        ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, "Generating payment request ... "));
        // Create payment object
        EditPayments payment = new EditPayments();
        payment.clientLocation = "";
        payment.clientName = mSignUpPreferenceManager.getMobileNumber();
        payment.referenceID = referenceID;
        payment.totalAmount = newPrice;
        payment.phoneNumber = mSignUpPreferenceManager.getMobileNumber().replaceAll("\\D", "");

        Call<RequestPaymentResponse> requestPayment = mAPIPayments.requestPayment(payment);
        requestPayment.enqueue(new Callback<RequestPaymentResponse>() {
            @Override
            public void onResponse(Call<RequestPaymentResponse> call, Response<RequestPaymentResponse> response) {
                AppHelper.hideDialog();

                if(response.isSuccessful()) {
                    payment.trxId = response.body().response.trx_id;
                    ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, response.body().response.cust_msg));
                    Call<PaymentResponse> statusResponseCall = mAPIPayments.createPayment(newPrice, gift.getId(), referenceID, "PENDING", gift.getDescription(), payment.trxId);
                    statusResponseCall.enqueue(new Callback<PaymentResponse>() {
                        @Override
                        public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                            AppHelper.hideDialog();
                            if (response.isSuccessful()) {

                                ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, "Processing your payment ... "));
                                Call<ConfirmPaymentResponse> confirmPayment = mAPIPayments.confirmPayment(payment.trxId);
                                confirmPayment.enqueue(new Callback<ConfirmPaymentResponse>() {
                                    @Override
                                    public void onResponse(Call<ConfirmPaymentResponse> call, Response<ConfirmPaymentResponse> response) {
                                        AppHelper.hideDialog();

                                        finish();
                                    }

                                    @Override
                                    public void onFailure(Call<ConfirmPaymentResponse> call, Throwable t) {
                                        AppHelper.hideDialog();
                                        AppHelper.CustomToast(ContributeActivity.this, "Failed to request payment.");
                                        AppHelper.LogCat("Failed to request payment " + t.getMessage());
                                    }
                                });

                            } else {
                                AppHelper.CustomToast(ContributeActivity.this, response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<PaymentResponse> call, Throwable t) {
                            AppHelper.hideDialog();
                            AppHelper.CustomToast(ContributeActivity.this, "Failed to add contribution.");
                            AppHelper.LogCat("Failed to add gift " + t.getMessage());
                        }
                    });
                } else if (response.errorBody() != null) {

                    Gson gson = new Gson();
                    final RequestPaymentResponse failedPayment;
                    try {
                        failedPayment = gson.fromJson(response.errorBody().string(), RequestPaymentResponse.class);
                        AppHelper.Snackbar(getApplicationContext(), contributeParentLayout, failedPayment.response.message, AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                    } catch (IOException e) {
                        AppHelper.Snackbar(getApplicationContext(), contributeParentLayout, "Could not complete Payment. Please try again.", AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                    }

                    return;
                }
            }

            @Override
            public void onFailure(Call<RequestPaymentResponse> call, Throwable t) {
                AppHelper.hideDialog();
                AppHelper.Snackbar(getApplicationContext(), contributeParentLayout, "Failed to request payment.", AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
                AppHelper.LogCat("Failed to request payment " + t.getMessage());
            }
        });

    }

}

