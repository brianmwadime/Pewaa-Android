package com.fortunekidew.pewaa.activities.gifts;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.wishlists.TransferMessageContactsAdapter;
import com.fortunekidew.pewaa.api.APIGifts;
import com.fortunekidew.pewaa.api.APIPayments;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.payments.ConfirmPaymentResponse;
import com.fortunekidew.pewaa.models.payments.EditPayments;
import com.fortunekidew.pewaa.models.payments.PaymentRequest;
import com.fortunekidew.pewaa.models.payments.PaymentResponse;
import com.fortunekidew.pewaa.models.payments.RequestPaymentResponse;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.wishlists.EditGift;
import com.fortunekidew.pewaa.models.wishlists.GiftResponse;
import com.fortunekidew.pewaa.models.wishlists.GiftsModel;
import com.fortunekidew.pewaa.presenters.EditGiftPresenter;
import com.fortunekidew.pewaa.ui.widget.BadgedFourThreeImageView;

import org.parceler.Parcels;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
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
    @Bind(R.id.amount)
    EditText EditAmount;

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
    @SuppressWarnings("unused")
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


        double newPrice = Double.parseDouble(EditAmount.getText().toString().trim());

        String referenceID = UUID.fromString(gift.getId()).toString();

        APIPayments mAPIPayments = mApiService.RootService(APIPayments.class, PreferenceManager.getToken(ContributeActivity.this), EndPoints.BASE_URL);
        ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, "Sending your payment ... "));
        Call<PaymentResponse> statusResponseCall = mAPIPayments.createPayment(newPrice, gift.getId(), referenceID, "PENDING", gift.getDescription());
        statusResponseCall.enqueue(new Callback<PaymentResponse>() {
            @Override
            public void onResponse(Call<PaymentResponse> call, Response<PaymentResponse> response) {
                AppHelper.hideDialog();
                if (response.isSuccessful()) {

                    ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, "Generating payment request ... "));
                    EditPayments payment = new EditPayments();
                    payment.clientLocation = "";
                    payment.clientName = "Brian Mwadime";
                    payment.referenceID = referenceID;
                    payment.totalAmount = newPrice;
                    payment.phoneNumber = "254728956895";

                    Call<RequestPaymentResponse> requestPayment = mAPIPayments.requestPayment(payment);
                    requestPayment.enqueue(new Callback<RequestPaymentResponse>() {
                        @Override
                        public void onResponse(Call<RequestPaymentResponse> call, Response<RequestPaymentResponse> response) {
                            AppHelper.hideDialog();

                            ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, "Confirm payment ... "));
                            Call<ConfirmPaymentResponse> confirmPayment = mAPIPayments.confirmPayment(response.body().response.trx_id);
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
                        }

                        @Override
                        public void onFailure(Call<RequestPaymentResponse> call, Throwable t) {
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
    }

}

