/*
 * Created by Kultala Aki on 2.5.2018 21:13
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 2.5.2018 21:13
 */

package kultalaaki.vpkapuri;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class tallennaArkistoon extends AppCompatActivity {

    TextView tunnus;
    TextView luokka;
    TextView viesti;
    EditText tunnusteksti;
    EditText luokkateksti;
    EditText viestiteksti;
    Button tallenna;
    static DBHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tallenna_arkistoon);

        tunnus = findViewById(R.id.tunnus);
        luokka = findViewById(R.id.luokka);
        viesti = findViewById(R.id.viesti);
        tunnusteksti = findViewById(R.id.tunnusteksti);
        luokkateksti = findViewById(R.id.luokkateksti);
        viestiteksti = findViewById(R.id.viestiteksti);
        tallenna = findViewById(R.id.tallenna);
        tallennaHalytys();
    }

    public void tallennaHalytys() {
        tallenna.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db = new DBHelper(getApplicationContext());
                        String tunnus = tunnusteksti.getText().toString();
                        String luokka = luokkateksti.getText().toString();
                        String viesti = viestiteksti.getText().toString();

                        boolean tallennettu = db.insertData( tunnus, luokka, viesti,"");
                        if(tallennettu) {
                            Intent etusivulle = new Intent(tallennaArkistoon.this, Etusivu.class);
                            etusivulle.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(etusivulle);
                        }
                    }
                }
        );
    }
}
