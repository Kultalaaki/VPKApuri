/*
 * Created by Kultala Aki on 2/8/2021, 12:33 AM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 9/7/2022
 */

package kultalaaki.vpkapuri.alarms;


import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Meant for VFD alarms.
 * Responsible for finding information from message
 */
public class RescueAlarm extends Alarm implements AlarmMessage {

    private final String[] messageParts;
    private List<String> cities;
    private Map<String, String> rescueIDs;
    private Map<String, String> ambulanceIDs;

    /**
     * Splits message text with regex.
     *
     * @param context Application context
     * @param message SMS Message object
     */
    public RescueAlarm(Context context, SMSMessage message) {
        super(context, message);
        messageParts = super.message.getMessage().split("[;/]");
        this.cities = new ArrayList<>();
    }

    /**
     * Get list of Finnish cities
     */
    private void readCities() {
        Cities readCities = new Cities();
        cities = readCities.getCityList();
    }

    /**
     * Get list of alarm ids for rescue and ambulance.
     */
    private void readAlarmIDs() {
        AlarmIDs alarmIDs = new AlarmIDs();
        this.rescueIDs = alarmIDs.rescueAlarmIDs();
        this.ambulanceIDs = alarmIDs.ambulanceAlarmIDs();
    }

    /**
     * Finding address from message.
     */
    public String getAddress() {
        readCities();
        for (String part : this.messageParts) {
            for (String city : cities) {
                if (part.contains(city)) {
                    return part.trim();
                }
            }
        }

        return "Osoitetta ei löytynyt.";
    }

    /**
     * Find id from message and assign alarm text clarification to it
     */
    public String getAlarmID() {
        readAlarmIDs();
        for (String part : this.messageParts) {
            part = part.trim();

            if (rescueIDs.containsKey(part)) {
                return part + ": " + rescueIDs.get(part);
            } else if (ambulanceIDs.containsKey(part)) {
                // If user has set different alarm sound for emergency alarms, then change that
                if (preferences.getBoolean("boolean_emergency_sound", false)) {
                    super.setAlarmSound("ringtone_emergency");
                }
                return part + ": " + ambulanceIDs.get(part);
            }
        }
        return "Tunnus ei ole luettelossa";
    }

    /**
     * Find urgency class from message.
     */
    public String getUrgencyClass() {
        for (String part : this.messageParts) {
            part = part.trim();
            switch (part) {
                case "A":
                case "B":
                case "C":
                case "D":
                case "N":
                    return part;
            }
        }
        return "Ei löytynyt";
    }

    /**
     * Look if message contains units that user has set in settings
     * return string containing only those units separated by comma
     * <p>
     */
    public String getUnits() {
        ArrayList<String> units = new ArrayList<>();
        units.add(super.preferences.getString("unit1", null));
        units.add(super.preferences.getString("unit2", null));
        units.add(super.preferences.getString("unit3", null));
        units.add(super.preferences.getString("unit4", null));
        units.add(super.preferences.getString("unit5", null));
        units.add(super.preferences.getString("unit6", null));
        units.add(super.preferences.getString("unit7", null));
        units.add(super.preferences.getString("unit8", null));
        units.add(super.preferences.getString("unit9", null));
        units.add(super.preferences.getString("unit10", null));

        // Take message parts and find which part contains units
        for (String part : this.messageParts) {
            for (String unit : units) {
                if (part != null && unit != null) {
                    String partLower = part.toLowerCase();
                    String unitLower = unit.toLowerCase();
                    if (partLower.contains(unitLower)) {
                        // This message part contains units
                        // Take this part and units and compare which units are found
                        return compareUnits(part, units);
                    }
                }
            }
        }

        // No units found in alarm, return blank string.
        return "";
    }

    private String compareUnits(String unitPart, ArrayList<String> units) {
        ArrayList<String> foundUnits = new ArrayList<>();
        String[] unitPartSplitted = unitPart.split(",");
        for (String unit : units) {
            for (String partUnit : unitPartSplitted) {
                if (partUnit.trim().equals(unit.trim())) {
                    foundUnits.add(unit.trim());
                }
            }
        }

        return String.join(", ", foundUnits);
    }

    /**
     * @return Message sender
     */
    public String getSender() {
        return message.getSender();
    }

    /**
     * @return Message text.
     */
    public String getMessage() {
        return message.getMessage();
    }

    /**
     * @return Timestamp when system received message.
     */
    public String getTimeStamp() {
        return message.getTimeStamp();
    }

}
