/*
 * Created by Kultala Aki on 29/7/2022
 * Copyright (c) 2022. All rights reserved.
 * Last modified 26/6/2022
 */

package kultalaaki.vpkapuri.util;


/**
 * Class for formatting numbers to be exactly same. Android system doesn't reliably give
 * phone numbers in same format. Haven't figured out the logic behind it.
 */
public class FormatNumber {

    /**
     * @return String value of given phone number.
     */
    public static String formatFinnishNumber(String number) {
        if (number != null && !number.isEmpty()) {
            if (number.startsWith("0")) {
                number = "0" + number.substring(1);
                number = number.replaceAll("[()\\s-+]+", "");
            } else if (number.startsWith("+358")) {
                number = "0" + number.substring(4);
                number = number.replaceAll("[()\\s-+]+", "");
            }

            return number;
        } else {
            return "Error";
        }
    }


}
