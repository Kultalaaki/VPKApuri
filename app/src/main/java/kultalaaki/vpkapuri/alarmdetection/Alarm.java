/*
 * Created by Kultala Aki on 8/2/21, 12:33 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/2/21, 12:33 AM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Alarm {

    private String sender;
    private final String message;
    private final String timeStamp;
    private String address;
    private String alarmID;
    private String alarmTextField;
    private List<String> cities;
    private Map<String, String> alarmIDs;

    /**
     * @param sender  SMSMessage sender
     * @param message SMSMessage message
     */
    public Alarm(String sender, String message, String timeStamp) {
        this.sender = sender;
        this.message = message;
        this.timeStamp = timeStamp;
        this.address = "";
        this.alarmID = "";
        this.alarmTextField = null;
        this.cities = new ArrayList<>();
    }

    /**
     * Find address from message
     */
    private void address() {
        String[] parts = message.split(";");

        // Todo make alarmdetection id search functionality. Test this solution!!
        for (String id : parts) {
            alarmID(id.trim());
            if (!(this.alarmTextField == null)) {
                break;
            }
        }

        for (String part : parts) {
            for(String city : cities) {
                if(part.contains(city)) {
                    this.address = part.trim();
                    return;
                }
            }
        }

        this.address = "Osoitetta ei löytynyt.";
    }

    /**
     * Find alarmdetection id from message and assign alarmtext to it
     */
    private void alarmID(String alarmID) {
        this.alarmID = alarmID;
        this.alarmTextField = alarmIDs.get(alarmID);
    }

    /**
     * @return true if sender is defined alarms sender, keyword in use and message contains keyword,
     * test alarmdetection is sent by user.
     */
    public boolean isAlarm(SharedPreferences preferences) {
        // 1. Test sender against numbers from shared preferences, don't forget VaPePa numbers
        if(sender == null) {
            return false;
        }
        sender = sender.trim();
        //      1.1. If it is VaPePa alarmdetection form alarmdetection differently
        List<String> numbers = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            String number = preferences.getString("halyvastaanotto" + i, null);
            if (number == null) {
                continue;
            }

            number = number.trim();

            numbers.add(number);
        }

        if (numbers.contains(sender)) {
            formAlarm();
            return true;
        }

        // 2. If keyword is in use, look if keyword is found in message
        boolean keyword = preferences.getBoolean("avainsana", false);
        if (keyword) {
            // Gather keywords from preferences to list
            List<String> keywords = new ArrayList<>();
            for (int i = 1; i < 6; i++) {
                String word = preferences.getString("avainsana" + i, null);
                if (word == null) {
                    continue;
                }
                keywords.add(word);
            }

            // Test if message contains either one of keywords
            for (String word : keywords) {
                if (this.message.contains(word)) {
                    formAlarm();
                    return true;
                }
            }
        }

        // 3. Test alarms must also work.. obviously
        if (this.message.contains("TESTIHÄLYTYS") || this.message.contains("SALSA")) {
            formAlarm();
            return true;
        }

        // 4. Todo check and confirm many times before getting this out of hands.
        return false;
    }

    public void formAlarm() {
        readCities();
        readAlarmIDs();
        address();
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
}
