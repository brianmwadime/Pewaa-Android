package com.fortunekidew.pewaa.activities.gifts;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.api.APIGifts;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.wishlists.EditGift;
import com.fortunekidew.pewaa.models.wishlists.GiftResponse;
import com.fortunekidew.pewaa.presenters.EditGiftPresenter;
import com.fortunekidew.pewaa.ui.widget.BadgedFourThreeImageView;

import java.io.File;
import java.util.Date;

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
public class AddGiftsActivity extends AppCompatActivity implements LoadingData {

    private static final int[] NORMAL_IMAGE_SIZE = new int[] { 400, 300 };
    private final int[] TWO_X_IMAGE_SIZE = new int[] { 800, 600 };

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.giftAvatar)
    BadgedFourThreeImageView giftAvatar;

    @Bind(R.id.addGiftFab)
    FloatingActionButton addAvatar;

    @Bind(R.id.edit_gift_name)
    EditText EditName;

    @Bind(R.id.edit_gift_description)
    EditText EditDescription;

    @Bind(R.id.edit_gift_price)
    EditText EditPrice;

    private APIService mApiService;
    private String PicturePath, wishlistID;

    private EditGiftPresenter mEditGiftPresenter = new EditGiftPresenter(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("wishlistID")) {
                wishlistID = getIntent().getExtras().getString("wishlistID");
            }
        }

        ButterKnife.bind(this);
        initializerView();
        mEditGiftPresenter.onCreate();
        EventBus.getDefault().register(this);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {

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
    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        switch (pusher.getAction()) {
            case "GiftImagePath":
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

        if (EditName.getText().toString().isEmpty())
            return;

        if (EditPrice.getText().toString().isEmpty())
            return;

        final GiftResponse statusResponse = null;
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
        float newPrice = Float.parseFloat(EditPrice.getText().toString().trim());

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

                    EventBus.getDefault().post(new Pusher("new_gift", gift));
                    finish();

                } else {
                    AppHelper.CustomToast(AddGiftsActivity.this, response.message());
                }
            }

            @Override
            public void onFailure(Call<GiftResponse> call, Throwable t) {
                AppHelper.hideDialog();
                AppHelper.CustomToast(AddGiftsActivity.this, "Failed to save wishlist.");
                AppHelper.LogCat("Failed to add gift " + t.getMessage());
            }
        });
    }

    @OnClick(R.id.action_discard)
    public void onBackPress(View view) {
        onBackPressed();
    }
}

