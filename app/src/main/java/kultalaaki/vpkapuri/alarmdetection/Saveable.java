/*
 * Created by Kultala Aki on 17/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 17/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;

public interface Saveable {

    String getAlarmID();

    String getUrgencyClass();

    String getMessage();

    String getAddress();

    String getTimeStamp();

    String getSender();
}
