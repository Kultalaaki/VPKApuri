/*
 * Created by Kultala Aki on 8/1/21, 10:25 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 8/1/21, 10:25 PM
 */

package kultalaaki.vpkapuri.alarmdetection;

import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import kultalaaki.vpkapuri.R;


/**
 * Reads file containing all finnish cities
 * and adds them to ArrayList<String>.
 * Used for finding address from parts of sms messages.
 */
public class ReadFileCities {

    List<String> cities;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public ReadFileCities() {
        this.cities = new ArrayList<>();
        getCities();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<String> getCities() {
        Resources resources = Resources.getSystem();
        InputStream is = resources.openRawResource(R.raw.cities);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            while (true) {
                if (reader.readLine() == null) {
                    break;
                }

                cities.add(reader.readLine());
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return cities;
    }


    /**
     * @return ArrayList<String> containing all cities of Finland.
     */
    public List<String> getCityList() {
        return this.cities;
    }
}
