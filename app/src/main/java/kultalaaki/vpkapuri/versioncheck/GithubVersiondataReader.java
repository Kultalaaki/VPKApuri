package kultalaaki.vpkapuri.versioncheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Read (JSON) version data from Github
 */
public class GithubVersiondataReader implements VersionDataReader {

    private final URL url;
    protected HttpsURLConnection urlConnection;

    /**
     * Constructor
     */
    public GithubVersiondataReader() throws MalformedURLException {
        String address = "https://api.github.com/repos/kultalaaki/VPKApuri/releases";
        this.url = new URL(address);
    }

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
        return "";
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
