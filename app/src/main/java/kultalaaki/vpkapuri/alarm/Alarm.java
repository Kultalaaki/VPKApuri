/*
 * Created by Kultala Aki on 8/2/21, 12:33 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/2/21, 12:33 AM
 */

package kultalaaki.vpkapuri.alarm;

import java.util.List;

public class Alarm {

    private String sender;
    private String message;
    private String timeStamp;
    private String address;
    private String alarmID;
    private String alarmTextField;
    private boolean isAlarm;

    /**
     * @param sender SMSMessage sender
     * @param message SMSMessage message
     */
    public Alarm(String sender, String message, String timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.address = "";
        this.alarmID = "";
        this.alarmTextField = "";
        this.isAlarm = false;
    }

    /**
     * Find address from message
     */
    private void address() {
        ReadFileCities readCities = new ReadFileCities();
        List<String> cities = readCities.getCityList();

        String[] parts = message.split(";");

        for(String part : parts) {
            if(cities.contains(part)) {
                this.address = part;
                return;
            }
        }

        this.address = "Osoitetta ei löytynyt.";
    }

    /**
     * Find alarm id from message and assign alarmtext to it
     */
    private void alarmID() {

        this.alarmID = "";
        this.alarmTextField = "";
    }

    /**
     * @param sender SMSMessage objects sender
     * @return true if sender is defined as alarm sender in apps settings.
     */
    public boolean isAlarm(String sender) {

        return false;
    }
}
