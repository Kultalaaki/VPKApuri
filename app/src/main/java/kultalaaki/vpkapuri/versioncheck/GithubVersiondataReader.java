package kultalaaki.vpkapuri.versioncheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import kultalaaki.vpkapuri.util.Constants;

/**
 * Read (JSON) version data from Github
 */
public class GithubVersiondataReader implements VersionData {

    private final URL url;
    protected HttpsURLConnection urlConnection;

    /**
     * Constructor
     */
    public GithubVersiondataReader() throws MalformedURLException {
        this.url = new URL(Constants.ADDRESS_GITHUB_RELEASES_INFO);
    }

    @Override
    public String getVersionData() throws IOException {
        openConnection();
        InputStream inStream = urlConnection.getInputStream();
        BufferedReader bfReader = new BufferedReader(new InputStreamReader(inStream));
        StringBuilder sBuilder = new StringBuilder();

        String line;
        while ((line = bfReader.readLine()) != null) {
            sBuilder.append(line);
        }
        bfReader.close();
        disconnect();

        return sBuilder.toString();
    }

    /**
     * Open connection
     *
     * @throws IOException handled by caller
     */
    private void openConnection() throws IOException {
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.connect();
    }

    /**
     * Disconnect connection
     */
    private void disconnect() {
        urlConnection.disconnect();
    }

}
