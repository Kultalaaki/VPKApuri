/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;

import static android.content.Context.POWER_SERVICE;

import kultalaaki.vpkapuri.alarmdetection.SMSBackgroundService;


public class SmsBroadcastReceiver extends BroadcastReceiver {

    private PowerManager.WakeLock wakeLock;

    public void onReceive(Context context, Intent intent) {


        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    "VPKApuri::HälytysServiceTaustalla");
        }

        final Bundle myBundle = intent.getExtras();
        String message = "";
        String senderNum = "";
        long aika;
        String Aika;

        try {

            if (myBundle != null) {
                StringBuilder content = new StringBuilder();
                final Object[] pdus = (Object[]) myBundle.get("pdus");
                int pituus = 0;
                if (pdus != null) {
                    pituus = pdus.length;
                }

                for (int i = 0; i < pituus; i++) {
                    {
                        String format = myBundle.getString("format");
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        senderNum = currentMessage.getDisplayOriginatingAddress();
                        message = currentMessage.getDisplayMessageBody();
                        content.append(message);
                    }
                    message = content.toString();
                }

                aika = System.currentTimeMillis();
                Aika = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(aika));

                Intent startService = new Intent(context.getApplicationContext(), SMSBackgroundService.class);
                startService.putExtra("message", message);
                startService.putExtra("halytysaani", "false");
                startService.putExtra("number", senderNum);
                startService.putExtra("timestamp", Aika);
                context.getApplicationContext().startForegroundService(startService);
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver " + e);
        }

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