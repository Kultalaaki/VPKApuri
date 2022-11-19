/*
 * Created by Kultala Aki on 17/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 17/7/2022
 */

package kultalaaki.vpkapuri.alarms;

/**
 * Implementing this interface alarm can be saved using same saving method in SMSBackgroundService.
 */
public interface AlarmMessage {

    /**
     * @return Alarm id
     */
    String getAlarmID();

    /**
     * @return Alarm urgency class. A B C or D
     */
    String getUrgencyClass();

    /**
     * @return Message text
     */
    String getMessage();

    /**
     * @return Address of message if found.
     */
    String getAddress();

    /**
     * @return Timestamp when system received message.
     */
    String getTimeStamp();

    /**
     * @return Sender of message.
     */
    String getSender();

    /**
     *
     * @return Alarmed units from message
     */
    String getUnits();
}
