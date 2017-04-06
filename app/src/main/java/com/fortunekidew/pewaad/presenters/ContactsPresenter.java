package com.fortunekidew.pewaad.presenters;


import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.fortunekidew.pewaad.R;
import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.fragments.ContactsFragment;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PermissionHandler;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.UpdateSettings;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.interfaces.Presenter;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.PusherContacts;
import com.fortunekidew.pewaad.models.users.contacts.SyncContacts;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */
public class ContactsPresenter implements Presenter {
    private ContactsFragment contactsFragmentView;
    private Realm realm;
    private ContactsService mContactsService;

    public ContactsPresenter(ContactsFragment contactsFragment) {
        this.contactsFragmentView = contactsFragment;
        this.realm = PewaaApplication.getRealmDatabaseInstance();

    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {

        if (!EventBus.getDefault().isRegistered(contactsFragmentView))
            EventBus.getDefault().register(contactsFragmentView);

        Handler handler = new Handler();
        APIService mApiService = APIService.with(contactsFragmentView.getActivity());
        mContactsService = new ContactsService(realm, contactsFragmentView.getActivity(), mApiService);
        try {
            mContactsService.getAllContacts().subscribe(contactsFragmentView::ShowContacts, contactsFragmentView::onErrorLoading, contactsFragmentView::onHideLoading);
        } catch (Exception e) {
            AppHelper.LogCat("getAllContacts Exception ContactsPresenter ");
        }
        handler.postDelayed(() -> {
            try {
                mContactsService.getContactInfo(PreferenceManager.getID(contactsFragmentView.getActivity())).subscribe(contactsModel -> AppHelper.LogCat("info user log contacts"), throwable -> AppHelper.LogCat("On error "));
                mContactsService.getUserStatus().subscribe(statusModels -> AppHelper.LogCat("status log contacts"), throwable -> AppHelper.LogCat("On error "));
            } catch (Exception e) {
                AppHelper.LogCat("contact info Exception ");
            }
        }, 1500);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {
        realm.close();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {
        if (PermissionHandler.checkPermission(contactsFragmentView.getActivity(), Manifest.permission.READ_CONTACTS)) {
            AppHelper.LogCat("Read contact data permission already granted.");
            contactsFragmentView.onShowLoading();
            rx.Observable.create(new rx.Observable.OnSubscribe<List<ContactsModel>>() {
                @Override
                public void call(Subscriber<? super List<ContactsModel>> subscriber) {
                    try {

                        List<ContactsModel> contactsList = UtilsPhone.GetPhoneContacts(contactsFragmentView.getActivity());
                        subscriber.onNext(contactsList);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(contactsList -> {
                SyncContacts syncContacts = new SyncContacts();
                syncContacts.setContactsModelList(contactsList);
                mContactsService.updateContacts(syncContacts).subscribe(contactsModelList -> {
                    contactsFragmentView.updateContacts(contactsModelList);
                    AppHelper.CustomToast(contactsFragmentView.getActivity(), contactsFragmentView.getString(R.string.success_response_contacts));
                }, throwable -> {
                    contactsFragmentView.onErrorLoading(throwable);
                    AlertDialog.Builder alert = new AlertDialog.Builder(contactsFragmentView.getActivity());
                    alert.setMessage(contactsFragmentView.getString(R.string.error_response_contacts));
                    alert.setPositiveButton(R.string.ok, (dialog, which) -> {
                    });
                    alert.setCancelable(false);
                    alert.show();
                }, contactsFragmentView::onHideLoading);

            }, contactsFragmentView::onErrorLoading);
            mContactsService.getContactInfo(PreferenceManager.getID(contactsFragmentView.getActivity())).subscribe(contactsModel -> AppHelper.LogCat(""), AppHelper::LogCat);

        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            PermissionHandler.requestPermission(contactsFragmentView.getActivity(), Manifest.permission.READ_CONTACTS);
        }

    }

    @Override
    public void onStop() {

    }

    @Subscribe
    public void onEventMainThread(PusherContacts pusher) {
        switch (pusher.getAction()) {
            case "updatedContactsList":
                contactsFragmentView.updateContacts(pusher.getContactsModelList());
//                new Handler().postDelayed(this::checkAppVersion, 2000);
                break;
            case "updatedContactsListThrowable":
                contactsFragmentView.onErrorLoading(pusher.getThrowable());
//                new Handler().postDelayed(this::checkAppVersion, 2000);
                break;
            case "ContactsPermission":
                onRefresh();
                break;
        }
    }

}