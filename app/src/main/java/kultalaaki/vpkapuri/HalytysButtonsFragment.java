package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


public class HalytysButtonsFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 3;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 4;
    private CardView call, fiveMin, tenMin, tenMinplus, hiljenna, openMap;
    private boolean five;
    private boolean ten;
    private boolean koneluku;
    private boolean tenplus = false;
    private boolean stopAlarm = false;
    private boolean SmsGreenVisible;
    private boolean SmsYellowVisible;
    private boolean SmsRedVisible;
    private boolean CallButtonVisible;
    private boolean showHiljennaButton;
    private String osoiteFromDB;
    SharedPreferences pref_general;
    private TextView callNumber, sms5Otsikko, sms5Sisalto, sms5Recipient, sms10Otsikko, sms10Sisalto, sms10Recipient, sms11Otsikko, sms11Sisalto, sms11Recipient, osoite, hiljennys;
    private String soittonumero, smsnumero, smsnumero10, smsnumero11, fivemintxtotsikko, fivemintxt, tenmintxtotsikko, tenmintxt, tenplusmintxtotsikko, tenplusmintxt;
    Intent intent;

    private Listener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface Listener {
        /** Called when a button is clicked in HalytysButtonsFragment */
        void hiljenna();
        void autoAukaisuPuhu();
        void avaaWebSivu(String url);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResources();
    }

    /*@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (Listener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement Listener");
        }
    }*/

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FireAlarmViewModel mViewModel = ViewModelProviders.of(this).get(FireAlarmViewModel.class);

        try {
            FireAlarm fireAlarm = mViewModel.lastEntry();
            osoiteFromDB = fireAlarm.getOsoite();
        } catch (Exception e) {
            // Empty database
            osoiteFromDB = "";
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
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
        if(showHiljennaButton) {
            autoAukaisu();
        }
    }

    private void setTexts() {
        if(CallButtonVisible) {
            callNumber.setText(soittonumero);
        } else {
            call.setVisibility(View.GONE);
        }

        osoite.setText(osoiteFromDB);

        if(SmsGreenVisible) {
            sms5Otsikko.setText(fivemintxtotsikko);
            sms5Sisalto.setText(fivemintxt);
            sms5Recipient.setText(smsnumero);
        } else {
            fiveMin.setVisibility(View.GONE);
        }

        if(SmsYellowVisible) {
            sms10Otsikko.setText(tenmintxtotsikko);
            sms10Sisalto.setText(tenmintxt);
            sms10Recipient.setText(smsnumero10);
        } else {
            tenMin.setVisibility(View.GONE);
        }

        if(SmsRedVisible) {
            sms11Otsikko.setText(tenplusmintxtotsikko);
            sms11Sisalto.setText(tenplusmintxt);
            sms11Recipient.setText(smsnumero11);
        } else {
            tenMinplus.setVisibility(View.GONE);
        }
    }

    void setTextHiljennaPuhe() {
        hiljenna.setVisibility(View.VISIBLE);
        hiljennys.setText(R.string.hiljenna_puhe);
    }

    void autoAukaisu() {
        stopAlarm = true;
        hiljenna.setVisibility(View.VISIBLE);
        hiljennys.setText(R.string.hiljenna_halytys);
        hiljenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getActivity();
                if(ctx != null) {
                    Intent stopAlarm = new Intent(ctx, IsItAlarmService.class);
                    ctx.stopService(stopAlarm);
                }
                if(koneluku) {
                    autoAukaisuHiljennaPuhe();
                } else {
                    hiljenna.setVisibility(View.GONE);
                }
            }
        });
    }

    private void autoAukaisuHiljennaPuhe() {
        mCallback.autoAukaisuPuhu();
        hiljennys.setText(R.string.hiljenna_puhe);
        hiljenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.hiljenna();
                hiljenna.setVisibility(View.GONE);
            }
        });
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
        hiljenna.setVisibility(View.GONE);
        hiljennys = view.findViewById(R.id.hiljennys);
    }

    private void setResources() {
        pref_general = PreferenceManager.getDefaultSharedPreferences(getActivity());
        showHiljennaButton = pref_general.getBoolean("showHiljenna", false);
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
        SmsGreenVisible = pref_general.getBoolean("SmsGreenVisible", true);
        SmsYellowVisible = pref_general.getBoolean("SmsYellowVisible", true);
        SmsRedVisible = pref_general.getBoolean("SmsRedVisible", true);
        CallButtonVisible = pref_general.getBoolean("CallButtonVisible", true);
    }

    private void setOnClickListeners() {
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + soittonumero));
                startActivity(callIntent);
                // todo testataan ilman lupia soittaa intentin avulla
                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
                }*/
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
                } else if(smsnumero != null && smsnumero.contains("www") && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    mCallback.avaaWebSivu(smsnumero);
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

        /*Cursor c = db.haeViimeisinLisays();
        //openMap.setText(c.getString(c.getColumnIndex(DBHelper.LUOKKA)));
        String osoitee = "";
        try {
            osoitee = c.getString(c.getColumnIndex(DBHelper.LUOKKA));
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Arkisto on tyhjä.", Toast.LENGTH_LONG).show();
        }
        final String osoite = osoitee;*/

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoiteFromDB);
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

        hiljenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.hiljenna();
                hiljenna.setVisibility(View.GONE);
            }
        });
    }

    void updateAddress(String updatedAddress) {
        osoiteFromDB = updatedAddress;
        osoite.setText(updatedAddress);
    }

    /*public void pyydaLuvatCallPhone() {
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
    }*/

    private void pyydaLuvatSms() {
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
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
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

    private void btnfive() {
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

    private void btnten() {
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

    private void btntenplus() {
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

    /*private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Et voi käyttää soita nappia jos et anna lupaa.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }*/

    private void showMessageOKCancelSms(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Sovelluksella ei ole lupaa lähettää viestejä. Et voi lähettää pikaviestiä ennen kuin lupa on myönnetty.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onDestroy() {
        super.onDestroy();
        Context ctx = getActivity();
        if(stopAlarm && ctx != null) {
            Intent stopAlarm = new Intent(ctx, IsItAlarmService.class);
            ctx.stopService(stopAlarm);
        }
        pref_general.edit().putBoolean("showHiljenna", false).commit();
    }
}
