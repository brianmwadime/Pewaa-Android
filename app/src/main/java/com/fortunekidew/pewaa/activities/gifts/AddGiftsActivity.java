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
import android.widget.ImageView;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.fragments.BottomSheetEditGift;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.presenters.EditGiftPresenter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


/**
 * Created by Brian Mwakima on 17/10/2016.
 * Email : mwadime@fortunekidew.co.ke
 */

@SuppressLint("SetTextI18n")
public class AddGiftsActivity extends AppCompatActivity implements LoadingData {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.giftAvatar)
    ImageView giftAvatar;

    @Bind(R.id.addGiftFab)
    FloatingActionButton addAvatar;

    @Bind(R.id.edit_gift_name)
    EditText EditName;

    @Bind(R.id.edit_gift_description)
    EditText EditDescription;

    @Bind(R.id.edit_gift_price)
    EditText EditPrice;

    private APIService mApiService;
    private String PicturePath;

    private EditGiftPresenter mEditGiftPresenter = new EditGiftPresenter(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gift);
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
            Picasso.with(this)
                    .load(path)
//                    .transform(new CropSquareTransformation())
                    .networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
//                    .centerCrop()
                    .into(giftAvatar);
        } else {
            giftAvatar.setPadding(2, 2, 2, 2);
            giftAvatar.setImageResource(R.drawable.ic_file_download_white_24dp);

        }
//        new EditProfileActivity.UploadFileToServer().execute();

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
        String newName = EditName.getText().toString().trim();
        String newDescription = EditDescription.getText().toString().trim();
        try {
            mEditGiftPresenter.EditGift(newName, newDescription);
        } catch (Exception e) {
            AppHelper.LogCat("Edit  name  Exception " + e.getMessage());
        }
    }

    @OnClick(R.id.action_discard)
    public void onBackPress(View view) {
        onBackPressed();
    }
}

