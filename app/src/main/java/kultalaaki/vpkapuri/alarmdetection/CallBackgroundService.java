package kultalaaki.vpkapuri.alarmdetection;

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