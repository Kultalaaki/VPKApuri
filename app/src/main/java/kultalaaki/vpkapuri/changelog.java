/*
 * Created by Kultala Aki on 6.5.2018 11:39
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 6.5.2018 11:39
 */

package kultalaaki.vpkapuri;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class changelog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changelog);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
