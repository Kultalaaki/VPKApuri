/*
 * Created by Kultala Aki on 2/14/21 9:02 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 2/14/21 7:33 PM
 */

package kultalaaki.vpkapuri;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import java.util.Calendar;

public class BootReadyReceiver extends BroadcastReceiver {

    DBTimer dbTimer;

    /** Android system loses pending intents in reboot.
     *
     *  This BroadcastReceiver checks if there is user defined timers in database.
     *  Setting pending intents to handle set timers.
     *
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            dbTimer = new DBTimer(context);
            Cursor cursor = dbTimer.getAllRows();

            if(cursor != null) {
                while (!cursor.isAfterLast()) {
                    String key = cursor.getString(cursor.getColumnIndex(DBTimer.COL_1));
                    String startTime = cursor.getString(cursor.getColumnIndex(DBTimer.STARTTIME));
                    String stopTime = cursor.getString(cursor.getColumnIndex(DBTimer.STOPTIME));
                    setAlarms(key, startTime, stopTime, context);
                    cursor.moveToNext();
                }
            }
        }
    }

    void setAlarms(String key, String startTime, String stopTime, Context ctx) {

        // requestCode is key + Hour + Minute for canceling reasons
        if(ctx != null) {
            String startHour = startTime.substring(0, 2);
            String startMinute = startTime.substring(3, 5);
            if (startHour.charAt(0) == '0') {
                startHour = startTime.substring(1, 2);
            }
            if (startMinute.charAt(0) == '0') {
                startMinute = startTime.substring(4, 5);
            }
            int startHourPar = Integer.parseInt(startHour);
            int startMinutePar = Integer.parseInt(startMinute);
            int requestCode = Integer.parseInt(key) + startHourPar + startMinutePar;

            AlarmManager alarmMgrStart = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent intentStart = new Intent(ctx, AlarmReceiver.class);
            intentStart.putExtra("primaryKey", key);
            intentStart.putExtra("StartOrStop", "Starting alarmdetection");
            PendingIntent alarmIntentStart = PendingIntent.getBroadcast(ctx, requestCode, intentStart, PendingIntent.FLAG_UPDATE_CURRENT);


            // Setting time based on user input
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, startHourPar);
            calendar.set(Calendar.MINUTE, startMinutePar);

            if(alarmMgrStart != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmMgrStart.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntentStart);
                }
                alarmMgrStart.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentStart);
            }

            String stopHour = stopTime.substring(0, 2);
            String stopMinute = stopTime.substring(3, 5);
            if (stopHour.charAt(0) == '0') {
                stopHour = stopTime.substring(1, 2);
            }
            if (stopMinute.charAt(0) == '0') {
                stopMinute = stopTime.substring(4, 5);
            }
            int stopHourPar = Integer.parseInt(stopHour);
            int stopMinutePar = Integer.parseInt(stopMinute);
            requestCode = Integer.parseInt(key) + stopHourPar + stopMinutePar;

            AlarmManager alarmMgrStop = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            Intent intentStop = new Intent(ctx, AlarmReceiver.class);
            intentStop.putExtra("primaryKey", key);
            intentStop.putExtra("StartOrStop", "Stopping alarmdetection");
            PendingIntent alarmIntentStop = PendingIntent.getBroadcast(ctx, requestCode, intentStop, PendingIntent.FLAG_UPDATE_CURRENT);


            // Setting time based on user input
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(System.currentTimeMillis());
            calendar1.set(Calendar.HOUR_OF_DAY, stopHourPar);
            calendar1.set(Calendar.MINUTE, stopMinutePar);

            if(alarmMgrStop != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmMgrStop.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), alarmIntentStop);
                }
                alarmMgrStop.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntentStop);
            }
        }
    }
}
