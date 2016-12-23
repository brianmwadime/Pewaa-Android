package com.fortunekidew.pewaad.activities.groups;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.adapters.recyclerView.groups.AddNewMembersToGroupAdapter;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.presenters.AddNewMembersToGroupPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Abderrahim El imame on 20/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class AddNewMembersToGroupActivity extends AppCompatActivity {
    @BindView(R.id.ContactsList)
    RecyclerView ContactsList;
    @BindView(R.id.ParentLayoutAddNewMembers)
    LinearLayout ParentLayoutAddContact;
    @BindView(R.id.app_bar)
    Toolbar toolbar;

    private List<ContactsModel> mContactsModelList;
    private AddNewMembersToGroupPresenter mAddMembersToGroupPresenter = new AddNewMembersToGroupPresenter(this);
    private int groupID;
    private Realm realm;
    private APIService mApiService;
    private SearchView searchView;
    private AddNewMembersToGroupAdapter mAddMembersToGroupListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_members_to_group);
        ButterKnife.bind(this);

        mApiService = new APIService(this);
        realm = Realm.getDefaultInstance();
        if (getIntent().hasExtra("groupID")) {
            groupID = getIntent().getExtras().getInt("groupID");
        }
        initializeView();
        setupToolbar();
    }

    /**
     * method to initialize the view
     */
    private void initializeView() {
        mAddMembersToGroupPresenter.onCreate();
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(PewaaApplication.getAppContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        ContactsList.setLayoutManager(mLinearLayoutManager);
        mAddMembersToGroupListAdapter = new AddNewMembersToGroupAdapter(this, mContactsModelList, groupID, mApiService);
        ContactsList.setAdapter(mAddMembersToGroupListAdapter);
        // this is the default; this call is actually only necessary with custom ItemAnimators
        ContactsList.setItemAnimator(new DefaultItemAnimator());


    }

    /**
     * method to show contacts
     *
     * @param contactsModelList this  parameter of ShowContacts method
     */
    public void ShowContacts(List<ContactsModel> contactsModelList) {
        RealmList<ContactsModel> contactsModels1 = new RealmList<ContactsModel>();
        for (ContactsModel contactsModel : contactsModelList) {
            contactsModels1.add(contactsModel);
        }
        mContactsModelList = contactsModels1;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mAddMembersToGroupPresenter.onDestroy();
        realm.close();
    }


    /**
     * method to setup the toolbar
     */
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_add_members_to_group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        // Set up SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.search_contacts).getActionView();
        searchView.setIconified(true);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(mQueryTextListener);
        searchView.setQueryHint("Search ...");
        return true;
    }

    private SearchView.OnQueryTextListener mQueryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            mAddMembersToGroupListAdapter.setString(s);
            Search(s);

            return true;
        }
    };


    /**
     * method to start searching
     *
     * @param string this  is parameter for Search method
     */
    public void Search(String string) {

        List<ContactsModel> filteredModelList = FilterList(string);
        if (filteredModelList.size() != 0) {
            mAddMembersToGroupListAdapter.animateTo(filteredModelList);
            ContactsList.scrollToPosition(0);
        }
    }

    /**
     * method to filter the list of contacts
     *
     * @param query this parameter for FilterList  method
     * @return this for what method will return
     */
    private List<ContactsModel> FilterList(String query) {
        Realm realm = Realm.getDefaultInstance();

        List<ContactsModel> contactsModels = realm.where(ContactsModel.class)
                .equalTo("Exist", true)
                .equalTo("Linked", true)
                .beginGroup()
                .contains("phone", query, Case.INSENSITIVE)
                .or()
                .contains("username", query, Case.INSENSITIVE)
                .endGroup()
                .findAll();

        realm.close();
        return contactsModels;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}