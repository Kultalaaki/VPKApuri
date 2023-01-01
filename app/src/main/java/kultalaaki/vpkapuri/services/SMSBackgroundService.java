/*
 * Created by Kultala Aki on 17/5/2022, 10:07 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.preference.PreferenceManager;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import kultalaaki.vpkapuri.AlarmActivity;
import kultalaaki.vpkapuri.AlertActivity;
import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.alarmdetection.AlarmNumberDetector;
import kultalaaki.vpkapuri.alarmdetection.AlarmNumberLists;
import kultalaaki.vpkapuri.alarms.AlarmMessage;
import kultalaaki.vpkapuri.alarms.RescueAlarm;
import kultalaaki.vpkapuri.alarms.SMSMessage;
import kultalaaki.vpkapuri.alarms.VapepaAlarm;
import kultalaaki.vpkapuri.dbfirealarm.FireAlarm;
import kultalaaki.vpkapuri.dbfirealarm.FireAlarmRepository;
import kultalaaki.vpkapuri.dbresponder.Responder;
import kultalaaki.vpkapuri.dbresponder.ResponderRepository;
import kultalaaki.vpkapuri.soundcontrols.AlarmMediaPlayer;
import kultalaaki.vpkapuri.soundcontrols.VibrateController;
import kultalaaki.vpkapuri.util.Constants;
import kultalaaki.vpkapuri.util.FormatNumber;
import kultalaaki.vpkapuri.util.MyNotifications;

/**
 * Background service handles incoming messages
 */
public class SMSBackgroundService extends Service {

    private static int previousStartId = 1;
    private AlarmMediaPlayer alarmMediaPlayer;
    private AlarmNumberLists numberLists = null;
    private SharedPreferences preferences;
    SMSMessage message;
    PowerManager.WakeLock wakelock;

