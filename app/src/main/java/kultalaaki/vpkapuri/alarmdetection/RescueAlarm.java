/*
 * Created by Kultala Aki on 2/8/2021, 12:33 AM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RescueAlarm extends Alarm {

    private String address, alarmID, urgencyClass;
    private List<String> cities = new ArrayList<>();
    private Map<String, String> rescueIDs;
    private Map<String, String> ambulanceIDs;

    // Alarm is for finding information from message
    // Possibly extending this class for recognizing unit IDs
    // With unit IDs app can highlight important units for user
    // Easier for stations with multiple units to see what unit is alarmed
    public RescueAlarm(Context context, SMSMessage message) {
        super(context, message);
    }

    public void formAlarm() {
        readCities();
        readAlarmIDs();
        getAlarmAddress();
        findAlarmID();
        findUrgencyClass();
    }

    private void readCities() {
        Cities readCities = new Cities();
        cities = readCities.getCityList();
    }

    private void readAlarmIDs() {
        AlarmIDs alarmIDs = new AlarmIDs();
        this.rescueIDs = alarmIDs.rescueAlarmIDs();
        this.ambulanceIDs = alarmIDs.ambulanceAlarmIDs();
    }

    // Find address from message
    // Exit when found
    private void getAlarmAddress() {
        String[] parts = message.getMessage().split(";");

        for (String part : parts) {
            for (String city : cities) {
                if (part.contains(city)) {
                    this.address = part.trim();
                    return;
                }
            }
        }

        this.address = "Osoitetta ei löytynyt.";
    }

    // Find id from message and assign alarmtext to it
    // Exit when found
    private void findAlarmID() {
        String[] parts = message.getMessage().split(";");

        for (String part : parts) {
            part = part.trim();

            if (rescueIDs.containsKey(part)) {
                this.alarmID = part + ": " + rescueIDs.get(part);
                return;
            } else if (ambulanceIDs.containsKey(part)) {
                this.alarmID = part + ": " + ambulanceIDs.get(part);
                super.setAlarmSound("ringtone_emergency");
                return;
            }
        }
        this.alarmID = "Tunnus ei ole luettelossa";
    }

    // Find urgency class and exit when found
    private void findUrgencyClass() {
        String[] parts = message.getMessage().split(";");
        for (String part : parts) {
            part = part.trim();
            switch (part) {
                case "A":
                case "B":
                case "C":
                case "D":
                    this.urgencyClass = part;
                    return;
            }
        }
    }

    public String getSender() {
        return message.getSender();
    }

    public String getMessage() {
        return message.getMessage();
    }

    public String getTimeStamp() {
        return message.getTimeStamp();
    }

    public String getAddress() {
        return address;
    }

    public String getAlarmID() {
        return alarmID;
    }

    public String getUrgencyClass() {
        return this.urgencyClass;
    }

}
