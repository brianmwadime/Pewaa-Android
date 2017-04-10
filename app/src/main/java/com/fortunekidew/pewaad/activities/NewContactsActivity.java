package com.fortunekidew.pewaad.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.SelectContactsAdapter;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.presenters.SelectContactsPresenter;
import com.fortunekidew.pewaad.ui.RecyclerViewFastScroller;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class NewContactsActivity extends AppCompatActivity {
    public static final String RESULT_EXTRA_WISHLIST_ID = "RESULT_EXTRA_WISHLIST_ID";
    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.app_bar)
    Toolbar toolbar;

    private List<ContactsModel> mContactsModelList;
    private SelectContactsAdapter mSelectContactsAdapter;
    private SelectContactsPresenter mContactsPresenter = new SelectContactsPresenter(this);

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);
        initializerView();
        mContactsPresenter.onCreate();
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.title_select_contributor));

        }
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mSelectContactsAdapter = new SelectContactsAdapter(this, mContactsModelList);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mSelectContactsAdapter);

        // set recyclerView to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
        super.onOptionsItemSelected(item);
        return true;
    }

    /**
     * method to show contacts list
     * @param contactsModels this is parameter for ShowContacts  method
     */
    public void ShowContacts(List<ContactsModel> contactsModels) {
        mContactsModelList = contactsModels;
        if (getSupportActionBar() != null)
            getSupportActionBar().setSubtitle("" + contactsModels.size() +" of "+ PreferenceManager.getContactSize(this));
        mSelectContactsAdapter.setContacts(contactsModels);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactsPresenter.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }
}
