/*
 * Created by Kultala Aki on 6/26/22, 6:18 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.util.List;

import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.dbfirealarm.FireAlarmViewModel;
import kultalaaki.vpkapuri.services.SMSBackgroundService;


public class AlarmButtonsFragment extends Fragment {

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
    private SharedPreferences pref_general;
    private TextView callNumber, sms5Otsikko, sms5Sisalto, sms5Recipient, sms10Otsikko, sms10Sisalto, sms10Recipient, sms11Otsikko, sms11Sisalto, sms11Recipient, osoite, hiljennys;
    private String soittonumero, smsnumero, smsnumero10, smsnumero11, fivemintxtotsikko, fivemintxt, tenmintxtotsikko, tenmintxt, tenplusmintxtotsikko, tenplusmintxt;

    private Listener mCallback;

    public interface Listener {
        void hiljenna();

        void autoAukaisuPuhu();

        void avaaWebSivu(String url);

        void showToast(String head, String message);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResources();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context ctx = getActivity();
        if (ctx != null) {
            LifecycleOwner lf = getViewLifecycleOwner();
            FireAlarmViewModel fireAlarmViewModel = ViewModelProviders.of(getActivity()).get(FireAlarmViewModel.class);
            fireAlarmViewModel.getAddress().observe(lf, new Observer<CharSequence>() {
                @Override
                public void onChanged(CharSequence charSequence) {
                    osoiteFromDB = charSequence.toString();
                    osoite.setText(charSequence);
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnClickListeners();
        setTexts();
        if (showHiljennaButton) {
            autoAukaisu();
        }
    }

    private void setTexts() {
        if (CallButtonVisible) {
            callNumber.setText(soittonumero);
        } else {
            call.setVisibility(View.GONE);
        }

        osoite.setText(osoiteFromDB);

        if (SmsGreenVisible) {
            sms5Otsikko.setText(fivemintxtotsikko);
            sms5Sisalto.setText(fivemintxt);
            sms5Recipient.setText(smsnumero);
        } else {
            fiveMin.setVisibility(View.GONE);
        }

        if (SmsYellowVisible) {
            sms10Otsikko.setText(tenmintxtotsikko);
            sms10Sisalto.setText(tenmintxt);
            sms10Recipient.setText(smsnumero10);
        } else {
            tenMin.setVisibility(View.GONE);
        }

        if (SmsRedVisible) {
            sms11Otsikko.setText(tenplusmintxtotsikko);
            sms11Sisalto.setText(tenplusmintxt);
            sms11Recipient.setText(smsnumero11);
        } else {
            tenMinplus.setVisibility(View.GONE);
        }
    }

    public void setTextHiljennaPuhe() {
        hiljenna.setVisibility(View.VISIBLE);
        hiljennys.setText(R.string.hiljenna_puhe);
    }

    public void autoAukaisu() {
        stopAlarm = true;
        hiljenna.setVisibility(View.VISIBLE);
        hiljennys.setText(R.string.hiljenna_halytys);
        hiljenna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = getActivity();
                if (ctx != null) {
                    Intent stopAlarm = new Intent(ctx, SMSBackgroundService.class);
                    ctx.stopService(stopAlarm);
                }
                if (koneluku) {
                    autoAukaisuHiljennaPuhe();
                } else {
                    hiljenna.setVisibility(View.GONE);
                }
            }
        });
    }

    public void autoAukaisuHiljennaPuhe() {
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
            }
        });

        fiveMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsnumero != null && smsnumero.equals("whatsapp")) {
                    sendTextToWhatsapp(fivemintxt);
                } else if (smsnumero != null && smsnumero.contains("www")) {
                    mCallback.avaaWebSivu(smsnumero);
                } else if (smsnumero != null && smsnumero.equals("valitse")) {
                    sendTextWithOtherMessageApp(fivemintxt);
                } else {
                    five = true;
                    pyydaLuvatSms();
                }
            }
        });

        tenMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsnumero10 != null && smsnumero10.equals("whatsapp")) {
                    sendTextToWhatsapp(tenmintxt);
                } else if (smsnumero10 != null && smsnumero10.equals("valitse")) {
                    sendTextWithOtherMessageApp(tenmintxt);
                } else {
                    ten = true;
                    pyydaLuvatSms();
                }
            }
        });

        tenMinplus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsnumero11 != null && smsnumero11.equals("whatsapp")) {
                    sendTextToWhatsapp(tenplusmintxt);
                } else if (smsnumero11 != null && smsnumero11.equals("valitse")) {
                    sendTextWithOtherMessageApp(tenplusmintxt);
                } else {
                    tenplus = true;
                    pyydaLuvatSms();
                }

            }
        });

        openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoiteFromDB);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                Context context = getActivity();
                if (context != null) {
                    PackageManager packageManager = context.getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent,
                            PackageManager.MATCH_DEFAULT_ONLY);
                    if (activities.size() > 0) {
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

    private void smsSendFailed() {
        mCallback.showToast("Lähetys epäonnistui.", "Tarkista lupa lähettää viestejä ja numeroiden asetukset.");
    }

    private void notifSend(String head, String message) {
        mCallback.showToast(head, message);
    }

    private void sendTextWithOtherMessageApp(String textToSend) {
        Intent signalMessage = new Intent(Intent.ACTION_SEND);
        signalMessage.putExtra(Intent.EXTRA_TEXT, textToSend);
        signalMessage.setType("text/plain");
        PackageManager packageManager = requireActivity().getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(signalMessage, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        if (isIntentSafe) {
            startActivity(signalMessage);
        }
    }

    private void sendTextToWhatsapp(String textToSend) {
        Intent whatsapptxt = new Intent();
        whatsapptxt.setAction(Intent.ACTION_SEND);
        whatsapptxt.putExtra(Intent.EXTRA_TEXT, textToSend);
        whatsapptxt.setType("text/plain");
        whatsapptxt.setPackage("com.whatsapp");
        startActivity(whatsapptxt);
    }

    private void pyydaLuvatSms() {
        Context ctx = getActivity();
        if (ctx != null) {
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
                                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
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
                if (five) {
                    btnfive();
                } else if (ten) {
                    btnten();
                } else if (tenplus) {
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
                        if (ctx != null) {
                            ContextCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE);
                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + soittonumero));
                            startActivity(callIntent);
                        }
                    } catch (Exception e) {
                        mCallback.showToast("Puhelu epäonnistui.", "Tarkista sovelluksen lupa soittaa ja asetettu numero.");
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
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (five) {
                        btnfive();
                    } else if (ten) {
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
        }
    }

    private void btnfive() {
        // Alle 5min ilmoitus
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero, null, fivemintxt, null, null);
            notifSend("Lähetetty.", "Viesti: " + fivemintxt);
        } catch (Exception e) {
            smsSendFailed();
            e.printStackTrace();
        }
        five = false;
    }

    private void btnten() {
        // Alle 10min ilmoitus
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero10, null, tenmintxt, null, null);
            notifSend("Lähetetty.", "Viesti: " + tenmintxt);
        } catch (Exception e) {
            smsSendFailed();
            e.printStackTrace();
        }
        ten = false;
    }

    private void btntenplus() {
        // Yli 10min ilmoitus
        try {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(smsnumero11, null, tenplusmintxt, null, null);
            notifSend("Lähetetty.", "Viesti: " + tenplusmintxt);
        } catch (Exception e) {
            smsSendFailed();
            e.printStackTrace();
        }
        tenplus = false;
    }

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
        if (stopAlarm && ctx != null) {
            Intent stopAlarm = new Intent(ctx, SMSBackgroundService.class);
            ctx.stopService(stopAlarm);
        }
        pref_general.edit().putBoolean("showHiljenna", false).commit();
    }
}
