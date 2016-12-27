package com.fortunekidew.pewaad.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.contacts.ContactsAdapter;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.PusherContacts;
import com.fortunekidew.pewaad.presenters.ContactsPresenter;
import com.fortunekidew.pewaad.ui.RecyclerViewFastScroller;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class ContactsFragment extends Fragment implements LoadingData {

    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.empty)
    LinearLayout emptyContacts;

    private List<ContactsModel> mContactsModelList;
    private ContactsAdapter mContactsAdapter;
    private ContactsPresenter mContactsPresenter = new ContactsPresenter(this);


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, mView);
        mContactsPresenter.onCreate();
        initializerView();
        return mView;
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContactsAdapter = new ContactsAdapter(getActivity(), mContactsModelList);
        setHasOptionsMenu(true);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mContactsAdapter);
        // set recycler view to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_contacts:
                mContactsPresenter.onRefresh();
                break;
        }
        return true;
    }

    /**
     * method to show contacts list
     *
     * @param contactsModels this is parameter for  ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contactsModels) {
        mContactsModelList = contactsModels;
        if (contactsModels.size() != 0) {
            fastScroller.setVisibility(View.VISIBLE);
            ContactsList.setVisibility(View.VISIBLE);
            emptyContacts.setVisibility(View.GONE);
            try {
                PreferenceManager.setContactSize(contactsModels.size(), getActivity());
            } catch (Exception e) {
                AppHelper.LogCat(" Exception size contact fragment");
            }

        } else {
            fastScroller.setVisibility(View.GONE);
            ContactsList.setVisibility(View.GONE);
            emptyContacts.setVisibility(View.VISIBLE);
        }

    }

    /**
     * method to update contacts
     *
     * @param contactsModels this is parameter for  updateContacts method
     */
    public void updateContacts(List<ContactsModel> contactsModels) {
        this.mContactsModelList = contactsModels;
        mContactsAdapter.notifyDataSetChanged();
    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(PusherContacts pusher) {
        mContactsPresenter.onEventMainThread(pusher);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mContactsPresenter.onDestroy();
    }


    @Override
    public void onShowLoading() {
        EventBus.getDefault().post(new Pusher("startRefresh"));
    }

    @Override
    public void onHideLoading() {
        EventBus.getDefault().post(new Pusher("stopRefresh"));
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat(throwable.getMessage());
        EventBus.getDefault().post(new Pusher("stopRefresh"));
    }
}