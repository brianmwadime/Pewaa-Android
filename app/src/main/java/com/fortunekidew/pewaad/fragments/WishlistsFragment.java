package com.fortunekidew.pewaad.fragments;


import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.wishlists.WishlistsAdapter;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.interfaces.LoadingData;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.wishlists.WishlistsModel;
import com.fortunekidew.pewaad.presenters.WishlistsPresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.fortunekidew.pewaad.R.id.swipeContainer;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class WishlistsFragment extends Fragment implements LoadingData, RecyclerView.OnItemTouchListener, ActionMode.Callback, SearchView.OnQueryTextListener {

    @BindView(R.id.WishlistsList)
    RecyclerView wishlistList;
    @BindView(R.id.empty)
    LinearLayout emptyWishlists;
    @BindView(swipeContainer)
    SwipeRefreshLayout SwipeToRefresh;

    private MenuItem searchItem;
    private SearchView searchView;

    private GestureDetectorCompat gestureDetector;
    private WishlistsAdapter mWishlistsAdapter;
    private WishlistsPresenter mWishlistsPresenter = new WishlistsPresenter(this);
    private ActionMode actionMode;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_wishlists, container, false);
        ButterKnife.bind(this, mView);
        mWishlistsPresenter.onCreate();
        initializerView();
        return mView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.wishlists_menu, menu);

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
        setHasOptionsMenu(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWishlistsAdapter = new WishlistsAdapter(getActivity());
        wishlistList.setLayoutManager(mLinearLayoutManager);
        wishlistList.setAdapter(mWishlistsAdapter);
        wishlistList.setItemAnimator(new DefaultItemAnimator());
        gestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewBenOnGestureListener());
        SwipeToRefresh.setOnRefreshListener(() -> mWishlistsPresenter.onRefresh());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    /**
     * method to toggle the selection
     *
     * @param position
     */
    private void ToggleSelection(int position) {
        mWishlistsAdapter.toggleSelection(position);
        String title = String.format("%s selected", mWishlistsAdapter.getSelectedItemCount());
        actionMode.setTitle(title);
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.select_conversation_menu, menu);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        EventBus.getDefault().post(new Pusher("actionModeStarted"));
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_conversations:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


                builder.setMessage(R.string.alert_message_delete_wishlist);

                builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
                    int currentPosition;
                    if (mWishlistsAdapter.getSelectedItemCount() != 0) {

                        AppHelper.showDialog(getActivity(), getString(R.string.deleting_wishlist));

                        for (int x = 0; x < mWishlistsAdapter.getSelectedItems().size(); x++) {
                            currentPosition = mWishlistsAdapter.getSelectedItems().get(x);
                            WishlistsModel wishlistsModel = mWishlistsAdapter.getItem(currentPosition);

                        }
                        AppHelper.hideDialog();
                    }
                    if (actionMode != null) {
                        mWishlistsAdapter.clearSelections();
                        actionMode.finish();
                        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    }
                });


                builder.setNegativeButton(R.string.No, (dialog, whichButton) -> {

                });

                builder.show();
                return true;
            default:
                return false;
        }
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
        mWishlistsAdapter.clearSelections();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String search) {
        if (search.isEmpty()) {
            ((WishlistsAdapter) wishlistList.getAdapter()).getFilter().filter("");
        } else {
            ((WishlistsAdapter) wishlistList.getAdapter()).getFilter().filter(search.toLowerCase());
        }

        return false;
    }


    private class RecyclerViewBenOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = wishlistList.findChildViewUnder(e.getX(), e.getY());
            int currentPosition = wishlistList.getChildAdapterPosition(view);

            super.onLongPress(e);
        }

    }

    /**
     * method to show conversation list
     *
     * @param wishlistsModels this is parameter for  ShowWishlist  method
     */
    public void ShowWishlist(List<WishlistsModel> wishlistsModels) {
        SwipeToRefresh.setRefreshing(false);
        if (wishlistsModels.size() != 0) {
            wishlistList.setVisibility(View.VISIBLE);
            emptyWishlists.setVisibility(View.GONE);
            List<WishlistsModel> wishlistsModels1 = new ArrayList<>();
            for (WishlistsModel conversationsModel : wishlistsModels) {
                wishlistsModels1.add(conversationsModel);
            }
            mWishlistsAdapter.setWishlists(wishlistsModels1);

        } else {
            wishlistList.setVisibility(View.GONE);
            emptyWishlists.setVisibility(View.VISIBLE);
        }

    }

    /**
     * method to show wishlist list
     *
     * @param wishlistsModels this is parameter for  UpdateWishlist  method
     */
    public void UpdateWishlist(List<WishlistsModel> wishlistsModels) {
        if (wishlistsModels.size() != 0) {
            wishlistList.setVisibility(View.VISIBLE);
            emptyWishlists.setVisibility(View.GONE);
            List<WishlistsModel> wishlistsModels1 = new ArrayList<WishlistsModel>();
            for (WishlistsModel wishlistsModel : wishlistsModels) {
                wishlistsModels1.add(wishlistsModel);
            }
            mWishlistsAdapter.setWishlists(wishlistsModels1);
        } else {
            wishlistList.setVisibility(View.GONE);
            emptyWishlists.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWishlistsPresenter.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onShowLoading() {
        EventBus.getDefault().post(new Pusher("startRefresh"));
    }

    @Override
    public void onHideLoading() {
        EventBus.getDefault().post(new Pusher("stopRefresh"));
    }


    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @Subscribe
    public void onEventMainThread(Pusher pusher) {
        int messageId = pusher.getMessageId();
        switch (pusher.getAction()) {
            case "ItemIsActivated":
                int idx = wishlistList.getChildAdapterPosition(pusher.getView());
                if (actionMode != null) {
                    ToggleSelection(idx);
                    return;
                }

                break;
            case AppConstants.EVENT_BUS_NEW_WISHLIST:
                WishlistsModel newWishlist = new WishlistsModel();
                newWishlist.setName(pusher.getWishlistObject().getName());
                newWishlist.setDescription(pusher.getWishlistObject().getDescription());
                newWishlist.setCategory(pusher.getWishlistObject().getCategory());
                newWishlist.setRecipients(pusher.getWishlistObject().getRecipients());
                newWishlist.setId(pusher.getWishlistObject().getId());
                newWishlist.setPermissions(pusher.getWishlistObject().getPermissions());
                mWishlistsAdapter.addItem(0, newWishlist);
                break;
            case AppConstants.EVENT_BUS_WISHLIST_DELETED:
            case AppConstants.EVENT_BUS_EXIT_WISHLIST:
                mWishlistsPresenter.updateWishlistList();
                break;
        }
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        SwipeToRefresh.setRefreshing(false);
        wishlistList.setVisibility(View.GONE);
        emptyWishlists.setVisibility(View.VISIBLE);
        AppHelper.LogCat("Wishlists Fragment " + throwable.getMessage());
    }


}
