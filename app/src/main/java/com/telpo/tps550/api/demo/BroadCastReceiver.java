package com.telpo.tps550.api.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by cardoso on 08/11/2015.
 */
public class BroadCastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction()))
        {

            /* Only perform this code if the BroadcastReceiver received the ACTION_BOOT_COMPLETED action.*/
            Intent i = new Intent(context, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        }

    }
}
