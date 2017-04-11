package com.fortunekidew.pewaad.activities.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.status.StatusActivity;
import com.fortunekidew.pewaad.api.APIContact;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.BottomSheetEditProfile;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.Files.ImageLoader;
import com.fortunekidew.pewaad.helpers.Files.MemoryCache;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.images.PewaaImageLoader;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.BindDimen;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.realm.Realm;
import io.socket.client.Socket;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_IMAGE_PROFILE_PATH;
import static com.fortunekidew.pewaad.app.AppConstants.EVENT_BUS_UPDATE_CURRENT_STATUS;
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
    @BindView(R.id.progress_bar_edit_profile)
    ProgressBar progressBar;
    private ContactsModel mContactsModel;
    private EditProfilePresenter mEditProfilePresenter = new EditProfilePresenter(this);
    private APIService mApiService;
    private String PicturePath;

    private Socket mSocket;
    private MemoryCache memoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        ButterKnife.bind(this);
        connectToChatServer();
        initializerView();
        memoryCache = new MemoryCache();
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
     * method to connect to the chat sever by socket
     */
    private void connectToChatServer() {

        PewaaApplication app = (PewaaApplication) getApplication();
        mSocket = app.getSocket();

        if (mSocket == null) {
            PewaaApplication.connectSocket();
            mSocket = app.getSocket();
        }
        if (!mSocket.connected())
            mSocket.connect();


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
        Bitmap bitmap = ImageLoader.GetCachedBitmapImage(memoryCache, mContactsModel.getImage(), this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);
        if (bitmap != null) {
            ImageLoader.SetBitmapImage(bitmap, userAvatar);
        } else {

            Target target = new BitmapImageViewTarget(userAvatar) {
                @Override
                public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                    super.onResourceReady(bitmap, anim);
                    userAvatar.setImageBitmap(bitmap);
                    ImageLoader.DownloadImage(memoryCache, EndPoints.ASSETS_BASE_URL + mContactsModel.getImage(), mContactsModel.getImage(), EditProfileActivity.this, mContactsModel.getId(), AppConstants.USER, AppConstants.EDIT_PROFILE);

                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    super.onLoadFailed(e, errorDrawable);
                    userAvatar.setImageDrawable(errorDrawable);
                }

                @Override
                public void onLoadStarted(Drawable placeholder) {
                    super.onLoadStarted(placeholder);
                    userAvatar.setImageDrawable(placeholder);
                }
            };
            PewaaImageLoader.loadCircleImage(this, EndPoints.ASSETS_BASE_URL + mContactsModel.getImage(), target, R.drawable.image_holder_ur_circle, AppConstants.EDIT_PROFILE_IMAGE_SIZE);
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
            case "updateImageProfile":
                if (pusher.isBool()) {
                    AppHelper.CustomToast(EditProfileActivity.this, pusher.getData());
                    FilesManager.downloadFilesToDevice(this, mContactsModel.getImage(), String.valueOf(mContactsModel.getId()), AppConstants.ROW_PROFILE);
                } else {
                    AppHelper.CustomToast(EditProfileActivity.this, pusher.getData());
                }
            case EVENT_BUS_IMAGE_PROFILE_PATH:
                PicturePath = String.valueOf(pusher.getData());
                if (PicturePath != null) {
                    try {
                        new UploadFileToServer().execute();
                    } catch (Exception e) {
                        AppHelper.LogCat(e);
                        AppHelper.CustomToast(EditProfileActivity.this, getString(R.string.oops_something));
                    }

                }
                break;
            case EVENT_BUS_UPDATE_CURRENT_STATUS:
                mEditProfilePresenter.onCreate();
                break;
            case AppConstants.EVENT_BUS_USERNAME_PROFILE_UPDATED:
                mEditProfilePresenter.loadData();
                break;
            case AppConstants.EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED:
                mEditProfilePresenter.loadData();
                break;
        }

    }

    /**
     * method to setup the image
     *
     * @param path this is parameter for setImage method
     */
    public void setImage(String path) {
        Target target = new BitmapImageViewTarget(userAvatar) {
            @Override
            public void onResourceReady(final Bitmap bitmap, GlideAnimation anim) {
                super.onResourceReady(bitmap, anim);
                userAvatar.setImageBitmap(bitmap);
                EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_MINE_IMAGE_PROFILE_UPDATED));
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("senderId", PreferenceManager.getID(EditProfileActivity.this));
                    jsonObject.put("phone", PreferenceManager.getPhone(EditProfileActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (mSocket != null)
                    mSocket.emit(AppConstants.SOCKET_IMAGE_PROFILE_UPDATED, jsonObject);

            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);
                userAvatar.setImageDrawable(errorDrawable);
            }

            @Override
            public void onLoadStarted(Drawable placeholder) {
                super.onLoadStarted(placeholder);
                userAvatar.setImageDrawable(placeholder);
            }
        };
        PewaaImageLoader.loadCircleImage(this, EndPoints.ASSETS_BASE_URL + path, target, R.drawable.image_holder_ur_circle, AppConstants.EDIT_PROFILE_IMAGE_SIZE);
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
            runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));
            APIContact mApiContact = mApiService.RootService(APIContact.class, PreferenceManager.getToken(EditProfileActivity.this), EndPoints.BASE_URL);
            EditProfileActivity.this.runOnUiThread(() -> AppHelper.showDialog(EditProfileActivity.this, "Updating ... "));
            Call<StatusResponse> statusResponseCall = mApiContact.uploadImage(requestFile);
            statusResponseCall.enqueue(new Callback<StatusResponse>() {
                @Override
                public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                    AppHelper.hideDialog();
                    if (response.isSuccessful()) {
                        EventBus.getDefault().post(new Pusher("updateImageProfile", response.body().getMessage(), response.body().isSuccess()));
                        runOnUiThread(() -> {
                            Realm realm = PewaaApplication.getRealmDatabaseInstance();
                            realm.executeTransactionAsync(realm1 -> {
                                ContactsModel contactsModel = realm1.where(ContactsModel.class).equalTo("id", PreferenceManager.getID(EditProfileActivity.this)).findFirst();
                                contactsModel.setImage(response.body().getUserImage());
                                realm1.copyToRealmOrUpdate(contactsModel);

                            }, () -> new Handler().postDelayed(() -> {
                                progressBar.setVisibility(View.GONE);
                                AppHelper.CustomToast(EditProfileActivity.this, response.body().getMessage());
                                setImage(response.body().getUserImage());
                            }, 700), error -> AppHelper.LogCat("error update group image in group model " + error.getMessage()));
                            realm.close();
                        });
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
