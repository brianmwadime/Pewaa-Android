package com.fortunekidew.pewaad.activities.wishlists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIContributor;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.api.APIWishlists;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PermissionHandler;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.UtilsString;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.ContributorsResponse;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;
import com.fortunekidew.pewaad.presenters.EditWishlistPresenter;
import com.fortunekidew.pewaad.ui.LabelledSpinner;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.app.AppConstants.STATUS_SELECTED_CONTRIBUTORS_SUCCESS;


/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
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

    @BindView(R.id.contributors_description)
    TextView contributors_description;

    private String mCategory;
    private APIService mApiService;
    private ArrayList<String> selectedContributors = new ArrayList<>();

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

        EditDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                description_wrapper.setErrorEnabled(false);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AppConstants.STATUS_SELECTED_CONTRIBUTORS_SUCCESS:
                    selectedContributors = (ArrayList<String>) data.getSerializableExtra("SELECTED_LIST");
                    contributors_description.setText(selectedContributors.size() + " Contributors");
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

    @OnClick(R.id.action_save)
    public void saveWishlist(View view) {
        String newName = EditName.getText().toString().trim();
        String newDescription = EditDescription.getText().toString().trim();

        if (newName.isEmpty()) {
            name_wrapper.setError("Please enter a name");
            return;
        }

        if (UtilsString.isNullOrBlank(mCategory)) {
            CategorySpinner.setDefaultErrorEnabled(true);
            return;
        }

        APIWishlists mApiWishlists = mApiService.RootService(APIWishlists.class, PreferenceManager.getToken(AddWishlistsActivity.this), EndPoints.BASE_URL);
        AddWishlistsActivity.this.runOnUiThread(() -> AppHelper.showDialog(AddWishlistsActivity.this, "Adding Wishlist..."));
        EditWishlist newWishlist = new EditWishlist();
        newWishlist.setName(newName);
        newWishlist.setCategory(mCategory);
        newWishlist.setDescription(newDescription);
        newWishlist.setPermissions("ADMINISTRATOR");
        Call<StatusResponse> statusResponseCall = mApiWishlists.editWishlist(newWishlist);
        statusResponseCall.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                AppHelper.hideDialog();
                if (response.isSuccessful()) {

                    newWishlist.setId(response.body().getId());

                    if (selectedContributors.size() > 0) {
                        saveContributor(newWishlist, selectedContributors);
                    } else {
                        EventBus.getDefault().post(new Pusher("new_wishlist", newWishlist));

                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    }

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

    private void saveContributor(EditWishlist wishlist, ArrayList<String> userIds) {

        APIContributor mApiContributor = mApiService.RootService(APIContributor.class, PreferenceManager.getToken(AddWishlistsActivity.this), EndPoints.BASE_URL);
        AddWishlistsActivity.this.runOnUiThread(() -> showLoading());
        Call<StatusResponse> statusResponseCall = mApiContributor.addContributors(userIds, wishlist.getId());
        statusResponseCall.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {

                    EventBus.getDefault().post(new Pusher("new_wishlist", wishlist));

                    finish();
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                } else {
                    AppHelper.CustomToast(AddWishlistsActivity.this, response.message());
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {

            }
        });

    }

    private void showLoading() {

    }

    @OnClick(R.id.action_discard)
    public void onBackPress(View view) {
        onBackPressed();
    }

    @OnClick(R.id.edit_contributors)
    public void selectcontributors() {
        Intent intent = new Intent(this, ListContributors.class);

        intent.putExtra(ListContributors.EXTRA_CONTRIBUTORS, selectedContributors);

        startActivityForResult(intent, STATUS_SELECTED_CONTRIBUTORS_SUCCESS);
    }

    @Override
    public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
        mCategory = null;

        if (position == 0) return;

        mCategory = adapterView.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

    }
}

