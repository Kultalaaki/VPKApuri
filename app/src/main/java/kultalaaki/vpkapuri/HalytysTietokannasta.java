package kultalaaki.vpkapuri;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HalytysTietokannasta extends AppCompatActivity {

    DBHelper db;
    TextView tunnus, tunnusteksti, luokka, luokkateksti, viesti, viestiteksti, kommentti;
    EditText kommenttiteksti;
    Button tallenna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halytys_tietokannasta);

        db = new DBHelper(this);
        tunnus = (TextView) findViewById(R.id.tunnus);
        tunnusteksti = (TextView) findViewById(R.id.tunnusteksti);
        luokka = (TextView) findViewById(R.id.luokka);
        luokkateksti = (TextView) findViewById(R.id.luokkateksti);
        viesti = (TextView) findViewById(R.id.viesti);
        viestiteksti = (TextView) findViewById(R.id.viestiteksti);
        kommentti = (TextView) findViewById(R.id.kommentti);
        kommenttiteksti = (EditText)findViewById(R.id.kommenttiteksti);
        kommenttiteksti.setCursorVisible(false);
        tallenna = (Button)findViewById(R.id.tallenna);

        Intent intent = getIntent();
        String primaryKey = intent.getStringExtra("primaryKey");

        Cursor cursor = db.halyID(primaryKey);
        if(cursor != null) {
            tunnusteksti = (TextView) findViewById(R.id.tunnusteksti);
            tunnusteksti.setText(cursor.getString(cursor.getColumnIndex(db.TUNNUS)));
            luokkateksti = (TextView) findViewById(R.id.luokkateksti);
            luokkateksti.setText(cursor.getString(cursor.getColumnIndex(db.LUOKKA)));
            viestiteksti = (TextView) findViewById(R.id.viestiteksti);
            viestiteksti.setText(cursor.getString(cursor.getColumnIndex(db.VIESTI)));
            kommenttiteksti = (EditText) findViewById(R.id.kommenttiteksti);
            kommenttiteksti.setText(cursor.getString(cursor.getColumnIndex(db.KOMMENTTI)));
        }

        lisaaKommentti();
        kommenttikursori();
    }

    public void kommenttikursori() {
        kommenttiteksti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kommenttiteksti.setCursorVisible(true);
            }
        });
    }

    public void lisaaKommentti() {
        tallenna.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        String primaryKey = intent.getStringExtra("primaryKey");
                        String kommentti = kommenttiteksti.getText().toString();
                        boolean lisattyKommentti = db.lisaaKommentti(primaryKey, kommentti);
                        if(lisattyKommentti){
                            Toast.makeText(HalytysTietokannasta.this, "Tallennettu", Toast.LENGTH_LONG).show();
                            kommenttiteksti.setCursorVisible(false);
                        } else {
                            Toast.makeText(HalytysTietokannasta.this, "Tallennus epäonnistui", Toast.LENGTH_LONG).show();
                        }
                        kommenttiteksti.setEnabled(false);
                        kommenttiteksti.setEnabled(true);
                    }
                }
        );
    }




}
