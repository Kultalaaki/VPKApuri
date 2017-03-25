package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;


public class aktiivinenHaly extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 1;
    private static int arkistointi_paalle = 0;
    Button callBtn;
    Button second;
    DBHelper db;
    private EditText textView;
    private Switch hiljenna;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktiivinenhaly);

        callBtn = (Button) findViewById(R.id.angry_btn);
        second = (Button) findViewById(R.id.second);
        textView = (EditText) findViewById(R.id.textView);
        hiljenna = (Switch) findViewById(R.id.hiljenna);
        hiljenna.setVisibility(View.INVISIBLE);
        pyydaLuvat2();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            asetaTeksti();
        } else {
            asetaTekstiAikaisemmatVersiot();
        }

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(this);
        final String soittonumero = pref_general.getString("example_text", null);

        if (Intent.ACTION_SEND.equals(action)&& type != null) {
            if ("text/plain".equals(type)){
                alarm();
                handleSendText(intent);
                hiljenna.setVisibility(View.VISIBLE);
                setHiljenna();
            } else {
                //Some other intent actions
            }
        }
        
        // add PhoneStateListener for monitoring
        PhoneStateListener phoneListener = new PhoneStateListener();
        TelephonyManager telephonyManager =
                (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        // receive notifications of telephony state changes
        telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pyydaLuvat();
                try {
                    int permissionCheck = ContextCompat.checkSelfPermission(aktiivinenHaly.this,
                            Manifest.permission.CALL_PHONE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                    startActivity(callIntent);
                }catch(Exception e) {
                    Toast.makeText(getApplicationContext(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa käyttää puhelinta.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
    }

    private void setHiljenna() {
        hiljenna.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // The toggle is enabled
                    mMediaPlayer.stop();
                } else {
                    // The toggle is disabled
                }
            }
        });
    }

    private void alarm(){
        SharedPreferences getAlarms = PreferenceManager.
                getDefaultSharedPreferences(getBaseContext());
        String alarms = getAlarms.getString("notifications_new_message_ringtone", "default ringtone");
        Uri uri = Uri.parse(alarms);
        playSound(this, uri);

        //call mMediaPlayer.stop(); when you want the sound to stop
    }


    private MediaPlayer mMediaPlayer;
    private void playSound(Context context, Uri alert) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context
                    .getSystemService(Context.AUDIO_SERVICE);
            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    public void asetaTeksti() {
        if(PreferenceManager.getDefaultSharedPreferences(this).getString("KEY", null) != null) {
            textView.setText(PreferenceManager.getDefaultSharedPreferences(this).getString("KEY", null));
            String halyviesti = textView.getText().toString();
            halyviesti(halyviesti);
        } else {
            textView.setText(R.string.First_text);
        }
    }

    public void asetaTekstiAikaisemmatVersiot() {
        String halyviesti = PreferenceManager.getDefaultSharedPreferences(this).getString("KEY", null);
        if(halyviesti != null) {
            textView.setText(halyviesti);
            halyviesti(halyviesti);
        } else {
            textView.setText(R.string.First_text);
        }
    }

    public void pyydaLuvat() {
        if (ContextCompat.checkSelfPermission(aktiivinenHaly.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(aktiivinenHaly.this,
                    Manifest.permission.CALL_PHONE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Et voi käyttää soita nappia jos et anna lupaa.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                        });
                return;

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(aktiivinenHaly.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void pyydaLuvat2(){
        // SMS Luvat
        if (ContextCompat.checkSelfPermission(aktiivinenHaly.this,
                Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(aktiivinenHaly.this,
                    Manifest.permission.RECEIVE_SMS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Sovelluksella ei ole lupaa lukea viestejä. Et voi vastaanottaa viestejä jos et anna lupaa.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.RECEIVE_SMS},
                                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
                            }
                        });
                return;

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(aktiivinenHaly.this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(aktiivinenHaly.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            textView.setText(sharedText);
            arkistointi_paalle = 1;
            halyviesti(sharedText);
        }
    }

    public void halyviesti(String haeOsoite) {

        //AsynctaskAlarmActivity ic = new AsynctaskAlarmActivity(this);
        //ic.viesti(haeOsoite);


        //Ensimmäinen osoite
        String osoite = "";
        Integer sanoja = 0;
        //Toinen osoite
        String osoite2 = "";
        Integer sanoja2 = 0;
        //Kolmas osoite
        String osoite3 = "";
        Integer sanoja3 = 0;
        final String valmisOsoite;
        Character luokka;
        String viesti = " ";
        String tunnus = " ";

        if(haeOsoite.substring(0, 5).equals("SALSA")){

            //Ensimmäinen osoite
            for (int maara = 34; sanoja < 3; maara++) {
                if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                    sanoja++;
                    osoite += " ";
                } else {
                    osoite += haeOsoite.charAt(maara);
                }
            }
            //Toinen osoite
            for (int maara = 34; sanoja2 < 4; maara++) {
                if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                    sanoja2++;
                    osoite2 += " ";
                } else {
                    osoite2 += haeOsoite.charAt(maara);
                }
            }
            //Kolmas osoite
            for (int maara = 34; sanoja3 < 5; maara++) {
                if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                    sanoja3++;
                    osoite3 += " ";
                } else {
                    osoite3 += haeOsoite.charAt(maara);
                }
            }

            // Kiireellisyysluokan tunnus
            for(int maara=29; maara < 32; maara++) {
                tunnus += haeOsoite.charAt(maara);
            }
            // Viesti
            for(int maara = 34; maara < haeOsoite.length(); maara++) {
                viesti += haeOsoite.charAt(maara);
            }

            luokka = haeOsoite.charAt(32);

        } /*else if(haeOsoite.substring(0,3).equals("Pri")) {
            //TODO tähän tulee ensivastekeikkojen osoitteenhaku 10 11 12 tunnus 13 luokka 14 alkaa osoite
            for (int maara = 14; sanoja < 4; maara++) {
                if (haeOsoite.charAt(maara) == '/') {
                    sanoja++;
                    osoite += " ";
                } else {
                    osoite += haeOsoite.charAt(maara);
                }
            }

            // Kiireellisyysluokan tunnus
            for(int maara=10; maara < 13; maara++) {
                tunnus += haeOsoite.charAt(maara);
            }
            // Viesti
            for(int maara = 15; maara < haeOsoite.length(); maara++) {
                viesti += haeOsoite.charAt(maara);
            }

            luokka = haeOsoite.charAt(13);
            //TODO ensivaste
        }*/ else {
            //Ensimmäinen osoite
            for (int maara = 5; sanoja < 3; maara++) {
                if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                    sanoja++;
                    osoite += " ";
                } else {
                    osoite += haeOsoite.charAt(maara);
                }
            }
            //Toinen osoite
            for (int maara = 5; sanoja2 < 4; maara++) {
                if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                    sanoja2++;
                    osoite2 += " ";
                } else {
                    osoite2 += haeOsoite.charAt(maara);
                }
            }
            //Kolmas osoite
            for (int maara = 5; sanoja3 < 5; maara++) {
                if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                    sanoja3++;
                    osoite3 += " ";
                } else {
                    osoite3 += haeOsoite.charAt(maara);
                }
            }

            // Kiireellisyysluokan tunnus
            for(int maara=0; maara < 3; maara++) {
                tunnus += haeOsoite.charAt(maara);
            }
            // Viesti
            for(int maara = 5; maara < haeOsoite.length(); maara++) {
                viesti += haeOsoite.charAt(maara);
            }

            luokka = haeOsoite.charAt(3);
        }

        //TODO pois käytöstä kun testataan salsa yhteensopivuutta
        /*//Ensimmäinen osoite
        for (int maara = 5; sanoja < 3; maara++) {
            if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                sanoja++;
                osoite += " ";
            } else {
                osoite += haeOsoite.charAt(maara);
            }
        }
        //Toinen osoite
        for (int maara = 5; sanoja2 < 4; maara++) {
            if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                sanoja2++;
                osoite2 += " ";
            } else {
                osoite2 += haeOsoite.charAt(maara);
            }
        }
        //Kolmas osoite
        for (int maara = 5; sanoja3 < 5; maara++) {
            if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                sanoja3++;
                osoite3 += " ";
            } else {
                osoite3 += haeOsoite.charAt(maara);
            }
        }

        // Kiireellisyysluokan tunnus
        String tunnus = " ";
        for(int maara=0; maara < 3; maara++) {
            tunnus += haeOsoite.charAt(maara);
        }
        // Viesti
        for(int maara = 5; maara < haeOsoite.length(); maara++) {
            viesti += haeOsoite.charAt(maara);
        }*/

        // Testaa mikä osoite sisältää numeroita = oikea osoite
        if (osoite.matches((".*[0-9].*"))) {
            valmisOsoite = osoite;
        } else if (osoite2.matches((".*[0-9].*"))) {
            valmisOsoite = osoite2;
        } else if (osoite3.matches((".*[0-9].*"))) {
            valmisOsoite = osoite3;
        } else {
            valmisOsoite = osoite;
        }

        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                naytaKartalla(null, valmisOsoite);
            }
        });

        //Letter indicator for urgency
        //luokka = haeOsoite.charAt(3);
        String Luokka = " " + luokka.toString();

        if(arkistointi_paalle == 1) {
            arkistoi(tunnus, Luokka, viesti);
            arkistointi_paalle = 0;
        }




    }

        /*Asynctask test
        class AsynctaskAlarmActivity extends Activity {

            //Button second;
            aktiivinenHaly outer;

            public AsynctaskAlarmActivity(aktiivinenHaly outer) {
                this.outer = outer;
                //second = (Button) findViewById(R.id.second);
            }

            String haeOsoite = "";

            public void viesti(String viesti) {
                haeOsoite += viesti;
            }

            @Override
            public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                new MyTask().execute(haeOsoite);
            }

            private class MyTask extends AsyncTask<String, Integer, String> {

                @Override
                protected void onPreExecute() {
                    second.setVisibility(View.INVISIBLE);
                }

                @Override
                protected String doInBackground(String... params) {
                    //String myString = params[0];



                    //Ensimmäinen osoite
                    String osoite = "";
                    Integer sanoja = 0;
                    //Toinen osoite
                    String osoite2 = "";
                    Integer sanoja2 = 0;
                    //Kolmas osoite
                    String osoite3 = "";
                    Integer sanoja3 = 0;
                    final String valmisOsoite;
                    Character luokka;
                    String viesti = " ";

                    //Ensimmäinen osoite
                    for (int maara = 5; sanoja < 3; maara++) {
                        if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                            sanoja++;
                            osoite += " ";
                        } else {
                            osoite += haeOsoite.charAt(maara);
                        }
                    }
                    //Toinen osoite
                    for (int maara = 5; sanoja2 < 4; maara++) {
                        if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                            sanoja2++;
                            osoite2 += " ";
                        } else {
                            osoite2 += haeOsoite.charAt(maara);
                        }
                    }
                    //Kolmas osoite
                    for (int maara = 5; sanoja3 < 5; maara++) {
                        if (Character.isWhitespace(haeOsoite.charAt(maara))) {
                            sanoja3++;
                            osoite3 += " ";
                        } else {
                            osoite3 += haeOsoite.charAt(maara);
                        }
                    }

                    // Testaa mikä osoite sisältää numeroita = oikea osoite
                    if (osoite.matches((".*[0-9].*"))) {
                        valmisOsoite = osoite;
                    } else if (osoite2.matches((".*[0-9].*"))) {
                        valmisOsoite = osoite2;
                    } else if (osoite3.matches((".*[0-9].*"))) {
                        valmisOsoite = osoite3;
                    } else {
                        valmisOsoite = osoite;
                    }

                    return valmisOsoite;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {

                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    second.setVisibility(View.VISIBLE);

                    final String Osoite = result;

                    second.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            naytaKartalla(null, Osoite);
                        }
                    });
                }
            }
        }*/

    public void arkistoi(String tunnus, String luokka, String viesti) {
        db = new DBHelper(this);
        boolean isInserted = db.insertData(tunnus, luokka, viesti, "");
        if(isInserted)
            Toast.makeText(aktiivinenHaly.this, "Hälytys arkistoitu.",Toast.LENGTH_LONG).show();
        else
            Toast.makeText(aktiivinenHaly.this, "Hälytystä ei voitu arkistoida.",Toast.LENGTH_LONG).show();

    }

    public void naytaKartalla (View v, String osoite){
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoite);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String viesti = textView.getText().toString();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("KEY", viesti).apply();
        //hiljenna.setChecked(false);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String toStore = PreferenceManager.getDefaultSharedPreferences(this).getString("KEY", null);
        textView.setText(toStore);
    }

}
