package kultalaaki.vpkapuri;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Date;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();

    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equalsIgnoreCase(
                Intent.ACTION_BOOT_COMPLETED)) {
            ComponentName component = new ComponentName(context, SmsBroadcastReceiver.class);
            int status = context.getPackageManager().getComponentEnabledSetting(component);
            if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                //Receiver on päällä, älä tee mitään
                Toast toast = Toast.makeText(context, "Hälytysviestien vastaanotto päällä.", Toast.LENGTH_LONG);
                toast.show();
            } else if (status == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
                context.getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                Toast toast = Toast.makeText(context, "Hälytysviestien vastaanotto kytketty päälle.", Toast.LENGTH_LONG);
                toast.show();
            }
        } else {

            Bundle myBundle = intent.getExtras();
            SmsMessage[] messages;
            String message = "";
            String senderNum = "";
            long aika;
            String Aika;

            if (myBundle != null) {
                Object[] pdus = (Object[]) myBundle.get("pdus");

                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = myBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                        //aika = System.currentTimeMillis();
                        //Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy h:mm:ss aa", new Date(aika));
                    } else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        //aika = System.currentTimeMillis();
                        //Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy h:mm:ss aa", new Date(aika));
                    }
                    senderNum = messages[i].getOriginatingAddress();

                    message += messages[i].getMessageBody();
                }

                aika = System.currentTimeMillis();
                Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy h:mm:ss aa", new Date(aika));

                message += "\n" + Aika;

                if (senderNum.charAt(0) == '0') {
                    senderNum = "+358" + senderNum.substring(1);
                }

                PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences settings = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);

                //TODO keksi tähän joku hienompi koodi
                final String halynumero1 = settings.getString("halyvastaanotto1", null);
                final String halynumero2 = settings.getString("halyvastaanotto2", null);
                final String halynumero3 = settings.getString("halyvastaanotto3", null);
                final String halynumero4 = settings.getString("halyvastaanotto4", null);
                final String halynumero5 = settings.getString("halyvastaanotto5", null);
                final String halynumero6 = settings.getString("halyvastaanotto6", null);
                final String halynumero7 = settings.getString("halyvastaanotto7", null);
                final String halynumero8 = settings.getString("halyvastaanotto8", null);
                final String halynumero9 = settings.getString("halyvastaanotto9", null);
                final String halynumero10 = settings.getString("halyvastaanotto10", null);
                //int duration = Toast.LENGTH_LONG;
                //Toast toast = Toast.makeText(context, "senderNum: "+ senderNum, Toast.LENGTH_LONG);
                //toast.show();

                //TODO ja tähän myös
                if (senderNum.equals(halynumero1) || senderNum.equals(halynumero2) || senderNum.equals(halynumero3) || senderNum.equals(halynumero4) || senderNum.equals(halynumero5) || senderNum.equals(halynumero6)
                        || senderNum.equals(halynumero7) || senderNum.equals(halynumero8) || senderNum.equals(halynumero9) || senderNum.equals(halynumero10)) {
                    Intent intentsms = new Intent(context.getApplicationContext(), aktiivinenHaly.class);
                    intentsms.setAction(Intent.ACTION_SEND);
                    intentsms.putExtra(Intent.EXTRA_TEXT, message);
                    intentsms.setType("text/plain");
                    intentsms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intentsms);
                }
            }
        }
    }
}

/*public abstract class SmsBroadcastReceiver extends Service {
    BroadcastReceiver mReceiver;

    // use this as an inner class like here or as a top-level class
    static public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // do something
            final SmsManager sms = SmsManager.getDefault();

                Bundle myBundle = intent.getExtras();
                SmsMessage [] messages;
                String message = "";
                String senderNum = "";
                long aika;
                String Aika = "";

                if (myBundle != null)
                {
                    Object [] pdus = (Object[]) myBundle.get("pdus");

                    messages = new SmsMessage[pdus.length];

                    for (int i = 0; i < messages.length; i++) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            String format = myBundle.getString("format");
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                            aika = System.currentTimeMillis();
                            Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy h:mm:ss aa", new Date(aika));
                        } else {
                            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            aika = System.currentTimeMillis();
                            Aika = (String) DateFormat.format("EEEE, dd MMMM, yyyy h:mm:ss aa", new Date(aika));
                        }
                        senderNum = messages[i].getOriginatingAddress();

                        message += messages[i].getMessageBody();
                    }

                    message += "\n" + Aika;

                    if(senderNum.charAt(0) == '0') {
                        senderNum = "+358" + senderNum.substring(1);
                    }

                    PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences settings = context.getSharedPreferences(context.getPackageName() + "_preferences", Context.MODE_PRIVATE);

                    //TODO keksi tähän joku hienompi koodi
                    final String halynumero1 = settings.getString("halyvastaanotto1", null);
                    final String halynumero2 = settings.getString("halyvastaanotto2", null);
                    final String halynumero3 = settings.getString("halyvastaanotto3", null);
                    final String halynumero4 = settings.getString("halyvastaanotto4", null);
                    final String halynumero5 = settings.getString("halyvastaanotto5", null);
                    final String halynumero6 = settings.getString("halyvastaanotto6", null);
                    final String halynumero7 = settings.getString("halyvastaanotto7", null);
                    final String halynumero8 = settings.getString("halyvastaanotto8", null);
                    final String halynumero9 = settings.getString("halyvastaanotto9", null);
                    final String halynumero10 = settings.getString("halyvastaanotto10", null);
                    //int duration = Toast.LENGTH_LONG;
                    //Toast toast = Toast.makeText(context, "senderNum: "+ senderNum, duration);
                    //toast.show();

                    //TODO ja tähän myös
                    if (senderNum.equals(halynumero1)||senderNum.equals(halynumero2)||senderNum.equals(halynumero3)||senderNum.equals(halynumero4)||senderNum.equals(halynumero5)||senderNum.equals(halynumero6)
                            ||senderNum.equals(halynumero7)||senderNum.equals(halynumero8)||senderNum.equals(halynumero9)||senderNum.equals(halynumero10)) {
                        Intent intentsms = new Intent(context.getApplicationContext(), aktiivinenHaly.class);
                        intentsms.setAction(Intent.ACTION_SEND);
                        intentsms.putExtra(Intent.EXTRA_TEXT, message);
                        intentsms.setType("text/plain");
                        intentsms.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentsms);
                    }
                }
        }

        // constructor
        public MyReceiver(){

        }
    }

    @Override
    public void onCreate() {
        // get an instance of the receiver in your service
        IntentFilter filter = new IntentFilter();
        filter.addAction("action");
        filter.addAction("anotherAction");
        mReceiver = new MyReceiver();
        registerReceiver(mReceiver, filter);
    }
}*/
