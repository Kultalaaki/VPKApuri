package kultalaaki.vpkapuri.versioncheck;

import java.io.IOException;

public interface VersionDataReader {

    /**
     *
     * @return Version data as String
     */
    String getVersionData() throws IOException;
}
