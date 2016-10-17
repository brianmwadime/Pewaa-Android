package com.fortunekidew.pewaa.activities.search;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.adapters.recyclerView.TextWatcherAdapter;
import com.fortunekidew.pewaa.adapters.recyclerView.wishlists.WishlistsAdapter;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.models.wishlists.WishlistsModel;
import com.fortunekidew.pewaa.presenters.SearchConversationsPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Abderrahim El imame on 8/12/16.
 *
 * @Email : abderrahim.elimame@gmail.com
 * @Author : https://twitter.com/bencherif_el
 */

public class SearchConversationsActivity extends AppCompatActivity {


    @Bind(R.id.close_btn_search_view)
    ImageView closeBtn;
    @Bind(R.id.search_input)
    TextInputEditText searchInput;
    @Bind(R.id.clear_btn_search_view)
    ImageView clearBtn;
    @Bind(R.id.searchList)
    RecyclerView searchList;

    private WishlistsAdapter mWishlistsAdapter;
    private SearchConversationsPresenter mSearchConversationsPresenter = new SearchConversationsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        searchInput.setFocusable(true);
        initializerSearchView(searchInput, clearBtn);
        initializerView();
        mSearchConversationsPresenter.onCreate();
    }

    /**
     * method to initialize the view
     */
    private void initializerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchList.setLayoutManager(mLinearLayoutManager);
        mWishlistsAdapter = new WishlistsAdapter(this);
        searchList.setAdapter(mWishlistsAdapter);
        closeBtn.setOnClickListener(v -> closeSearchView());
        clearBtn.setOnClickListener(v -> clearSearchView());
    }

    /**
     * method to show conversations list
     *
     * @param wishlistsModels this is the parameter for  ShowWishlist  method
     */
    public void ShowConversation(List<WishlistsModel> wishlistsModels) {
        RealmList<WishlistsModel> wishlistsModels1 = new RealmList<WishlistsModel>();
        for (WishlistsModel wishlistsModel : wishlistsModels) {
            wishlistsModels1.add(wishlistsModel);
        }
        mWishlistsAdapter.setWishlists(wishlistsModels1);
    }

    /**
     * method to clear/reset   the search view
     */
    public void clearSearchView() {
        if (searchInput.getText() != null)
            searchInput.setText("");
    }

    /**
     * method to close the search view
     */
    public void closeSearchView() {
        finish();
        overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSearchConversationsPresenter.onDestroy();
    }


    /**
     * method to initialize the search view
     */
    public void initializerSearchView(TextInputEditText searchInput, ImageView clearSearchBtn) {

        final Context context = this;
        searchInput.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

        });
        searchInput.addTextChangedListener(new TextWatcherAdapter() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                clearSearchBtn.setVisibility(View.GONE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mWishlistsAdapter.setString(s.toString());
                Search(s.toString().trim());
                clearSearchBtn.setVisibility(View.VISIBLE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    clearSearchBtn.setVisibility(View.GONE);
                }
            }
        });

    }

    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Conversation search " + throwable.getMessage());
    }

    /**
     * method to start searching
     *
     * @param string this is parameter for  Search  method
     */
    public void Search(String string) {

        final List<WishlistsModel> filteredModelList;
        filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {
            mWishlistsAdapter.animateTo(filteredModelList);
            searchList.scrollToPosition(0);
        }
    }

    /**
     * method to filter the conversation list
     *
     * @param query this is parameter for   method
     * @return this is what  method will return
     */
    private List<WishlistsModel> FilterList(String query) {
        Realm realm = Realm.getDefaultInstance();
        List<WishlistsModel> wishlistsModels = realm.where(WishlistsModel.class)
                .contains("RecipientUsername", query, Case.INSENSITIVE)
                .findAll();
        return wishlistsModels;
    }
}
