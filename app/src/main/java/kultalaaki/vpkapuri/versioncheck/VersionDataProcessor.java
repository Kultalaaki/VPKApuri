package kultalaaki.vpkapuri.versioncheck;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import kultalaaki.vpkapuri.databasebackupandrestore.JSONArrayReader;

public class VersionDataProcessor {

    private final VersionData versionDataReader;
    private final List<VersionInfo> versions;
    private final int currentVersionCode;
    private VersionInfo highestStable, highestBeta;

    /**
     * VersionDataProcessor. Handles processing version information from VersionDataReader.
     *
     * @param versionCode installed app version code
     */
    public VersionDataProcessor(int versionCode) throws MalformedURLException {
        this.versionDataReader = new GithubVersiondataReader();
        this.versions = new ArrayList<>();
        this.currentVersionCode = versionCode;
    }

    /**
     * Transform JSON objects to VersionData and adds them to ArrayList
     *
     * @throws IOException   caller must handle
     * @throws JSONException caller must handle
     */
    public void readObjectsToArray() throws IOException, JSONException {
        JSONArrayReader json = new JSONArrayReader(versionDataReader.getVersionData());
        ArrayList<JSONObject> objects = json.getObjects();

        for (JSONObject object : objects) {
            String name = object.getString("name");
            String tagName = object.getString("tag_name");
            String description = object.getString("body");

            boolean preRelease = object.getBoolean("prerelease");

            versions.add(new VersionInfo(name, tagName, description, preRelease));
        }
    }

    /**
     * Iterate over array and check highest version ID's
     * Beta and stable are both evaluated
     */
    public void setHighestVersions() {
        int highestStableCode = 0;
        int highestPreReleaseCode = 0;
        for (VersionInfo version : versions) {
            if (version.getPreRelease()) {
                if (version.getVersionCode() > highestPreReleaseCode) {
                    highestPreReleaseCode = version.getVersionCode();
                    highestBeta = version;
                }
            } else if (version.getVersionCode() > highestStableCode) {
                highestStableCode = version.getVersionCode();
                highestStable = version;
            }
        }
    }

    /**
     * Compare current version to newest stable on github
     *
     * @return true if new version is available
     */
    public boolean isNewStableVersionAvailable() {
        return currentVersionCode < highestStable.getVersionCode();
    }

    /**
     * Compare current version to newest Beta on github
     *
     * @return true if new version is available
     */
    public boolean isNewBetaVersionAvailable() {
        return currentVersionCode < highestBeta.getVersionCode();
    }

    /**
     * Getter
     *
     * @return newest stable version
     */
    public VersionInfo getHighestStable() {
        return highestStable;
    }

    /**
     * Getter
     *
     * @return newest beta version
     */
    public VersionInfo getHighestBeta() {
        return highestBeta;
    }


}
