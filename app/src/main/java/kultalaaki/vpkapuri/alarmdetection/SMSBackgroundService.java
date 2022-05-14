/*
 * Created by Kultala Aki on 8/3/21, 11:21 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/3/21, 9:42 AM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.preference.PreferenceManager;

import kultalaaki.vpkapuri.FireAlarm;
import kultalaaki.vpkapuri.FireAlarmRepository;

public class SMSBackgroundService extends Service {

    private static final String TAG = "VPK Apuri käynnissä.";
    private static final int MY_ALARM_NOTIFICATION_ID = 264981;
    private static int previousStartId = 1;

    private SharedPreferences preferences;
    SMSMessage message;

    PowerManager powerManager;
    PowerManager.WakeLock wakelock;

    public SMSBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, final int startId) {

        // Kill process if intent is null
        checkIntent(intent);

        // Acquire wakelock to ensure that android doesn't kill this process
        acquireWakelock();

        // Check starting id of this service
        startIDChecker(startId);

        // Create SMSMessage object from intent
        formMessage(intent);

        // Todo design how to detect what alarm is
        /* Inside message object use sender number to detect if sender is marked as number
        *  to alarm and use that to determine what alarm the message is.
        *
        *
        * */

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String whatAlarm = message.getDetectedSender();

        return Service.START_STICKY;
    }

    private boolean isItAlarm(SMSMessage message) {

        return  false;
    }

    private void checkIntent(Intent intent) {
        if(intent == null) {
            stopSelf();
        }
    }

    private void acquireWakelock() {
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if(powerManager != null) {
            wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "VPK Apuri::Hälytys taustalla.");
        }
    }

    private void startIDChecker(int startId) {
        if (previousStartId != startId) {
            stopSelf(previousStartId);
        }
        previousStartId = startId;
    }

    private void formMessage(Intent intent) {
        // Take sms message from broadcastreceiver and make it object
        message = new SMSMessage(intent.getStringExtra("number"),
                intent.getStringExtra("message"),
                intent.getStringExtra("timestamp"));
    }

    public void saveToDatabase(Alarm alarm) {
        /* FireAlarmRepository handles saving alarm to database */
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.insert(new FireAlarm(alarm.getAlarmID(), alarm.getUrgencyClass(),
                alarm.getMessage(), alarm.getAddress(), "", "",
                alarm.getTimeStamp(), "", "", "", ""));

        Log.i("VPK Apuri", "alarm came through");
        Log.i("Alarm sender: ", alarm.getSender());
        Log.i("Alarm message: ", alarm.getMessage());
        Log.i("Alarm address: ", alarm.getAddress());
        Log.i("Alarm ID: ", alarm.getAlarmID());
        Log.i("Alarm text: ", alarm.getAlarmTextField());
        Log.i("Alarm timestamp: ", alarm.getTimeStamp());
    }

    public void startForegroundNotification(String message) {
        Notification.Builder builder = new Notification.Builder(this, "ACTIVE SERVICE")
                .setContentTitle("VPK Apuri")
                .setContentText(message)
                .setAutoCancel(true);

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ACTIVE SERVICE", "ACTIVE SERVICE", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("VPK Apuri käynnissä.");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        startForeground(15, notification);
    }

    public void onDestroy() {
        super.onDestroy();
        if (wakelock != null) {
            try {
                wakelock.release();
            } catch (Throwable th) {
                // No Need to do anything.
            }

        }
    }
}