package kultalaaki.vpkapuri.databasebackupandrestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Reads objects from json array
 */
public class JSONArrayReader {

    private final JSONArray jsonArray;
    private final ArrayList<JSONObject> objects;

    /**
     * Constructor
     *
     * @param jsonString json string
     */
    public JSONArrayReader(String jsonString) throws JSONException {
        this.objects = new ArrayList<>();
        this.jsonArray = new JSONArray(jsonString);
        jsonArrayToArrayList();
    }

    /**
     * Adds json objects to arraylist
     *
     * @throws JSONException caller must handle exception
     */
    private void jsonArrayToArrayList() throws JSONException {
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
