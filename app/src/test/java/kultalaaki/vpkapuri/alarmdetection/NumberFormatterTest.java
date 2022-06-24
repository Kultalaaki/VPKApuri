/*
 * Created by Kultala Aki on 6/24/22, 2:01 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/24/22, 2:01 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NumberFormatterTest {

    @Test
    public void NumberFormatterGivesCorrectFormWhenNumberIsInternationallyFormatted() {
        NumberFormatter formatter = new NumberFormatter();
        assertEquals("0401234567", formatter.formatNumber("+358401234567"));
    }

    @Test
    public void NumberFormatterGivesCorrectFormWhenNumberIsNotInternationallyFormatted() {
        NumberFormatter formatter = new NumberFormatter();
        assertEquals("0401234567", formatter.formatNumber("0401234567"));
    }

    @Test
    public void NumberIsNotNumberButStringOfSenderName() {
        NumberFormatter formatter = new NumberFormatter();
        assertEquals("SENDER", formatter.formatNumber("SENDER"));
    }

    @Test
    public void numberIsNull() {
        NumberFormatter formatter = new NumberFormatter();
        assertEquals("Error", formatter.formatNumber(null));
    }

    @Test
    public void numberIsEmpty() {
        NumberFormatter formatter = new NumberFormatter();
        assertEquals("Error", formatter.formatNumber(""));
    }
}