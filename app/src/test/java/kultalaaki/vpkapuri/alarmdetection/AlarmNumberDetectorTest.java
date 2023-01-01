package kultalaaki.vpkapuri.alarmdetection;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AlarmNumberDetectorTest {

    NumberLists numberListsStub;
    AlarmNumberDetector alarmNumberDetector;

    @Before
    public void setUp() {
        this.numberListsStub = new AlarmNumberListsStub();
        this.alarmNumberDetector = new AlarmNumberDetector();
    }

    @Test
    public void testThatNumberInAlarmnumbersGiveVlaueOne() {
        assertEquals(1, alarmNumberDetector.numberID("0401234567", numberListsStub));
    }

    @Test
    public void testThatNumberInMemberNumbersGiveValueTwo() {
        assertEquals(2, alarmNumberDetector.numberID("0401234566", numberListsStub));
    }

    @Test
    public void testThatNumberInVapepaNumbersGiveValueThree() {
        assertEquals(3, alarmNumberDetector.numberID("0401234565", numberListsStub));
    }

    @Test
    public void testThatNumberNotInAnyListGiveValueZero() {
        assertEquals(0, alarmNumberDetector.numberID("040404040404", numberListsStub));
    }
}