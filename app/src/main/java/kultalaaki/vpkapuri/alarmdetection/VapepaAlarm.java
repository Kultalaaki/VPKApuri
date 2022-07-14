/*
 * Created by Kultala Aki on 14/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.Context;

public class VapepaAlarm extends Alarm {

    public VapepaAlarm(Context context, SMSMessage message) {
        super(context, message);
    }

    @Override
    public String getAlarmID() {
        return "";
    }

    @Override
    public String getUrgencyClass() {
        return "";
    }

    @Override
    public String getMessage() {
        return message.getMessage();
    }

    @Override
    public String getAddress() {
        return "";
    }

    @Override
    public String getTimeStamp() {
        return message.getTimeStamp();
    }

    @Override
    public String getSender() {
        return message.getSender();
    }
}
