package com.fortunekidew.pewaad.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.fortunekidew.pewaad.api.APIService;
import com.fortunekidew.pewaad.app.AppConstants;
import com.fortunekidew.pewaad.app.PewaaApplication;
import com.fortunekidew.pewaad.helpers.AppHelper;
import com.fortunekidew.pewaad.helpers.PreferenceManager;
import com.fortunekidew.pewaad.helpers.UtilsPhone;
import com.fortunekidew.pewaad.models.users.contacts.ContactsModel;
import com.fortunekidew.pewaad.models.users.contacts.PusherContacts;
import com.fortunekidew.pewaad.models.users.contacts.SyncContacts;
import com.fortunekidew.pewaad.services.apiServices.ContactsService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

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
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private ContactsService mUsersContacts;
    private Realm realm;
    private Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        AppHelper.LogCat("Sync Adapter created." + "Sync Adapter created.");
        realm = PewaaApplication.getRealmDatabaseInstance();
        initializer(context);
        this.mContext = context;
    }

    private void initializer(Context mContext) {
        APIService mApiService = APIService.with(mContext);
        mUsersContacts = new ContactsService(realm, mContext, mApiService);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        AppHelper.LogCat("Sync Adapter called." + "Sync Adapter called.");
        if (PreferenceManager.getToken(mContext) != null) {
            rx.Observable.create(new rx.Observable.OnSubscribe<List<ContactsModel>>() {
                @Override
                public void call(Subscriber<? super List<ContactsModel>> subscriber) {
                    try {

                        List<ContactsModel> contactsList = UtilsPhone.GetPhoneContacts(mContext);
                        subscriber.onNext(contactsList);
                        subscriber.onCompleted();
                    } catch (Exception e) {
                        subscriber.onError(e);
                    }
                }
            }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(contactsList -> {
                SyncContacts syncContacts = new SyncContacts();
                syncContacts.setContactsModelList(contactsList);
                mUsersContacts.updateContacts(syncContacts).subscribe(contactsModelList -> {
                    if (contactsModelList != null)
                        EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_UPDATE_CONTACTS_LIST, contactsModelList));
                }, throwable -> {
                    EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_UPDATE_CONTACTS_LIST_THROWABLE, throwable));
                });

            }, throwable -> {
                EventBus.getDefault().post(new PusherContacts(AppConstants.EVENT_BUS_UPDATE_CONTACTS_LIST_THROWABLE, throwable));
            });

        }
    }

}
