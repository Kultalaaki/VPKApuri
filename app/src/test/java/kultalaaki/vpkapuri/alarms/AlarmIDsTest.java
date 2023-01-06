package kultalaaki.vpkapuri.alarms;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

public class AlarmIDsTest {

    HashMap<String, String> rescueIdS;
    HashMap<String, String> ambulanceIds;

    @Before
    public void setUp() {
        AlarmIDs alarmIDs = new AlarmIDs();
        this.rescueIdS = alarmIDs.rescueAlarmIDs();
        this.ambulanceIds = alarmIDs.ambulanceAlarmIDs();
    }

    @Test
    public void testThatRescueIdsContainValues() {
        assertEquals("PALOHÄLYTYS", rescueIdS.get("103"));
        assertEquals("LIIKENNEVÄLINEPALO: MAANALLA: KESKISUURI", rescueIdS.get("415"));
        assertEquals("PELASTUSTOIMI POIKKEUSOLOISSA", rescueIdS.get("901"));
    }

    @Test
    public void testThatAmbulanceIdsContainValues() {
        assertEquals("Eloton", ambulanceIds.get("700"));
        assertEquals("Aistioire", ambulanceIds.get("784"));
        assertEquals("Monipotilastilanne/ Suuronnettomuus", ambulanceIds.get("796"));
    }

}