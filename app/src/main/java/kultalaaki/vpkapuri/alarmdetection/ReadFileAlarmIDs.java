/*
 * Created by Kultala Aki on 8/1/21, 10:25 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/1/21, 10:25 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.res.Resources;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import kultalaaki.vpkapuri.R;

/**
 * Read file that contains Alarm IDs.
 * Adds them to HashMap<String, String>.
 */
public class ReadFileAlarmIDs {

    Map<String, String> alarmIDs;

    public ReadFileAlarmIDs() {
        this.alarmIDs = new HashMap<>();
        readAlarmIDs();
    }

    private void readAlarmIDs() {
        Resources resources = Resources.getSystem();
        InputStream is = resources.openRawResource(R.raw.alarmids);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            while (true) {
                if (reader.readLine() == null) {
                    break;
                }

                String row = reader.readLine();
                String[] parts = row.split("=");
                this.alarmIDs.put(parts[0], parts[1]);
            }
        } catch (Exception e) {
            Log.e("File reader.", "Error reading file: " + e.getMessage());
        }
    }


    /**
     * @return Alarm ID text
     */
    public Map<String, String> getAlarmIDs() {
        return this.alarmIDs;
    }
}
