package kultalaaki.vpkapuri.versioncheck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class VersionInfoTest {

    VersionInfo versionInfo;

    @Before
    public void setUp() {
        this.versionInfo = new VersionInfo(
                "name",
                "114",
                "description",
                true);
    }

    @Test
    public void testThatGetNameWorks() {
        assertEquals("name", versionInfo.getName());
    }

    @Test
    public void testThatGetPreReleaseWorks() {
        assertTrue(versionInfo.getPreRelease());
    }

    @Test
    public void testThatGetVersionCodeWorks() {
        assertEquals(114, versionInfo.getVersionCode());
    }

    @Test
    public void testThatToStringWorks() {
        assertEquals("VersionData{tagName='114', description='description'}", versionInfo.toString());
    }
}