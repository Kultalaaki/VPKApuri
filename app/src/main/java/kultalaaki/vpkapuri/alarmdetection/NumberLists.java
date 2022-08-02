/*
 * Created by Kultala Aki on 6/25/22, 6:25 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/25/22, 6:25 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;

import java.util.ArrayList;

import kultalaaki.vpkapuri.util.FormatNumber;

public class NumberLists {

    protected ArrayList<String> alarmNumbers;
    protected ArrayList<String> memberNumbers;
    protected ArrayList<String> vapepaNumbers;

    private final SharedPreferences preferences;

    /**
     * Add numbers to lists so PhoneNumberDetector can check if message sender is marked in settings
     *
     * @param preferences needed for reading preferences
     */
    public NumberLists(SharedPreferences preferences) {
        super();
        this.alarmNumbers = new ArrayList<>();
        this.memberNumbers = new ArrayList<>();
        this.vapepaNumbers = new ArrayList<>();
        this.preferences = preferences;
        populateAlarmingNumbers();
        populateMemberNumbers();
        populateVapepaNumbers();
    }

    public void populateAlarmingNumbers() {
        for (int i = 1; i <= 11; i++) {
            this.alarmNumbers.add(FormatNumber.formatFinnishNumber(this.preferences.getString("halyvastaanotto" + i, null)));
        }
    }

    public void populateMemberNumbers() {
        for (int i = 1; i <= 50; i++) {
            this.memberNumbers.add(FormatNumber.formatFinnishNumber(this.preferences.getString("puhelinnumero" + i, null)));
        }
    }

    public void populateVapepaNumbers() {
        for (int i = 1; i <= 5; i++) {
            this.vapepaNumbers.add(FormatNumber.formatFinnishNumber(this.preferences.getString("vapepanumber" + i, null)));
        }
    }

    public int getIndexPositionOfMember(String number) {
        return this.memberNumbers.indexOf(number);
    }

    public ArrayList<String> getAlarmNumbers() {
        return this.alarmNumbers;
    }

    public ArrayList<String> getMemberNumbers() {
        return this.memberNumbers;
    }

    public ArrayList<String> getVapepaNumbers() {
        return this.vapepaNumbers;
    }
}
