package com.fortunekidew.pewaa.presenters;


import android.Manifest;
import android.os.Handler;

import com.fortunekidew.pewaa.R;
import com.fortunekidew.pewaa.api.APIService;
import com.fortunekidew.pewaa.app.PewaaApplication;
import com.fortunekidew.pewaa.fragments.ContactsFragment;
import com.fortunekidew.pewaa.helpers.AppHelper;
import com.fortunekidew.pewaa.helpers.PreferenceManager;
import com.fortunekidew.pewaa.helpers.UpdateSettings;
import com.fortunekidew.pewaa.helpers.UtilsPhone;
import com.fortunekidew.pewaa.interfaces.Presenter;
import com.fortunekidew.pewaa.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaa.models.users.contacts.PusherContacts;
import com.fortunekidew.pewaa.models.users.contacts.SyncContacts;
import com.fortunekidew.pewaa.services.apiServices.ContactsService;

import java.util.Date;
import java.util.List;

import io.realm.Realm;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Abderrahim El imame on 20/02/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsPresenter implements Presenter {
    private ContactsFragment contactsFragmentView;
    private Realm realm;
    private ContactsService mContactsService;
    private UpdateSettings updateSettings;
    private static final int UPDATE_MINUTES = 2 * 60 * 1000;

    public ContactsPresenter(ContactsFragment contactsFragment) {
        this.contactsFragmentView = contactsFragment;
        this.realm = Realm.getDefaultInstance();

    }


    @Override
    public void onStart() {
    }

    @Override
    public void onCreate() {

        if (AppHelper.checkPermission(contactsFragmentView.getActivity(), Manifest.permission.READ_CONTACTS)) {
            AppHelper.LogCat("Read contact data permission already granted.");
        } else {
            AppHelper.LogCat("Please request Read contact data permission.");
            AppHelper.requestPermission(contactsFragmentView.getActivity(), Manifest.permission.READ_CONTACTS);
        }


        if (AppHelper.checkPermission(contactsFragmentView.getActivity(), Manifest.permission.WRITE_CONTACTS)) {
            AppHelper.LogCat("write contact data permission already granted.");
        } else {
            AppHelper.LogCat("Please request write contact data permission.");
            AppHelper.requestPermission(contactsFragmentView.getActivity(), Manifest.permission.WRITE_CONTACTS);
        }

        Handler handler = new Handler();
        APIService mApiService = APIService.with(contactsFragmentView.getActivity());
        updateSettings = new UpdateSettings(contactsFragmentView.getActivity());
        mContactsService = new ContactsService(realm, contactsFragmentView.getActivity(), mApiService);
        mContactsService.getAllContacts().subscribe(contactsFragmentView::ShowContacts, contactsFragmentView::onErrorLoading, contactsFragmentView::onHideLoading);

        // Only update contacts on start if it hasn't been done in the past 2 minutes.
        if (new Date().getTime() - updateSettings.getLastContactsUpdate() > UPDATE_MINUTES) {
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
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(contactsList -> {
                SyncContacts syncContacts = new SyncContacts();
                syncContacts.setUserID(PreferenceManager.getID(contactsFragmentView.getActivity()));
                syncContacts.setContactsModelList(contactsList);
                mContactsService.updateContacts(syncContacts).subscribe(contactsModelList -> {
                    contactsFragmentView.updateContacts(contactsModelList);
                    updateSettings.setLastContactsUpdate();
                }, contactsFragmentView::onErrorLoading);
            }, contactsFragmentView::onErrorLoading);

        }
        handler.postDelayed(() -> {
            try {
                mContactsService.getContactInfo(PreferenceManager.getID(PewaaApplication.getAppContext())).subscribe(contactsModel -> AppHelper.LogCat("info user log contacts"), throwable -> AppHelper.LogCat("On error "));
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
            syncContacts.setUserID(PreferenceManager.getID(contactsFragmentView.getActivity()));
            syncContacts.setContactsModelList(contactsList);
            mContactsService.updateContacts(syncContacts).subscribe(contactsModelList -> {
                contactsFragmentView.updateContacts(contactsModelList);
                updateSettings.setLastContactsUpdate();
                AppHelper.CustomToast(contactsFragmentView.getActivity(), contactsFragmentView.getString(R.string.success_response_contacts));
            }, throwable -> {
                contactsFragmentView.onErrorLoading(throwable);
                AppHelper.CustomToast(contactsFragmentView.getActivity(), contactsFragmentView.getString(R.string.error_response_contacts));
            }, contactsFragmentView::onHideLoading);

        }, contactsFragmentView::onErrorLoading);
        mContactsService.getContactInfo(PreferenceManager.getID(PewaaApplication.getAppContext())).subscribe(contactsModel -> AppHelper.LogCat(""), AppHelper::LogCat);

    }

    @Override
    public void onStop() {

    }

    public void onEventMainThread(PusherContacts pusher) {
        switch (pusher.getAction()) {
            case "updatedContactsList":
                contactsFragmentView.updateContacts(pusher.getContactsModelList());
                break;
            case "updatedContactsListThrowable":
                contactsFragmentView.onErrorLoading(pusher.getThrowable());
                break;
        }
    }
}