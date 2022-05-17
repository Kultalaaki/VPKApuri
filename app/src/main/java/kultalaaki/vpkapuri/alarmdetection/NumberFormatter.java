/*
 * Created by Kultala Aki on 5/14/22, 7:26 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 5/14/22, 7:26 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.telephony.PhoneNumberUtils;

import java.util.Locale;

/**
 * Class for formatting numbers to be exactly same. Android system doesn't reliably give
 * phone numbers in same format. Haven't figured out the logic behind it.
 */
public class NumberFormatter {

    /**
     * @return String value of given phone number.
     */
    public String formatNumber(String numberToFormat) {
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

            return numberToFormat;
        } else {
            return "Error";
        }
    }
}
