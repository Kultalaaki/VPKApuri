package kultalaaki.vpkapuri.versioncheck;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import kultalaaki.vpkapuri.databasebackupandrestore.JSONArrayReader;

public class VersionDataProcessor {

    private final VersionDataReader versionDataReader;
    private final List<VersionData> versions;
    private final int currentVersionCode;
    private VersionData highestStable, highestBeta;

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
    private void readObjectsToArray() throws IOException, JSONException {
        JSONArrayReader json = new JSONArrayReader(versionDataReader.getVersionData());
        ArrayList<JSONObject> objects = json.getObjects();

        for (JSONObject object : objects) {
            String name = object.getString("name");
            String tagName = object.getString("tag_name");
            String description = object.getString("body");
            String downloadUri = "";

            JSONArrayReader jsonInner = new JSONArrayReader(object.getString("assets"));

            ArrayList<JSONObject> jsonObjectsInner = jsonInner.getObjects();
            for (JSONObject objectInner : jsonObjectsInner) {
                downloadUri = objectInner.getString("browser_download_url");
            }
            boolean preRelease = object.getBoolean("prerelease");
            int versionId = object.getInt("id");
            versions.add(new VersionData(name, tagName, description, downloadUri, preRelease, versionId));
        }
    }

    /**
     * Iterate over array and check highest version ID's
     * Beta and stable are both evaluated
     */
    private void setHighestVersions() {
        int highestStableID = 0;
        int highestPreReleaseID = 0;
        for (VersionData version : versions) {
            if (version.getPreRelease()) {
                if (version.getVersionID() > highestPreReleaseID) {
                    highestPreReleaseID = version.getVersionID();
                    highestBeta = version;
                }
            } else if (version.getVersionID() > highestStableID) {
                highestStableID = version.getVersionID();
                highestStable = version;
            }
        }
    }

    /**
     * Compare current version to newest stable on github
     *
     * @return true if new version is available
     * @throws JSONException caller must handle
     * @throws IOException   caller must handle
     */
    public boolean isNewStableVersionAvailable() throws JSONException, IOException {
        readObjectsToArray();
        setHighestVersions();
        return currentVersionCode < highestStable.getVersionCode();
    }

    /**
     * Compare current version to newest Beta on github
     *
     * @return true if new version is available
     * @throws JSONException caller must handle
     * @throws IOException   caller must handle
     */
    public boolean isNewBetaVersionAvailable() throws JSONException, IOException {
        readObjectsToArray();
        setHighestVersions();
        return currentVersionCode < highestBeta.getVersionCode();
    }

    /**
     * Getter
     *
     * @return newest stable version
     */
    public VersionData getHighestStable() {
        return highestStable;
    }

    /**
     * Getter
     *
     * @return newest beta version
     */
    public VersionData getHighestBeta() {
        return highestBeta;
    }


}
