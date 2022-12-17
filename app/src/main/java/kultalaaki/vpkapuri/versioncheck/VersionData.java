package kultalaaki.vpkapuri.versioncheck;

import androidx.annotation.NonNull;

/**
 * Store version information with this class
 */
public class VersionData {

    private final String tagName, description, downloadUri;
    private final boolean preRelease;
    private final int versionID, versionCode;

    /**
     * Constructor
     *
     * @param tagName     version tag name
     * @param description version description text
     * @param downloadUri version download address
     * @param preRelease  boolean pre release or stable
     * @param versionID   version id number
     */
    public VersionData(String tagName, String description, String downloadUri, boolean preRelease, int versionID) {
        this.tagName = tagName;
        this.description = description;
        this.downloadUri = downloadUri;
        this.preRelease = preRelease;
        this.versionID = versionID;
        this.versionCode = Integer.parseInt(tagName);
    }

    /**
     * Getter
     *
     * @return version tag name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * Getter
     *
     * @return version description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Getter
     *
     * @return Uri where to download this version
     */
    public String getDownloadUri() {
        return downloadUri;
    }

    /**
     * Getter
     *
     * @return boolean true = pre release version
     */
    public boolean getPreRelease() {
        return preRelease;
    }

    /**
     * Getter
     *
     * @return version id number
     */
    public int getVersionID() {
        return versionID;
    }

    /**
     * Getter
     *
     * @return version code as integer
     */
    public int getVersionCode() {
        return versionCode;
    }

    @NonNull
    @Override
    public String toString() {
        return "VersionData{" +
                "tagName='" + tagName + '\'' +
                ", description='" + description + '\'' +
                ", id=" + versionID +
                '}';
    }
}
