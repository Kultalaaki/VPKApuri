/*
 * Created by Kultala Aki on 6/25/22, 6:25 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/25/22, 6:25 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;

import java.util.ArrayList;

import kultalaaki.vpkapuri.util.FormatNumber;

/**
 * Add numbers to lists so PhoneNumberDetector can check if message sender is marked in settings.
 */
public class AlarmNumberLists implements NumberLists {

    protected ArrayList<String> alarmNumbers;
    protected ArrayList<String> memberNumbers;
    protected ArrayList<String> vapepaNumbers;

    private final SharedPreferences preferences;

    /**
     * @param preferences needed for reading preferences
     */
    public AlarmNumberLists(SharedPreferences preferences) {
        super();
        this.alarmNumbers = new ArrayList<>();
        this.memberNumbers = new ArrayList<>();
        this.vapepaNumbers = new ArrayList<>();
        this.preferences = preferences;
        populateAlarmingNumbers();
        populateMemberNumbers();
        populateVapepaNumbers();
    }

    /**
     * Alarming numbers for fire rescue missions.
     */
    private void populateAlarmingNumbers() {
        for (int i = 1; i <= 11; i++) {
            this.alarmNumbers.add(FormatNumber.formatFinnishNumber(this.preferences.getString("halyvastaanotto" + i, null)));
        }
    }

    /**
     * Member numbers for detecting station board incoming members.
     */
    private void populateMemberNumbers() {
        for (int i = 1; i <= 50; i++) {
            this.memberNumbers.add(FormatNumber.formatFinnishNumber(this.preferences.getString("puhelinnumero" + i, null)));
        }
    }

    /**
     * Vapepa numbers for detecting incoming OHTO alarms
     */
    private void populateVapepaNumbers() {
        for (int i = 1; i <= 5; i++) {
            this.vapepaNumbers.add(FormatNumber.formatFinnishNumber(this.preferences.getString("vapepanumber" + i, null)));
        }
    }

    /**
     * @param number Phone number used for finding member position.
     * @return index position where member is in settings
     */
    public int getIndexPositionOfMember(String number) {
        return this.memberNumbers.indexOf(number);
    }

    /**
     * @return Fire alarms numbers list.
     */
    @Override
    public ArrayList<String> getAlarmNumbers() {
        return this.alarmNumbers;
    }

    /**
     * @return Member numbers list.
     */
    @Override
    public ArrayList<String> getMemberNumbers() {
        return this.memberNumbers;
    }

    /**
     * @return Vapepa alarms number list.
     */
    @Override
    public ArrayList<String> getVapepaNumbers() {
        return this.vapepaNumbers;
    }
}
