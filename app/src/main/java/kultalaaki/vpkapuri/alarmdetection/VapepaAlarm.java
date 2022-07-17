/*
 * Created by Kultala Aki on 14/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.Context;

public class VapepaAlarm extends Alarm implements Saveable {

    public VapepaAlarm(Context context, SMSMessage message) {
        super(context, message);
    }

    public void setAlarmSound(String sound) {
        super.setAlarmSound(sound);
    }

    public String getAlarmID() {
        return "";
    }

    public String getUrgencyClass() {
        return "";
    }

    public String getMessage() {
        return message.getMessage();
    }

    public String getAddress() {
        return "";
    }

    public String getTimeStamp() {
        return message.getTimeStamp();
    }

    public String getSender() {
        return message.getSender();
    }
}
