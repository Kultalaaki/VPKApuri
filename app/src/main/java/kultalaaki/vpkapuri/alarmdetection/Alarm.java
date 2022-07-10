/*
 * Created by Kultala Aki on 2/8/2021, 12:33 AM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri.alarmdetection;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Alarm {

    private final String sender;
    private final String message;
    private final String timeStamp;
    private String address;
    private String alarmID;
    private String urgencyClass;
    private List<String> cities;
    private Map<String, String> alarmIDs;

    // Alarm is for finding information from message
    // Possibly extending this class for recognizing unit IDs
    // With unit IDs app can highlight important units for user
    // Easier for stations with multiple units to see what unit is alarmed
    public Alarm(String sender, String message, String timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.address = "";
        this.alarmID = "";
        this.urgencyClass = "";
        this.cities = new ArrayList<>();
    }


    // Find address from message
    // Exit when found
    private void findAddressFromString() {
        String[] parts = message.split(";");

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
        String[] parts = message.split(";");

        for (String part : parts) {
            part = part.trim();

            if (alarmIDs.containsKey(part)) {
                this.alarmID = part + ": " + alarmIDs.get(part);
                return;
            }
        }
        this.alarmID = "Tunnus ei ole luettelossa";
    }

    // Find urgency class and exit when found
    private void findUrgencyClass() {
        String[] parts = message.split(";");
        for (String part : parts) {
            part = part.trim();
            switch (part) {
                case "A":
                    this.urgencyClass = part;
                    return;
                case "B":
                    this.urgencyClass = part;
                    return;
                case "C":
                    this.urgencyClass = part;
                    return;
                case "D":
                    this.urgencyClass = part;
                    return;
            }
        }
    }

    public void formAlarm() {
        readCities();
        readAlarmIDs();
        findAddressFromString();
        findAlarmID();
        findUrgencyClass();
    }

    private void readCities() {
        Cities readCities = new Cities();
        cities = readCities.getCityList();
    }

    private void readAlarmIDs() {
        AlarmIDs alarmIDs = new AlarmIDs();
        this.alarmIDs = alarmIDs.mappedAlarmIDs();
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimeStamp() {
        return timeStamp;
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
