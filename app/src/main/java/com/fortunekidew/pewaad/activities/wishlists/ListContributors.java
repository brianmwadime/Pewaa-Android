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

package com.fortunekidew.pewaad.activities.wishlists;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.contacts.SelectContactsAdapter;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.presenters.SelectContactsPresenter;
import com.fortunekidew.pewaad.ui.transitions.FabTransform;
import com.fortunekidew.pewaad.ui.transitions.MorphTransform;
import com.fortunekidew.pewaad.util.TransitionUtils;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class ListContributors extends Activity {

    public static final String EXTRA_CONTRIBUTORS = "EXTRA_CONTRIBUTORS";
    boolean isDismissing = false;
    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.dialog_title) TextView title;
    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.actions_container)
    FrameLayout actionsContainer;
    @BindView(R.id.cancel) Button cancel;
    @BindView(R.id.add) Button add;
    @BindView(R.id.loading) ProgressBar loading;

    private List<ContactsModel> mContactsModelList;
    private SelectContactsAdapter mSelectContactsAdapter;
    private SelectContactsPresenter mContactsPresenter = new SelectContactsPresenter(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_contributors);
        ButterKnife.bind(this);


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

        initializerView();

        mContactsPresenter.onCreate();

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra(EXTRA_CONTRIBUTORS)) {
                mSelectContactsAdapter.mselectedContacts = (List<String>) getIntent().getExtras().getSerializable(EXTRA_CONTRIBUTORS);
            }
        }

    }

    /**
     * method to show contacts list
     * @param contacts this is parameter for  ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contacts) {

        mContactsModelList = contacts;

        mSelectContactsAdapter.setContacts(mContactsModelList);

    }

    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Search contacts " + throwable.getMessage());
    }

    /**
     * method to initialize the  view
     */
    private void initializerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectContactsAdapter = new SelectContactsAdapter(this, mContactsModelList);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mSelectContactsAdapter);
    }

    @Override
    public void onBackPressed() {
        dismiss(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContactsPresenter.onDestroy();
    }


    public void select(View view) {
        Intent intent=new Intent();
        intent.putExtra("SELECTED_LIST", (Serializable) mSelectContactsAdapter.mselectedContacts);
        setResult(RESULT_OK,intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        } else {
            finish();
        }
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
        title.setVisibility(View.GONE);
        actionsContainer.setVisibility(View.GONE);
        loading.setVisibility(View.VISIBLE);
    }

}
