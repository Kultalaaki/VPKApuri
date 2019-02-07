/*
 * Created by Kultala Aki on 9.9.2017 9:27
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 8.9.2017 19:13
 */

package kultalaaki.vpkapuri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Date;


public class halypuhelu extends BroadcastReceiver {

    String Aika;
    long aika;

    public void onReceive(final Context context, Intent intent) {
        //PreferenceManager.getDefaultSharedPreferences(context);
        //SharedPreferences settings = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);

        String action = intent.getAction();
        String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
        // Käytetään samoja numeroita kuin tekstiviestihälytyksissä 3 ensimmäistä
        /*final String halynumero1 = settings.getString("halyvastaanotto1", null);
        final String halynumero2 = settings.getString("halyvastaanotto2", null);
        final String halynumero3 = settings.getString("halyvastaanotto3", null);*/

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if(action != null) {
                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING) && action.equals("android.intent.action.PHONE_STATE"))  {
                    number = PhoneNumberUtils.formatNumber(number);
                    if (number.charAt(0) == '0') {
                        number = "+358" + number.substring(1);
                    }

                    Toast.makeText(context, "puhelureceiver " + number, Toast.LENGTH_LONG).show();

                    Intent startService = new Intent(context.getApplicationContext(), halyaaniService.class);
                    startService.putExtra("message", "999");
                    startService.putExtra("number", number);
                    context.getApplicationContext().startService(startService);



                    /*if (number.equals(halynumero1) || number.equals(halynumero2) || number.equals(halynumero3)) {
                        aika = System.currentTimeMillis();
                        Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy h:mm:ss aa", new Date(aika));

                        Intent intentpuhelu = new Intent(context.getApplicationContext(), aktiivinenHaly.class);
                        intentpuhelu.setAction(Intent.ACTION_SEND);
                        intentpuhelu.putExtra(Intent.EXTRA_TEXT, "999A Hälytys tuli puheluna. " + Aika);
                        intentpuhelu.setType("phonecall");
                        intentpuhelu.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.getApplicationContext().startActivity(intentpuhelu);
                    }*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


