package kultalaaki.vpkapuri.databasebackupandrestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Reads objects from json array
 */
public class ReadJsonObjectsFromJsonArray {

    private JSONArray jsonArray;
    private final String jsonString;
    private final ArrayList<JSONObject> objects;

    /**
     * Constructor
     *
     * @param jsonString json string
     */
    public ReadJsonObjectsFromJsonArray(String jsonString) {
        this.jsonString = jsonString;
        this.objects = new ArrayList<>();
    }

    /**
     * Creates json array from string
     *
     * @throws JSONException caller must handle exception
     */
    public void createJsonArray() throws JSONException {
        jsonArray = new JSONArray(jsonString);
    }

    /**
     * Adds json objects to arraylist
     *
     * @throws JSONException caller must handle exception
     */
    public void addJsonObjectsToArrayList() throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            objects.add(object);
        }
    }

    /**
     * Getter
     *
     * @return arraylist containing objects
     */
    public ArrayList<JSONObject> getObjects() {
        return objects;
    }

}
