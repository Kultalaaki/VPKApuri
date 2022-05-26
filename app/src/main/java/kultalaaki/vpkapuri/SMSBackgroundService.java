/*
 * Created by Kultala Aki on 5/17/22, 10:07 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 5/17/22, 9:44 PM
 */

package kultalaaki.vpkapuri;

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
import kultalaaki.vpkapuri.alarmdetection.Alarm;
import kultalaaki.vpkapuri.alarmdetection.PhoneNumberDetector;
import kultalaaki.vpkapuri.alarmdetection.SMSMessage;

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

        // Create notification to make sure this service doesn't get cancelled
        startForegroundNotification();

        // Check starting id of this service
        startIDChecker(startId);

        // Create SMSMessage object from intent
        formMessage(intent);


        // Message senderID comes from PhoneNumberDetector.java
        switch (message.getSenderID()) {
            case 0:
                // Not important message to this app,
                // let it go, let it go
                // Can't hold it back anymore
                stopSelf();
                break;
            case 1:
                // Message from alarm provider. Needs reaction
                // Todo: Make alarm go loud
                // Create Alarm object and use formAlarm() method to create it ready.
                //
                Alarm alarm = new Alarm(message.getSender(), message.getMessage(), message.getTimeStamp());
                alarm.formAlarm();

                saveToDatabase(alarm);
                break;
            case 2:
                // Message from person attending alarm.
                // Todo: Form responder
                // Todo: Save to responder database
                break;
            case 3:
                // It is alarm for Vapepa personnel
                // No need to form alarm before saving
                Alarm alarm1 = new Alarm(message.getSender(), message.getMessage(), message.getTimeStamp());

                saveToDatabase(alarm1);
        }

        return Service.START_STICKY;
    }

    private void checkIntent(Intent intent) {
        if (intent == null) {
            stopSelf();
        }
    }

    private void acquireWakelock() {
        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
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

    /** Creates SMSMessage object
     * Is used for detecting sender
     * SMSMessage object gets ID based on sender number
     * ID defines what app needs to do
     */
    private void formMessage(Intent intent) {
        // Take sms message from broadcastreceiver and make it object
        message = new SMSMessage(intent.getStringExtra("number"),
                intent.getStringExtra("message"),
                intent.getStringExtra("timestamp"));

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        PhoneNumberDetector detector = new PhoneNumberDetector(message.getSender(), preferences);
        message.setSenderID(detector.whoSent());
    }

    public void saveToDatabase(Alarm alarm) {
        /* FireAlarmRepository handles saving alarm to database */
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.insert(new FireAlarm(alarm.getAlarmID(), alarm.getUrgencyClass(),
                alarm.getMessage(), alarm.getAddress(), "", "",
                alarm.getTimeStamp(), alarm.getSender(), "", "", ""));

        Log.i("VPK Apuri", "alarm came through");
        Log.i("Alarm sender: ", alarm.getSender());
        Log.i("Alarm message: ", alarm.getMessage());
        Log.i("Alarm address: ", alarm.getAddress());
        Log.i("Alarm ID: ", alarm.getAlarmID());
        Log.i("Alarm text: ", alarm.getAlarmTextField());
        Log.i("Alarm timestamp: ", alarm.getTimeStamp());
    }

    public void startForegroundNotification() {
        Notification.Builder builder = new Notification.Builder(this, "ACTIVE SERVICE")
                .setContentTitle("VPK Apuri")
                .setContentText("Viestin tarkistus menossa!")
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

        startForeground(MY_ALARM_NOTIFICATION_ID, notification);
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