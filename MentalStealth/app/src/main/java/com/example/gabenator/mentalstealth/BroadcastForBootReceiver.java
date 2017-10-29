package com.example.gabenator.mentalstealth;

/**
 * Created by Gabenator on 10/29/2017.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastForBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, MyService.class);
            context.startService(serviceIntent);
        }
    }

}
