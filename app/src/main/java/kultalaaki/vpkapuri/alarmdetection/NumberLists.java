/*
 * Created by Kultala Aki on 6/25/22, 6:25 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/25/22, 6:25 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;

import java.util.ArrayList;

public class NumberLists {

    protected ArrayList<String> alarmNumbers;
    protected ArrayList<String> memberNumbers;
    protected ArrayList<String> vapepaNumbers;

    private final SharedPreferences preferences;

    private final NumberFormatter formatter;

    public NumberLists(SharedPreferences preferences) {
        super();
        this.alarmNumbers = new ArrayList<>();
        this.memberNumbers = new ArrayList<>();
        this.vapepaNumbers = new ArrayList<>();
        this.preferences = preferences;
        this.formatter = new NumberFormatter();
        populateAlarmingNumbers();
        populateMemberNumbers();
        populateVapepaNumbers();
    }

    public void populateAlarmingNumbers() {
        for (int i = 1; i <= 10; i++) {
            alarmNumbers.add(formatter.formatNumber(preferences.getString("halyvastaanotto" + i, null)));
        }
    }

    public void populateMemberNumbers() {
        for (int i = 1; i <= 50; i++) {
            memberNumbers.add(formatter.formatNumber(preferences.getString("nimi" + i, null)));
        }
    }

    public void populateVapepaNumbers() {
        for (int i = 1; i <= 5; i++) {
            vapepaNumbers.add(formatter.formatNumber(preferences.getString("vapepanumber" + i, null)));
        }
    }

    public int getIndexPositionOfMember(String number) {
        return memberNumbers.indexOf(number);
    }

    public ArrayList<String> getAlarmNumbers() {
        return alarmNumbers;
    }

    public ArrayList<String> getMemberNumbers() {
        return memberNumbers;
    }

    public ArrayList<String> getVapepaNumbers() {
        return vapepaNumbers;
    }
}
