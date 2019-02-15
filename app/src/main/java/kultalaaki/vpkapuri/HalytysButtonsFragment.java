package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.TargetApi;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class HalytysButtonsFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4;
    CardView call, fiveMin, tenMin, tenMinplus, hiljenna, openMap;
    boolean five, ten, tenPlus, autoAukaisu, koneluku, five2 = false, ten2 = false, tenplus = false, palautaMediaVolBoolean = false;
    String fiveminText, tenMinText, tenPlusMin;
    static DBHelper db;
    TextView callNumber, sms5Otsikko, sms5Sisalto, sms5Recipient, sms10Otsikko, sms10Sisalto, sms10Recipient, sms11Otsikko, sms11Sisalto, sms11Recipient, osoite;
    String soittonumero, smsnumero, smsnumero10, smsnumero11, fivemintxtotsikko, fivemintxt, tenmintxtotsikko, tenmintxt, tenplusmintxtotsikko, tenplusmintxt, action, type;
    TextToSpeech t1;
    int tekstiPuheeksiVol, palautaMediaVol;
    Intent intent;
    SharedPreferences aaneton = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResources();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.halytys_buttons_fragment, parent, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        setOnClickListeners();
        setTexts();
    }

    public void setTexts() {
        callNumber.setText(soittonumero);

        osoite.setText(osoite());

        sms5Otsikko.setText(fivemintxtotsikko);
        sms5Sisalto.setText(fivemintxt);
        sms5Recipient.setText(smsnumero);

        sms10Otsikko.setText(tenmintxtotsikko);
        sms10Sisalto.setText(tenmintxt);
        sms10Recipient.setText(smsnumero10);

        sms11Otsikko.setText(tenplusmintxtotsikko);
        sms11Sisalto.setText(tenplusmintxt);
        sms11Recipient.setText(smsnumero11);
    }

    public String osoite(){
        try {
            db = new DBHelper(getActivity());
            Cursor c = db.haeViimeisinLisays();
            if(c != null) {
                return c.getString(c.getColumnIndex(DBHelper.LUOKKA));
            }
            return "";
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        call = view.findViewById(R.id.callCard);
        callNumber = view.findViewById(R.id.number);

        fiveMin = view.findViewById(R.id.greenCard);
        sms5Otsikko = view.findViewById(R.id.sms5Otsikko);
        sms5Sisalto = view.findViewById(R.id.sms5sisalto);
        sms5Recipient = view.findViewById(R.id.sms5Recipient);

        tenMin = view.findViewById(R.id.orangeCard);
        sms10Otsikko = view.findViewById(R.id.sms10Otsikko);
        sms10Sisalto = view.findViewById(R.id.sms10sisalto);
        sms10Recipient = view.findViewById(R.id.sms10Recipient);

        tenMinplus = view.findViewById(R.id.redCard);
        sms11Otsikko = view.findViewById(R.id.sms11Otsikko);
        sms11Sisalto = view.findViewById(R.id.sms11sisalto);
        sms11Recipient = view.findViewById(R.id.sms11Recipient);

        osoite = view.findViewById(R.id.osoiteteksti);

        openMap = view.findViewById(R.id.addressCard);
        hiljenna = view.findViewById(R.id.somethingCard);
    }

    void setResources() {
        SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(getActivity());
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
        db = new DBHelper(getActivity());
    }

    void setOnClickListeners() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    pyydaLuvatCallPhone();
                } else {
                    Context context = getActivity();
                    if(context != null) {
                        try {
                            ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                            startActivity(callIntent);
                        }catch(Exception e) {
                            Toast.makeText(context,"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                                    Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        fiveMin.setOnClickListener(new View.OnClickListener() {
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
                        Context context = getActivity();
                        if(context != null) {
                            try {
                                //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(smsnumero, null, fivemintxt, null, null);
                                Toast.makeText(context,"Alle 5min ilmoitus lähetetty. (" + fivemintxt + ")", Toast.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Toast.makeText(context,"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });

        tenMin.setOnClickListener(new View.OnClickListener() {
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
                        Context context = getActivity();
                        if(context != null) {
                            try {
                                //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(smsnumero10, null, tenmintxt, null, null);
                                Toast.makeText(context,"Alle 10min ilmoitus lähetetty. (" + tenmintxt + ")", Toast.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Toast.makeText(context,"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });

        tenMinplus.setOnClickListener(new View.OnClickListener() {
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
                        Context context = getActivity();
                        if(context != null) {
                            try {
                                //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                                SmsManager sms = SmsManager.getDefault();
                                sms.sendTextMessage(smsnumero11, null, tenplusmintxt, null, null);
                                Toast.makeText(context,"Yli 10min ilmoitus lähetetty. (" + tenplusmintxt + ")", Toast.LENGTH_LONG).show();
                            } catch(Exception e) {
                                Toast.makeText(context,"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                                        Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }
                    }
                }

            }
        });

        Cursor c = db.haeViimeisinLisays();
        //openMap.setText(c.getString(c.getColumnIndex(DBHelper.LUOKKA)));
        final String osoite = c.getString(c.getColumnIndex(DBHelper.LUOKKA));
        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoite);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                Context context = getActivity();
                if(context != null) {
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }

            }
        });
    }

    public void pyydaLuvatCallPhone() {
        Context context = getActivity();
        if(context != null) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Pitäisikö näyttää selite?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CALL_PHONE)) {

                    // Näytä selite, älä blokkaa threadia.
                    showMessageOKCancel(
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

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            } else {
                // soita
                try {
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                    startActivity(callIntent);
                }catch(Exception e) {
                    Toast.makeText(getActivity(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }

    public void pyydaLuvatSms() {
        Context ctx = getActivity();
        if(ctx != null) {
            if (ContextCompat.checkSelfPermission(ctx,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {

                // Pitäisikö näyttää selite?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.SEND_SMS)) {

                    // Näytä selite, älä blokkaa threadia.
                    showMessageOKCancelSms(
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

                    ActivityCompat.requestPermissions(getActivity(),
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
    }

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
                        Context ctx = getActivity();
                        if(ctx != null) {
                            ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE);
                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                            startActivity(callIntent);
                        }
                    }catch(Exception e) {
                        Toast.makeText(getActivity(),"Puhelu ei onnistunut. Tarkista sovelluksen lupa soittaa ja asetettu numero.",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    // lupaa ei ole. pysäytä toiminto
                    new AlertDialog.Builder(getActivity())
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
                    new AlertDialog.Builder(getActivity())
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

    public int saadaAani(int voima) {
        Context ctx = getActivity();
        if(ctx != null) {
            final AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                tekstiPuheeksiVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                double aani = (double)tekstiPuheeksiVol/100*voima;
                tekstiPuheeksiVol = (int) aani;
            }
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

    public void btnfive() {
        // Alle 5min ilmoitus
        try {
            //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero, null, fivemintxt, null, null);
            Toast.makeText(getActivity(),"Alle 5min ilmoitus lähetetty. (" + fivemintxt + ")", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(getActivity(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
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
            Toast.makeText(getActivity(),"Alle 10min ilmoitus lähetetty. (" + tenmintxt + ")", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(getActivity(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
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
            Toast.makeText(getActivity(),"Yli 10min ilmoitus lähetetty. (" + tenplusmintxt + ")", Toast.LENGTH_LONG).show();
        } catch(Exception e) {
            Toast.makeText(getActivity(),"Tekstiviestin lähetys ei onnistunut. Tarkista sovelluksen lupa lähettää viestejä ja numeroiden asetukset.",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        tenplus = false;
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Et voi käyttää soita nappia jos et anna lupaa.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }

    private void showMessageOKCancelSms(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Sovelluksella ei ole lupaa lähettää viestejä. Et voi lähettää pikaviestiä ennen kuin lupa on myönnetty.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }
}
