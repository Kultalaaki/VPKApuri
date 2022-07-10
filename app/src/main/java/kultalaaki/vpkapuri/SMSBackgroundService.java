/*
 * Created by Kultala Aki on 17/5/2022, 10:07 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;

import kultalaaki.vpkapuri.alarmdetection.Alarm;
import kultalaaki.vpkapuri.alarmdetection.NumberFormatter;
import kultalaaki.vpkapuri.alarmdetection.NumberLists;
import kultalaaki.vpkapuri.alarmdetection.PhoneNumberDetector;
import kultalaaki.vpkapuri.alarmdetection.SMSMessage;

public class SMSBackgroundService extends Service {

    private static int previousStartId = 1;
    private NumberLists numberLists = null;
    private SharedPreferences preferences;
    SMSMessage message;
    PowerManager.WakeLock wakelock;

    public SMSBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("VPK Apuri käynnissä", "onBind()");
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

        // Create SMSMessage object from intent
        formMessage(intent);

        // Create notification to make sure this service doesn't get cancelled
        notificationAlarmMessage();

        // Check starting id of this service
        startIDChecker(startId);

        // Message senderID comes from PhoneNumberDetector.java
        switch (message.getSenderID()) {
            case 0:
                // Not important message to this app,
                // let it go, let it go
                // Can't hold it back anymore
                stopSelf();
                break;
            case 1:
                // Message from alarm provider. Create notification.
                notificationAlarmMessage();

                // Create Alarm object and use formAlarm() method to create it ready.
                Alarm alarm = new Alarm(message.getSender(), message.getMessage(), message.getTimeStamp());
                alarm.formAlarm();
                saveAlarm(alarm);

                // Todo: Make alarm go loud
                break;
            case 2:
                // Message from person attending alarm.
                createPersonComingToAlarm();
                break;
            case 3:
                // It is alarm for Vapepa personnel
                // No need to form alarm before saving
                Alarm vapepaAlarm = new Alarm(message.getSender(), message.getMessage(), message.getTimeStamp());
                saveAlarm(vapepaAlarm);

                // Todo: Make alarm go loud
                break;
        }

        return Service.START_STICKY;
    }

    // Section: Service start procedures
    private void checkIntent(Intent intent) {
        if (intent == null) {
            stopSelf();
        }
    }

    private void acquireWakelock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakelock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "VPK Apuri::Hälytys taustalla.");
        }
    }

    /**
     * Creates SMSMessage object
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

        numberLists = new NumberLists(preferences);

        PhoneNumberDetector phoneNumberDetector = new PhoneNumberDetector();
        NumberFormatter formatter = new NumberFormatter();

        String senderNumber = formatter.formatNumber(message.getSender());

        message.setSenderID(phoneNumberDetector.whoSent(senderNumber, numberLists));
    }

    public void notificationAlarmMessage() {
        // This intent is responsible for opening AlarmActivity
        Intent intentsms = new Intent(getApplicationContext(), AlarmActivity.class);
        intentsms.setAction(Intent.ACTION_SEND);
        intentsms.setType("text/plain");
        intentsms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intentsms);
        PendingIntent pendingIntentWithBackStack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);

        // Stopping this service when "POISTA ILMOITUS" button in notification is clicked
        // Stopping service also removes notification
        Intent stopAlarm = new Intent(this, StopSMSBackgroundService.class);
        PendingIntent stop = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), stopAlarm, PendingIntent.FLAG_IMMUTABLE);

        // Foreground notification to show user and keeping service alive
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SMSBackgroundService.this, "HALYTYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.alarm))
                .setContentText(message.getMessage())
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentWithBackStack)
                .addAction(R.mipmap.ic_launcher, "POISTA ILMOITUS", stop)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDeleteIntent(stop)
                .setAutoCancel(true);

        Notification notification = mBuilder.build();
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        startForeground(264981, notification);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            try {
                notificationManager.cancel(15);
            } catch (Exception e) {
                Log.i("IsItAlarmService", "There was not notification to cancel.");
            }
        }
    }

    private void startIDChecker(int startId) {
        if (previousStartId != startId) {
            stopSelf(previousStartId);
        }
        previousStartId = startId;
    }

    // Section: Saving alarm and incoming personnel
    public void saveAlarm(Alarm alarm) {
        /* FireAlarmRepository handles saving alarm to database */
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.insert(new FireAlarm(alarm.getAlarmID(), alarm.getUrgencyClass(),
                alarm.getMessage(), alarm.getAddress(), "", "",
                alarm.getTimeStamp(), alarm.getSender(), "", "", ""));
    }

    void createPersonComingToAlarm() {
        String positionInList = Integer.toString(numberLists.getIndexPositionOfMember(message.getSender()) + 1);
        String name = preferences.getString("nimi" + positionInList, null);
        boolean driversLicense = preferences.getBoolean("kortti" + positionInList, false);
        boolean smoke = preferences.getBoolean("savusukeltaja" + positionInList, false);
        boolean chemical = preferences.getBoolean("kemikaalisukeltaja" + positionInList, false);
        boolean leader = preferences.getBoolean("yksikonjohtaja" + positionInList, false);
        String vacancyNumber = preferences.getString("vakanssinumero" + positionInList, null);
        String optional1 = preferences.getString("optional1_" + positionInList, null);
        String optional2 = preferences.getString("optional2_" + positionInList, null);
        String optional3 = preferences.getString("optional3_" + positionInList, null);
        String optional4 = preferences.getString("optional4_" + positionInList, null);
        String optional5 = preferences.getString("optional5_" + positionInList, null);
        String driver = "";
        String smok = "";
        String chem = "";
        String lead = "";
        if (driversLicense) {
            driver = "C";
        }
        if (smoke) {
            smok = "S";
        }
        if (chemical) {
            chem = "K";
        }
        if (leader) {
            lead = "Y";
        }

        Responder responder = new Responder(name, vacancyNumber, message.getMessage(), lead, driver, smok, chem, optional1, optional2, optional3, optional4, optional5);
        ResponderRepository repository = new ResponderRepository(getApplication());
        repository.insert(responder);

        Toast.makeText(this, name + " lähetti ilmoituksen.", Toast.LENGTH_SHORT).show();

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