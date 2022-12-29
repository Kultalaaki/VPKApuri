package kultalaaki.vpkapuri.versioncheck;

import androidx.annotation.NonNull;

/**
 * Store version information with this class
 */
public class VersionInfo {

    private final String name, tagName, description;
    private final boolean preRelease;

    /**
     * Constructor
     *
     * @param tagName     version tag name
     * @param description version description text
     * @param preRelease  boolean pre release or stable
     */
    public VersionInfo(String name, String tagName, String description, boolean preRelease) {
        this.name = name;
        this.tagName = tagName;
        this.description = description;
        this.preRelease = preRelease;
    }

    /**
     * Getter
     *
     * @return version name
     */
    public String getName() {
        return name;
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
     * @return version code
     */
    public int getVersionCode() {
        return Integer.parseInt(tagName);
    }

    @NonNull
    @Override
    public String toString() {
        return "VersionData{" +
                "tagName='" + tagName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
