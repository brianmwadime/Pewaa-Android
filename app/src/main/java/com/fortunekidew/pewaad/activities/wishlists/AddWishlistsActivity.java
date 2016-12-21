package com.fortunekidew.pewaad.activities.wishlists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.api.APIWishlists;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.Files.FilesManager;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;
import com.fortunekidew.pewaad.presenters.EditWishlistPresenter;
import com.fortunekidew.pewaad.ui.LabelledSpinner;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Brian Mwakima on 17/10/2016.
 * Email : mwadime@fortunekidew.co.ke
 */

@SuppressLint("SetTextI18n")
public class AddWishlistsActivity extends AppCompatActivity implements LoadingData, LabelledSpinner.OnItemChosenListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.spinner)
    LabelledSpinner CategorySpinner;
    @BindView(R.id.edit_wishlist_name)
    EditText EditName;
    @BindView(R.id.edit_name_wrapper)
    TextInputLayout name_wrapper;

    @BindView(R.id.edit_wishlist_description)
    EditText EditDescription;
    @BindView(R.id.edit_description_wrapper)
    TextInputLayout description_wrapper;

    @BindView(R.id.edit_wishlist_recipient)
    EditText EditRecipients;
    @BindView(R.id.edit_recipient_wrapper)
    TextInputLayout recipientWrapper;

    private ActionMode actionMode;
    private String FileImagePath, FileSize, mCategory;
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
        CategorySpinner.setItemsArray(R.array.wishlist_categories_arrays);
        CategorySpinner.setDefaultErrorEnabled(true);
        CategorySpinner.setDefaultErrorText("Please select a category.");  // Displayed when first item remains selected
        CategorySpinner.setOnItemChosenListener(this);
        mApiService = new APIService(this);

        CategorySpinner.requestFocus();

        EditName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                name_wrapper.setErrorEnabled(false);
            }
        });

        EditRecipients.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                recipientWrapper.setErrorEnabled(false);
            }
        });

        EditDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                description_wrapper.setErrorEnabled(false);
            }
        });
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
        String newRecipients = EditRecipients.getText().toString().trim();
        String newDescription = EditDescription.getText().toString().trim();

        if (newName.isEmpty()) {
            name_wrapper.setError("Please enter a name");
            return;
        }


        if (newRecipients.isEmpty()) {
            recipientWrapper.setError("Please enter recipient(s)");
            return;
        }


        if (newDescription.isEmpty()) {
            name_wrapper.setError("Please add a description");
            return;
        }




        APIWishlists mApiWishlists = mApiService.RootService(APIWishlists.class, PreferenceManager.getToken(AddWishlistsActivity.this), EndPoints.BASE_URL);
        AddWishlistsActivity.this.runOnUiThread(() -> AppHelper.showDialog(AddWishlistsActivity.this, "Adding Wishlist..."));
        EditWishlist newWishlist = new EditWishlist();
        newWishlist.setName(newName);
        newWishlist.setRecipients(newRecipients);
        newWishlist.setCategory(mCategory);
        newWishlist.setDescription(newDescription);
        Call<StatusResponse> statusResponseCall = mApiWishlists.editWishlist(newWishlist);
        statusResponseCall.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                AppHelper.hideDialog();
                if (response.isSuccessful()) {

                    newWishlist.setId(response.body().getId());

                    EventBus.getDefault().post(new Pusher("new_wishlist", newWishlist));

                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

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

    @Override
    public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
        mCategory = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

    }
}

