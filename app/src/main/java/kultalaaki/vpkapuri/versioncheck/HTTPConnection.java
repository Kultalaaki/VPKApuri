package kultalaaki.vpkapuri.versioncheck;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Establish http connection
 */
public class HTTPConnection {

    private final URL url;
    protected HttpsURLConnection urlConnection;

    /**
     * Constructor
     *
     * @param url String address to connect
     */
    public HTTPConnection(String url) throws MalformedURLException {
        this.url = new URL(url);
    }

    /**
     * Open connection
     *
     * @throws IOException handled by caller
     */
    public void openConnection() throws IOException {
        urlConnection = (HttpsURLConnection) url.openConnection();
        urlConnection.connect();
    }

    /**
     * Disconnect connection
     */
    public void disconnect() {
        urlConnection.disconnect();
    }

}
