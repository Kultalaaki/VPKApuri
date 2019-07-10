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
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;

import java.util.Date;

public class PhonecallBroadcastReceiver extends BroadcastReceiver {

    //The receiver will be recreated whenever android feels like it.  We need a static variable to remember data between instantiations

    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    //private static Date callStartTime;
    private static boolean isIncoming;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "check receive", Toast.LENGTH_LONG).show();
        //We listen to two intents.  The new outgoing call only tells us of an outgoing call.  We use it to get the number.
        String action = intent.getAction();

        if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
            String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

            if (number.contains("0") || number.contains("1") || number.contains("2") || number.contains("3") || number.contains("4") || number.contains("5") || number.contains("6") || number.contains("7")
                    || number.contains("8") || number.contains("9")) {
                String stateStr = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

                long aika = System.currentTimeMillis();
                String Aika = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(aika));

                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }

                onCallStateChanged(context, state, number, Aika);
            }
        }
    }

    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, String aika) {
        //Toast.makeText(ctx, "check number " + number, Toast.LENGTH_LONG).show();
        if (number != null) {
            Intent startService = new Intent(ctx.getApplicationContext(), IsItAlarmService.class);
            startService.putExtra("message", "Hälytys tulossa.");
            startService.putExtra("halytysaani", "true");
            startService.putExtra("timestamp", aika);
            //number = PhoneNumberUtils.formatNumber(number);
            //Log.i("onIncomingCallStarted", "startService");
            //Toast.makeText(ctx, "service starttaa: " + number, Toast.LENGTH_LONG).show();
            startService.putExtra("number", number);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                //Log.e("onIncomingCallStarted", "startService");
                ctx.getApplicationContext().startForegroundService(startService);
            } else {
                //Log.e("onIncomingCallStarted", "startService");
                ctx.getApplicationContext().startService(startService);
            }

        }

    }

    protected void onOutgoingCallStarted(Context ctx, String number) {
        if (number != null) {
            //Log.e("onOutgoingCallStarted", "stopService");
            Intent stopService = new Intent(ctx.getApplicationContext(), IsItAlarmService.class);
            ctx.getApplicationContext().stopService(stopService);
        }
        //Toast.makeText(ctx, "puhelureceiver " + number, Toast.LENGTH_LONG).show();

    }

    protected void onIncomingCallEnded(Context ctx, String number) {
        if (number != null) {
            //Log.e("onIncomingCallEnded", "stopService");
            Intent stopService = new Intent(ctx.getApplicationContext(), IsItAlarmService.class);
            ctx.getApplicationContext().stopService(stopService);
        }

        /*Toast.makeText(ctx, "puhelureceiver " + number, Toast.LENGTH_LONG).show();
        Intent startService = new Intent(ctx.getApplicationContext(), IsItAlarmService.class);
        startService.putExtra("message", "999A Hälytys tuli puheluna. ");
        startService.putExtra("halytysaani", false);
        startService.putExtra("number", number);
        ctx.getApplicationContext().startService(startService);*/
    }

    protected void onOutgoingCallEnded(/*Context ctx, String number, Date start, Date end*/) {
        //Log.e("onOutgoingCallEnded", "Mitään ei tehdä.");
    }

    protected void onMissedCall(Context ctx, String number) {
        if (number != null) {
            //Log.e("onMissedCall", "stopService");
            Intent stopService = new Intent(ctx.getApplicationContext(), IsItAlarmService.class);
            ctx.getApplicationContext().stopService(stopService);
        }

    }

    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number, String aika) {
        if (lastState == state) {
            //No change, debounce extras
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                onIncomingCallStarted(context, number, aika);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    //callStartTime = new Date();
                    onOutgoingCallStarted(context, number);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    //Ring but no pickup-  a miss
                    onMissedCall(context, number);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number);
                } else {
                    onOutgoingCallEnded(/*context, savedNumber, callStartTime, new Date()*/);
                }
                break;
        }
        lastState = state;
    }
}