/*
 * Created by Kultala Aki on 5/14/22, 7:26 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 5/14/22, 7:26 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.telephony.PhoneNumberUtils;

import java.util.Locale;

public class NumberFormatter {

    private String number;


    // Todo maybe constructor can be empty?? Think this better.
    public NumberFormatter(String numberToFormat) {
        this.number = numberToFormat;

        numberFormat(number);
    }

    /** @return returns formatted number, like it isn't obvious!
     */
    public String formattedNumber() {
        return number;
    }

    private void numberFormat(String numberToFormat) {
        if (numberToFormat != null && !numberToFormat.isEmpty()) {
            if (numberToFormat.startsWith("O")) {
                numberToFormat = numberToFormat.substring(1);
            }
            numberToFormat = PhoneNumberUtils.formatNumber(numberToFormat, Locale.getDefault().getCountry());
            if (numberToFormat != null) {
                if (numberToFormat.charAt(0) == '0') {
                    numberToFormat = "+358" + numberToFormat.substring(1);
                }
                numberToFormat = numberToFormat.replaceAll("[()\\s-+]+", "");
                numberToFormat = "0" + numberToFormat.substring(3);
            }
        }

        number = numberToFormat;
    }
}
