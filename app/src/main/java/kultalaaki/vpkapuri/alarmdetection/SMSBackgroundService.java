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
import android.preference.PreferenceManager;
import android.util.Log;

import kultalaaki.vpkapuri.FireAlarm;
import kultalaaki.vpkapuri.FireAlarmRepository;

public class SMSBackgroundService extends Service {

    private static final String TAG = "VPK Apuri käynnissä.";
    private static final int MY_ALARM_NOTIFICATION_ID = 264981;
    private static int previousStartId = 1;

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
        // If intent is empty then skip
        if (intent != null) {
            // Create Alarm object that holds intent (sms message) information
            Alarm alarm = new Alarm(intent.getStringExtra("number"),
                    intent.getStringExtra("message"),
                    intent.getStringExtra("timestamp"));

            // Sharedpreferences to be used in Alarm class.
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

            // Todo OHTO alarm detection is not done
            // if erica setting is false then it is OHTO alarm for Vapepa

            // Check if message is alarm message
            String whatAlarm = alarm.isAlarm(preferences);

            if (whatAlarm.equals("true")) {
                // Start foreground service notification to ensure survivability of service
                startForegroundNotification("Uusi hälytys!\nViesti piiloitettu!");

                // Todo it is alarm, do things to alarm person
                // TODO Play sound | Create another service to handle alarming sounds


            } else if (whatAlarm.equals("OHTO")){
                // Todo check if it is OHTO alarm
            }

            // Todo
            boolean stationboard = preferences.getBoolean("asemataulu", false);
            if (stationboard) {
                // Todo
                // 1. Compare sender number to numbers of added persons from preferences
                // 2. Add them to database containing responders
            }
        }

        return Service.START_STICKY;
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
    }
}