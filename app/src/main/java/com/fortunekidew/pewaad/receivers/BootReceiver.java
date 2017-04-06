package com.fortunekidew.pewaad.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fortunekidew.pewaad.services.BootService;


/**
 * Created by Brian Mwakima on 12/25/16.
 *
 * @Email : mwadime@fortunekidew.co.ke
 * @Author : https://twitter.com/brianmwadime
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        String iAction = intent.getAction();
        if (iAction.equals("android.intent.action.BOOT_COMPLETED")) {
            context.startService(new Intent(context, BootService.class));
        }
    }
}