package com.fortunekidew.pewaad.activities.gifts;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIGifts;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.gifts.EditGift;
import com.fortunekidew.pewaad.models.gifts.GiftResponse;
import com.fortunekidew.pewaad.presenters.EditGiftPresenter;
import com.fortunekidew.pewaad.ui.widget.BadgedFourThreeImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import okhttp3.MediaType;
import okhttp3.RequestBody;
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
public class AddGiftsActivity extends AppCompatActivity implements LoadingData {

    private static final int[] NORMAL_IMAGE_SIZE = new int[] { 400, 300 };
    private final int[] TWO_X_IMAGE_SIZE = new int[] { 800, 600 };

    public final static String RESULT_EXTRA_GIFT_ID = "RESULT_EXTRA_GIFT_ID";

    @BindView(R.id.ParentLayoutAddGift)
    CoordinatorLayout parent;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.giftAvatar)
    BadgedFourThreeImageView giftAvatar;

    @BindView(R.id.addGiftFab)
    FloatingActionButton addAvatar;

    @BindView(R.id.edit_gift_name)
    EditText EditName;

    @BindView(R.id.edit_gift_description)
    EditText EditDescription;

    @BindView(R.id.edit_price_wrapper)
    TextInputLayout price_wrapper;

    @BindView(R.id.edit_name_wrapper)
    TextInputLayout name_wrapper;

    @BindView(R.id.edit_description_wrapper)
    TextInputLayout description_wrapper;

    @BindView(R.id.edit_gift_price)
    EditText EditPrice;
    private APIService mApiService;
    private String PicturePath, wishlistID;
    private EditGiftPresenter mEditGiftPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);
        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(RESULT_EXTRA_GIFT_ID)) {
                wishlistID = getIntent().getExtras().getString(RESULT_EXTRA_GIFT_ID);
            }
        }

        ButterKnife.bind(this);
        mEditGiftPresenter = new EditGiftPresenter(this);
        initializerView();
        EventBus.getDefault().register(this);

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        mEditGiftPresenter.onCreate();

        EditPrice.setOnFocusChangeListener((v, hasFocus) -> price_wrapper.setErrorEnabled(false));
        EditName.setOnFocusChangeListener((v, hasFocus) -> name_wrapper.setErrorEnabled(false));
        EditDescription.setOnFocusChangeListener((v, hasFocus) -> description_wrapper.setErrorEnabled(false));

        EditDescription.setImeOptions(EditorInfo.IME_ACTION_DONE);
        EditDescription.setRawInputType(InputType.TYPE_CLASS_TEXT);

        addAvatar.setOnClickListener(v -> {
            BottomSheetEditGift bottomSheetEditGift = new BottomSheetEditGift();
            bottomSheetEditGift.show(getSupportFragmentManager(), bottomSheetEditGift.getTag());
        });
        mApiService = new APIService(this);
    }

    /**
     * method to setup the image
     *
     * @param path this is parameter for setImage method
     */
    public void setImage(String path) {
        if (path != null) {
            Glide.with(this)
                    .load(path)
                    .fitCenter()
                    .override(NORMAL_IMAGE_SIZE[0], NORMAL_IMAGE_SIZE[1])
                    .into(giftAvatar);
        } else {
            giftAvatar.setPadding(2, 2, 2, 2);
            giftAvatar.setImageResource(R.drawable.ic_file_download_white_24dp);

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditGiftPresenter.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

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
            case AppConstants.EVENT_BUS_IMAGE_GIFT_PATH:
                PicturePath = pusher.getData();
                new Handler().postDelayed(() -> setImage(pusher.getData()), 500);
                break;
        }
    }

    private int convertToDp(float value) {
        return (int) Math.ceil(1 * value);
    }

    @OnClick(R.id.action_save)
    public void saveGift(View view) {

        if (EditName.getText().toString().isEmpty()){
            name_wrapper.setError("Please enter your gift name.");
            return;
        }

        if (EditPrice.getText().toString().isEmpty()) {
            price_wrapper.setError("Please enter your gift price.");
            return;
        }

        double price = Double.parseDouble(EditPrice.getText().toString().trim());

        if(price < 2000){
            price_wrapper.setError("The minimum gift price is Kes. 2000");
            return;
        }


        if (EditDescription.getText().toString().isEmpty()) {
            description_wrapper.setError("Please enter a description.");
            return;
        }


        RequestBody requestFile;

        if (PicturePath != null) {
            // use the FileUtils to get the actual file by uri
            File file = new File(PicturePath);
            // create RequestBody instance from file
            requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        } else {
            requestFile = null;
        }

        RequestBody newName = RequestBody.create(MediaType.parse("multipart/form-data"), EditName.getText().toString().trim());
        RequestBody newDescription = RequestBody.create(MediaType.parse("multipart/form-data"), EditDescription.getText().toString().trim());
        double newPrice = Double.parseDouble(EditPrice.getText().toString().trim());

        APIGifts mApiGift = mApiService.RootService(APIGifts.class, PreferenceManager.getToken(AddGiftsActivity.this), EndPoints.BASE_URL);
        AddGiftsActivity.this.runOnUiThread(() -> AppHelper.showDialog(AddGiftsActivity.this, "Adding ... "));
        RequestBody requestID = RequestBody.create(MediaType.parse("multipart/form-data"), wishlistID);
        Call<GiftResponse> statusResponseCall = mApiGift.editGift(requestID, newName, requestFile, newDescription, newPrice);
        statusResponseCall.enqueue(new Callback<GiftResponse>() {
            @Override
            public void onResponse(Call<GiftResponse> call, Response<GiftResponse> response) {
                AppHelper.hideDialog();
                if (response.isSuccessful()) {
                    EditGift gift = new EditGift();
                    gift.setAvatar(response.body().getAvatar());
                    gift.setName(response.body().getName());
                    gift.setId(response.body().getId());
                    gift.setCreatedOn(new Date());
                    gift.setWishlistId(response.body().getWishlistId());
                    gift.setDescription(response.body().getDescription());
                    gift.setPrice(response.body().getPrice());
                    gift.setContributed(0);
                    gift.setCashout_status(null);
                    gift.setCreator_phone(PreferenceManager.getPhone(PewaaApplication.getAppContext()));

                    EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_NEW_GIFT, gift));
                    finish();

                } else {
                    Snackbar snackbar = Snackbar.make(parent, response.message(), Snackbar.LENGTH_SHORT);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundColor(Color.RED);
                }

            }

            @Override
            public void onFailure(Call<GiftResponse> call, Throwable t) {
                AppHelper.hideDialog();
                Snackbar snackbar = Snackbar.make(parent, t.getMessage(), Snackbar.LENGTH_SHORT);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(Color.RED);
                snackbar.show();
            }
        });
    }

    @OnClick(R.id.action_discard)
    public void onBackPress(View view) {
        onBackPressed();
    }
}

