/*
 * Created by Kultala Aki on 8/2/21, 12:33 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/2/21, 12:33 AM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Todo work in progress..
public class Alarm {

    private String sender;
    private final String message;
    private final String timeStamp;
    private String address;
    private String alarmID;
    private String alarmTextField;
    private String urgencyClass;
    private List<String> cities;
    private Map<String, String> alarmIDs;

    /**
     * @param sender    SMSMessage sender
     * @param message   SMSMessage message
     * @param timeStamp SMSMessage received
     */
    public Alarm(String sender, String message, String timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.address = "";
        this.alarmID = "";
        this.alarmTextField = null;
        this.urgencyClass = "";
        this.cities = new ArrayList<>();
    }

    /**
     * Find address from message
     */
    private void address() {
        String[] parts = message.split(";");

        for (String id : parts) {
            alarmID(id.trim());
            if (!(this.alarmTextField == null)) {
                break;
            }
        }

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

    /**
     * Find id from message and assign alarmtext to it
     */
    private void alarmID(String alarmID) {
        this.alarmID = alarmID;
        this.alarmTextField = alarmIDs.get(alarmID);
    }

    private void urgencyClass() {
        String[] parts = message.split(";");
        for (String part : parts) {
            if (part.trim().equals("A") || part.trim().equals("B") || part.trim().equals("C") || part.trim().equals("D")) {
                this.urgencyClass = part;
            }
        }
    }

    public void formAlarm() {
        readCities();
        readAlarmIDs();
        address();
        urgencyClass();
    }

    private void readCities() {
        Cities readCities = new Cities();
        cities = readCities.getCityList();
    }

    private void readAlarmIDs() {
        AlarmIDs readFile = new AlarmIDs();
        alarmIDs = readFile.getAlarmIDs();
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

    public String getAlarmTextField() {
        return alarmTextField;
    }

    public String getUrgencyClass() {
        return this.urgencyClass;
    }
}
