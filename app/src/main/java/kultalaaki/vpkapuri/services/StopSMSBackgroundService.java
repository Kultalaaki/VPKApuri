/*
 * Created by Kultala Aki on 9/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class StopSMSBackgroundService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent stopService = new Intent(context, SMSBackgroundService.class);
        context.stopService(stopService);
    }
}
