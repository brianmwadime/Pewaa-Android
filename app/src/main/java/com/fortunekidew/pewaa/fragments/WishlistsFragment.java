package com.fortunekidew.pewaa.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.wishlists.WishlistsAdapter;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.interfaces.LoadingData;
import com.fortunekidew.pewaa.models.users.Pusher;
import com.fortunekidew.pewaa.models.wishlists.WishlistsModel;
import com.fortunekidew.pewaa.presenters.WishlistsPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmList;
import io.socket.client.Socket;

import static com.fortunekidew.pewaa.R.id.swipeContainer;

/**
 * Created by Abderrahim El imame  on 20/01/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class WishlistsFragment extends Fragment implements LoadingData, ActionMode.Callback {

    @Bind(R.id.WishlistsList)
    RecyclerView WishlistList;
    @Bind(R.id.empty)
    LinearLayout EmptyWishlists;
    @Bind(swipeContainer)
    SwipeRefreshLayout SwipeToRefresh;


    private WishlistsAdapter mWishlistsAdapter;
    private WishlistsPresenter mWishlistsPresenter = new WishlistsPresenter(this);
    private Realm realm;
    private GestureDetectorCompat gestureDetector;
    private ActionMode actionMode;
    private Socket mSocket;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mView = inflater.inflate(R.layout.fragment_wishlists, container, false);
        ButterKnife.bind(this, mView);
        realm = Realm.getDefaultInstance();
        mWishlistsPresenter.onCreate();
        initializerView();
        return mView;
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        setHasOptionsMenu(true);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mWishlistsAdapter = new WishlistsAdapter(getActivity(), WishlistList, mSocket);
        WishlistList.setLayoutManager(mLinearLayoutManager);
        WishlistList.setAdapter(mWishlistsAdapter);
        WishlistList.setItemAnimator(new DefaultItemAnimator());

        SwipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWishlistsPresenter.onRefresh();
            }
        });

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


                builder.setMessage(R.string.alert_message_delete_conversation);

                builder.setPositiveButton(R.string.Yes, (dialog, whichButton) -> {
                    Realm realm = Realm.getDefaultInstance();
                    int currentPosition;
                    if (mWishlistsAdapter.getSelectedItemCount() != 0) {

                        AppHelper.showDialog(getActivity(), getString(R.string.deleting_gift));

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
                    realm.close();
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


    private class RecyclerViewBenOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return super.onSingleTapConfirmed(e);
        }

        public void onLongPress(MotionEvent e) {
            View view = WishlistList.findChildViewUnder(e.getX(), e.getY());
            int currentPosition = WishlistList.getChildAdapterPosition(view);

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
            WishlistList.setVisibility(View.VISIBLE);
            EmptyWishlists.setVisibility(View.GONE);
            RealmList<WishlistsModel> wishlistsModels1 = new RealmList<WishlistsModel>();
            for (WishlistsModel conversationsModel : wishlistsModels) {
                wishlistsModels1.add(conversationsModel);
            }
            mWishlistsAdapter.setWishlists(wishlistsModels1);

        } else {
            WishlistList.setVisibility(View.GONE);
            EmptyWishlists.setVisibility(View.VISIBLE);
        }

    }

    /**
     * method to show wishlist list
     *
     * @param wishlistsModels this is parameter for  UpdateWishlist  method
     */
    public void UpdateWishlist(List<WishlistsModel> wishlistsModels) {
        if (wishlistsModels.size() != 0) {
            WishlistList.setVisibility(View.VISIBLE);
            EmptyWishlists.setVisibility(View.GONE);
            RealmList<WishlistsModel> wishlistsModels1 = new RealmList<WishlistsModel>();
            for (WishlistsModel wishlistsModel : wishlistsModels) {
                wishlistsModels1.add(wishlistsModel);
            }
            mWishlistsAdapter.setWishlists(wishlistsModels1);
        } else {
            WishlistList.setVisibility(View.GONE);
            EmptyWishlists.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWishlistsPresenter.onDestroy();
        realm.close();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onShowLoading() {

    }

    @Override
    public void onHideLoading() {
    }


    /**
     * method of EventBus
     *
     * @param pusher this is parameter of onEventMainThread method
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(Pusher pusher) {
        int messageId = pusher.getMessageId();
        switch (pusher.getAction()) {
            case "ItemIsActivated":
                int idx = WishlistList.getChildAdapterPosition(pusher.getView());
                if (actionMode != null) {
                    ToggleSelection(idx);
                    return;
                }

                break;
            case "new_wishlist":
                WishlistsModel newWishlist = new WishlistsModel();
                newWishlist.setName(pusher.getWishlistObject().getName());
                newWishlist.setName(pusher.getWishlistObject().getDescription());
                newWishlist.setId(pusher.getWishlistObject().getId());
                mWishlistsAdapter.addItem(0, newWishlist);
                break;
            case "deleteWishlist":
            case "exitWishlist":
                mWishlistsPresenter.updateWishlistList();
                break;
        }
    }

    @Override
    public void onErrorLoading(Throwable throwable) {
        SwipeToRefresh.setRefreshing(false);
        WishlistList.setVisibility(View.GONE);
        EmptyWishlists.setVisibility(View.VISIBLE);
        AppHelper.LogCat("Wishlists Fragment " + throwable.getMessage());
    }


}
