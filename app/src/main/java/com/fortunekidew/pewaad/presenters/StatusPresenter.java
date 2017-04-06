package com.fortunekidew.pewaad.presenters;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.activities.popups.StatusDelete;
import com.fortunekidew.pewaad.activities.status.EditStatusActivity;
import com.fortunekidew.pewaad.activities.status.StatusActivity;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.users.Pusher;
import com.fortunekidew.pewaad.models.users.status.StatusModel;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import io.realm.Realm;

/**
 * Created by Abderrahim El imame on 28/04/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class StatusPresenter implements Presenter {

    private StatusActivity view;
    private EditStatusActivity editStatusActivity;
    private StatusDelete viewDelete;
    private Realm realm;
    private ContactsService mContactsService;
    private APIService mApiService;

    public StatusPresenter(StatusActivity statusActivity) {
        this.view = statusActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }

    public StatusPresenter(StatusDelete statusDelete) {
        this.viewDelete = statusDelete;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }

    public StatusPresenter(EditStatusActivity editStatusActivity) {
        this.editStatusActivity = editStatusActivity;
        this.realm = PewaaApplication.getRealmDatabaseInstance();
        this.mApiService = APIService.with(this.editStatusActivity);
        this.mContactsService = new ContactsService(this.realm, this.editStatusActivity, this.mApiService);
    }


    @Override
    public void onStart() {

    }

    @Override
    public void onCreate() {
        if (!EventBus.getDefault().isRegistered(view)) EventBus.getDefault().register(view);
        this.mApiService = APIService.with(view);
        mContactsService = new ContactsService(realm, view, this.mApiService);
        getStatusFromLocal();
        getStatusFromServer();

    }

    private void getStatusFromLocal() {
        mContactsService.getAllStatus().subscribe(view::ShowStatus, view::onErrorLoading);

    }

    private void getStatusFromServer() {
        mContactsService.getUserStatus().subscribe(view::updateStatusList, view::onErrorLoading);

    }

    public void getCurrentStatus() {
        mContactsService.getCurrentStatusFromLocal().subscribe(view::ShowCurrentStatus, view::onErrorLoading);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {
    }

    @Override
    public void onDestroy() {
        if (view != null) {
            EventBus.getDefault().unregister(view);
        }
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onStop() {

    }

    public void DeleteAllStatus() {
        AppHelper.showDialog(view, view.getString(R.string.delete_all_status_dialog));
        mContactsService.deleteAllStatus().subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                realm.executeTransaction(realm1 -> realm1.where(StatusModel.class).equalTo("userID", PreferenceManager.getID(view)).findAll().deleteAllFromRealm());
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                view.startActivity(view.getIntent());
            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);

            }
        },throwable -> AppHelper.LogCat("Delete Status Error StatusPresenter "));
    }


    public void DeleteStatus(int statusID) {
        APIService mApiServiceDelete = APIService.with(viewDelete);
        ContactsService mContactsServiceDelete = new ContactsService(realm, viewDelete, mApiServiceDelete);
        AppHelper.showDialog(viewDelete, viewDelete.getString(R.string.deleting));
        mContactsServiceDelete.deleteStatus(statusID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                EventBus.getDefault().post(new Pusher("deleteStatus", String.valueOf(statusID)));
                viewDelete.finish();
            } else {
                AppHelper.hideDialog();
                AppHelper.LogCat("delete  status " + statusResponse.getMessage());
            }
        }, throwable -> {
            AppHelper.hideDialog();
            AppHelper.LogCat("delete  status " + throwable.getMessage());
        });
    }

    public void UpdateCurrentStatus(String status, int statusID) {
        AppHelper.showDialog(view, view.getString(R.string.updating_status));
        mContactsService.updateStatus(statusID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher("updateCurrentStatus"));
                view.ShowCurrentStatus(status);


            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, throwable -> {
            AppHelper.hideDialog();
            AppHelper.LogCat("update current status " + throwable.getMessage());
        });

    }


    public void EditCurrentStatus(String status, int statusID) {
        mContactsService.editStatus(status, statusID).subscribe(statusResponse -> {
            if (statusResponse.isSuccess()) {
                AppHelper.hideDialog();
                AppHelper.Snackbar(editStatusActivity.getBaseContext(), editStatusActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                EventBus.getDefault().post(new Pusher("updateStatus"));
                editStatusActivity.finish();
            } else {
                AppHelper.hideDialog();
                AppHelper.Snackbar(editStatusActivity.getBaseContext(), editStatusActivity.findViewById(R.id.ParentLayoutStatusEdit), statusResponse.getMessage(), AppConstants.MESSAGE_COLOR_WARNING, AppConstants.TEXT_COLOR);
            }
        }, throwable -> {
            AppHelper.hideDialog();
            AppHelper.LogCat("update current status " + throwable.getMessage());
        });

    }

    public void onEventPush(Pusher pusher) {
        switch (pusher.getAction()) {
            case "deleteStatus":
                int id = Integer.parseInt(pusher.getData());
                realm.executeTransaction(realm1 -> realm1.where(StatusModel.class).equalTo("id", id).findFirst().deleteFromRealm());
                AppHelper.Snackbar(view.getBaseContext(), view.findViewById(R.id.ParentLayoutStatus), view.getString(R.string.your_status_updated_successfully), AppConstants.MESSAGE_COLOR_SUCCESS, AppConstants.TEXT_COLOR);
                getStatusFromServer();
                getStatusFromLocal();
                getCurrentStatus();
                break;
            case "create":

                break;
            case "updateStatus":
                getStatusFromLocal();
                getStatusFromServer();
                getCurrentStatus();
                break;
            case "updateCurrentStatus":
                //TODO get status realm p
                //getStatusFromServer();
                // view.startActivity(view.getIntent());
                // view.finish();
                break;
        }
    }
}