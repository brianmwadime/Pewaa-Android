/*
 * Copyright 2015 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortunekidew.pewaad.activities.contributors;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIContributor;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.models.wishlists.ContributorsResponse;
import com.fortunekidew.pewaad.ui.transitions.FabTransform;
import com.fortunekidew.pewaad.ui.transitions.MorphTransform;
import com.fortunekidew.pewaad.util.TransitionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignContributor extends Activity {

    public static final String EXTRA_WISHLIST_ID = "EXTRA_WISHLIST_ID";
    public static final String EXTRA_CONTRIBUTOR_ID = "EXTRA_CONTRIBUTOR_ID";
    boolean isDismissing = false;
    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.dialog_title) TextView title;
    @BindView(R.id.spinner)
    Spinner permissions;
    @BindView(R.id.actions_container)
    FrameLayout actionsContainer;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.add) Button add;
    @BindView(R.id.loading) ProgressBar loading;

    private APIService mApiService;

    private String wishlistId, contributorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_contributor);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(EXTRA_WISHLIST_ID)) {
                wishlistId = getIntent().getExtras().getString(EXTRA_WISHLIST_ID);
            }

            if (getIntent().hasExtra(EXTRA_CONTRIBUTOR_ID)) {
                contributorId = getIntent().getExtras().getString(EXTRA_CONTRIBUTOR_ID);
            }
        }

        if (!FabTransform.setup(this, container)) {
            MorphTransform.setup(this, container,
                    ContextCompat.getColor(this, R.color.background_light),
                    getResources().getDimensionPixelSize(R.dimen.dialog_corners));
        }
        if (getWindow().getSharedElementEnterTransition() != null) {
            getWindow().getSharedElementEnterTransition().addListener(new TransitionUtils.TransitionListenerAdapter() {
                @Override
                public void onTransitionEnd(Transition transition) {
                    getWindow().getSharedElementEnterTransition().removeListener(this);

                }
            });
        }

        permissions.setOnItemSelectedListener(new ItemSelectedListener());

        loading.setVisibility(View.GONE);

        mApiService = new APIService(this);

    }

    public class ItemSelectedListener implements AdapterView.OnItemSelectedListener {

        //get strings of first item
        String firstItem = String.valueOf(permissions.getSelectedItem());

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (firstItem.equals(String.valueOf(permissions.getSelectedItem()))) {
                // ToDo when first item is selected
            } else {
                Toast.makeText(parent.getContext(),
                        "You have selected : " + parent.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_LONG).show();
                // Todo when item is selected by the user
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg) {

        }

    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void doAdd(View view) {
        showLoading();
        saveContributor();
    }

    private void saveContributor() {

        APIContributor mApiContributor = mApiService.RootService(APIContributor.class, PreferenceManager.getToken(AssignContributor.this), EndPoints.BASE_URL);
        AssignContributor.this.runOnUiThread(() -> showLoading());
        RequestBody wishlistID = RequestBody.create(MediaType.parse("multipart/form-data"), wishlistId);
        RequestBody contributorID = RequestBody.create(MediaType.parse("multipart/form-data"), contributorId);
        RequestBody permission = RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(permissions.getSelectedItem()));
        Call<ContributorsResponse> statusResponseCall = mApiContributor.editContributor(contributorId, wishlistId, String.valueOf(permissions.getSelectedItem()));
        statusResponseCall.enqueue(new Callback<ContributorsResponse>() {
            @Override
            public void onResponse(Call<ContributorsResponse> call, Response<ContributorsResponse> response) {
                if (response.isSuccessful()) {

                    setResult(Activity.RESULT_OK);
                    finish();

                } else {
                    Snackbar.make(container, response.message(), Snackbar.LENGTH_SHORT).show();
                    showAddContributor();
                }
            }

            @Override
            public void onFailure(Call<ContributorsResponse> call, Throwable t) {
                Snackbar.make(container, "Failed to add contributor", Snackbar.LENGTH_SHORT).show();
                showAddContributor();
            }
        });

    }

    public void dismiss(View view) {
        isDismissing = true;
        setResult(Activity.RESULT_CANCELED);
        finishAfterTransition();
    }

    private void showLoading() {
        TransitionManager.beginDelayedTransition(container);
        title.setVisibility(View.GONE);
        actionsContainer.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void showAddContributor() {
        TransitionManager.beginDelayedTransition(container);
        title.setVisibility(View.VISIBLE);
        actionsContainer.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }


    private void showAddFailed() {
        Snackbar.make(container, "Log in failed", Snackbar.LENGTH_SHORT).show();
        showAddContributor();
    }

}
