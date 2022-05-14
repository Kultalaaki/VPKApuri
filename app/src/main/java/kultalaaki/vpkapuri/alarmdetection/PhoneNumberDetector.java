/*
 * Created by Kultala Aki on 5/14/22, 6:56 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 5/14/22, 6:56 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class PhoneNumberDetector {

    private String numberToCheck;
    private ArrayList<String> alarmNumbers, members, OHTO;
    private SharedPreferences preferences;
    private NumberFormatter formatter;


    // Todo in progress. Think what is best way to approach this.
    public int PhoneNumberDetector (String numberToCheck, SharedPreferences preferences) {
        this.numberToCheck = numberToCheck;
        this.alarmNumbers = new ArrayList<>();
        this.members = new ArrayList<>();
        this.OHTO = new ArrayList<>();
        this.preferences = preferences;
        this.formatter = new NumberFormatter(numberToCheck);

        populateNumbers();
        populateMembers();

        return whoSent();
    }

    private void populateNumbers() {
        for(int i = 1; i <= 10; i++) {
            alarmNumbers.add(preferences.getString("halyvastaanotto" + i, null));
        }
    }

    private void populateMembers() {
        for(int i = 1; i <= 50; i++) {
            members.add(preferences.getString("nimi" + i, null));
        }
    }

    private void populateOHTOAlarmNumbers() {

    }

    /**
     * @return
     * number 0 is not in application settings
     * number 1 is alarm number
     * number 2 is member
     */
    public int whoSent() {
        if(alarmNumbers.contains(numberToCheck)) {
            return 1;
        } else if(members.contains(numberToCheck)) {
            return 2;
        }
        return 0;
    }
}
