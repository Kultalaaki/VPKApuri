package kultalaaki.vpkapuri.alarms;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CitiesTest {

    @Test
    public void testThatCitiesAreInList() {
        Cities cities = new Cities();

        assertTrue(cities.getCityList().contains("Akaa"));
        assertTrue(cities.getCityList().contains("Hämeenkyrö"));
        assertTrue(cities.getCityList().contains("Äänekoski"));
    }

}