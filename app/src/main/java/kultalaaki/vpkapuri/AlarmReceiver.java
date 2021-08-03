/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:58
 */

package kultalaaki.vpkapuri;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {

    String primaryKey, StartOrStop, currentDay, monday, tuesday, wednesday, thursday, friday, saturday, sunday, mode;
    Calendar calendar;
    DBTimer dbTimer;
    SoundControls soundControls;

    @Override
    public void onReceive(Context context, Intent intent) {

        soundControls = new SoundControls();
        StartOrStop = intent.getStringExtra("StartOrStop");
        primaryKey = intent.getStringExtra("primaryKey");
        dbTimer = new DBTimer(context);
        calendar = Calendar.getInstance();
        currentDay = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH);

        Cursor cursor = dbTimer.timerID(primaryKey);
        monday = cursor.getString(cursor.getColumnIndex(DBTimer.MA));
        tuesday = cursor.getString(cursor.getColumnIndex(DBTimer.TI));
        wednesday = cursor.getString(cursor.getColumnIndex(DBTimer.KE));
        thursday = cursor.getString(cursor.getColumnIndex(DBTimer.TO));
        friday = cursor.getString(cursor.getColumnIndex(DBTimer.PE));
        saturday = cursor.getString(cursor.getColumnIndex(DBTimer.LA));
        sunday = cursor.getString(cursor.getColumnIndex(DBTimer.SU));
        mode = cursor.getString(cursor.getColumnIndex(DBTimer.SELECTOR));

        if(monday.equals("Ma")) {
            monday = "Monday";
        }
        if(tuesday.equals("Ti")) {
            tuesday = "Tuesday";
        }
        if(wednesday.equals("Ke")) {
            wednesday = "Wednesday";
        }
        if(thursday.equals("To")) {
            thursday = "Thursday";
        }
        if(friday.equals("Pe")) {
            friday = "Friday";
        }
        if(saturday.equals("La")) {
            saturday = "Saturday";
        }
        if(sunday.equals("Su")) {
            sunday = "Sunday";
        }

        if(StartOrStop.equals("Starting alarmdetection")) {
            // Alarm start time reached. Check currentDay of week.
            if(currentDay.equals(monday) || currentDay.equals(tuesday) || currentDay.equals(wednesday) || currentDay.equals(thursday) || currentDay.equals(friday) || currentDay.equals(saturday) || currentDay.equals(sunday)) {
               // Check mode to set
               if(mode.equals("Yötila")) {
                   soundControls.setNightMode(context);
               } else if(mode.equals("Äänetön")) {
                   soundControls.setSilent(context);
               }
            }
        } else if(StartOrStop.equals("Stopping alarmdetection")) {
            // Alarm stop time reached, setting sound settings to normal.
            soundControls.setNormal(context);
        }
    }
}
