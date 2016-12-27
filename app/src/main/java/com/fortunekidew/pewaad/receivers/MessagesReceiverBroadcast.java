package com.fortunekidew.pewaad.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public abstract class MessagesReceiverBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context mContext, Intent intent) {
        MessageReceived(mContext, intent);
    }
    protected abstract void MessageReceived(Context context, Intent intent);
}
