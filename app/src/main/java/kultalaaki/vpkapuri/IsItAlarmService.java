/*
 * Created by Kultala Aki on 29.4.2018 18:30
 * Copyright (c) 2018. All rights reserved.
 *
 * Last modified 29.4.2018 18:30
 */

package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Toast;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IsItAlarmService extends Service implements MediaPlayer.OnPreparedListener {

    private static final String TAG = "VPK Apuri käynnissä.";
    private static int previousStartId = 1;
    private static boolean mediaplayerRunning = false;
    MediaPlayer mMediaPlayer;
    Vibrator viber;
    static ArrayList<String> kunnat = new ArrayList<>(), halytunnukset = new ArrayList<>(), halytekstit = new ArrayList<>(), OHTOnumbers = new ArrayList<>();
    SharedPreferences sharedPreferences;
    private static final int MY_HALY_NOTIFICATION_ID = 264981;
    int aanenVoimakkuus, volume, aanetonser, palautaAani, palautaStreamAlarm;
    boolean tarina, autoAukaisu, aanetVaiEi, puhelu, pitaaPalauttaa = false, OHTO = false, ensivaste = false, alarmIsEnsivaste = false, puheluAani = false;
    String puheluHaly = "false";
    static boolean erica;
    static boolean asemataulu;

    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    Context context;

    public IsItAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind()");
        return null;
    }

    public void onCreate() {
        super.onCreate();
        context = this;
        //Log.e("IsItAlarmService", "onCreate");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeGround(TAG);
        }
    }

    @SuppressLint("ApplySharedPref")
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (intent != null) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if(powerManager != null) {
                wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        "VPKApuri::HälytysServiceTaustalla");
            }

            if (previousStartId != startId) {
                stopSelf(previousStartId);
                if (mediaplayerRunning && mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mediaplayerRunning = false;
                }
            }
            previousStartId = startId;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForeGround(TAG);
            }

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            autoAukaisu = sharedPreferences.getBoolean("autoAukaisu", false);
            String numero = intent.getStringExtra("number");
            String message = intent.getStringExtra("message");
            String timestamp = intent.getStringExtra("timestamp");

            puheluHaly = intent.getStringExtra("halytysaani");
            // if erica is false, it's OHTO alarm.
            erica = sharedPreferences.getBoolean("Erica", true);
            OHTO = sharedPreferences.getBoolean("OHTO", false);
            ensivaste = sharedPreferences.getBoolean("Ensivaste", false);
            asemataulu = sharedPreferences.getBoolean("asemataulu", false);
            // isItAlarmSMS testaa numeron ja viestin | halytysaani true (puhelu) false(sms) kummasta broadcastreceiveristä tuli
            if (asemataulu) {
                // Test if it is alarm message. If not alarm, test is it person attending alarm.
                if (isItAlarmSMS(numero, message)) {

                    if (erica) {
                        lisaaHalyTunnukset();
                        lisaaKunnatErica();
                        addressLookUp(message, timestamp);
                    } else {
                        // OHTO alarm
                        OHTOAlarm(message, timestamp, numero);
                    }

                    if(!sharedPreferences.getBoolean("HalytysOpen", false)) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent openHalytysActivity = new Intent(IsItAlarmService.this, HalytysActivity.class);
                                openHalytysActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                IsItAlarmService.this.startActivity(openHalytysActivity);
                                stopSelf(startId);
                            }
                        }, 3000);
                    }

                } else {
                    // Test who is coming and save to database..
                    whoIsComing(numero, message);
                    stopSelf(startId);
                }
            } else if (isItAlarmSMS(numero, message) && puheluHaly.equals("false")) {

                lisaaHalyTunnukset();
                if (erica) {
                    lisaaKunnatErica();
                    addressLookUp(message, timestamp);
                } else {
                    // OHTO alarm
                    OHTOAlarm(message, timestamp, numero);
                }

                alarmSound(startId);

                if (autoAukaisu) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent halyAuki = new Intent(IsItAlarmService.this, HalytysActivity.class);
                            halyAuki.setAction(Intent.ACTION_SEND);
                            halyAuki.setType("automaattinen");
                            halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(halyAuki);
                        }
                    }, 3000);
                } else {
                    createNotification(message);
                }
            } else if (isItAlarmSMS(numero, message) && puheluHaly.equals("true")) {
                puhelu = sharedPreferences.getBoolean("puhelu", false);
                if (puhelu) {
                    puheluAani = sharedPreferences.getBoolean("Puhelu", false);
                    alarmSound(startId);
                }
                //db = new DBHelper(getApplicationContext());
                //db.insertData("999A", "Ei osoitetta", "Hälytys tuli puheluna", "");
                FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
                FireAlarm fireAlarm = new FireAlarm("999", "A", "Hälytys tuli puheluna",
                        "Ei osoitetta", "", "", timestamp, "", "", "", "");

                fireAlarmRepository.insert(fireAlarm);

                if (autoAukaisu) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent halyAuki = new Intent(IsItAlarmService.this, HalytysActivity.class);
                            halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            halyAuki.setAction(Intent.ACTION_SEND);
                            halyAuki.setType("automaattinen");
                            startActivity(halyAuki);
                        }
                    }, 3000);
                } else {
                    createNotificationPuhelu("Hälytys tuli puheluna");
                }
            } else {
                //Log.e("IsItAlarmService", "onStartCommand stopSelf + startId: " + startId);
                stopSelf(startId);
            }
        } else {
            //Log.e("IsItAlarmService", "onStartCommand stopSelf + startId: " + startId);
            stopSelf(startId);
        }
        return Service.START_STICKY;
    }

    private String numberFormat(String number) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (number != null && !number.isEmpty()) {
                if(number.substring(0,1).equals("O")) {
                    number = number.substring(1);
                    // this is OHTO alarm number
                    OHTOnumbers.add(number);
                }
                number = PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
                if(number != null) {
                    if (number.charAt(0) == '0') {
                        number = "+358" + number.substring(1);
                    }
                    number = number.replaceAll("[()\\s-+]+", "");
                    return number;
                }
                return "99987654321";
            }
            return "99987654321";
        } else {
            if (number != null && !number.isEmpty()) {
                if(number.substring(0,1).equals("O")) {
                    number = number.substring(1);
                    // this is OHTO alarm number
                    OHTOnumbers.add(number);
                }
                number = PhoneNumberUtils.formatNumber(number);
                if(number != null) {
                    if (number.charAt(0) == '0') {
                        number = "+358" + number.substring(1);
                    }
                    number = number.replaceAll("[()\\s-+]+", "");
                    return number;
                }
                return "99987654321";
            }
            return "99987654321";
        }
    }

    private void whoIsComing(String numero, String message) {
        ResponderRepository repository = new ResponderRepository(getApplication());
        numero = numberFormat(numero);

        if(!numero.equals("99987654321")) {
            for (int i = 1; i <= 40; i++) {
                String numeroFromSettings = sharedPreferences.getString("puhelinnumero" + i, null);
                Log.e("TAG", "Numero: " + numeroFromSettings + i);
                numeroFromSettings = numberFormat(numeroFromSettings);

                if (numeroFromSettings != null && !numeroFromSettings.isEmpty()) {
                    if (numeroFromSettings.equals(numero)) {
                        // TODO: numero löydetty asetetuista jäsenistä. Koosta henkilö ja tallenna tietokantaan
                        Log.e("TAG", "Numero tunnistettu. Koostetaan henkilö. NumeroFromSettings: " + numeroFromSettings + ". Message: " + message);
                        String name = sharedPreferences.getString("nimi" + i, null);
                        boolean driversLicense = sharedPreferences.getBoolean("kortti" + i, false);
                        boolean smoke = sharedPreferences.getBoolean("savusukeltaja" + i, false);
                        boolean chemical = sharedPreferences.getBoolean("kemikaalisukeltaja" + i, false);
                        boolean leader = sharedPreferences.getBoolean("yksikonjohtaja" + i, false);
                        String vacancyNumber = sharedPreferences.getString("vakanssinumero" + i, null);
                        String optional1 = sharedPreferences.getString("optional1_" + i, null);
                        String optional2 = sharedPreferences.getString("optional2_" + i, null);
                        String optional3 = sharedPreferences.getString("optional3_" + i, null);
                        String optional4 = sharedPreferences.getString("optional4_" + i, null);
                        String optional5 = sharedPreferences.getString("optional5_" + i, null);
                        String driver = "";
                        String smok = "";
                        String chem = "";
                        String lead = "";
                        if (driversLicense) {
                            driver = "C";
                        }
                        if (smoke) {
                            smok = "S";
                        }
                        if (chemical) {
                            chem = "K";
                        }
                        if (leader) {
                            lead = "Y";
                        }
                        Responder responder = new Responder(name, vacancyNumber, message, lead, driver, smok, chem, optional1, optional2, optional3, optional4, optional5);
                        repository.insert(responder);

                        Toast.makeText(this, name + " ilmoittautui hälytykseen.", Toast.LENGTH_SHORT).show();

                        break;
                    }
                }
            }
        }
    }

    private boolean isItAlarmSMS(String numero, String message) {
        numero = numberFormat(numero);

        if(!numero.equals("99987654321")) {
            String halynumero1 = sharedPreferences.getString("halyvastaanotto1", null);
            String halynumero2 = sharedPreferences.getString("halyvastaanotto2", null);
            String halynumero3 = sharedPreferences.getString("halyvastaanotto3", null);
            String halynumero4 = sharedPreferences.getString("halyvastaanotto4", null);
            String halynumero5 = sharedPreferences.getString("halyvastaanotto5", null);
            String halynumero6 = sharedPreferences.getString("halyvastaanotto6", null);
            String halynumero7 = sharedPreferences.getString("halyvastaanotto7", null);
            String halynumero8 = sharedPreferences.getString("halyvastaanotto8", null);
            String halynumero9 = sharedPreferences.getString("halyvastaanotto9", null);
            String halynumero10 = sharedPreferences.getString("halyvastaanotto10", null);

            halynumero1 = numberFormat(halynumero1);
            halynumero2 = numberFormat(halynumero2);
            halynumero3 = numberFormat(halynumero3);
            halynumero4 = numberFormat(halynumero4);
            halynumero5 = numberFormat(halynumero5);
            halynumero6 = numberFormat(halynumero6);
            halynumero7 = numberFormat(halynumero7);
            halynumero8 = numberFormat(halynumero8);
            halynumero9 = numberFormat(halynumero9);
            halynumero10 = numberFormat(halynumero10);

            for(String testattava : OHTOnumbers) {
                if(testattava.equals(numero)) {
                    erica = false;
                    break;
                }
            }

            boolean kaytaAvainsanaa = sharedPreferences.getBoolean("avainsana", false);
            boolean numeroTasmaa = false;
            boolean avainsanaTasmaa = false;

            String salsa = "";
            if (message.length() > 5) {
                salsa = message.substring(0, 5);
            }
            String testaahaly = "";
            if (message.length() > 13) {
                testaahaly = message.substring(0, 13);
            }

            if (numero.equals(halynumero1) || numero.equals(halynumero2) || numero.equals(halynumero3) || numero.equals(halynumero4) ||
                    numero.equals(halynumero5) || numero.equals(halynumero6)
                    || numero.equals(halynumero7) || numero.equals(halynumero8) || numero.equals(halynumero9) || numero.equals(halynumero10) ||
                    salsa.equals("SALSA") || testaahaly.equals("TESTIHÄLYTYS:") || testaahaly.equals("TESTIHÄLYTYS;")) {
                if (kaytaAvainsanaa) {
                    if (testaahaly.equals("TESTIHÄLYTYS:") || testaahaly.equals("TESTIHÄLYTYS;")) {
                        return true;
                    }
                    numeroTasmaa = true;
                } else {
                    return true;
                }
            }
            if (kaytaAvainsanaa) {
                String avainsana1 = sharedPreferences.getString("avainsana1", null);
                String avainsana2 = sharedPreferences.getString("avainsana2", null);
                String avainsana3 = sharedPreferences.getString("avainsana3", null);
                String avainsana4 = sharedPreferences.getString("avainsana4", null);
                String avainsana5 = sharedPreferences.getString("avainsana5", null);
                // tarkista viestin sanat ja hälytä jos avainsana havaittu
                if (message.length() > 3) {
                    char merkki;
                    StringBuilder sana = new StringBuilder();
                    ArrayList<String> sanat = new ArrayList<>();

                    for (int i = 0; i <= message.length() - 1; i++) {
                        merkki = message.charAt(i);
                        // Katko sanat regex:in mukaan
                        if (Character.toString(merkki).matches("[.,/:; ]")) {
                            sanat.add(sana.toString());
                            sana.delete(0, sana.length());
                        } else {
                            sana.append(merkki);
                        }
                    }
                    for (String avainsana : sanat) {
                        if (avainsana.equals(avainsana1) || avainsana.equals(avainsana2) || avainsana.equals(avainsana3) || avainsana.equals(avainsana4) || avainsana.equals(avainsana5)) {
                            avainsanaTasmaa = true;
                        }
                    }
                    sanat.clear();
                }
            }
            return kaytaAvainsanaa && numeroTasmaa && avainsanaTasmaa;
        }
        return false;
    }

    private boolean rajaapoisvuosiluvut(String vuosiluku) {
        return (vuosiluku.length() < 4 || !vuosiluku.equals("2018")) && !vuosiluku.equals("2019") && !vuosiluku.equals("2020") && !vuosiluku.equals("2021") && !vuosiluku.equals("2022");
    }

    private void addressLookUp(String viesti, String timeStamp) {

        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());

        String osoite = "";
        //String[] palautus = new String[5];
        int length = viesti.length();
        //int pituus = strings[0].length();
        int halytunnusSijainti = 0;
        int listaPaikka = 0;
        ArrayList<String> viestinSanat = new ArrayList<>();
        ArrayList<String> sanatYksinaan = new ArrayList<>();
        StringBuilder viestinLauseet = new StringBuilder();
        StringBuilder viestiTeksti = new StringBuilder();
        StringBuilder sanatYksitellen = new StringBuilder();
        String sana;
        String sanaYksin;
        //String halytysLuokka;
        String kiireellisyysLuokka = "";
        char merkki;
        boolean loytyi = false;
        boolean kiire = false;
        boolean osoitet = false;

        for (int o = 0; o <= length - 1; o++) {
            viestiTeksti.append(viesti.charAt(o));
        }

        // Katkotaan viesti sanoihin
        for (int i = 0; i <= length - 1; i++) {
            merkki = viesti.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(viesti.charAt(i));
            }
        }

        // Katkotaan viesti osiin puolipilkkujen mukaan
        for (int i = 0; i <= length - 1; i++) {
            merkki = viesti.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(viesti.charAt(i));
            }
        }

        String kommentti = "";
        // Etsitään mikä lause sisältää kunnan
        try {
            outer:
            for (String valmisSana : viestinSanat) {
                //String pieni = valmisSana.toLowerCase();
                for (String kunta : kunnat) {
                    if (valmisSana.contains(kunta)) {
                        osoite = valmisSana;
                        osoitet = true;
                        break outer;
                    }
                }
            }

            // Kiireellisyysluokan kirjaimen etsiminen
            for (String luokkaKirjain : viestinSanat) {
                if (luokkaKirjain.trim().equals("A") || luokkaKirjain.trim().equals("B") || luokkaKirjain.trim().equals("C") || luokkaKirjain.trim().equals("D")) {
                    kiireellisyysLuokka = luokkaKirjain;
                    kiire = true;
                    break;
                }
            }

            // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
            for (String valmisSana : sanatYksinaan) {
                if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                    //String osaSana = valmisSana.substring(0,3);
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                        if (valmisSana.substring(0, 3).equals("H35")) {
                            valmisSana = "H351";
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf("H351");
                            loytyi = true;
                            break;
                        }
                        halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                        listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                        loytyi = true;
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";

        } catch (Exception e) {
            kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";

        }

        if(!kiire) {
            kiireellisyysLuokka = "Ei löytynyt";
        }
        if(!osoitet) {
            osoite = "Ei löytynyt";
        }

        String tallennettavaTunnus = "Ei löytynyt";

        if(loytyi) {
            tallennettavaTunnus = sanatYksinaan.get(halytunnusSijainti).trim() + " " + halytekstit.get(listaPaikka).trim();
            if(listaPaikka >= 98 && ensivaste) {
                alarmIsEnsivaste = true;
            }
        }

        if(!kiire && !osoitet && !loytyi) {
            // Tunnus, osoite ja kiireellisyysluokka ei löytynyt. Yhdistä edellisen hälytyksen kanssa.
            FireAlarm fireAlarmLastEntry = fireAlarmRepository.getLatest();
            if(fireAlarmLastEntry != null) {
                if(fireAlarmLastEntry.getTunnus().equals("OHTO Hälytys") || fireAlarmLastEntry.getTunnus().equals("999")) {
                    // Last alarm was OHTO or phonecall alarm. Make new alarm.
                    FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                            osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                    fireAlarmRepository.insert(fireAlarm);
                } else {
                    // Last alarm was not OHTO or phonecall alarm, update last alarm with new information if time difference is smaller than 30 minutes.
                    if(calculateTimeDifference(fireAlarmLastEntry.getTimeStamp(), timeStamp)) {
                        String addMessage = fireAlarmLastEntry.getViesti();
                        addMessage += "\n" +
                                "\n" + viesti;
                        fireAlarmLastEntry.setViesti(addMessage);
                        fireAlarmRepository.update(fireAlarmLastEntry);
                    } else {
                        FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                                osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                        fireAlarmRepository.insert(fireAlarm);
                    }
                }
            } else {
                FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                        osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                fireAlarmRepository.insert(fireAlarm);
            }

        } else if(sanatYksinaan.get(0).equals("PÄIVITYS")) {
            // TODO: päivitysviesti, hae tiedot ja päivitä viesti
            FireAlarm fireAlarmLastEntry = fireAlarmRepository.getLatest();

            if(fireAlarmLastEntry != null) {
                if(fireAlarmLastEntry.getTunnus().equals("OHTO Hälytys") || fireAlarmLastEntry.getTunnus().equals("999")) {
                    // Last alarm was OHTO or phonecall alarm. Make new alarm.
                    FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                            osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                    fireAlarmRepository.insert(fireAlarm);
                } else {
                    // Last alarm was not OHTO or phonecall alarm, update last alarm with new information if time difference is smaller than 30 minutes.
                    if(calculateTimeDifference(fireAlarmLastEntry.getTimeStamp(), timeStamp)) {
                        String LastEntryViesti = fireAlarmLastEntry.getViesti();
                        LastEntryViesti += "\n" +
                                "\n" + viesti;
                        if(kiire) {
                            fireAlarmLastEntry.setLuokka(kiireellisyysLuokka.trim());
                        }
                        if(osoitet) {
                            fireAlarmLastEntry.setOsoite(osoite.trim());
                        }
                        if(loytyi) {
                            fireAlarmLastEntry.setTunnus(tallennettavaTunnus);
                        }
                        fireAlarmLastEntry.setViesti(LastEntryViesti);
                        fireAlarmRepository.update(fireAlarmLastEntry);
                    } else {
                        // Time difference over 30 minutes. Make new alarm.
                        FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                                osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                        fireAlarmRepository.insert(fireAlarm);
                    }

                }

            } else {
                // Last fireAlarm was empty, make new alarm.
                FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                        osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                fireAlarmRepository.insert(fireAlarm);
            }
        } else {
            FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), viesti,
                    osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

            fireAlarmRepository.insert(fireAlarm);
        }
        viestinSanat.clear();
        sanatYksinaan.clear();
    }

    private boolean calculateTimeDifference(String oldTime, String newTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd.MMM yyyy, H:mm:ss", Locale.getDefault());
        try {
            Date dateOld = dateFormat.parse(oldTime);
            Date dateNew = dateFormat.parse(newTime);
            long oldAlarmTime = dateOld.getTime();
            long newAlarmTime = dateNew.getTime();
            if(newAlarmTime - oldAlarmTime <= (60000*30)) {
                long difference = newAlarmTime - oldAlarmTime;
                Log.e("TAG", "Time difference: " + difference);
                return true;
            } else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void OHTOAlarm(String message, String timestamp, String number) {
        number = numberFormat(number);
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        FireAlarm fireAlarm = new FireAlarm("OHTO Hälytys", "", message,
                "", "", "", timestamp, number, "", "", "");

        fireAlarmRepository.insert(fireAlarm);
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void startForeGround(String viesti) {
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {}
        Notification.Builder builder = new Notification.Builder(this, "ACTIVE SERVICE")
                .setContentTitle("VPK Apuri")
                .setContentText(viesti)
                .setAutoCancel(true);

        Notification notification = builder.build();

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("ACTIVE SERVICE", "ACTIVE SERVICE", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("VPK Apuri käynnissä.");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        startForeground(15, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        if (viber != null) {
            viber.cancel();
        }
        kunnat.clear();
        halytunnukset.clear();
        halytekstit.clear();
        OHTOnumbers.clear();

        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null && pitaaPalauttaa) {
            audioManager.setStreamVolume(AudioManager.STREAM_RING, palautaAani, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, palautaStreamAlarm, 0);
        }
        if(wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Throwable th) {
                // No Need to do anything.
            }

        }
    }

    public void createNotification(String viesti) {
        Intent intentsms = new Intent(getApplicationContext(), HalytysActivity.class);
        intentsms.setAction(Intent.ACTION_SEND);
        intentsms.setType("text/plain");
        intentsms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intentsms);
        PendingIntent pendingIntentWithBackStack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(IsItAlarmService.this, 0, intentsms, PendingIntent.FLAG_CANCEL_CURRENT);

        Intent stopAlarm = new Intent(this, stopHalyaaniService.class);
        PendingIntent stop = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), stopAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(IsItAlarmService.this, "HALYTYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("HÄLYTYS")
                .setContentText(viesti)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentWithBackStack)
                .addAction(R.mipmap.ic_launcher, "HILJENNÄ", stop)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDeleteIntent(stop)
                //.setFullScreenIntent(pendingIntent, true) // AndroidQ fullScreenIntent testing. Launches immediately and stops alarmsounds.
                //.setOngoing(true)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(IsItAlarmService.this);
        notificationManager.notify(MY_HALY_NOTIFICATION_ID, mBuilder.build());
    }

    public void createNotificationPuhelu(String viesti) {
        Intent intentsms = new Intent(IsItAlarmService.this, HalytysActivity.class);
        intentsms.setAction(Intent.ACTION_SEND);
        intentsms.setType("text/plain");
        intentsms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intentsms);
        PendingIntent pendingIntentWithBackStack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //PendingIntent pendingIntent = PendingIntent.getActivity(IsItAlarmService.this, 0, intentsms, PendingIntent.FLAG_CANCEL_CURRENT);

        //Intent stopAlarm = new Intent(this, stopHalyaaniService.class);
        //PendingIntent stop = PendingIntent.getBroadcast(this,(int) System.currentTimeMillis(), stopAlarm,PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(IsItAlarmService.this, "HALYTYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("HÄLYTYS")
                .setContentText(viesti)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentWithBackStack)
                //.addAction(R.mipmap.ic_launcher, "HILJENNÄ", stop)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //.setDeleteIntent(stop)
                //.setOngoing(true)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(IsItAlarmService.this);
        notificationManager.notify(MY_HALY_NOTIFICATION_ID, mBuilder.build());
    }

    public void alarmSound(int startId) {

        String alarms = sharedPreferences.getString("ringtone", null);

        if(OHTO && !erica) {
            alarms = sharedPreferences.getString("ringtoneOHTO", null);
        } else if(alarmIsEnsivaste) {
            alarms = sharedPreferences.getString("ringtoneEnsivaste", null);
        } else if(puheluAani) {
            alarms = sharedPreferences.getString("ringtonePuhelu", null);
        }

        aanetonser = sharedPreferences.getInt("aaneton_profiili", -1);
        if (alarms != null) {
            Uri uri = Uri.parse(alarms);
            //Toast.makeText(aktiivinenHaly.this, " " + uri, Toast.LENGTH_LONG).show();
            playSound2(IsItAlarmService.this, uri, startId);
        } else {
            Uri uri = Uri.parse("android.resource://kultalaaki.vpkapuri/" + R.raw.virve);
            playSound2(IsItAlarmService.this, uri, startId);
        }
    }

    private void playSound2(Context context, Uri alert, int startId) {
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            tarina = sharedPreferences.getBoolean("varina", true);
            aanetVaiEi = sharedPreferences.getBoolean("aanetVaiEi", false);
            aanenVoimakkuus = sharedPreferences.getInt("SEEKBAR_VALUE", -1);
            int checkVolume = -1;

            if (audioManager != null) {
                checkVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                palautaStreamAlarm = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                palautaAani = checkVolume;

                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                pitaaPalauttaa = true;
            }

            if (aanetonser == 2) {
                if (tarina) {
                    vibrateSilent();
                }
            } else if (aanetonser == 3) {
                //Yötila
                if (aanetVaiEi && checkVolume == 0) {
                    // ei saa tulla äänettömän läpi
                    return;
                }
                aanenVoimakkuus = 10;
                volume = saadaAani(aanenVoimakkuus);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepareAsync();
                    if (tarina) {
                        vibrate();
                    }
                }
            } else {
                // Normaali
                if (aanetVaiEi && checkVolume == 0) {
                    // ei saa tulla äänettömän läpi
                    return;
                }
                aanenVoimakkuus = 50;
                aanenVoimakkuus = sharedPreferences.getInt("SEEKBAR_VALUE", -1);
                volume = saadaAani(aanenVoimakkuus);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepareAsync();
                    if (tarina) {
                        vibrate();
                    }
                }
            }

            final int stopper = startId;
            int stopTime;
            int stop = 60;
            try {
                String aika = sharedPreferences.getString("stopTime", null);
                if (aika != null) {
                    stop = Integer.parseInt(aika);
                }
            } catch (Exception e) {
                Log.e("Halyservice", "Stop ajastuksen arvoa ei voitu lukea.");
            }
            if (stop < 10) {
                stop = 10;
            }
            stopTime = stop * 1000;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    stopSelf(stopper);
                }
            }, stopTime);

        } catch (IOException e) {
            System.out.println("OOPS");
        }
    }

    @Override
    public void onLowMemory() {
        Log.i(TAG, "Muisti alhainen");
    }

    public int saadaAani(int voima) {
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            double aani = (double) volume / 100 * voima;
            volume = (int) aani;
        }

        if (volume == 0) {
            return 1;
        } else if (volume == 1) {
            return 1;
        } else if (volume == 2) {
            return 2;
        } else if (volume == 3) {
            return 3;
        } else if (volume == 4) {
            return 4;
        } else if (volume == 5) {
            return 5;
        } else if (volume == 6) {
            return 6;
        } else if (volume == 7) {
            return 7;
        }

        return volume;
    }

    public void onPrepared(final MediaPlayer player) {
        Thread music = new Thread() {
            @Override
            public void run() {
                player.start();
            }
        };

        music.start();
        mediaplayerRunning = true;
    }

    public void vibrate() {
        if (Build.VERSION.SDK_INT >= 21) {
            viber = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (viber != null && viber.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= 26) {
                    long[] pattern = new long[]{0, 200, 200, 200, 200, 200, 200, 200};
                    viber.vibrate(VibrationEffect.createWaveform(pattern, 5));
                } else {
                    long[] pattern = new long[]{0, 200, 200, 200, 200, 200, 200, 200};
                    viber.vibrate(pattern, 5);
                }
            }
        }
    }

    public void vibrateSilent() {
        if (Build.VERSION.SDK_INT >= 21) {
            viber = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (viber != null && viber.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= 26) {
                    long[] pattern = new long[]{0, 200, 200, 200, 200, 200, 200, 200};
                    viber.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    long[] pattern = new long[]{0, 200, 200, 200, 200, 200, 200, 200};
                    viber.vibrate(pattern, -1);
                }
            }
        }
    }

    public void lisaaKunnatErica() {
        //ArrayList<String> kunnat = new ArrayList<>();
        kunnat.add("Akaa");
        kunnat.add("Alajärvi");
        kunnat.add("Alavieska");
        kunnat.add("Alavus");
        kunnat.add("Asikkala");
        kunnat.add("Askola");
        kunnat.add("Aura");
        kunnat.add("Brändö");
        kunnat.add("Eckerö");
        kunnat.add("Enonkoski");

        kunnat.add("Enontekiö");
        kunnat.add("Espoo");
        kunnat.add("Eura");
        kunnat.add("Eurajoki");
        kunnat.add("Evijärvi");
        kunnat.add("Finström");
        kunnat.add("Forssa");
        kunnat.add("Föglö");
        kunnat.add("Geta");
        kunnat.add("Haapajärvi");

        kunnat.add("Haapavesi");
        kunnat.add("Hailuoto");
        kunnat.add("Halsua");
        kunnat.add("Hamina");
        kunnat.add("Hammarland");
        kunnat.add("Hankasalmi");
        kunnat.add("Hanko");
        kunnat.add("Harjavalta");
        kunnat.add("Hartola");
        kunnat.add("Hattula");

        kunnat.add("Hausjärvi");
        kunnat.add("Heinola");
        kunnat.add("Heinävesi");
        kunnat.add("Helsinki");
        kunnat.add("Hirvensalmi");
        kunnat.add("Hollola");
        kunnat.add("Honkajoki");
        kunnat.add("Huittinen");
        kunnat.add("Humppila");
        kunnat.add("Hyrynsalmi");

        kunnat.add("Hyvinkää");
        kunnat.add("Hämeenkyrö");
        kunnat.add("Hämeenlinna");
        kunnat.add("Ii");
        kunnat.add("Iisalmi");
        kunnat.add("Iitti");
        kunnat.add("Ikaalinen");
        kunnat.add("Ilmajoki");
        kunnat.add("Ilomantsi");
        kunnat.add("Imatra");

        kunnat.add("Inari");
        kunnat.add("Pohjois-lapin seutukunta");
        kunnat.add("Pedersöre");
        kunnat.add("Inkoo");
        kunnat.add("Isojoki");
        kunnat.add("Isokyrö");
        kunnat.add("Janakkala");
        kunnat.add("Joensuu");
        kunnat.add("Jokioinen");
        kunnat.add("Jomala");
        kunnat.add("Joroinen");

        kunnat.add("Joutsa");
        kunnat.add("Juuka");
        kunnat.add("Juupajoki");
        kunnat.add("Juva");
        kunnat.add("Jyväskylä");
        kunnat.add("Jämijärvi");
        kunnat.add("Jämsä");
        kunnat.add("Järvenpää");
        kunnat.add("Kaarina");
        kunnat.add("Kaavi");

        kunnat.add("Kajaani");
        kunnat.add("Kalajoki");
        kunnat.add("Kangasala");
        kunnat.add("Kangasniemi");
        kunnat.add("Kankaanpää");
        kunnat.add("Kannonkoski");
        kunnat.add("Kannus");
        kunnat.add("Karijoki");
        kunnat.add("Karkkila");
        kunnat.add("Karstula");

        kunnat.add("Karvia");
        kunnat.add("Kaskinen");
        kunnat.add("Kauhajoki");
        kunnat.add("Kauhava");
        kunnat.add("Kauniainen");
        kunnat.add("Kaustinen");
        kunnat.add("Keitele");
        kunnat.add("Kemi");
        kunnat.add("Kemijärvi");
        kunnat.add("Keminmaa");

        kunnat.add("Kemiönsaari");
        kunnat.add("Kempele");
        kunnat.add("Kerava");
        kunnat.add("Keuruu");
        kunnat.add("Kihniö");
        kunnat.add("Kinnula");
        kunnat.add("Kirkkonummi");
        kunnat.add("Kitee");
        kunnat.add("Kittilä");
        kunnat.add("Kiuruvesi");

        kunnat.add("Kivijärvi");
        kunnat.add("Kokemäki");
        kunnat.add("Kokkola");
        kunnat.add("Kolari");
        kunnat.add("Konnevesi");
        kunnat.add("Kontiolahti");
        kunnat.add("Korsnäs");
        kunnat.add("Koski tl");
        kunnat.add("Kotka");
        kunnat.add("Kouvola");

        kunnat.add("Kristiinankaupunki");
        kunnat.add("Kruunupyy");
        kunnat.add("Kuhmo");
        kunnat.add("Kuhmoinen");
        kunnat.add("Kumlinge");
        kunnat.add("Kuopio");
        kunnat.add("Kuortane");
        kunnat.add("Kurikka");
        kunnat.add("Kustavi");
        kunnat.add("Kuusamo");

        kunnat.add("Kyyjärvi");
        kunnat.add("Kärkölä");
        kunnat.add("Kärsämäki");
        kunnat.add("Kökar");
        kunnat.add("Lahti");
        kunnat.add("Laihia");
        kunnat.add("Laitila");
        kunnat.add("Lapinjärvi");
        kunnat.add("Lapinlahti");
        kunnat.add("Lappajärvi");

        kunnat.add("Lappeenranta");
        kunnat.add("Lapua");
        kunnat.add("Laukaa");
        kunnat.add("Lemi");
        kunnat.add("Lemland");
        kunnat.add("Lempäälä");
        kunnat.add("Leppävirta");
        kunnat.add("Lestijärvi");
        kunnat.add("Lieksa");
        kunnat.add("Lieto");

        kunnat.add("Liminka");
        kunnat.add("Liperi");
        kunnat.add("Lohja");
        kunnat.add("Loimaa");
        kunnat.add("Loppi");
        kunnat.add("Loviisa");
        kunnat.add("Luhanka");
        kunnat.add("Lumijoki");
        kunnat.add("Lumparland");
        kunnat.add("Luoto");

        kunnat.add("Luumäki");
        kunnat.add("Maalahti");
        kunnat.add("Maarianhamina");
        kunnat.add("Marttila");
        kunnat.add("Masku");
        kunnat.add("Merijärvi");
        kunnat.add("Merikarvia");
        kunnat.add("Miehikkälä");
        kunnat.add("Mikkeli");
        kunnat.add("Muhos");

        kunnat.add("Multia");
        kunnat.add("Muonio");
        kunnat.add("Mustasaari");
        kunnat.add("Muurame");
        kunnat.add("Mynämäki");
        kunnat.add("Myrskylä");
        kunnat.add("Mäntsälä");
        kunnat.add("Mänttä-vilppula");
        kunnat.add("Mänttä");
        kunnat.add("Vilppula");
        kunnat.add("Mäntyharju");
        kunnat.add("Naantali");

        kunnat.add("Nakkila");
        kunnat.add("Nivala");
        kunnat.add("Nokia");
        kunnat.add("Nousiainen");
        kunnat.add("Nurmes");
        kunnat.add("Nurmijärvi");
        kunnat.add("Närpiö");
        kunnat.add("Orimattila");
        kunnat.add("Oripää");
        kunnat.add("Orivesi");

        kunnat.add("Oulainen");
        kunnat.add("Oulu");
        kunnat.add("Outokumpu");
        kunnat.add("Padasjoki");
        kunnat.add("Paimio");
        kunnat.add("Paltamo");
        kunnat.add("Parainen");
        kunnat.add("Parikkala");
        kunnat.add("Parkano");
        kunnat.add("Pedersören kunta");
        kunnat.add("Pedersöre");

        kunnat.add("Pelkosenniemi");
        kunnat.add("Pello");
        kunnat.add("Perho");
        kunnat.add("Pertunmaa");
        kunnat.add("Petäjävesi");
        kunnat.add("Pieksämäki");
        kunnat.add("Pielavesi");
        kunnat.add("Pietarsaari");
        kunnat.add("Pihtipudas");
        kunnat.add("Pirkkala");

        kunnat.add("Polvijärvi");
        kunnat.add("Pomarkku");
        kunnat.add("Pori");
        kunnat.add("Pornainen");
        kunnat.add("Porvoo");
        kunnat.add("Posio");
        kunnat.add("Pudasjärvi");
        kunnat.add("Pukkila");
        kunnat.add("Punkalaidun");
        kunnat.add("Puolanka");

        kunnat.add("Puumala");
        kunnat.add("Pyhtää");
        kunnat.add("Pyhäjoki");
        kunnat.add("Pyhäjärvi");
        kunnat.add("Pyhäntä");
        kunnat.add("Pyhäranta");
        kunnat.add("Pälkäne");
        kunnat.add("Pöytyä");
        kunnat.add("Raahe");
        kunnat.add("Raasepori");

        kunnat.add("Raisio");
        kunnat.add("Rantasalmi");
        kunnat.add("Ranua");
        kunnat.add("Rauma");
        kunnat.add("Rautalampi");
        kunnat.add("Rautavaara");
        kunnat.add("Rautjärvi");
        kunnat.add("Reisjärvi");
        kunnat.add("Riihimäki");
        kunnat.add("Ristijärvi");

        kunnat.add("Rovaniemi");
        kunnat.add("Ruokolahti");
        kunnat.add("Ruovesi");
        kunnat.add("Rusko");
        kunnat.add("Rääkkylä");
        kunnat.add("Saarijärvi");
        kunnat.add("Salla");
        kunnat.add("Salo");
        kunnat.add("Saltvik");
        kunnat.add("Sastamala");

        kunnat.add("Sauvo");
        kunnat.add("Savitaipale");
        kunnat.add("Savonlinna");
        kunnat.add("Savukoski");
        kunnat.add("Seinäjoki");
        kunnat.add("Sievi");
        kunnat.add("Siikainen");
        kunnat.add("Siikajoki");
        kunnat.add("Siikalatva");
        kunnat.add("Siilinjärvi");

        kunnat.add("Simo");
        kunnat.add("Sipoo");
        kunnat.add("Siuntio");
        kunnat.add("Sodankylä");
        kunnat.add("Soini");
        kunnat.add("Somero");
        kunnat.add("Sonkajärvi");
        kunnat.add("Sotkamo");
        kunnat.add("Sottunga");
        kunnat.add("Sulkava");

        kunnat.add("Sund");
        kunnat.add("Suomussalmi");
        kunnat.add("Suonenjoki");
        kunnat.add("Sysmä");
        kunnat.add("Säkylä");
        kunnat.add("Taipalsaari");
        kunnat.add("Taivalkoski");
        kunnat.add("Taivassalo");
        kunnat.add("Tammela");
        kunnat.add("Tampere");

        kunnat.add("Tervo");
        kunnat.add("Tervola");
        kunnat.add("Teuva");
        kunnat.add("Tohmajärvi");
        kunnat.add("Toholampi");
        kunnat.add("Toivakka");
        kunnat.add("Tornio");
        kunnat.add("Turku");
        kunnat.add("Tuusniemi");
        kunnat.add("Tuusula");

        kunnat.add("Tyrnävä");
        kunnat.add("Ulvila");
        kunnat.add("Urjala");
        kunnat.add("Utajärvi");
        kunnat.add("Utsjoki");
        kunnat.add("Uurainen");
        kunnat.add("Uusikaarlepyy");
        kunnat.add("Uusikaupunki");
        kunnat.add("Vaala");
        kunnat.add("Vaasa");

        kunnat.add("Valkeakoski");
        kunnat.add("Valtimo");
        kunnat.add("Vantaa");
        kunnat.add("Varkaus");
        kunnat.add("Vehmaa");
        kunnat.add("Vesanto");
        kunnat.add("Vesilahti");
        kunnat.add("Veteli");
        kunnat.add("Vieremä");
        kunnat.add("Vihti");

        kunnat.add("Viitasaari");
        kunnat.add("Vimpeli");
        kunnat.add("Virolahti");
        kunnat.add("Virrat");
        kunnat.add("Vårdö");
        kunnat.add("Vöyri");
        kunnat.add("Ylitornio");
        kunnat.add("Ylivieska");
        kunnat.add("Ylöjärvi");
        kunnat.add("Ypäjä");

        kunnat.add("Ähtäri");
        kunnat.add("Äänekoski");
        //Toast.makeText(aktiivinenHaly.this, "Kuntia listassa " + kunnat.size() + ".",Toast.LENGTH_LONG).show();
    }

    public void lisaaHalyTunnukset() {

        halytunnukset.add("103");
        halytekstit.add("PALOHÄLYTYS");
        halytunnukset.add("104");
        halytekstit.add("SÄTEILYHÄLYTYS");
        halytunnukset.add("105");
        halytekstit.add("HISSIHÄLYTYS");
        halytunnukset.add("106");
        halytekstit.add("LAITEVIKA");
        halytunnukset.add("107");
        halytekstit.add("YHTEYSVIKA");
        halytunnukset.add("108");
        halytekstit.add("HUOLTO");
        halytunnukset.add("200");
        halytekstit.add("TIELIIKENNE: MUU TAI ONNETTOMUUDEN UHKA");
        halytunnukset.add("201");
        halytekstit.add("TIELIIKENNE: PELTIKOLARI, SUISTUMINEN");
        halytunnukset.add("202");
        halytekstit.add("TIELIIKENNE: PIENI");
        halytunnukset.add("203");
        halytekstit.add("TIELIIKENNE: KESKISUURI");
        halytunnukset.add("204");
        halytekstit.add("TIELIIKENNE: SUURI");
        halytunnukset.add("205");
        halytekstit.add("TIELIIKENNE: ELÄIN OSALLISEENA");
        halytunnukset.add("206");
        halytekstit.add("TIELIIKENNE: MAAN ALLA: PIENI");
        halytunnukset.add("207");
        halytekstit.add("TIELIIKENNE: MAAN ALLA: KESKISUURI");
        halytunnukset.add("208");
        halytekstit.add("TIELIIKENNE: MAAN ALLA: SUURI");
        halytunnukset.add("210");
        halytekstit.add("RAIDELIIKENNE: MUU");
        halytunnukset.add("211");
        halytekstit.add("RAIDELIIKENNE: PELTIKOLARI");
        halytunnukset.add("212");
        halytekstit.add("RAIDELIIKENNE: PIENI");
        halytunnukset.add("213");
        halytekstit.add("RAIDELIIKENNE: KESKISUURI");
        halytunnukset.add("214");
        halytekstit.add("RAIDELIIKENNE: SUURI");
        halytunnukset.add("215");
        halytekstit.add("RAIDELIIKENNE: ELÄIN OSALLISENA");
        halytunnukset.add("216");
        halytekstit.add("RAIDELIIKENNE: MAAN ALLA: PIENI");
        halytunnukset.add("217");
        halytekstit.add("RAIDELIIKENNE: MAAN ALLA: KESKISUURI");
        halytunnukset.add("218");
        halytekstit.add("RAIDELIIKENNE: MAAN ALLA: SUURI");
        halytunnukset.add("220");
        halytekstit.add("VESILIIKENNE: MUU");
        halytunnukset.add("221");
        halytekstit.add("VESILIIKENNE: PIENI");
        halytunnukset.add("222");
        halytekstit.add("VESILIIKENNE: KESKISUURI");
        halytunnukset.add("223");
        halytekstit.add("VESILIIKENNE: SUURI");
        halytunnukset.add("231");
        halytekstit.add("ILMALIIKENNEONNETTOMUUS: PIENI");
        halytunnukset.add("232");
        halytekstit.add("ILMALIIKENNEONNETTOMUUS: KESKISUURI");
        halytunnukset.add("233");
        halytekstit.add("ILMALIIKENNEONNETTOMUUS: SUURI");
        halytunnukset.add("234");
        halytekstit.add("ILMALIIKENNE VAARA: PIENI");
        halytunnukset.add("235");
        halytekstit.add("ILMALIIKENNE VAARA: KESKISUURI");
        halytunnukset.add("236");
        halytekstit.add("ILMALIIKENNE VAARA: SUURI");
        halytunnukset.add("271");
        halytekstit.add("MAASTOLIIKENNEONNETTOMUUS");
        halytunnukset.add("H351");
        halytekstit.add("VARIKKO TAI ASEMAVALMIUS");
        halytunnukset.add("401");
        halytekstit.add("RAKENNUSPALO: PIENI");
        halytunnukset.add("402");
        halytekstit.add("RAKENNUSPALO: KESKISUURI");
        halytunnukset.add("403");
        halytekstit.add("RAKENNUSPALO: SUURI");
        halytunnukset.add("404");
        halytekstit.add("RAKENNUSPALO: MAAN ALLA: PIENI");
        halytunnukset.add("405");
        halytekstit.add("RAKENNUSPALO: MAAN ALLA: KESKISUURI");
        halytunnukset.add("406");
        halytekstit.add("RAKENNUSPALO: MAAN ALLA: SUURI");
        halytunnukset.add("411");
        halytekstit.add("LIIKENNEVÄLINEPALO: PIENI");
        halytunnukset.add("412");
        halytekstit.add("LIIKENNEVÄLINEPALO: KESKISUURI");
        halytunnukset.add("413");
        halytekstit.add("LIIKENNEVÄLINEPALO: SUURI");
        halytunnukset.add("414");
        halytekstit.add("LIIKENNEVÄLINEPALO: MAAN ALLA: PIENI");
        halytunnukset.add("415");
        halytekstit.add("LIIKENNEVÄLINEPALO: MAAN ALLA: KESKISUURI");
        halytunnukset.add("416");
        halytekstit.add("LIIKENNEVÄLINEPALO: MAAN ALLA: SUURI");
        halytunnukset.add("420");
        halytekstit.add("SAVUHAVAINTO");
        halytunnukset.add("421");
        halytekstit.add("MAASTOPALO: PIENI");
        halytunnukset.add("422");
        halytekstit.add("MAASTOPALO: KESKISUURI");
        halytunnukset.add("423");
        halytekstit.add("MAASTOPALO: SUURI");
        halytunnukset.add("424");
        halytekstit.add("TURVETUOTANTOALUEPALO: PIENI");
        halytunnukset.add("425");
        halytekstit.add("TURVETUOTANTOALUEPALO: KESKISUURI");
        halytunnukset.add("426");
        halytekstit.add("TURVETUOTANTOALUEPALO: SUURI");
        halytunnukset.add("431");
        halytekstit.add("TULIPALO, MUU: PIENI");
        halytunnukset.add("432");
        halytekstit.add("TULIPALO, MUU: KESKISUURI");
        halytunnukset.add("433");
        halytekstit.add("TULIPALO, MUU: SUURI");
        halytunnukset.add("434");
        halytekstit.add("TULIPALO, MUU: MAAN ALLA: PIENI");
        halytunnukset.add("435");
        halytekstit.add("TULIPALO, MUU: MAAN ALLA: KESKISUURI");
        halytunnukset.add("436");
        halytekstit.add("TULIPALO, MUU: MAAN ALLA: SUURI");
        halytunnukset.add("441");
        halytekstit.add("RÄJÄHDYS/SORTUMA: PIENI");
        halytunnukset.add("442");
        halytekstit.add("RÄJÄHDYS/SORTUMA: KESKISUURI");
        halytunnukset.add("443");
        halytekstit.add("RÄJÄHDYS/SORTUMA: SUURI");
        halytunnukset.add("444");
        halytekstit.add("RÄJÄHDYS-/SORTUMAVAARA");
        halytunnukset.add("451");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: PIENI");
        halytunnukset.add("452");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: KESKISUURI");
        halytunnukset.add("453");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: SUURI");
        halytunnukset.add("455");
        halytekstit.add("VAARALLISEN AINEEN ONNETTOMUUS: ONNETTOMUUSVAARA");
        halytunnukset.add("461");
        halytekstit.add("VAHINGONTORJUNTA: PIENI");
        halytunnukset.add("462");
        halytekstit.add("VAHINGONTORJUNTA: KESKISUURI");
        halytunnukset.add("463");
        halytekstit.add("VAHINGONTORJUNTA: SUURI");
        halytunnukset.add("471");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: MAALLA: PIENI");
        halytunnukset.add("472");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: MAALLA: KESKISUURI");
        halytunnukset.add("473");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: MAALLA: SUURI");
        halytunnukset.add("474");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: PIENI");
        halytunnukset.add("475");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: KESKISUURI");
        halytunnukset.add("476");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: VESISTÖSSÄ: SUURI");
        halytunnukset.add("477");
        halytekstit.add("ÖLJYVAHINKO/YMPÄRISTÖONNETTOMUUS: ONNETTOMUUSVAARA");
        halytunnukset.add("480");
        halytekstit.add("IHMISEN PELASTAMINEN: MUU");
        halytunnukset.add("481");
        halytekstit.add("IHMISEN PELASTAMINEN: ETSINTÄ");
        halytunnukset.add("482");
        halytekstit.add("IHMISEN PELASTAMINEN: AVUNANTO");
        halytunnukset.add("483");
        halytekstit.add("IHMISEN PELASTAMINEN: VEDESTÄ");
        halytunnukset.add("484");
        halytekstit.add("IHMISEN PELASTAMINEN: PINTAPELASTUS");
        halytunnukset.add("485");
        halytekstit.add("IHMISEN PELASTAMINEN: MAASTOSTA");
        halytunnukset.add("486");
        halytekstit.add("IHMISEN PELASTAMINEN: PURISTUKSISTA");
        halytunnukset.add("487");
        halytekstit.add("IHMISEN PELASTAMINEN: YLHÄÄLTÄ/ALHAALTA");
        halytunnukset.add("490");
        halytekstit.add("EPÄSELVÄ ONNETTOMUUS");
        halytunnukset.add("491");
        halytekstit.add("LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: PIENI");
        halytunnukset.add("492");
        halytekstit.add("LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: KESKISUURI");
        halytunnukset.add("493");
        halytekstit.add("LIIKENNEVÄLINEPALO- TAI MUU TULIPALO MAAN ALLA: SUURI");
        halytunnukset.add("550");
        halytekstit.add("AVUNANTO: MUU");
        halytunnukset.add("551");
        halytekstit.add("VIRKA-APUTEHTÄVÄ");
        halytunnukset.add("552");
        halytekstit.add("AVUNANTOTEHTÄVÄ");
        halytunnukset.add("553");
        halytekstit.add("UHKA-/VARUILLAOLO");
        halytunnukset.add("554");
        halytekstit.add("TARKISTUS-/VARMISTUS");
        halytunnukset.add("580");
        halytekstit.add("ELÄINTEHTÄVÄ: MUU");
        halytunnukset.add("581");
        halytekstit.add("ELÄIMEN PELASTAMINEN");
        halytunnukset.add("700");
        halytekstit.add("Eloton");
        halytunnukset.add("701");
        halytekstit.add("Elvytys");
        halytunnukset.add("702");
        halytekstit.add("Tajuttomuus");
        halytunnukset.add("703");
        halytekstit.add("Hengitysvaikeus");
        halytunnukset.add("704");
        halytekstit.add("Rintakipu");
        halytunnukset.add("705");
        halytekstit.add("PEH; Muu äkillisesti heikentynyt yleistila");
        halytunnukset.add("706");
        halytekstit.add("Aivohalvaus");
        halytunnukset.add("710");
        halytekstit.add("Tukehtuminen");
        halytunnukset.add("711");
        halytekstit.add("Ilmatie-este");
        halytunnukset.add("712");
        halytekstit.add("Jääminen suljettuun tilaan");
        halytunnukset.add("713");
        halytekstit.add("Hirttäytyminen, Kuristuminen");
        halytunnukset.add("714");
        halytekstit.add("Hukuksiin joutuminen");
        halytunnukset.add("741");
        halytekstit.add("Putoaminen");
        halytunnukset.add("744");
        halytekstit.add("Haava");
        halytunnukset.add("745");
        halytekstit.add("Kaatuminen");
        halytunnukset.add("746");
        halytekstit.add("Isku");
        halytunnukset.add("747");
        halytekstit.add("Vamma; muu");
        halytunnukset.add("751");
        halytekstit.add("Kaasumyrkytys");
        halytunnukset.add("752");
        halytekstit.add("Myrkytys");
        halytunnukset.add("753");
        halytekstit.add("Sähköisku");
        halytunnukset.add("755");
        halytekstit.add("Palovamma, lämpöhalvaus");
        halytunnukset.add("756");
        halytekstit.add("Alilämpöisyys");
        halytunnukset.add("757");
        halytekstit.add("Onnettomuus; muu");
        halytunnukset.add("761");
        halytekstit.add("Verenvuoto, Suusta");
        halytunnukset.add("762");
        halytekstit.add("Verenvuoto, Gynekologinen/urologinen");
        halytunnukset.add("763");
        halytekstit.add("Vernevuoto, Korva/nenä");
        halytunnukset.add("764");
        halytekstit.add("Säärihaava/Muu");
        halytunnukset.add("770");
        halytekstit.add("Sairauskohtaus");
        halytunnukset.add("771");
        halytekstit.add("Sokeritasapainon häiriö");
        halytunnukset.add("772");
        halytekstit.add("Kouristelu");
        halytunnukset.add("773");
        halytekstit.add("Yliherkkyysreaktio");
        halytunnukset.add("774");
        halytekstit.add("Heikentynyt yleistila, muu sairaus");
        halytunnukset.add("775");
        halytekstit.add("Oksentelu, Ripuli");
        halytunnukset.add("781");
        halytekstit.add("Vatsakipu");
        halytunnukset.add("782");
        halytekstit.add("Pää-/Niskasärky");
        halytunnukset.add("783");
        halytekstit.add("Selkä-/raaja-/vartalokipu");
        halytunnukset.add("784");
        halytekstit.add("Aistioire");
        halytunnukset.add("785");
        halytekstit.add("Mielenterveysongelma");
        halytunnukset.add("790");
        halytekstit.add("Hälytys puhelun aikana");
        halytunnukset.add("791");
        halytekstit.add("Synnytys");
        halytunnukset.add("792");
        halytekstit.add("Varallaolo, valmiussiirto");
        halytunnukset.add("793");
        halytekstit.add("Hoitolaitossiirto");
        halytunnukset.add("794");
        halytekstit.add("Muu sairaankuljetustehtävä");
        halytunnukset.add("796");
        halytekstit.add("Monipotilastilanne/Suuronnettomuus");
        halytunnukset.add("901");
        halytekstit.add("PELASTUSTOIMI POIKKEUSOLOISSA");
    }

}

