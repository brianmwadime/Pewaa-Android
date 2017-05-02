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

package com.fortunekidew.pewaad.activities.main;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIGifts;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.api.APIWishlists;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.EndPoints;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.models.DefaultResponse;
import com.fortunekidew.pewaad.models.gifts.EditGift;
import com.fortunekidew.pewaad.models.users.status.StatusResponse;
import com.fortunekidew.pewaad.models.wishlists.EditWishlist;
import com.fortunekidew.pewaad.ui.transitions.FabTransform;
import com.fortunekidew.pewaad.ui.transitions.MorphTransform;
import com.fortunekidew.pewaad.util.TransitionUtils;
import com.google.gson.Gson;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.fortunekidew.pewaad.app.AppConstants.STATUS_REPORT_SUCCESS;
import static com.fortunekidew.pewaad.app.AppConstants.STATUS_REPORT_WISHLIST_SUCCESS;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class Report extends Activity {

    public static final String EXTRA_WISHLIST_ID = "EXTRA_WISHLIST_ID";
    public static final String EXTRA_GIFT_ID = "EXTRA_GIFT_ID";
    public static final String EXTRA_REPORT_TYPE = "EXTRA_REPORT_TYPE";
    boolean isDismissing = false;
    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.dialog_title) TextView title;
    @BindView(R.id.actions_container)
    FrameLayout actionsContainer;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.report) Button report;
    @BindView(R.id.loading) ProgressBar loading;
    @BindView(R.id.description) EditText description;

    private APIService mApiService;

    private String id;

    private AppConstants.REPORT_TYPE type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        ButterKnife.bind(this);

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(EXTRA_WISHLIST_ID)) {
                id = getIntent().getExtras().getString(EXTRA_WISHLIST_ID);
            }

            if (getIntent().hasExtra(EXTRA_GIFT_ID)) {
                id = getIntent().getExtras().getString(EXTRA_GIFT_ID);
            }

            if (getIntent().hasExtra(EXTRA_REPORT_TYPE)) {
                type = (AppConstants.REPORT_TYPE) getIntent().getExtras().getSerializable(EXTRA_REPORT_TYPE);
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

        loading.setVisibility(View.GONE);

        mApiService = new APIService(this);

    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    public void doReport(View view) {
        showLoading();
        report();
    }

    private void report() {

        Call<DefaultResponse> reportCall = null;
        Report.this.runOnUiThread(() -> showLoading());

        switch (type) {
            case GIFT:
                APIGifts mGiftApi = mApiService.RootService(APIGifts.class, PreferenceManager.getToken(Report.this), EndPoints.BASE_URL);
                EditGift gift = new EditGift();
                gift.setFlagged(true);
                if(!description.getText().toString().isEmpty()) gift.setFlagged_description(description.getText().toString().trim());
                reportCall = mGiftApi.reportGift(id, gift);
                break;
            case WISHLIST:
                EditWishlist wishlist = new EditWishlist();
                wishlist.setFlagged(true);
                if(!description.getText().toString().isEmpty()) wishlist.setFlagged_description(description.getText().toString().trim());
                APIWishlists mWishlistApi = mApiService.RootService(APIWishlists.class, PreferenceManager.getToken(Report.this), EndPoints.BASE_URL);
                reportCall = mWishlistApi.reportWishlist(id, wishlist);
                break;
        }

        reportCall.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful()) {

                    Intent intentMessage = new Intent();

                    intentMessage.putExtra("EXTRA_ID",id);


                    switch (type) {
                        case GIFT:
                            setResult(STATUS_REPORT_SUCCESS, intentMessage);
                            break;
                        case WISHLIST:
                            setResult(STATUS_REPORT_WISHLIST_SUCCESS, intentMessage);

                            break;
                    }

                    finish();

                } else {

                    try {
                        Gson gson = new Gson();
                        StatusResponse res = gson.fromJson(response.errorBody().string(), StatusResponse.class);
                        Snackbar snackbar = Snackbar.make(container, res.getMessage(), Snackbar.LENGTH_SHORT);
                        // get snackbar view
                        View snackbarView = snackbar.getView();
                        snackbarView.setBackgroundColor(Color.RED);
                        snackbar.show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    showAddContributor();
                }

            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                Snackbar.make(container, "Failed to report " + type, Snackbar.LENGTH_SHORT).show();
                showAddContributor();
            }
        });

    }

    public void dismiss(View view) {
        isDismissing = true;
        setResult(Activity.RESULT_CANCELED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
    }

    private void showLoading() {
        TransitionManager.beginDelayedTransition(container);
        description.setVisibility(View.GONE);
        actionsContainer.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

    private void showAddContributor() {
        TransitionManager.beginDelayedTransition(container);
        description.setVisibility(View.VISIBLE);
        actionsContainer.setVisibility(View.VISIBLE);
        loading.setVisibility(View.GONE);
    }

}
