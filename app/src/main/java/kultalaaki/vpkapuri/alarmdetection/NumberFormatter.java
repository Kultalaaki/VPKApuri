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
    public String formatNumber(String number) {
        if(number != null && !number.isEmpty()) {
            if(number.startsWith("0")) {
                number = "0" + number.substring(1);
                number = number.replaceAll("[()\\s-+]+", "");
            } else if(number.startsWith("+358")) {
                number = "0" + number.substring(4);
                number = number.replaceAll("[()\\s-+]+", "");
            }

            return number;
        } else {
            return "Error";
        }
    }


}
