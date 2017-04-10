package com.fortunekidew.pewaad.activities.search;

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

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.TextWatcherAdapter;
import com.fortunekidew.pewaad.adapters.recyclerView.contacts.ContactsAdapter;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.PewaaContact;
import com.fortunekidew.pewaad.presenters.SearchContactsPresenter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class SearchContactsActivity extends AppCompatActivity {


    @BindView(R.id.close_btn_search_view)
    ImageView closeBtn;
    @BindView(R.id.search_input)
    TextInputEditText searchInput;
    @BindView(R.id.clear_btn_search_view)
    ImageView clearBtn;
    @BindView(R.id.searchList)
    RecyclerView searchList;

    private ContactsAdapter mContactsAdapter;
    private SearchContactsPresenter mSearchContactsPresenter = new SearchContactsPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        searchInput.setFocusable(true);
        initializerSearchView(searchInput, clearBtn);
        initializerView();
        mSearchContactsPresenter.onCreate();
    }

    /**
     * method to initialize the  view
     */
    private void initializerView() {
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        searchList.setLayoutManager(mLinearLayoutManager);
        mContactsAdapter = new ContactsAdapter(this);
        searchList.setAdapter(mContactsAdapter);
        closeBtn.setOnClickListener(v -> closeSearchView());
        clearBtn.setOnClickListener(v -> clearSearchView());
    }

    /**
     * method to show contacts list
     * @param contacts this is parameter for  ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contacts) {

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

    }

    /**
     * method to clear/reset the search view
     */
    public void clearSearchView() {
        if (searchInput.getText() != null) {
            searchInput.setText("");
            mSearchContactsPresenter.onCreate();
        }
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
        mSearchContactsPresenter.onDestroy();
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
                mContactsAdapter.setString(s.toString());
//                Search(s.toString().trim());
                clearSearchBtn.setVisibility(View.VISIBLE);
            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void afterTextChanged(Editable s) {

                if (s.length() == 0) {
                    clearSearchBtn.setVisibility(View.GONE);
                    mSearchContactsPresenter.onCreate();
                }
            }
        });

    }

    public void onErrorLoading(Throwable throwable) {
        AppHelper.LogCat("Search contacts " + throwable.getMessage());
    }

}
