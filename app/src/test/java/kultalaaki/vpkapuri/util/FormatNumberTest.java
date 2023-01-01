/*
 * Created by Kultala Aki on 6/24/22, 2:01 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/24/22, 2:01 PM
 */

package kultalaaki.vpkapuri.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FormatNumberTest {

    @Test
    public void NumberFormatterGivesCorrectFormWhenNumberIsInternationallyFormatted() {
        assertEquals("0401234567", FormatNumber.formatFinnishNumber("+358401234567"));
    }

    @Test
    public void NumberFormatterGivesCorrectFormWhenNumberIsNotInternationallyFormatted() {
        assertEquals("0401234567", FormatNumber.formatFinnishNumber("0401234567"));
    }

    @Test
    public void NumberIsNotNumberButStringOfSenderName() {
        assertEquals("SENDER", FormatNumber.formatFinnishNumber("SENDER"));
    }

    @Test
    public void numberIsNull() {
        assertEquals("Error", FormatNumber.formatFinnishNumber(null));
    }

    @Test
    public void numberIsEmpty() {
        assertEquals("Error", FormatNumber.formatFinnishNumber(""));
    }
}