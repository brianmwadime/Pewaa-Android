package com.fortunekidew.pewaad.activities.gifts;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import butterknife.OnCheckedChanged;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIPayments;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.DefaultResponse;
import com.fortunekidew.pewaad.models.payments.EditPayments;
import com.fortunekidew.pewaad.models.payments.RequestPaymentResponse;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.gifts.GiftsModel;
import com.google.gson.Gson;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.parceler.Parcels;
import java.io.IOException;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

@SuppressLint("SetTextI18n")
public class ContributeActivity extends AppCompatActivity implements LoadingData {

    public static final String EXTRA_GIFT = "EXTRA_GIFT";

    private APIService mApiService;
    @BindView(R.id.ContributeParentLayout)
    RelativeLayout contributeParentLayout;
    @BindView(R.id.amount)
    EditText EditAmount;
    @BindView(R.id.edit_amount_wrapper)
    TextInputLayout amount_wrapper;
    private GiftsModel gift;
    private Boolean is_anonymous = false;

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

        // Minus the contributed amount from the gift price
        EditAmount.setOnFocusChangeListener((v, hasFocus) -> amount_wrapper.setErrorEnabled(false));

        EditAmount.setText(String.valueOf(gift.getPrice() - gift.getContributed()));

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_contribute));

        }

        mApiService = new APIService(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            //setResultAndFinish();
            this.onBackPressed();
        }

        return true;
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
        AppHelper.LogCat("Contribute Activity " + throwable.getMessage());
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

    @OnCheckedChanged(R.id.is_anonymous)
    public void isAnonymous(CompoundButton button, boolean checked) {
        is_anonymous = checked;
    }

    @OnClick(R.id.contribute)
    public void contribute(View view) {

        if (EditAmount.getText().toString().isEmpty()){
            amount_wrapper.setError("Please enter an amount");
            return;
        }

        // Hide keyboard
        final InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(EditAmount.getWindowToken(), 0);

        double newPrice = Double.parseDouble(EditAmount.getText().toString().trim());

        // If entered amount is greater than the gift price
        if(newPrice > gift.getPrice()) {
            amount_wrapper.setError("Amount entered is greater than the gift price.");
            return;
        }

        if((gift.getContributed() + newPrice) > gift.getPrice()) {
            amount_wrapper.setError("Amount entered exceeds the total gift price.");
            return;
        }

        String referenceID = gift.getName();

        APIPayments mAPIPayments = mApiService.RootService(APIPayments.class, PreferenceManager.getToken(ContributeActivity.this), EndPoints.BASE_URL);

        ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, "Generating payment request ... "));
        // Create payment object
        EditPayments payment = new EditPayments();
        payment.clientLocation = "";
        payment.clientName  = PreferenceManager.getNumber(ContributeActivity.this);
        payment.referenceID = referenceID;
        payment.totalAmount = (int) Math.floor(newPrice);
        payment.phoneNumber = PreferenceManager.getNumber(ContributeActivity.this).replaceAll("\\D", "");
        payment.userId      = PreferenceManager.getID(ContributeActivity.this);

        Call<RequestPaymentResponse> requestPayment = mAPIPayments.requestPayment(payment);
        requestPayment.enqueue(new Callback<RequestPaymentResponse>() {
            @Override
            public void onResponse(Call<RequestPaymentResponse> call, Response<RequestPaymentResponse> response) {
                AppHelper.hideDialog();

                if(response.isSuccessful()) {
                    payment.trxId = response.body().response.trx_id;
                    ContributeActivity.this.runOnUiThread(() -> AppHelper.showDialog(ContributeActivity.this, response.body().response.cust_msg));
                    Call<DefaultResponse> statusResponseCall = mAPIPayments.createPayment(newPrice, gift.getId(), referenceID, AppConstants.PENDING_STATUS, gift.getDescription(), payment.trxId, is_anonymous);
                    statusResponseCall.enqueue(new Callback<DefaultResponse>() {
                        @Override
                        public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                            AppHelper.hideDialog();
                            if (response.isSuccessful()) {

                                AppHelper.hideDialog();
                                finish();

                            } else {
                                AppHelper.CustomToast(ContributeActivity.this, response.message());
                            }
                        }

                        @Override
                        public void onFailure(Call<DefaultResponse> call, Throwable t) {
                            AppHelper.hideDialog();
                            AppHelper.CustomToast(ContributeActivity.this, "Failed to add contribution. Please try again later.");
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

