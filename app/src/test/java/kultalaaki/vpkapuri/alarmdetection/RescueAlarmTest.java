/*
 * Created by Kultala Aki on 8/4/21, 3:53 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/4/21, 3:53 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class RescueAlarmTest {

    RescueAlarm rescueAlarm;
    RescueAlarm alarm2;
    RescueAlarm alarm3;

    @Before
    public void setUp() {
        rescueAlarm = new RescueAlarm("0403546491", "103; B; 12:31:32_19.07.2021; Alasentie 12, Hämeenkyrö; Tupala Kyröskoski; RPI32, RPI7415, RPI751, RPI761",
                "testing");
        alarm2 = new RescueAlarm("0403546491", "TESTIHÄLYTYS; H352; B; 12:31:32_19.07.2021; Alasentie 12, Hämeenkyrö; Tupala Kyröskoski; RPI32, RPI7415, RPI751, RPI761",
                "testing");
        alarm3 = new RescueAlarm("0403546491", "461; B; 12:31:32_19.07.2021; Alasentie 12, Hämeenkyrö; Tupala Kyröskoski; RPI32, RPI7415, RPI751, RPI761",
                "testing");
        rescueAlarm.formAlarm();
        alarm2.formAlarm();
        alarm3.formAlarm();
    }

    @Test
    public void testAddressSearch() {
        Assert.assertEquals("Was something else", "Alasentie 12, Hämeenkyrö", rescueAlarm.getAddress());
        Assert.assertEquals("Was something else", "Alasentie 12, Hämeenkyrö", alarm2.getAddress());
        Assert.assertEquals("Was something else", "Alasentie 12, Hämeenkyrö", alarm3.getAddress());
    }

    @Test
    public void testIDSearch() {
        Assert.assertEquals("Alarm ID was not correctly found", "103", rescueAlarm.getAlarmID());
        Assert.assertEquals("Alarm ID was not correctly found", "H352", alarm2.getAlarmID());
        Assert.assertEquals("Alarm ID was not correctly found", "461", alarm3.getAlarmID());
    }

    @Test
    public void testAlarmTextFieldValue() {
        Assert.assertEquals("Alarm textfield was not correct", "PALOHÄLYTYS", rescueAlarm.getAlarmTextField());
        Assert.assertEquals("Alarm textfield was not correct", "VALMIUSSIIRTO", alarm2.getAlarmTextField());
        Assert.assertEquals("Alarm textfield was not correct", "VAHINGONTORJUNTA: PIENI", alarm3.getAlarmTextField());
    }

    @Test
    public void testIsAlarmReturnsTrue() {
        //Assert.assertEquals(true, "", alarm.isAlarm());
    }

}