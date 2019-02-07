/*
 * Created by Kultala Aki on 9.9.2017 9:29
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 16.7.2016 15:37
 */

package kultalaaki.vpkapuri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent2 = new Intent(splashScreen.this, Etusivu.class);
        startActivity(intent2);
        finish();
    }
}
