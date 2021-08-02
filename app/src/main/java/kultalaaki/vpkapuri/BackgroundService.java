/*
 * Created by Kultala Aki on 8/2/21, 6:06 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/2/21, 6:06 PM
 */

package kultalaaki.vpkapuri;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BackgroundService extends Service {

    public BackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, final int startId) {

        return Service.START_STICKY;
    }

    public void onDestroy() {
        super.onDestroy();
    }
}