package com.fortunekidew.pewaa.activities.wishlists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.EditText;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.api.APIWishlists;
import com.fortunekidew.pewaa.app.AppConstants;
import com.fortunekidew.pewaa.app.EndPoints;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.Files.FilesManager;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.users.status.StatusResponse;
import com.fortunekidew.pewaa.models.wishlists.EditWishlist;
import com.fortunekidew.pewaa.presenters.EditWishlistPresenter;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Brian Mwakima on 17/10/2016.
 * Email : mwadime@fortunekidew.co.ke
 */

@SuppressLint("SetTextI18n")
public class AddWishlistsActivity extends AppCompatActivity implements LoadingData {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.edit_wishlist_name)
    EditText EditName;

    @Bind(R.id.edit_wishlist_description)
    EditText EditDescription;

    private ActionMode actionMode;
    private String FileImagePath, FileSize;
    private APIService mApiService;

    private EditWishlistPresenter mEditWishlistPresenter = new EditWishlistPresenter(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist);
        ButterKnife.bind(this);

        initializerView();
        mEditWishlistPresenter.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {

        mApiService = new APIService(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        File fileVideo = null;
        // Get file from file name
        File file = null;
        if (resultCode == RESULT_OK) {
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
            switch (requestCode) {
                case AppConstants.UPLOAD_PICTURE_REQUEST_CODE:
                    FileImagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());

                    if (FileImagePath != null) {
                        file = new File(FileImagePath);
                    }
                    if (file != null) {
                        FileSize = String.valueOf(file.length());

                    }
                    break;
                case AppConstants.SELECT_MESSAGES_CAMERA:
                    if (data.getData() != null) {
                        FileImagePath = FilesManager.getPath(PewaaApplication.getAppContext(), data.getData());
                        if (FileImagePath != null) {
                            file = new File(FileImagePath);
                        }
                        if (file != null) {
                            FileSize = String.valueOf(file.length());
                        }
                    } else {
                        try {
                            String[] projection = new String[]{MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA, MediaStore
                                    .Images.ImageColumns.BUCKET_DISPLAY_NAME, MediaStore.Images.ImageColumns.DATE_TAKEN, MediaStore.Images
                                    .ImageColumns.MIME_TYPE};
                            final Cursor cursor = PewaaApplication.getAppContext().getContentResolver()
                                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Images.ImageColumns
                                            .DATE_TAKEN + " DESC");

                            if (cursor != null && cursor.moveToFirst()) {
                                String imageLocation = cursor.getString(1);
                                cursor.close();
                                File imageFile = new File(imageLocation);
                                if (imageFile.exists()) {
                                    FileImagePath = imageFile.getPath();
                                    file = new File(FileImagePath);
                                    FileSize = String.valueOf(file.length());
                                }
                            }
                        } catch (Exception e) {
                            AppHelper.LogCat("error" + e);
                        }
                    }
                    break;
            }

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

    private int convertToDp(float value) {
        return (int) Math.ceil(1 * value);
    }

    @OnClick(R.id.action_save)
    public void saveWishlist(View view) {
        String newName = EditName.getText().toString().trim();
        String newDescription = EditDescription.getText().toString().trim();

        if (newName.isEmpty())
            return;

        APIWishlists mApiWishlists = mApiService.RootService(APIWishlists.class, PreferenceManager.getToken(AddWishlistsActivity.this), EndPoints.BASE_URL);
        AddWishlistsActivity.this.runOnUiThread(() -> AppHelper.showDialog(AddWishlistsActivity.this, "Adding Wishlist..."));
        EditWishlist newWishlist = new EditWishlist();
        newWishlist.setName(newName);
        newWishlist.setDescription(newDescription);
        Call<StatusResponse> statusResponseCall = mApiWishlists.editWishlist(newWishlist);
        statusResponseCall.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                AppHelper.hideDialog();
                if (response.isSuccessful()) {

                    newWishlist.setId(response.body().getId());

                    EventBus.getDefault().post(new Pusher("new_wishlist", newWishlist));

                } else {
                    AppHelper.CustomToast(AddWishlistsActivity.this, response.message());
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                AppHelper.hideDialog();
                AppHelper.CustomToast(AddWishlistsActivity.this, getString(R.string.failed_upload_image));
                AppHelper.LogCat("Failed to add gift " + t.getMessage());
            }
        });

    }

    @OnClick(R.id.action_discard)
    public void onBackPress(View view) {
        onBackPressed();
    }
}

