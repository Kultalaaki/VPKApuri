package kultalaaki.vpkapuri.versioncheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Read application version data
 */
public class ReadVersionData {

    private String data = "";
    private final String url; // json file address: https://api.github.com/repos/kultalaaki/VPKApuri/releases/latest

    /**
     * Constructor
     */
    public ReadVersionData(String url) {
        this.url = url;
    }

    /**
     * Read data from connection.
     * Close connection after read operation.
     *
     * @return String representation of read data.
     */
    public String readFromConnection() {
        try {
            HTTPConnection connection = new HTTPConnection(url);
            connection.openConnection();
            InputStream inputStream = connection.urlConnection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder stringBuilder = new StringBuilder();

            String line = "";

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            data = stringBuilder.toString();

            bufferedReader.close();
            connection.disconnect();
        } catch (IOException e) {
            // Todo log error
        }

        return data;
    }
}
