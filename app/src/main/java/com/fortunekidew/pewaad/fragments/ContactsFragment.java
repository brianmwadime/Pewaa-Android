package com.fortunekidew.pewaad.fragments;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.contacts.ContactsAdapter;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.PewaaContact;
import com.fortunekidew.pewaad.models.users.contacts.PusherContacts;
import com.fortunekidew.pewaad.presenters.ContactsPresenter;
import com.fortunekidew.pewaad.ui.RecyclerViewFastScroller;
import com.squareup.picasso.Picasso;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import static android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class ContactsFragment extends Fragment implements LoadingData, SearchView.OnQueryTextListener {

    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.fastscroller)
    RecyclerViewFastScroller fastScroller;
    @BindView(R.id.empty)
    LinearLayout emptyContacts;

    private MenuItem searchItem;
    private SearchView searchView;

    private List<PewaaContact> mContactsModelList;
    private ContactsAdapter mContactsAdapter;
    private ContactsPresenter mContactsPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_contacts, container, false);
        ButterKnife.bind(this, mView);
        mContactsPresenter = new ContactsPresenter(this);
        mContactsPresenter.onCreate();
        initializerView();
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.contacts_menu, menu);

        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContactsAdapter = new ContactsAdapter(getActivity(), mContactsModelList);
        setHasOptionsMenu(true);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        ContactsList.setAdapter(mContactsAdapter);
        ContactsList.setItemAnimator(new DefaultItemAnimator());
        ContactsList.getItemAnimator().setChangeDuration(0);
        ContactsList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int scrollState) {
                final Picasso picasso = Picasso.with(getActivity());

                if (scrollState == SCROLL_STATE_IDLE || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    picasso.resumeTag(getActivity());
                } else {
                    picasso.pauseTag(getActivity());
                }
            }
        });
        // set recycler view to fastScroller
        fastScroller.setRecyclerView(ContactsList);
        fastScroller.setViewsToUse(R.layout.contacts_fragment_fast_scroller, R.id.fastscroller_bubble, R.id.fastscroller_handle);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        if (search.isEmpty()) {
            ((ContactsAdapter) ContactsList.getAdapter()).getFilter().filter("");
        } else {
            ((ContactsAdapter) ContactsList.getAdapter()).getFilter().filter(search.toLowerCase());
        }

        return false;
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
     * @param contacts this is parameter for  ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contacts, boolean isRefresh) {

        List<PewaaContact> contacts1 = new ArrayList<>();
        for (ContactsModel item : contacts) {
            PewaaContact contact = new PewaaContact();
            contact.setId(item.getId());
            contact.setContactID(item.getContactID());
            contact.setUsername(item.getUsername());
            contact.setName(item.getName());
            contact.setPhone(item.getPhone());
            contact.setLinked(item.isLinked());
            contact.setExist(item.isExist());
            contact.setImage(item.getImage());
            contact.setStatus(item.getStatus());
            contacts1.add(contact);
        }

        mContactsAdapter.setContacts(contacts1);

        if (!isRefresh) {
            mContactsModelList = contacts1;
        } else {
            mContactsAdapter.setContacts(contacts1);
        }

        if (contacts1.size() != 0) {
            fastScroller.setVisibility(View.VISIBLE);
            ContactsList.setVisibility(View.VISIBLE);
            emptyContacts.setVisibility(View.GONE);
            try {
                PreferenceManager.setContactSize(contacts1.size(), getActivity());
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
     * @param contacts this is parameter for  updateContacts method
     */
    public void updateContacts(List<ContactsModel> contacts) {
        if (contacts.size() != 0) {
            List<PewaaContact> contacts1 = new ArrayList<>();
            for (ContactsModel item : contacts) {
                PewaaContact contact = new PewaaContact();
                contact.setId(item.getId());
                contact.setContactID(item.getContactID());
                contact.setUsername(item.getUsername());
                contact.setName(item.getName());
                contact.setPhone(item.getPhone());
                contact.setLinked(item.isLinked());
                contact.setExist(item.isExist());
                contact.setImage(item.getImage());
                contact.setStatus(item.getStatus());
                contacts1.add(contact);
            }

            ContactsList.setVisibility(View.VISIBLE);
            emptyContacts.setVisibility(View.GONE);
            mContactsAdapter.setContacts(contacts1);
        } else {
            ContactsList.setVisibility(View.GONE);
            emptyContacts.setVisibility(View.VISIBLE);
        }


    }

    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @Subscribe
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
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_START_REFRESH));
    }

    @Override
    public void onHideLoading() {
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_STOP_REFRESH));
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat(throwable.getMessage());
        ContactsList.setVisibility(View.GONE);
        emptyContacts.setVisibility(View.VISIBLE);
        EventBus.getDefault().post(new Pusher(AppConstants.EVENT_BUS_STOP_REFRESH));
    }
}