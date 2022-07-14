/*
 * Created by Kultala Aki on 14/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public abstract class Alarm {

    protected String sound;
    protected SMSMessage message;
    SharedPreferences preferences;

    public Alarm(Context context, SMSMessage message) {
        this.message = message;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        sound = preferences.getString("ringtone_rescue", null);
    }

    public String getAlarmSound() {
        return sound;
    }

    public void setAlarmSound(String alarmSound) {
        sound = preferences.getString(alarmSound, null);
    }

    public abstract String getAlarmID();

    public abstract String getUrgencyClass();

    public abstract String getMessage();

    public abstract String getAddress();

    public abstract String getTimeStamp();

    public abstract String getSender();
}
