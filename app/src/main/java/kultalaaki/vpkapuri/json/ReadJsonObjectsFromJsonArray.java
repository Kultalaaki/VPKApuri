package kultalaaki.vpkapuri.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReadJsonObjectsFromJsonArray {

    private JSONArray jsonArray;
    private final String jsonString;
    private final ArrayList<JSONObject> objects;

    public ReadJsonObjectsFromJsonArray(String jsonString) {
        this.jsonString = jsonString;
        this.objects = new ArrayList<>();
    }

    public void createJsonArray() throws JSONException {
        jsonArray = new JSONArray(jsonString);
    }

    public void addJsonObjectsToArrayList() throws JSONException {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            objects.add(object);
        }
    }

    public ArrayList<JSONObject> getObjects() {
        return objects;
    }

}
