package com.fortunekidew.pewaad.sync;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by Abderrahim El imame on 01/03/2016.
 * Email : abderrahim.elimame@gmail.com
 */
public class ContactsSyncAdapterService extends Service {

    private static SyncAdapterImpl sSyncAdapter = null;

    public ContactsSyncAdapterService() {
        super();
    }

    private static class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            try {
                ContactsSyncAdapterService.performSync(mContext, account, extras, authority, provider, syncResult);
            } catch (OperationCanceledException e) {

            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return getSyncAdapter().getSyncAdapterBinder();
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null) {
            sSyncAdapter = new SyncAdapterImpl(this);
        }
        return sSyncAdapter;
    }

    private static void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {
        Log.d("pewaa", "performSync: " + account.toString());
    }
}