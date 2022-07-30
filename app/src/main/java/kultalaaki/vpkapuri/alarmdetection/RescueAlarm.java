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


public class RescueAlarm extends Alarm implements Saveable {

    private String address, alarmID, urgencyClass;
    private final String[] messageParts;
    private List<String> cities;
    private Map<String, String> rescueIDs;
    private Map<String, String> ambulanceIDs;

    // Alarm is for finding information from message
    // Possibly extending this class for recognizing unit IDs
    // With unit IDs app can highlight important units for user
    // Easier for stations with multiple units to see what unit is alarmed
    public RescueAlarm(Context context, SMSMessage message) {
        super(context, message);
        messageParts = super.message.getMessage().split(";");
        this.cities = new ArrayList<>();
    }

    public void formAlarm() {
        findAlarmAddress();
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
    private void findAlarmAddress() {
        readCities();
        for (String part : this.messageParts) {
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
        readAlarmIDs();
        for (String part : this.messageParts) {
            part = part.trim();

            if (rescueIDs.containsKey(part)) {
                this.alarmID = part + ": " + rescueIDs.get(part);
                return;
            } else if (ambulanceIDs.containsKey(part)) {
                this.alarmID = part + ": " + ambulanceIDs.get(part);
                // If user has set different alarm sound for emergency alarms, then change that
                if (preferences.getBoolean("boolean_emergency_sound", false)) {
                    super.setAlarmSound("ringtone_emergency");
                }
                return;
            }
        }
        this.alarmID = "Tunnus ei ole luettelossa";
    }

    // Find urgency class and exit when found
    private void findUrgencyClass() {
        for (String part : this.messageParts) {
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
