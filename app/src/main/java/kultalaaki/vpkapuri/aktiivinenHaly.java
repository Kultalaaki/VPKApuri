package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;


public class aktiivinenHaly extends AppCompatActivity {

    FirebaseAnalytics mFirebaseAnalytics;
    //firebase ilmoitus hälytys tapahtumasta
    /*Bundle bundle = new Bundle();
        bundle.putString("Halytys_vastaanotettu", haly);
        mFirebaseAnalytics.logEvent("Halytys_tapahtunut", bundle);*/

    SharedPreferences aaneton = null;

    //private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 1;
    //private static final int MY_PERMISSIONS_REQUEST_RECEIVE_SMS = 2;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4;
    Button callBtn;
    Button second;
    Button btnfive;
    Button btnten;
    Button btntenplus;
    Button avaaKartta;
    static DBHelper db;
    EditText textViewHalytunnus;
    EditText textView;
    String soittonumero;
    String smsnumero;
    String smsnumero10;
    String smsnumero11;
    String fivemintxtotsikko;
    String fivemintxt;
    String tenmintxtotsikko;
    String tenmintxt;
    String tenplusmintxtotsikko;
    String tenplusmintxt;
    Boolean five = false;
    Boolean ten = false;
    Boolean tenplus = false;
    TextToSpeech t1;
    Button hiljenna;
    Boolean koneluku;
    Boolean autoAukaisu;
    int tekstiPuheeksiVol;
    int palautaMediaVol;
    boolean palautaMediaVolBoolean = false;

