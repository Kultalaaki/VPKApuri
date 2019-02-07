/*
 * Created by Kultala Aki on 9.9.2017 9:28
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 15.7.2016 19:57
 */

package kultalaaki.vpkapuri;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class ArkistoActivity extends AppCompatActivity {

    DBHelper db;
    //RadioButton tunnus, luokka, teksti;
    ListView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arkisto);

        db = new DBHelper(this);

        //naytaKaikki = findViewById(R.id.button);
        //etsi = findViewById(R.id.button2);
        //editText = findViewById(R.id.editText);

        //tunnus = findViewById(R.id.tunnus);
        //luokka = findViewById(R.id.luokka);
        //teksti = findViewById(R.id.teksti);

        myList = findViewById(R.id.listViewHalyt);

        /*tunnus.setChecked(true);

        etsi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String haku = editText.getText().toString();
                if(!haku.isEmpty()) {
                    if(tunnus.isChecked()) {
                        populateListViewTunnuksella(haku);
                    } else if (luokka.isChecked()) {
                        populateListViewLuokalla(haku);
                    } else if (teksti.isChecked()) {
                        populateListViewTekstista(haku);
                    }

                    editText.setText("");
                    editText.setEnabled(false);
                    editText.setEnabled(true);
                } else {
                    showMessage("Haku kenttä ei voi olla tyhjä. Anna hälytystunnus tai kiireellisyysluokka.");
                }
            }
        });

        naytaKaikki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = db.getAllData();
                if(res.getCount() == 0) {
                    // Show message
                    showMessage("Arkistoituja hälytyksiä ei ole.");
                } else {
                    populateListView();
                }
            }
        });*/
        populateListView();
    }

    private void populateListView() {
        Cursor cursor = db.getAllRows();
        String[] fromFieldNames = new String[] {DBHelper.COL_1, DBHelper.VIESTI, DBHelper.TUNNUS};
        final int[] toViewIDs = new int[] {R.id.sija, R.id.viesti, R.id.tunnus};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_layout, cursor, fromFieldNames, toViewIDs, 0);
        //ListView myList = (ListView) findViewById(R.id.listViewHalyt);
        myList.setAdapter(myCursorAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked perform some action...
                //TODO
                TextView textView = view.findViewById(R.id.sija);
                String primaryKey = textView.getText().toString();
                Intent myIntent = new Intent(ArkistoActivity.this, HalytysTietokannasta.class);
                myIntent.putExtra("paikkanumero", position);
                myIntent.putExtra("primaryKey", primaryKey);
                ArkistoActivity.this.startActivity(myIntent);
            }
        });
    }

    /*private void populateListViewTunnuksella(String haettava) {
        Cursor cursor = db.hakuTunnuksella(haettava);
        String[] fromFieldNames = new String[] {DBHelper.COL_1, DBHelper.VIESTI};
        final int[] toViewIDs = new int[] {R.id.sija, R.id.viesti};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_layout, cursor, fromFieldNames, toViewIDs, 0);
        //ListView myList = (ListView) findViewById(R.id.listViewHalyt);
        myList.setAdapter(myCursorAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked perform some action...
                TextView textView = view.findViewById(R.id.sija);
                String primaryKey = textView.getText().toString();
                Intent myIntent = new Intent(ArkistoActivity.this, HalytysTietokannasta.class);
                myIntent.putExtra("paikkanumero", position);
                myIntent.putExtra("primaryKey", primaryKey);
                ArkistoActivity.this.startActivity(myIntent);
            }
        });
    }

    private void populateListViewLuokalla(String haettava) {
        Cursor cursor = db.hakuLuokalla(haettava);
        String[] fromFieldNames = new String[] {DBHelper.COL_1, DBHelper.VIESTI};
        final int[] toViewIDs = new int[] {R.id.sija, R.id.viesti};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_layout, cursor, fromFieldNames, toViewIDs, 0);
        //ListView myList = (ListView) findViewById(R.id.listViewHalyt);
        myList.setAdapter(myCursorAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked perform some action...
                TextView textView = view.findViewById(R.id.sija);
                String primaryKey = textView.getText().toString();
                Intent myIntent = new Intent(ArkistoActivity.this, HalytysTietokannasta.class);
                myIntent.putExtra("paikkanumero", position);
                myIntent.putExtra("primaryKey", primaryKey);
                ArkistoActivity.this.startActivity(myIntent);
            }
        });
    }

    private void populateListViewTekstista(String haettava) {
        Cursor cursor = db.hakuTekstista(haettava);
        String[] fromFieldNames = new String[] {DBHelper.COL_1, DBHelper.VIESTI};
        final int[] toViewIDs = new int[] {R.id.sija, R.id.viesti};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_layout, cursor, fromFieldNames, toViewIDs, 0);
        //ListView myList = (ListView) findViewById(R.id.listViewHalyt);
        myList.setAdapter(myCursorAdapter);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked perform some action...
                TextView textView = view.findViewById(R.id.sija);
                String primaryKey = textView.getText().toString();
                Intent myIntent = new Intent(ArkistoActivity.this, HalytysTietokannasta.class);
                myIntent.putExtra("paikkanumero", position);
                myIntent.putExtra("primaryKey", primaryKey);
                ArkistoActivity.this.startActivity(myIntent);
            }
        });
    }


    public void showMessage(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Huomautus");
        builder.setMessage(message);
        builder.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.show();
    }*/

}

    //TODO poista arkistosta
    //TODO oma palokalenteri harjoitukset/kuntotestit/keikat
    //TODO vuosittaiset pakolliset harjoitukset