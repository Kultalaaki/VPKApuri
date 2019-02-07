/*
 * Created by Kultala Aki on 9.9.2017 9:29
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 15.7.2016 19:44
 */

package kultalaaki.vpkapuri;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HalytysTietokannasta extends AppCompatActivity {

    DBHelper db;
    TextView tunnus, luokka, viesti, kommentti;
    EditText tunnusteksti, luokkateksti, viestiteksti, kommenttiteksti;
    Button tallenna, avaaKartta, poista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tietokannasta2);

        db = new DBHelper(this);
        tunnus = findViewById(R.id.tunnus);
        tunnusteksti = findViewById(R.id.tunnusteksti);
        luokka = findViewById(R.id.luokka);
        luokkateksti = findViewById(R.id.luokkateksti);
        viesti = findViewById(R.id.viesti);
        viestiteksti = findViewById(R.id.viestiteksti);
        kommentti = findViewById(R.id.kommentti);
        kommenttiteksti = findViewById(R.id.kommenttiteksti);
        kommenttiteksti.setCursorVisible(false);
        tallenna = findViewById(R.id.tallenna);
        avaaKartta = findViewById(R.id.naytaKartalla);
        poista = findViewById(R.id.poista);

        Intent intent = getIntent();
        String primaryKey = intent.getStringExtra("primaryKey");

        Cursor cursor = db.halyID(primaryKey);
        if(cursor != null) {
            tunnusteksti = findViewById(R.id.tunnusteksti);
            tunnusteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.TUNNUS)));
            luokkateksti = findViewById(R.id.luokkateksti);
            luokkateksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.LUOKKA)));
            viestiteksti = findViewById(R.id.viestiteksti);
            viestiteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.VIESTI)));
            kommenttiteksti = findViewById(R.id.kommenttiteksti);
            kommenttiteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KOMMENTTI)));
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        lisaaKommentti();

        avaaKartta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String osoite = luokkateksti.getText().toString();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoite);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        poista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageOKCancelPoistaHaly();
            }
        });

    }

    private void showMessageOKCancelPoistaHaly() {

        AlertDialog.Builder builder = new AlertDialog.Builder(HalytysTietokannasta.this)
                .setTitle("Poista hälytys.")
                .setMessage("Haluatko varmasti poistaa tämän hälytyksen tietokannasta?")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Poista hälytys tietokannasta ja palaa arkiston etusivulle ja hae päivitety tietokanta esiin.
                        Intent intent = getIntent();
                        int paikka = Integer.parseInt(intent.getStringExtra("primaryKey"));
                        db.deleteRow(paikka);

                        dialogInterface.dismiss();

                        Intent intentark = new Intent(HalytysTietokannasta.this, ArkistoActivity.class);
                        intentark.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intentark);
                    }
                });
        builder.create().show();
    }

    public void lisaaKommentti() {
        tallenna.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        String primaryKey = intent.getStringExtra("primaryKey");
                        String kommentti = kommenttiteksti.getText().toString();
                        String tunnus = tunnusteksti.getText().toString();
                        String luokka = luokkateksti.getText().toString();
                        String viesti = viestiteksti.getText().toString();
                        boolean lisattyKommentti = db.lisaaKommentti(primaryKey, tunnus, luokka, viesti, kommentti);
                        if(lisattyKommentti){
                            Toast.makeText(HalytysTietokannasta.this, "Tallennettu", Toast.LENGTH_LONG).show();
                            kommenttiteksti.setCursorVisible(false);
                        } else {
                            Toast.makeText(HalytysTietokannasta.this, "Tallennus epäonnistui", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }




}
