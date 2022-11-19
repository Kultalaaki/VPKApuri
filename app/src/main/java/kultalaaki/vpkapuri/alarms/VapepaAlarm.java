/*
 * Created by Kultala Aki on 14/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 14/7/2022
 */

package kultalaaki.vpkapuri.alarms;

import android.content.Context;

/**
 * Vapepa alarms
 */
public class VapepaAlarm extends Alarm implements AlarmMessage {

    public VapepaAlarm(Context context, SMSMessage message) {
        super(context, message);
    }

    /**
     * @param sound Alarm sound to be used with these alarms
     */
    public void setAlarmSound(String sound) {
        super.setAlarmSound(sound);
    }

    /**
     * @return Vapepa. There is no alarm id in Vapepa alarms.
     */
    public String getAlarmID() {
        return "Vapepa";
    }

    /**
     * @return blank string. No urgency class in these alarms.
     */
    public String getUrgencyClass() {
        return "";
    }

    /**
     * @return Message text.
     */
    public String getMessage() {
        return message.getMessage();
    }

    /**
     * @return blank string. Not yet possible to find address from these messages.
     */
    public String getAddress() {
        return "";
    }

    /**
     * @return Message timestamp when system received message.
     */
    public String getTimeStamp() {
        return message.getTimeStamp();
    }

    /**
     * @return Number of message sender.
     */
    public String getSender() {
        return message.getSender();
    }

    /**
     * @return blank string. No unit finder in Vapepa alarms.
     */
    @Override
    public String getUnits() {
        return "";
    }
}
