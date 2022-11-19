/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.receivers;

import static android.content.Context.POWER_SERVICE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.Date;

import kultalaaki.vpkapuri.services.SMSBackgroundService;


public class SmsBroadcastReceiver extends BroadcastReceiver {

    private PowerManager.WakeLock wakeLock;

    public void onReceive(Context context, Intent intent) {

        getWakeLock(context);

        final Bundle bundle = intent.getExtras();
        String message = "";
        String senderNum = "";
        long systemTime;
        String formattedTime = "";

        try {
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                assert pdus != null;
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    String format = bundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    Log.i("VPK Apuri", "Format is: " + format);
                }
                if (messages.length > 0) {
                    StringBuilder content = new StringBuilder();
                    for (SmsMessage sms : messages) {
                        content.append(sms.getDisplayMessageBody());
                        message = content.toString();
                    }
                    senderNum = messages[0].getDisplayOriginatingAddress();
                    systemTime = System.currentTimeMillis();
                    formattedTime = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(systemTime));
                }
                startService(context.getApplicationContext(), message, senderNum, formattedTime);
                releaseWakeLock();
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().log("Could not read sms message: " + e);
        }
    }

    private void startService(Context context, String message, String senderNumber, String formattedTime) {
        Intent startBackgroundService = new Intent(context.getApplicationContext(), SMSBackgroundService.class);
        startBackgroundService.putExtra("message", message);
        startBackgroundService.putExtra("number", senderNumber);
        startBackgroundService.putExtra("timestamp", formattedTime);
        Log.i("TAG", "broadcastreceiver");
        context.getApplicationContext().startForegroundService(startBackgroundService);
    }

    private void getWakeLock(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "VPKApuri::HälytysServiceTaustalla");
        }
    }

    private void releaseWakeLock() {
        try {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (wakeLock.isHeld()) {
                        wakeLock.release();
                    }
                }
            }, 500);
        } catch (Exception e) {
            // Wakelock already released
        }
    }
}