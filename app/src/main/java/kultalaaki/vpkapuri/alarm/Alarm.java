/*
 * Created by Kultala Aki on 8/2/21, 12:33 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/2/21, 12:33 AM
 */

package kultalaaki.vpkapuri.alarm;

public class Alarm {

    private String sender;
    private String message;
    private String address;
    private String alarmID;
    private String alarmTextField;
    private boolean isAlarm;

    /**
     * @param sender SMSMessage sender
     * @param message SMSMessage message
     */
    public Alarm(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.address = "";
        this.alarmID = "";
        this.alarmTextField = "";
        this.isAlarm = false;
    }


    private void address() {

    }

    private void alarmID() {

    }

    private void alarmTextField() {

    }


    /**
     * @param sender SMSMessage objects sender
     * @return true if sender is defined as alarm sender in apps settings.
     */
    public boolean isAlarm(String sender) {

        return false;
    }
}
