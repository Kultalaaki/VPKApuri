/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.TaskStackBuilder;

public class StopSMSBackgroundService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent startAlarmActivity = new Intent(context, AlarmActivity.class);
        startAlarmActivity.setAction(Intent.ACTION_SEND);
        startAlarmActivity.setType("text/plain");
        startAlarmActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntentWithParentStack(startAlarmActivity);
        PendingIntent pendingIntentWithBackStack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        context.startActivity(startAlarmActivity);

        Intent stopService = new Intent(context, SMSBackgroundService.class);
        context.stopService(stopService);
    }
}
