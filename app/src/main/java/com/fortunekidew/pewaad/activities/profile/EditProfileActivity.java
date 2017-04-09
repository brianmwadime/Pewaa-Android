package com.fortunekidew.pewaad.activities.profile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.SharedElementCallback;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.status.StatusActivity;
import com.fortunekidew.pewaad.api.APIContact;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.fragments.BottomSheetEditProfile;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.presenters.EditProfilePresenter;
import com.fortunekidew.pewaad.ui.CropSquareTransformation;
import com.fortunekidew.pewaad.util.glide.CircleTransform;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.helpers.UtilsString.unescapeJavaString;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class EditProfileActivity extends AppCompatActivity implements LoadingData {

    @BindView(R.id.userAvatar)
    ImageView userAvatar;
    @BindView(R.id.addAvatar)
    FloatingActionButton addAvatar;
    @BindView(R.id.wishlist_name)
    TextView username;
    @BindView(R.id.numberPhone)
    TextView numberPhone;
    @BindDimen(R.dimen.large_avatar_height) int largeAvatarSize;
    private ContactsModel mContactsModel;
    private EditProfilePresenter mEditProfilePresenter = new EditProfilePresenter(this);
    private APIService mApiService;
    private String PicturePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        initializerView();
        mEditProfilePresenter.onCreate();
        ActivityCompat.setEnterSharedElementCallback(this, new SharedElementCallback() {
            @Override
            public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                addAvatar.setVisibility(View.GONE);
                final Animation animation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.scale_for_button_animtion_enter);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        addAvatar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                addAvatar.startAnimation(animation);

            }
        });
        EventBus.getDefault().register(this);
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        addAvatar.setOnClickListener(v -> {
            BottomSheetEditProfile bottomSheetEditProfile = new BottomSheetEditProfile();
            bottomSheetEditProfile.show(getSupportFragmentManager(), bottomSheetEditProfile.getTag());
        });
        mApiService = new APIService(this);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.editUsernameBtn)
    public void launchEditUsername() {
        Intent mIntent = new Intent(this, EditUsernameActivity.class);
        mIntent.putExtra("currentUsername", mContactsModel.getName());
        startActivity(mIntent);
    }

    /**
     * method to show contact info
     *
     * @param mContactsModel this is parameter for ShowContact  method
     */
    public void ShowContact(ContactsModel mContactsModel) {
        this.mContactsModel = mContactsModel;
        if (mContactsModel.getPhone() != null) {
            numberPhone.setText(mContactsModel.getPhone());
        }
        if (mContactsModel.getName() != null) {
            username.setText(mContactsModel.getName());
        } else {
            username.setText(getString(R.string.no_username));
        }
        if (mContactsModel.getImage() != null) {
            Glide.with(this)
                    .load(EndPoints.ASSETS_BASE_URL + mContactsModel.getImage())
                    .transform(new CircleTransform(this))
                    .placeholder(R.drawable.avatar_placeholder)
                    .override(largeAvatarSize, largeAvatarSize)
                    .into(userAvatar);
//            if (FilesManager.isFileImagesProfileExists(FilesManager.getProfileImage(String.valueOf(mContactsModel.getId()), mContactsModel.getId()))) {
////                Picasso.with(this)
////                        .load(FilesManager.getFileImageProfile(String.valueOf(mContactsModel.getId()), mContactsModel.getId()))
////                        .transform(new CropSquareTransformation())
////                        .resize(200, 200)
////                        .networkPolicy(NetworkPolicy.NO_CACHE)
////                        .memoryPolicy(MemoryPolicy.NO_CACHE)
////                        .into(userAvatar);
//
//                Glide.with(this)
//                    .load(EndPoints.ASSETS_BASE_URL + mContactsModel.getImage())
//                    .transform(new CircleTransform(this))
//                    .placeholder(R.drawable.avatar_placeholder)
//                    .override(largeAvatarSize, largeAvatarSize)
//                    .into(userAvatar);
//
//            } else {
//                Glide.with(this)
//                    .load(EndPoints.ASSETS_BASE_URL + mContactsModel.getImage())
//                    .transform(new CircleTransform(this))
//                    .placeholder(R.drawable.avatar_placeholder)
//                    .override(largeAvatarSize, largeAvatarSize)
//                    .into(userAvatar);
//
////                Picasso.with(this)
////                        .load(EndPoints.ASSETS_BASE_URL + mContactsModel.getImage())
////                        .transform(new CropSquareTransformation())
////                        .resize(200, 200)
////                        .networkPolicy(NetworkPolicy.NO_CACHE)
////                        .memoryPolicy(MemoryPolicy.NO_CACHE)
////                        .into(userAvatar);
//            }
        } else {

            userAvatar.setPadding(2, 2, 2, 2);
            userAvatar.setImageResource(R.drawable.ic_user_holder_white_48dp);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                final Animation animation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.scale_for_button_animtion_exit);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        addAvatar.setVisibility(View.GONE);
                        if (AppHelper.isAndroid5()) {
                            ActivityCompat.finishAfterTransition(EditProfileActivity.this);
                        } else {
                            finish();
                            overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                        }
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                addAvatar.startAnimation(animation);

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final Animation animation = AnimationUtils.loadAnimation(EditProfileActivity.this, R.anim.scale_for_button_animtion_exit);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addAvatar.setVisibility(View.GONE);

                if (AppHelper.isAndroid5()) {
                    ActivityCompat.finishAfterTransition(EditProfileActivity.this);
                } else {
                    finish();
                    overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        addAvatar.startAnimation(animation);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEditProfilePresenter.onDestroy();
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
            case "Path":
                PicturePath = pusher.getData();
                new Handler().postDelayed(() -> setImage(pusher.getData()), 500);
                break;
            case "updateCurrentStatus":
                mEditProfilePresenter.onCreate();
                break;
            case "updateImageProfile":
                if (pusher.isBool()) {
                    AppHelper.CustomToast(EditProfileActivity.this, pusher.getData());
                    FilesManager.downloadFilesToDevice(this, mContactsModel.getImage(), String.valueOf(mContactsModel.getId()), mContactsModel.getUsername(), "profile");
                } else {
                    AppHelper.CustomToast(EditProfileActivity.this, pusher.getData());
                }

                break;
        }

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
                    .transform(new CircleTransform(this))
                    .placeholder(R.drawable.avatar_placeholder)
                    .override(400, 400)
                    .into(userAvatar);
        } else {
            userAvatar.setPadding(2, 2, 2, 2);
            userAvatar.setImageResource(R.drawable.ic_user_holder_white_48dp);

        }
        new UploadFileToServer().execute();

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {

    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat(throwable.getMessage());
    }


    /**
     * Uploading the image  to server
     */
    private class UploadFileToServer extends AsyncTask<Void, Integer, StatusResponse> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppHelper.LogCat("onPreExecute  image ");
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            AppHelper.LogCat("progress image " + (int) (progress[0]));
        }

        @Override
        protected StatusResponse doInBackground(Void... params) {
            return uploadFile();
        }

        private StatusResponse uploadFile() {
            RequestBody requestFile;
            final StatusResponse statusResponse = null;
            if (PicturePath != null) {
                // use the FileUtils to get the actual file by uri
                File file = new File(PicturePath);
                // create RequestBody instance from file
                requestFile =
                        RequestBody.create(MediaType.parse("image/*"), file);
            } else {
                requestFile = null;
            }
            APIContact mApiContact = mApiService.RootService(APIContact.class, PreferenceManager.getToken(EditProfileActivity.this), EndPoints.BASE_URL);
            EditProfileActivity.this.runOnUiThread(() -> AppHelper.showDialog(EditProfileActivity.this, "Updating ... "));
            Call<StatusResponse> statusResponseCall = mApiContact.uploadImage(requestFile);
            statusResponseCall.enqueue(new Callback<StatusResponse>() {
                @Override
                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                    AppHelper.hideDialog();
                    if (response.isSuccessful()) {
                        EventBus.getDefault().post(new Pusher("updateImageProfile", response.body().getMessage(), response.body().isSuccess()));

                    } else {
                        AppHelper.CustomToast(EditProfileActivity.this, response.message());
                    }
                }

                @Override
                public void onFailure(Call<StatusResponse> call, Throwable t) {
                    AppHelper.hideDialog();
                    AppHelper.CustomToast(EditProfileActivity.this, getString(R.string.failed_upload_image));
                    AppHelper.LogCat("Failed  upload your image " + t.getMessage());
                }
            });
            return statusResponse;
        }


        @Override
        protected void onPostExecute(StatusResponse response) {
            super.onPostExecute(response);
            AppHelper.LogCat("Response from server: " + response);

        }


    }

}
