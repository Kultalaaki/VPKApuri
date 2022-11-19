/*
 * Created by Kultala Aki on 14/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/7/2022
 */

package kultalaaki.vpkapuri.alarms;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

/**
 * Base implementation for alarms
 */
public class Alarm {

    protected String sound;
    protected SMSMessage message;
    protected SharedPreferences preferences;

    /**
     * Assign alarm sound to this alarm object. This class is base implementation for other
     * alarm classes to use.
     *
     * @param context application context
     * @param message Object containing information about message
     */
    public Alarm(Context context, SMSMessage message) {
        this.message = message;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sound = preferences.getString("ringtone_rescue", null);
    }

    /**
     * @return String representation for alarm sound. Need to be parsed before using.
     */
    public String getAlarmSound() {
        return this.sound;
    }

    /**
     * Changing alarm sound
     *
     * @param alarmSound String representation for alarm sound.
     */
    public void setAlarmSound(String alarmSound) {
        this.sound = preferences.getString(alarmSound, null);
    }

}
