/*
 * Created by Kultala Aki on 8/4/21, 3:53 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/4/21, 3:53 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

/**
 * Tests will be written after i have completed the reworking of this app
 * Also adding roboelectric to this project
 */
public class RescueAlarmTest {

    /*RescueAlarm rescueAlarm;
    RescueAlarm alarm2;
    RescueAlarm alarm3;

    @Before
    public void setUp() {
        rescueAlarm = new RescueAlarm(InstrumentationRegistry.getInstrumentation().getContext(), new SMSMessage(
                "0401234567",
                "TESTIHÄLYTYS; 103; B; 12:31:32_19.07.2021; Alasentie 12, Hämeenkyrö; Tupala Kyröskoski; RPI32, RPI7415, RPI751, RPI761",
                "Not relevant"));
        alarm2 = new RescueAlarm(InstrumentationRegistry.getInstrumentation().getContext(), new SMSMessage(
                "0401234567",
                "TESTIHÄLYTYS; H352; B; 12:31:32_19.07.2021; Alasentie 12, Ikaalinen; Tupala Kyröskoski; RPI32, RPI7415, RPI751, RPI761",
                "Not relevant"));
        alarm3 = new RescueAlarm(InstrumentationRegistry.getInstrumentation().getContext(), new SMSMessage(
                "0401234567",
                "TESTIHÄLYTYS; 461; B; 12:31:32_19.07.2021; Alasentie 12, Pelkoseniemi; Tupala Kyröskoski; RPI32, RPI7415, RPI751, RPI761",
                "Not relevant"));
        rescueAlarm.formAlarm();
        alarm2.formAlarm();
        alarm3.formAlarm();
    }

    @Test
    public void testAddressSearch() {
        Assert.assertEquals("Was something else", "Alasentie 12, Hämeenkyrö", rescueAlarm.getAddress());
        Assert.assertEquals("Was something else", "Alasentie 12, Ikaalinen", alarm2.getAddress());
        Assert.assertEquals("Was something else", "Alasentie 12, Pelkosenniemi", alarm3.getAddress());
        Assert.assertEquals("Was something else", "Osoitetta ei löytynyt.", alarm3.getAddress());
    }

    @Test
    public void testIDSearch() {
        Assert.assertEquals("Alarm ID was not correctly found", "103", rescueAlarm.getAlarmID());
        Assert.assertEquals("Alarm ID was not correctly found", "H352", alarm2.getAlarmID());
        Assert.assertEquals("Alarm ID was not correctly found", "461", alarm3.getAlarmID());
    }

    @Test
    public void testAlarmTextFieldValue() {
        Assert.assertEquals("Alarm textfield was not correct", "103: PALOHÄLYTYS", rescueAlarm.getAlarmID());
        Assert.assertEquals("Alarm textfield was not correct", "H352: VALMIUSSIIRTO", alarm2.getAlarmID());
        Assert.assertEquals("Alarm textfield was not correct", "461: VAHINGONTORJUNTA: PIENI", alarm3.getAlarmID());
    }

    @Test
    public void testIsAlarmReturnsTrue() {
        //Assert.assertEquals(true, "", alarm.isAlarm());
    }
*/
}