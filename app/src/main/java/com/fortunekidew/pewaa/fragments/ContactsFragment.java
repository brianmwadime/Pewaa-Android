package com.fortunekidew.pewaa.fragments;

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

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.contacts.ContactsAdapter;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaa.models.users.contacts.PusherContacts;
import com.fortunekidew.pewaa.presenters.ContactsPresenter;
import com.fortunekidew.pewaa.ui.RecyclerViewFastScroller;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Abderrahim El imame on 02/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsFragment extends Fragment implements LoadingData {

    @Bind(R.id.ContactsList)
    RecyclerView ContactsList;
    @Bind(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @Bind(R.id.empty)
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