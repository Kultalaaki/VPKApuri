/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
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