    public SMSBackgroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Set first notfication to make sure service doesn't throw exception
        notificationAlarmMessage("VPK Apuri", "Taustapalvelu tarkistaa onko viesti hälytys.");
    }

    /**
     * Main logic of service
     *
     * @param intent  contains information carried over from SMSBroadcastReceiver.java
     * @param startId service start id
     * @return Service.START_STICKY
     */
    public int onStartCommand(Intent intent, int flags, final int startId) {
        // Kill process if intent is null
        checkIntent(intent);

        // Acquire wakelock to ensure that android doesn't kill this process
        acquireWakelock();

        // Create SMSMessage object from intent
        formMessage(intent);

        // Check starting id of this service and set timer to stop this service
        startIDChecker(startId);

        // Message senderID comes from PhoneNumberDetector.java
        switch (message.getSenderID()) {
            case 0:
                // Not important message to this app,
                // let it go, let it go
                // Can't hold it back anymore
                stopForeground(true);
                stopSelf();
                break;
            case 1:
                // Create Alarm object and use formAlarm() method to create it ready.
                RescueAlarm rescueAlarm = new RescueAlarm(this, message);
                saveAlarm(rescueAlarm);

                startAlertActivity(rescueAlarm.getAlarmID());

                // Message from alarm provider. Create notification.
                notificationAlarmMessage(rescueAlarm.getAlarmID(), rescueAlarm.getMessage());

                String alarmSound = rescueAlarm.getAlarmSound();
                playAlarmSound(alarmSound);
                break;
            case 2:
                // Message from person attending alarm.
                notificationAlarmMessage("Lähtijä", "Lähtijä ilmoittautunut.");
                createPersonComingToAlarm();
                break;
            case 3:
                // It is alarm for Vapepa personnel
                // No need to form alarm before saving
                VapepaAlarm vapepaAlarm = new VapepaAlarm(this, message);

                startAlertActivity("Vapepa hälytys.");

                // If user has set different alarm sound for vapepa alarms, then change that
                if (preferences.getBoolean("boolean_vapepa_sound", false)) {
                    vapepaAlarm.setAlarmSound("ringtone_vapepa");
                }

                notificationAlarmMessage("Vapepa", message.getMessage());

                saveAlarm(vapepaAlarm);

                String vapepaAlarmSound = vapepaAlarm.getAlarmSound();
                playAlarmSound(vapepaAlarmSound);
                break;
        }

        return Service.START_STICKY;
    }

    /**
     * Checks intent for null
     *
     * @param intent if it is null, stop service
     */
    private void checkIntent(Intent intent) {
        if (intent == null) {
            stopForeground(true);
            stopSelf();
        }
    }

    /**
     * Acquire wakelock that service doesn't get stopped before foreground notification
     * is in place
     */
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

        numberLists = new AlarmNumberLists(preferences);

        AlarmNumberDetector alarmDetector = new AlarmNumberDetector();

        // Format number and assign it for sender
        message.setSender(FormatNumber.formatFinnishNumber(message.getSender()));
        // Set sender ID. ID is based on comparing sender number and user set numbers.
        message.setSenderID(alarmDetector.numberID(message.getSender(), numberLists));
    }

    /**
     * Create foreground notification. Notification text is message text.
     * Button in notification "POISTA ILMOITUS" is meant for clearing notification
     * and closing service.
     */
    public void notificationAlarmMessage(String title, String content) {
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
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(SMSBackgroundService.this, Constants.NOTIFICATION_CHANNEL_ALARM)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentWithBackStack)
                .addAction(R.mipmap.ic_launcher, "POISTA ILMOITUS", stop)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDeleteIntent(stop)
                .setAutoCancel(true);

        Notification notification = mBuilder.build();
        mBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        startForeground(Constants.ALARM_NOTIFICATION_ID, notification);
    }

    /**
     * Checks start id and stops alarm media player if it is running
     *
     * @param startId start id of service
     */
    private void startIDChecker(int startId) {
        if (previousStartId != startId) {
            try {
                alarmMediaPlayer.stopAlarmMedia();
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().log("SMSBackgroundService.java: Could not stop alarm media player." + e);
            }
            stopSelf(previousStartId);
        }
        previousStartId = startId;
    }

    private void startAlertActivity(String header) {
        Intent intent = new Intent(this, AlertActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.putExtra("header", header);
        startActivity(intent);
    }

    /**
     * Saves alarm to database
     *
     * @param alarm sms message object that contains all needed information
     */
    public void saveAlarm(AlarmMessage alarm) {
        /* FireAlarmRepository handles saving alarm to database */
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.insert(new FireAlarm(alarm.getAlarmID(), alarm.getUrgencyClass(),
                alarm.getMessage(), alarm.getAddress(), "", "",
                alarm.getTimeStamp(), alarm.getSender(), alarm.getUnits(), "", ""));
    }

    /**
     * Look person attributes from preferences.
     * Persons have to be added in Asemataulu settings page before this recognizes them.
     * Save person to database.
     */
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

        stopForeground(true);
        stopSelf();
    }

    /**
     * Creates new AlarmMediaPlayer.
     * Checks if media player is not null and not already playing.
     * Check if do not disturb is allowed to this app, allowed: ask audio focus,
     * not allowed: start notification vibration to alarm user.
     *
     * @param alarmSound string representation for alarm sound to use. This is user set in settings.
     */
    private void playAlarmSound(String alarmSound) {
        MyNotifications notification = new MyNotifications(this);
        // Make alarm go boom
        try {
            Uri uri = Uri.parse(alarmSound);
            alarmMediaPlayer = new AlarmMediaPlayer(this, preferences, uri);
            if (alarmMediaPlayer.mediaPlayer != null && alarmMediaPlayer.mediaPlayer.isPlaying()) {
                alarmMediaPlayer.stopAlarmMedia();
            }
            if (alarmMediaPlayer.isDoNotDisturbAllowed()) {
                alarmMediaPlayer.audioFocusRequest();
            } else {
                notification.showInformationNotification("Do Not Disturb ei ole sallittu. Anna lupa sovelluksen asetuksissa.");
                // Use vibration notification
                VibrateController vibrateController = new VibrateController(this, preferences);
                vibrateController.vibrateNotification();
            }
        } catch (NullPointerException e) {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.log("NullPointerException: " + e);
            notification.showInformationNotification("Hälytysääntä ei ole valittu.");
        }
    }

    /**
     * When service gets stopped.
     * Release wakelock if it isn't null
     * Stop alarm media player if it isn't null
     */
    public void onDestroy() {
        super.onDestroy();
        if (wakelock != null) {
            try {
                wakelock.release();
            } catch (Throwable th) {
                // No Need to do anything.
            }
        }

        if (alarmMediaPlayer != null) {
            alarmMediaPlayer.stopAlarmMedia();
        }

    }
}