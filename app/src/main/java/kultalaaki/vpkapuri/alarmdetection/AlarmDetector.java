/*
 * Created by Kultala Aki on 5/14/22, 6:56 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 5/14/22, 6:56 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

/**
 * Public class for detecting if sender is alarm provider, person who is attending alarm or
 * someone else.
 */
public class AlarmDetector {

    /**
     * @return number 0 is not in application settings
     * number 1 is alarm number
     * number 2 is member
     * number 3 is Vapepa
     */
    public int isItAlarm(String numberToCheck, NumberLists numbers) {
        if (numbers.getAlarmNumbers().contains(numberToCheck)) {
            return 1;
        } else if (numbers.getMemberNumbers().contains(numberToCheck)) {
            return 2;
        } else if (numbers.getVapepaNumbers().contains(numberToCheck)) {
            return 3;
        }
        return 0;
    }
}
