/*
 * Created by Kultala Aki on 9.9.2017 9:28
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 30.1.2017 19:35
 */

package kultalaaki.vpkapuri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;
import java.util.Locale;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {

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
                if(pdus != null) {
                    pituus = pdus.length;
                }

                for (int i = 0; i < pituus; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = myBundle.getString("format");
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        senderNum = currentMessage.getDisplayOriginatingAddress();
                        message = currentMessage.getDisplayMessageBody();
                        content.append(message);
                    } else {
                        SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        senderNum = currentMessage.getDisplayOriginatingAddress();
                        message = currentMessage.getDisplayMessageBody();
                        content.append(message);
                    }
                    message = content.toString();
                }

                aika = System.currentTimeMillis();
                Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy H:mm:ss", new Date(aika));
                // Todo poistettu numeroiden formatointi vanha käytöstä tästä alta ja testataan ongelmatapausten kanssa
                //senderNum = PhoneNumberUtils.formatNumber(senderNum);
                /*if (senderNum.charAt(0) == '0') {
                    senderNum = "+358" + senderNum.substring(1);
                }*/
                message += "\n" + Aika;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    senderNum = PhoneNumberUtils.formatNumber(senderNum, Locale.getDefault().getCountry());
                } else {
                    senderNum = PhoneNumberUtils.formatNumber(senderNum); //Deprecated method
                }



                //Toast.makeText(context, "Aseta numero näin: " + senderNum, Toast.LENGTH_LONG).show();

                Intent startService = new Intent(context.getApplicationContext(), IsItAlarmService.class);
                startService.putExtra("message", message);
                startService.putExtra("halytysaani", "false");
                startService.putExtra("number", senderNum);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.getApplicationContext().startForegroundService(startService);
                } else {
                    context.getApplicationContext().startService(startService);
                }

            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver " + e);
        }
    }
}