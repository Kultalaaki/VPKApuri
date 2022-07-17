/*
 * Created by Kultala Aki on 14/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class Alarm {

    protected String sound;
    protected SMSMessage message;
    protected SharedPreferences preferences;

    public Alarm(Context context, SMSMessage message) {
        this.message = message;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sound = preferences.getString("ringtone_rescue", null);
    }

    public String getAlarmSound() {
        return this.sound;
    }

    public void setAlarmSound(String alarmSound) {
        this.sound = preferences.getString(alarmSound, null);
    }

}
