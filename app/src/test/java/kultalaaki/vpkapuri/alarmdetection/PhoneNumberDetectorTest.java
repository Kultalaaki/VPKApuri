/*
 * Created by Kultala Aki on 6/24/22, 7:49 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/24/22, 7:49 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class PhoneNumberDetectorTest {

    @Mock
    SharedPreferences preferences;

    @Before
    public void before() {
        preferences = Mockito.mock(SharedPreferences.class);
    }

    @Test
    public void alarmNumberListSenderDetected() {
        PhoneNumberDetector detector = new PhoneNumberDetector("0400112326", preferences);
        detector.addNumberToAlarmNumberList("0400112326");
        assertEquals(1, detector.whoSent());
    }

    @Test
    public void memberNumberListSenderDetected() {
        PhoneNumberDetector detector = new PhoneNumberDetector("0400112326", preferences);
        detector.addNumberToMemberNumberList("0400112326");
        assertEquals(2, detector.whoSent());
    }

    @Test
    public void vapepaNumberListSenderDetected() {
        PhoneNumberDetector detector = new PhoneNumberDetector("0400112326", preferences);
        detector.addNumberTovapepaNumberList("0400112326");
        assertEquals(3, detector.whoSent());
    }

    @Test
    public void numberNotInAnyListReturnsZero() {
        PhoneNumberDetector detector = new PhoneNumberDetector("0400112996", preferences);
        detector.addNumberTovapepaNumberList("0400112326");
        assertEquals(0, detector.whoSent());
    }
}