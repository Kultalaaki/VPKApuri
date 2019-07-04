/*
 * Created by Kultala Aki on 29.4.2018 20:38
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 29.4.2018 20:38
 */

package kultalaaki.vpkapuri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopIsItAlarmService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent stopService = new Intent(context, IsItAlarmService.class);
        context.stopService(stopService);
    }
}