    @Override
    @SuppressLint("ApplySharedPref")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aktiivinehalyscroll);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        callBtn = findViewById(R.id.angry_btn);
        btnfive = findViewById(R.id.buttonf5);
        btnten = findViewById(R.id.buttonf10);
        btntenplus = findViewById(R.id.buttonplus);
        second = findViewById(R.id.second);
        textViewHalytunnus = findViewById(R.id.textViewHalytunnus);
        textView = findViewById(R.id.textView);
        avaaKartta = findViewById(R.id.avaakarttaBlanc);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pyydaLuvatPhoneState();
            pyydaLuvat2();
        }*/

        haeUusinHalyTietokannasta();
        //lupaAanienMuuttamiseen(getApplicationContext());

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(this);
        soittonumero = pref_general.getString("example_text", null);
        smsnumero = pref_general.getString("sms_numero", null);
        smsnumero10 = pref_general.getString("sms_numero10", null);
        smsnumero11 = pref_general.getString("sms_numero11", null);
        fivemintxtotsikko = pref_general.getString("fivemintextotsikko", null);
        fivemintxt = pref_general.getString("fivemintxt", null);
        tenmintxtotsikko = pref_general.getString("tenmintextotsikko", null);
        tenmintxt = pref_general.getString("tenmintxt", null);
        tenplusmintxtotsikko = pref_general.getString("tenplusmintextotsikko", null);
        tenplusmintxt = pref_general.getString("tenplusmintxt", null);
        koneluku = pref_general.getBoolean("koneluku", false);
        autoAukaisu = pref_general.getBoolean("autoAukaisu", false);

        btnfive.setText(fivemintxtotsikko);
        btnten.setText(tenmintxtotsikko);
        btntenplus.setText(tenplusmintxtotsikko);
        hiljenna = findViewById(R.id.hiljenna);
        hiljenna.setVisibility(View.INVISIBLE);

        aaneton = getSharedPreferences("kultalaaki.vpkapuri.aaneton", Activity.MODE_PRIVATE);
        if (aaneton.getBoolean("firstrun", true)) {
            aaneton.edit().putInt("aaneton_profiili", 1).commit();
            aaneton.edit().putBoolean("firstrun", false).commit();
        }

        if (Intent.ACTION_SEND.equals(action)&& type != null) {
            if ("text/plain".equals(type)){
                Intent stopAlarm = new Intent(aktiivinenHaly.this, halyaaniService.class);
                aktiivinenHaly.this.stopService(stopAlarm);
                if(koneluku && !autoAukaisu) {
                    hiljenna.setVisibility(View.VISIBLE);
                    hiljenna.setText(R.string.hiljenna_puhe);
                    AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    if(ad != null) {
                        palautaMediaVol = ad.getStreamVolume(AudioManager.STREAM_MUSIC);
                        palautaMediaVolBoolean = true;
                        ad.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0);
                        // teksti puheeksi äänenvoimakkuus
                        try {
                            SharedPreferences prefe_general = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            tekstiPuheeksiVol = prefe_general.getInt("tekstiPuheeksiVol", -1);
                            tekstiPuheeksiVol = saadaAani(tekstiPuheeksiVol);
                            ad.setStreamVolume(AudioManager.STREAM_MUSIC, tekstiPuheeksiVol, 0);
                        } catch (Exception e) {
                            Log.i("VPK Apuri", "Teksti puheeksi äänenvoimakkuuden lukeminen asetuksista epäonnistui.");
                        }
                    }
                    hiljenna.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                            if(ad != null) {
                                ad.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                hiljenna.setVisibility(View.INVISIBLE);
                                if(t1 != null) {
                                    t1.stop();
                                    t1.shutdown();
                                }
                            }
                        }
                    });
                    txtToSpeech();
                }
            } else if ("phonecall".equals(type)){
                // Hälytys tuli puhelinsoitolla
                //db.insertData("999A", "", "Hälytys tuli puheluna.", "");
                //osoitehaku näppäin piiloon puhelu hälyille
                second.setVisibility(View.INVISIBLE);
            }
        }


        if(autoAukaisu) {
            hiljenna.setVisibility(View.VISIBLE);
            hiljenna.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent stopAlarm = new Intent(aktiivinenHaly.this, halyaaniService.class);
                    aktiivinenHaly.this.stopService(stopAlarm);
                    if(koneluku) {
                        AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        if(ad != null) {
                            // teksti puheeksi äänenvoimakkuus
                            palautaMediaVol = ad.getStreamVolume(AudioManager.STREAM_MUSIC);
                            palautaMediaVolBoolean = true;
                            ad.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0);
                            try {
                                SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                tekstiPuheeksiVol = pref_general.getInt("tekstiPuheeksiVol", -1);
                                tekstiPuheeksiVol = saadaAani(tekstiPuheeksiVol);
                                ad.setStreamVolume(AudioManager.STREAM_MUSIC, tekstiPuheeksiVol, 0);
                            } catch (Exception e) {
                                Log.i("VPK Apuri", "Teksti puheeksi äänenvoimakkuuden lukeminen asetuksista epäonnistui.");
                            }
                        }
                        txtToSpeech();
                        hiljenna.setText(R.string.hiljenna_puhe);
                        hiljenna.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                                if(ad != null) {
                                    ad.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                    hiljenna.setVisibility(View.INVISIBLE);
                                }
                                if(t1 != null) {
                                    t1.stop();
                                    t1.shutdown();
                                }
                            }
                        });
                    }
                }
            });
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pyydaLuvatCallPhone();
                } else {
                    try {
                        ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.CALL_PHONE);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                        startActivity(callIntent);
                    }catch(Exception e) {
                        Toast.makeText(getApplicationContext(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        btnfive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsnumero != null && smsnumero.equals("whatsapp")) {
                    Intent whatsapptxt = new Intent();
                    whatsapptxt.setAction(Intent.ACTION_SEND);
                    whatsapptxt.putExtra(Intent.EXTRA_TEXT, fivemintxt);
                    whatsapptxt.setType("text/plain");
                    whatsapptxt.setPackage("com.whatsapp");
                    startActivity(whatsapptxt);
                } else {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        five = true;
                        pyydaLuvatSms();
                    } else {
                        try {
                            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(smsnumero, null, fivemintxt, null, null);
                            Toast.makeText(getApplicationContext(),"Alle 5min ilmoitus lähetetty. (" + fivemintxt + ")", Toast.LENGTH_LONG).show();
                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }

                }

            }
        });

        btnten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsnumero10 != null && smsnumero10.equals("whatsapp")) {
                    Intent whatsapptxt = new Intent();
                    whatsapptxt.setAction(Intent.ACTION_SEND);
                    whatsapptxt.putExtra(Intent.EXTRA_TEXT, tenmintxt);
                    whatsapptxt.setType("text/plain");
                    whatsapptxt.setPackage("com.whatsapp");
                    startActivity(whatsapptxt);
                } else {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        ten = true;
                        pyydaLuvatSms();
                    } else {
                        try {
                            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(smsnumero10, null, tenmintxt, null, null);
                            Toast.makeText(getApplicationContext(),"Alle 10min ilmoitus lähetetty. (" + tenmintxt + ")", Toast.LENGTH_LONG).show();
                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        btntenplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsnumero11 != null && smsnumero11.equals("whatsapp")) {
                    Intent whatsapptxt = new Intent();
                    whatsapptxt.setAction(Intent.ACTION_SEND);
                    whatsapptxt.putExtra(Intent.EXTRA_TEXT, tenplusmintxt);
                    whatsapptxt.setType("text/plain");
                    whatsapptxt.setPackage("com.whatsapp");
                    startActivity(whatsapptxt);
                } else {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        tenplus = true;
                        pyydaLuvatSms();
                    } else {
                        try {
                            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                            SmsManager sms = SmsManager.getDefault();
                            sms.sendTextMessage(smsnumero11, null, tenplusmintxt, null, null);
                            Toast.makeText(getApplicationContext(),"Yli 10min ilmoitus lähetetty. (" + tenplusmintxt + ")", Toast.LENGTH_LONG).show();
                        } catch(Exception e) {
                            Toast.makeText(getApplicationContext(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }

            }
        });

        avaaKartta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avaakarttatyhjana();
            }
        });

    }

    /*public void lupaAanienMuuttamiseen(Context context){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !notificationManager.isNotificationPolicyAccessGranted()) {

                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);

                startActivity(intent);
            }
        }
    }*/

    public int saadaAani(int voima) {
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(audioManager != null) {
            tekstiPuheeksiVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            double aani = (double)tekstiPuheeksiVol/100*voima;
            tekstiPuheeksiVol = (int) aani;
        }

        if(tekstiPuheeksiVol == 0) { return 1;
        } else if(tekstiPuheeksiVol == 1) {
            return 1;
        } else if(tekstiPuheeksiVol == 2) {
            return 2;
        } else if(tekstiPuheeksiVol == 3) {
            return 3;
        } else if(tekstiPuheeksiVol == 4) {
            return 4;
        } else if(tekstiPuheeksiVol == 5) {
            return 5;
        } else if(tekstiPuheeksiVol == 6) {
            return 6;
        } else if(tekstiPuheeksiVol == 7) {
            return 7;
        }

        return tekstiPuheeksiVol;
    }

    public void txtToSpeech(){
        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getApplicationContext(), "Kieli ei ole tuettu.", Toast.LENGTH_LONG).show();
                    }
                    puhu();
                } else {
                    Toast.makeText(getApplicationContext(), "Virhe", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void puhu() {
        String puheeksi = textViewHalytunnus.getText().toString() + " " + textView.getText().toString();
        if(Build.VERSION.SDK_INT >= 21) {
            t1.playSilentUtterance(1000, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            t1.playSilence(1000, TextToSpeech.QUEUE_FLUSH, null);
        }
        t1.speak(puheeksi, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void btnfive() {
        // Alle 5min ilmoitus
        try {
            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero, null, fivemintxt, null, null);
            Toast.makeText(getApplicationContext(),"Alle 5min ilmoitus lähetetty. (" + fivemintxt + ")", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        five = false;
    }

    public void btnten() {
        // Alle 10min ilmoitus
        try {
            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero10, null, tenmintxt, null, null);
            Toast.makeText(getApplicationContext(),"Alle 10min ilmoitus lähetetty. (" + tenmintxt + ")", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        ten = false;
    }

    public void btntenplus() {
        // Yli 10min ilmoitus
        try {
            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero11, null, tenplusmintxt, null, null);
            Toast.makeText(getApplicationContext(),"Yli 10min ilmoitus lähetetty. (" + tenplusmintxt + ")", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(getApplicationContext(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        tenplus = false;
    }

    public void haeUusinHalyTietokannasta(){
        try {
            db = new DBHelper(getApplicationContext());
            Cursor c = db.haeViimeisinLisays();
            if(c != null) {
                textViewHalytunnus.setText(c.getString(c.getColumnIndex(DBHelper.TUNNUS)));
                textView.setText(c.getString(c.getColumnIndex(DBHelper.VIESTI)));
                second.setText(c.getString(c.getColumnIndex(DBHelper.LUOKKA)));
                final String valmisOsoite = c.getString(c.getColumnIndex(DBHelper.LUOKKA));
                second.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + valmisOsoite);
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        //mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }
                    }
                });
            }
        } catch (Exception e) {
            new AlertDialog.Builder(aktiivinenHaly.this)
                    .setTitle("Huomautus!")
                    .setMessage("Arkisto on tyhjä. Ei näytettävää hälytystä.")
                    .setPositiveButton("OK", null)
                    .create()
                    .show();
        }

    }

    public void pyydaLuvatCallPhone() {
        if (ContextCompat.checkSelfPermission(aktiivinenHaly.this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {

            // Pitäisikö näyttää selite?
            if (ActivityCompat.shouldShowRequestPermissionRationale(aktiivinenHaly.this,
                    Manifest.permission.CALL_PHONE)) {

                // Näytä selite, älä blokkaa threadia.
                showMessageOKCancel("Et voi käyttää soita nappia jos et anna lupaa.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            }
                        });
            } else {

                // Selitettä ei tarvita.

                ActivityCompat.requestPermissions(aktiivinenHaly.this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else {
            // soita
            try {
                ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.CALL_PHONE);
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                startActivity(callIntent);
            }catch(Exception e) {
                Toast.makeText(getApplicationContext(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    /*public void pyydaLuvatPhoneState() {
        if (ContextCompat.checkSelfPermission(aktiivinenHaly.this,
                Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            // Pitäisikö näyttää selite?
            if (ActivityCompat.shouldShowRequestPermissionRationale(aktiivinenHaly.this,
                    Manifest.permission.READ_PHONE_STATE)) {

                // Näytä selite, älä blokkaa threadia.
                showMessageOKCancel("Puheluilla tulevia hälytyksiä ei voi tunnistaa ilman tätä lupaa.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.READ_PHONE_STATE},
                                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                            }
                        });
            } else {

                // Selitettä ei tarvita.

                ActivityCompat.requestPermissions(aktiivinenHaly.this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } /*else {
            // soita
            try {
                ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.READ_PHONE_STATE);
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                startActivity(callIntent);
            }catch(Exception e) {
                Toast.makeText(getApplicationContext(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                        Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }*/
    //}*/

    public void pyydaLuvatSms() {
        if (ContextCompat.checkSelfPermission(aktiivinenHaly.this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            // Pitäisikö näyttää selite?
            if (ActivityCompat.shouldShowRequestPermissionRationale(aktiivinenHaly.this,
                    Manifest.permission.SEND_SMS)) {

                // Näytä selite, älä blokkaa threadia.
                showMessageOKCancel("Et voi lähettää viestiä jos et anna lupaa.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                                        MY_PERMISSIONS_REQUEST_SEND_SMS);
                            }
                        });
            } else {

                // Selitettä ei tarvita.

                ActivityCompat.requestPermissions(aktiivinenHaly.this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        } else {
            if(five) {
                btnfive();
            } else if(ten) {
                btnten();
            } else if(tenplus) {
                btntenplus();
            }
        }
    }

    /*public void pyydaLuvat2(){
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
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(aktiivinenHaly.this,
                        new String[]{Manifest.permission.RECEIVE_SMS},
                        MY_PERMISSIONS_REQUEST_RECEIVE_SMS);
            }
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // lupa annettu
                    try {
                        ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.CALL_PHONE);
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                        startActivity(callIntent);
                    }catch(Exception e) {
                        Toast.makeText(getApplicationContext(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    // lupaa ei ole. pysäytä toiminto
                    new AlertDialog.Builder(aktiivinenHaly.this)
                            .setMessage("Sovelluksella ei ole lupaa soittamiseen. Et voi soittaa ennen kuin lupa on myönnetty.")
                            .setNegativeButton("Peruuta", null)
                            .create()
                            .show();
                }
                return;
            }
            /*case MY_PERMISSIONS_REQUEST_RECEIVE_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // lupa on
                    haeUusinHalyTietokannasta();
                } else {
                    // ei lupaa
                    new AlertDialog.Builder(aktiivinenHaly.this)
                            .setMessage("Sovelluksella ei ole lupaa vastaanottaa viestejä. Et saa hälytyksiä ilman lupaa.")
                            .setNegativeButton("Peruuta", null)
                            .create()
                            .show();
                }
                return;
            }*/
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(five) {
                        btnfive();
                    } else if(ten) {
                        btnten();
                    } else if (tenplus) {
                        btntenplus();
                    }
                } else {
                    new AlertDialog.Builder(aktiivinenHaly.this)
                            .setMessage("Sovelluksella ei ole lupaa lähettää viestejä. Et voi lähettää pikaviestiä ennen kuin lupa on myönnetty.")
                            .setNegativeButton("Peruuta", null)
                            .create()
                            .show();
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request.
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

    public void avaakarttatyhjana () {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + "");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        //mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    protected void onPause () {
        if(t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if(ad != null && palautaMediaVolBoolean) {
            ad.setStreamVolume(AudioManager.STREAM_MUSIC, palautaMediaVol, 0);
        }
        super.onPause();
    }

    @Override
    protected void onResume() { super.onResume(); }
}
