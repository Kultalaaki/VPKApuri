/*
 * Created by Kultala Aki on 5/17/22, 10:08 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 4/29/22, 5:13 PM
 */

package kultalaaki.vpkapuri;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class CallBackgroundService extends Service {
    public CallBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}