/*private static class haeOsoite extends AsyncTask<String, Void, String[]> {

        private boolean rajaapoisvuosiluvut(String vuosiluku) {
            return (vuosiluku.length() < 4 || !vuosiluku.equals("2018")) && !vuosiluku.equals("2019") && !vuosiluku.equals("2020") && !vuosiluku.equals("2021") && !vuosiluku.equals("2022");
        }

        @Override
        protected String[] doInBackground(String... strings) {

            String osoite = "";
            String osoite1 = "";
            String osoite2 = "";
            String osoite3 = "";
            String[] palautus = new String[5];
            int pituus = strings[0].length();
            final String valmisOsoite;
            int kuntaSijainti = 0;
            int halytunnusSijainti = 0;
            int listaPaikka = 0;
            ArrayList<String> viestinSanat = new ArrayList<>();
            ArrayList<String> osoitteet = new ArrayList<>();
            StringBuilder halyOsoite = new StringBuilder();
            StringBuilder viestiTeksti = new StringBuilder();
            String sana;
            String halytysLuokka;
            char merkki;
            boolean loytyi = false;

            for (int o = 0; o <= pituus - 1; o++) {
                viestiTeksti.append(strings[0].charAt(o));
            }
            // Erotellaan viestistä sanat perustuen tiettyihin merkkeihin ja lisätään sanat listaan
            for (int i = 0; i <= pituus - 1; i++) {
                merkki = strings[0].charAt(i);
                // Katko sanat regex:in mukaan
                if (Character.toString(merkki).matches("[.,/:; \\r\\n]")) {
                    sana = halyOsoite.toString();
                    if (sana.length() > 1 || sana.matches("[0-9]")) {
                        String sanaLower = sana.toLowerCase();
                        switch (sanaLower) {
                            case "koski":
                                sana = "koski tl";
                                break;
                            case "pedersören":
                                sana = "pedersören kunta";
                                break;
                            case "pohjois-lapin":
                                sana = "pohjois-lapin seutukunta";
                                break;
                            case "karkkil":
                                sana = "karkkila";
                                break;
                        }
                        viestinSanat.add(sana);
                    }
                    halyOsoite.delete(0, halyOsoite.length());
                } else {
                    halyOsoite.append(strings[0].charAt(i));
                }
            }
            String kommentti = "";
            // Etsitään mikä sana listassa on kunta
            try {
                for (String valmisSana : viestinSanat) {
                    String kirjPieneksi = valmisSana.toLowerCase();
                    if (kunnat.contains(kirjPieneksi)) {
                        kuntaSijainti = viestinSanat.indexOf(valmisSana);
                        break;
                    }
                }
                // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
                for (String valmisSana : viestinSanat) {
                    if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                        //String osaSana = valmisSana.substring(0,3);
                        if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {
                            if (valmisSana.substring(0, 3).equals("H35")) {
                                valmisSana = "H351";
                                halytunnusSijainti = viestinSanat.indexOf(valmisSana);
                                listaPaikka = halytunnukset.indexOf("H351");
                                loytyi = true;
                                break;
                            }
                            halytunnusSijainti = viestinSanat.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                            loytyi = true;
                            break;
                        }
                    }
                }

                if (kuntaSijainti != 0) {
                    if (viestinSanat.size() >= kuntaSijainti + 1) {
                        osoitteet.add(viestinSanat.get(kuntaSijainti) +
                                " " + viestinSanat.get(kuntaSijainti + 1));
                        osoite = osoitteet.get(0);
                    }
                    if (viestinSanat.size() >= kuntaSijainti + 2) {
                        osoitteet.add(viestinSanat.get(kuntaSijainti) +
                                " " + viestinSanat.get(kuntaSijainti + 1) +
                                " " + viestinSanat.get(kuntaSijainti + 2));
                        osoite1 = osoitteet.get(1);
                    }
                    if (viestinSanat.size() >= kuntaSijainti + 3) {
                        osoitteet.add(viestinSanat.get(kuntaSijainti) +
                                " " + viestinSanat.get(kuntaSijainti + 1) +
                                " " + viestinSanat.get(kuntaSijainti + 2) +
                                " " + viestinSanat.get(kuntaSijainti + 3));
                        osoite2 = osoitteet.get(2);
                    }
                    if (viestinSanat.size() >= kuntaSijainti + 4) {
                        osoitteet.add(viestinSanat.get(kuntaSijainti) +
                                " " + viestinSanat.get(kuntaSijainti + 1) +
                                " " + viestinSanat.get(kuntaSijainti + 2) +
                                " " + viestinSanat.get(kuntaSijainti + 3) +
                                " " + viestinSanat.get(kuntaSijainti + 4));
                        osoite3 = osoitteet.get(3);
                    }
                }

                if (osoite.matches((".*[0-9].*"))) {
                    valmisOsoite = osoite;
                } else if (osoite1.matches((".*[0-9].*"))) {
                    valmisOsoite = osoite1;
                } else if (osoite2.matches((".*[0-9].*"))) {
                    valmisOsoite = osoite2;
                } else if (osoite3.matches((".*[0-9].*"))) {
                    valmisOsoite = osoite3;
                } else {
                    valmisOsoite = osoite;
                }

                if (loytyi) {
                    halytysLuokka = halytekstit.get(listaPaikka);
                } else {
                    halytysLuokka = "Ei löytynyt listalta";
                }
                //palautus = new String[5];
                palautus[0] = valmisOsoite;
                palautus[1] = viestinSanat.get(halytunnusSijainti);
                palautus[3] = halytysLuokka;

            } catch (ArrayIndexOutOfBoundsException e) {
                kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
                palautus[1] = "Tapahtui virhe.";
                palautus[3] = "Katso arkistosta hälytyksen kommentti.";
            } catch (Exception e) {
                kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
                palautus[1] = "Tapahtui virhe.";
                palautus[3] = "Katso arkistosta hälytyksen kommentti.";
            }

            palautus[2] = viestiTeksti.toString();
            palautus[4] = kommentti;
            osoitteet.clear();
            viestinSanat.clear();
            return palautus;
        }

        protected void onPostExecute(String[] result) {
            //db.insertData(result[1] + " " + result[3], result[0], result[2], result[4]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }*/

    /*private static class haeOsoiteErica extends AsyncTask<String, Void, String[]> {

        private boolean rajaapoisvuosiluvut(String vuosiluku) {
            return (vuosiluku.length() < 4 || !vuosiluku.equals("2018")) && !vuosiluku.equals("2019") && !vuosiluku.equals("2020") && !vuosiluku.equals("2021") && !vuosiluku.equals("2022");
        }

        @Override
        protected String[] doInBackground(String... strings) {

            String osoite = "";
            String[] palautus = new String[5];
            int pituus = strings[0].length();
            int halytunnusSijainti = 0;
            int listaPaikka = 0;
            ArrayList<String> viestinSanat = new ArrayList<>();
            ArrayList<String> sanatYksinaan = new ArrayList<>();
            StringBuilder viestinLauseet = new StringBuilder();
            StringBuilder viestiTeksti = new StringBuilder();
            StringBuilder sanatYksitellen = new StringBuilder();
            String sana;
            String sanaYksin;
            String halytysLuokka;
            String kiireellisyysLuokka;
            char merkki;
            boolean loytyi = false;

            for (int o = 0; o <= pituus - 1; o++) {
                viestiTeksti.append(strings[0].charAt(o));
            }

            // Katkotaan viesti sanoihin
            for (int i = 0; i <= pituus - 1; i++) {
                merkki = strings[0].charAt(i);
                // Katko sanat regex:in mukaan
                if (Character.toString(merkki).matches("[.,/:; ]")) {
                    sanaYksin = sanatYksitellen.toString();
                    if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                        sanatYksinaan.add(sanaYksin);
                    }
                    sanatYksitellen.delete(0, sanatYksitellen.length());
                } else {
                    sanatYksitellen.append(strings[0].charAt(i));
                }
            }

            // Katkotaan viesti osiin puolipilkkujen mukaan
            for (int i = 0; i <= pituus - 1; i++) {
                merkki = strings[0].charAt(i);
                // Katko sanat regex:in mukaan
                if (Character.toString(merkki).matches("[;]")) {
                    sana = viestinLauseet.toString();
                    if (sana.length() > 1 || sana.matches("[0-9]")) {
                        viestinSanat.add(sana);
                    }
                    viestinLauseet.delete(0, viestinLauseet.length());
                } else {
                    viestinLauseet.append(strings[0].charAt(i));
                }
            }

            String kommentti = "";
            // Etsitään mikä lause sisältää kunnan
            try {
                outer:
                for (String valmisSana : viestinSanat) {
                    //String pieni = valmisSana.toLowerCase();
                    for (String kunta : kunnat) {
                        if (valmisSana.contains(kunta)) {
                            osoite = valmisSana;
                            break outer;
                        }
                    }
                }

                // Kiireellisyysluokan kirjaimen etsiminen
                for (String luokkaKirjain : viestinSanat) {
                    if (luokkaKirjain.equals("A") || luokkaKirjain.equals("B") || luokkaKirjain.equals("C") || luokkaKirjain.equals("D")) {
                        kiireellisyysLuokka = luokkaKirjain;
                        break;
                    }
                }

                // Etsitään listalta hälytunnus ja luokka. Tee tähän alapuolelle.
                for (String valmisSana : sanatYksinaan) {
                    if (valmisSana.length() >= 3 && rajaapoisvuosiluvut(valmisSana)) {
                        //String osaSana = valmisSana.substring(0,3);
                        if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.substring(0, 3).equals("H35")) {

                            if (valmisSana.substring(0, 3).equals("H35")) {
                                valmisSana = "H351";
                                halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                                listaPaikka = halytunnukset.indexOf("H351");
                                loytyi = true;
                                break;
                            }
                            halytunnusSijainti = sanatYksinaan.indexOf(valmisSana);
                            listaPaikka = halytunnukset.indexOf(valmisSana.substring(0, 3));
                            loytyi = true;
                            break;
                        }
                    }
                }

                if (loytyi) {
                    halytysLuokka = halytekstit.get(listaPaikka);
                } else {
                    halytysLuokka = "Ei löytynyt listalta";
                }

                palautus[0] = osoite;
                palautus[1] = sanatYksinaan.get(halytunnusSijainti);
                palautus[3] = halytysLuokka;

            } catch (ArrayIndexOutOfBoundsException e) {
                kommentti = "Tapahtui virhe haettaessa listalta oikeaa tunnusta tai kuntaa. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
                palautus[1] = "Tapahtui virhe.";
                palautus[3] = "Katso arkistosta hälytyksen kommentti.";
            } catch (Exception e) {
                kommentti = "Tuntematon virhe esti osoitteen löytämisen viestistä. Lähetä palautetta kehittäjälle ongelman ratkaisemiseksi.";
                palautus[1] = "Tapahtui virhe.";
                palautus[3] = "Katso arkistosta hälytyksen kommentti.";
            }

            palautus[2] = viestiTeksti.toString();
            palautus[4] = kommentti;
            viestinSanat.clear();
            sanatYksinaan.clear();
            return palautus;
        }

        protected void onPostExecute(String[] result) {
            db.insertData(result[1] + " " + result[3], result[0], result[2], result[4]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }*/

    /*public void lisaaKunnat() {
        //ArrayList<String> kunnat = new ArrayList<>();
        kunnat.add("akaa");
        kunnat.add("alajärvi");
        kunnat.add("alavieska");
        kunnat.add("alavus");
        kunnat.add("asikkala");
        kunnat.add("askola");
        kunnat.add("aura");
        kunnat.add("brändö");
        kunnat.add("eckerö");
        kunnat.add("enonkoski");

        kunnat.add("enontekiö");
        kunnat.add("espoo");
        kunnat.add("eura");
        kunnat.add("eurajoki");
        kunnat.add("evijärvi");
        kunnat.add("finström");
        kunnat.add("forssa");
        kunnat.add("föglö");
        kunnat.add("geta");
        kunnat.add("haapajärvi");

        kunnat.add("haapavesi");
        kunnat.add("hailuoto");
        kunnat.add("halsua");
        kunnat.add("hamina");
        kunnat.add("hammarland");
        kunnat.add("hankasalmi");
        kunnat.add("hanko");
        kunnat.add("harjavalta");
        kunnat.add("hartola");
        kunnat.add("hattula");

        kunnat.add("hausjärvi");
        kunnat.add("heinola");
        kunnat.add("heinävesi");
        kunnat.add("helsinki");
        kunnat.add("hirvensalmi");
        kunnat.add("hollola");
        kunnat.add("honkajoki");
        kunnat.add("huittinen");
        kunnat.add("humppila");
        kunnat.add("hyrynsalmi");

        kunnat.add("hyvinkää");
        kunnat.add("hämeenkyrö");
        kunnat.add("hämeenlinna");
        kunnat.add("ii");
        kunnat.add("iisalmi");
        kunnat.add("iitti");
        kunnat.add("ikaalinen");
        kunnat.add("ilmajoki");
        kunnat.add("ilomantsi");
        kunnat.add("imatra");

        kunnat.add("inari");
        kunnat.add("pohjois-lapin seutukunta");
        kunnat.add("pedersöre");
        kunnat.add("inkoo");
        kunnat.add("isojoki");
        kunnat.add("isokyrö");
        kunnat.add("janakkala");
        kunnat.add("joensuu");
        kunnat.add("jokioinen");
        kunnat.add("jomala");
        kunnat.add("joroinen");

        kunnat.add("joutsa");
        kunnat.add("juuka");
        kunnat.add("juupajoki");
        kunnat.add("juva");
        kunnat.add("jyväskylä");
        kunnat.add("jämijärvi");
        kunnat.add("jämsä");
        kunnat.add("järvenpää");
        kunnat.add("kaarina");
        kunnat.add("kaavi");

        kunnat.add("kajaani");
        kunnat.add("kalajoki");
        kunnat.add("kangasala");
        kunnat.add("kangasniemi");
        kunnat.add("kankaanpää");
        kunnat.add("kannonkoski");
        kunnat.add("kannus");
        kunnat.add("karijoki");
        kunnat.add("karkkila");
        kunnat.add("karstula");

        kunnat.add("karvia");
        kunnat.add("kaskinen");
        kunnat.add("kauhajoki");
        kunnat.add("kauhava");
        kunnat.add("kauniainen");
        kunnat.add("kaustinen");
        kunnat.add("keitele");
        kunnat.add("kemi");
        kunnat.add("kemijärvi");
        kunnat.add("keminmaa");

        kunnat.add("kemiönsaari");
        kunnat.add("kempele");
        kunnat.add("kerava");
        kunnat.add("keuruu");
        kunnat.add("kihniö");
        kunnat.add("kinnula");
        kunnat.add("kirkkonummi");
        kunnat.add("kitee");
        kunnat.add("kittilä");
        kunnat.add("kiuruvesi");

        kunnat.add("kivijärvi");
        kunnat.add("kokemäki");
        kunnat.add("kokkola");
        kunnat.add("kolari");
        kunnat.add("konnevesi");
        kunnat.add("kontiolahti");
        kunnat.add("korsnäs");
        kunnat.add("koski tl");
        kunnat.add("kotka");
        kunnat.add("kouvola");

        kunnat.add("kristiinankaupunki");
        kunnat.add("kruunupyy");
        kunnat.add("kuhmo");
        kunnat.add("kuhmoinen");
        kunnat.add("kumlinge");
        kunnat.add("kuopio");
        kunnat.add("kuortane");
        kunnat.add("kurikka");
        kunnat.add("kustavi");
        kunnat.add("kuusamo");

        kunnat.add("kyyjärvi");
        kunnat.add("kärkölä");
        kunnat.add("kärsämäki");
        kunnat.add("kökar");
        kunnat.add("lahti");
        kunnat.add("laihia");
        kunnat.add("laitila");
        kunnat.add("lapinjärvi");
        kunnat.add("lapinlahti");
        kunnat.add("lappajärvi");

        kunnat.add("lappeenranta");
        kunnat.add("lapua");
        kunnat.add("laukaa");
        kunnat.add("lemi");
        kunnat.add("lemland");
        kunnat.add("lempäälä");
        kunnat.add("leppävirta");
        kunnat.add("lestijärvi");
        kunnat.add("lieksa");
        kunnat.add("lieto");

        kunnat.add("liminka");
        kunnat.add("liperi");
        kunnat.add("lohja");
        kunnat.add("loimaa");
        kunnat.add("loppi");
        kunnat.add("loviisa");
        kunnat.add("luhanka");
        kunnat.add("lumijoki");
        kunnat.add("lumparland");
        kunnat.add("luoto");

        kunnat.add("luumäki");
        kunnat.add("maalahti");
        kunnat.add("maarianhamina");
        kunnat.add("marttila");
        kunnat.add("masku");
        kunnat.add("merijärvi");
        kunnat.add("merikarvia");
        kunnat.add("miehikkälä");
        kunnat.add("mikkeli");
        kunnat.add("muhos");

        kunnat.add("multia");
        kunnat.add("muonio");
        kunnat.add("mustasaari");
        kunnat.add("muurame");
        kunnat.add("mynämäki");
        kunnat.add("myrskylä");
        kunnat.add("mäntsälä");
        kunnat.add("mänttä-vilppula");
        kunnat.add("mänttä");
        kunnat.add("vilppula");
        kunnat.add("mäntyharju");
        kunnat.add("naantali");

        kunnat.add("nakkila");
        kunnat.add("nivala");
        kunnat.add("nokia");
        kunnat.add("nousiainen");
        kunnat.add("nurmes");
        kunnat.add("nurmijärvi");
        kunnat.add("närpiö");
        kunnat.add("orimattila");
        kunnat.add("oripää");
        kunnat.add("orivesi");

        kunnat.add("oulainen");
        kunnat.add("oulu");
        kunnat.add("outokumpu");
        kunnat.add("padasjoki");
        kunnat.add("paimio");
        kunnat.add("paltamo");
        kunnat.add("parainen");
        kunnat.add("parikkala");
        kunnat.add("parkano");
        kunnat.add("pedersören kunta");
        kunnat.add("pedersöre");

        kunnat.add("pelkosenniemi");
        kunnat.add("pello");
        kunnat.add("perho");
        kunnat.add("pertunmaa");
        kunnat.add("petäjävesi");
        kunnat.add("pieksämäki");
        kunnat.add("pielavesi");
        kunnat.add("pietarsaari");
        kunnat.add("pihtipudas");
        kunnat.add("pirkkala");

        kunnat.add("polvijärvi");
        kunnat.add("pomarkku");
        kunnat.add("pori");
        kunnat.add("pornainen");
        kunnat.add("porvoo");
        kunnat.add("posio");
        kunnat.add("pudasjärvi");
        kunnat.add("pukkila");
        kunnat.add("punkalaidun");
        kunnat.add("puolanka");

        kunnat.add("puumala");
        kunnat.add("pyhtää");
        kunnat.add("pyhäjoki");
        kunnat.add("pyhäjärvi");
        kunnat.add("pyhäntä");
        kunnat.add("pyhäranta");
        kunnat.add("pälkäne");
        kunnat.add("pöytyä");
        kunnat.add("raahe");
        kunnat.add("raasepori");

        kunnat.add("raisio");
        kunnat.add("rantasalmi");
        kunnat.add("ranua");
        kunnat.add("rauma");
        kunnat.add("rautalampi");
        kunnat.add("rautavaara");
        kunnat.add("rautjärvi");
        kunnat.add("reisjärvi");
        kunnat.add("riihimäki");
        kunnat.add("ristijärvi");

        kunnat.add("rovaniemi");
        kunnat.add("ruokolahti");
        kunnat.add("ruovesi");
        kunnat.add("rusko");
        kunnat.add("rääkkylä");
        kunnat.add("saarijärvi");
        kunnat.add("salla");
        kunnat.add("salo");
        kunnat.add("saltvik");
        kunnat.add("sastamala");

        kunnat.add("sauvo");
        kunnat.add("savitaipale");
        kunnat.add("savonlinna");
        kunnat.add("savukoski");
        kunnat.add("seinäjoki");
        kunnat.add("sievi");
        kunnat.add("siikainen");
        kunnat.add("siikajoki");
        kunnat.add("siikalatva");
        kunnat.add("siilinjärvi");

        kunnat.add("simo");
        kunnat.add("sipoo");
        kunnat.add("siuntio");
        kunnat.add("sodankylä");
        kunnat.add("soini");
        kunnat.add("somero");
        kunnat.add("sonkajärvi");
        kunnat.add("sotkamo");
        kunnat.add("sottunga");
        kunnat.add("sulkava");

        kunnat.add("sund");
        kunnat.add("suomussalmi");
        kunnat.add("suonenjoki");
        kunnat.add("sysmä");
        kunnat.add("säkylä");
        kunnat.add("taipalsaari");
        kunnat.add("taivalkoski");
        kunnat.add("taivassalo");
        kunnat.add("tammela");
        kunnat.add("tampere");

        kunnat.add("tervo");
        kunnat.add("tervola");
        kunnat.add("teuva");
        kunnat.add("tohmajärvi");
        kunnat.add("toholampi");
        kunnat.add("toivakka");
        kunnat.add("tornio");
        kunnat.add("turku");
        kunnat.add("tuusniemi");
        kunnat.add("tuusula");

        kunnat.add("tyrnävä");
        kunnat.add("ulvila");
        kunnat.add("urjala");
        kunnat.add("utajärvi");
        kunnat.add("utsjoki");
        kunnat.add("uurainen");
        kunnat.add("uusikaarlepyy");
        kunnat.add("uusikaupunki");
        kunnat.add("vaala");
        kunnat.add("vaasa");

        kunnat.add("valkeakoski");
        kunnat.add("valtimo");
        kunnat.add("vantaa");
        kunnat.add("varkaus");
        kunnat.add("vehmaa");
        kunnat.add("vesanto");
        kunnat.add("vesilahti");
        kunnat.add("veteli");
        kunnat.add("vieremä");
        kunnat.add("vihti");

        kunnat.add("viitasaari");
        kunnat.add("vimpeli");
        kunnat.add("virolahti");
        kunnat.add("virrat");
        kunnat.add("vårdö");
        kunnat.add("vöyri");
        kunnat.add("ylitornio");
        kunnat.add("ylivieska");
        kunnat.add("ylöjärvi");
        kunnat.add("ypäjä");

        kunnat.add("ähtäri");
        kunnat.add("äänekoski");
        //Toast.makeText(aktiivinenHaly.this, "Kuntia listassa " + kunnat.size() + ".",Toast.LENGTH_LONG).show();
    }*/
