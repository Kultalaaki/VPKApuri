/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 10.7.2019 23:00
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
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import android.provider.Settings;
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
    private static final int MY_ALARM_NOTIFICATION_ID = 264981;
    int soundVolume, volume, soundMode, revertSound, revertStreamAlarmVolume;
    boolean tarina, automaticOpen, throughSilentMode, puhelu, pitaaPalauttaa = false, OHTO = false, ensivaste = false, alarmIsEnsivaste = false, puheluAani = false;
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
            startForegroundNotification(TAG);
        }
    }

    @SuppressLint("ApplySharedPref")
    public int onStartCommand(Intent intent, int flags, final int startId) {
        if (intent != null) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            if (powerManager != null) {
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
                startForegroundNotification(TAG);
            }

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            automaticOpen = sharedPreferences.getBoolean("automaticOpen", false);
            String number = intent.getStringExtra("number");
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
                if (isItAlarmSMS(number, message)) {

                    if (erica) {
                        lisaaHalyTunnukset();
                        lisaaKunnatErica();
                        addressLookUp(message, timestamp, number);
                    } else {
                        // OHTO alarm
                        saveOhtoAlarmToDatabase(message, timestamp, number);
                    }

                    if (sharedPreferences.getBoolean("stationboard_sounds", false)) {
                        selectAlarmSound(startId);
                        notificationAlarmMessage(message);
                        /*if (automaticOpen) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Intent halyAuki = new Intent(IsItAlarmService.this, AlarmActivity.class);
                                    halyAuki.setAction(Intent.ACTION_SEND);
                                    halyAuki.setType("automaattinen");
                                    halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(halyAuki);
                                }
                            }, 3000);
                        } else {

                        }*/
                    } else {
                        if (!sharedPreferences.getBoolean("HalytysOpen", false)) {
                            Intent openHalytysActivity = new Intent(IsItAlarmService.this, AlarmActivity.class);
                            openHalytysActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            IsItAlarmService.this.startActivity(openHalytysActivity);
                            stopSelf(startId);
                        }
                    }
                } else {
                    // Test who is coming and save to database..
                    whoIsComing(number, message);
                    stopSelf(startId);
                }
            } else if (isItAlarmSMS(number, message) && puheluHaly.equals("false")) {


                if (erica) {
                    lisaaHalyTunnukset();
                    lisaaKunnatErica();
                    addressLookUp(message, timestamp, number);
                } else {
                    // OHTO alarm
                    saveOhtoAlarmToDatabase(message, timestamp, number);
                }

                selectAlarmSound(startId);

                if (automaticOpen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (Settings.canDrawOverlays(getApplicationContext())) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent halyAuki = new Intent(IsItAlarmService.this, AlarmActivity.class);
                                halyAuki.setAction(Intent.ACTION_SEND);
                                halyAuki.setType("automaattinen");
                                halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(halyAuki);
                            }
                        }, 3000);
                    } else {
                        notificationAlarmMessage(message);
                    }
                } else if (automaticOpen) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent halyAuki = new Intent(IsItAlarmService.this, AlarmActivity.class);
                            halyAuki.setAction(Intent.ACTION_SEND);
                            halyAuki.setType("automaattinen");
                            halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(halyAuki);
                        }
                    }, 3000);
                } else {
                    notificationAlarmMessage(message);
                }

            } else if (isItAlarmSMS(number, message) && puheluHaly.equals("true")) {
                puhelu = sharedPreferences.getBoolean("puhelu", false);
                if (puhelu) {
                    puheluAani = sharedPreferences.getBoolean("Puhelu", false);
                    selectAlarmSound(startId);
                }
                //db = new DBHelper(getApplicationContext());
                //db.insertData("999A", "Ei osoitetta", "Hälytys tuli puheluna", "");
                FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
                FireAlarm fireAlarm = new FireAlarm("999", "A", "Hälytys tuli puheluna",
                        "Ei osoitetta", "", "", timestamp, "", "", "", "");

                fireAlarmRepository.insert(fireAlarm);

                if (automaticOpen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if(Settings.canDrawOverlays(getApplicationContext())) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Intent halyAuki = new Intent(IsItAlarmService.this, AlarmActivity.class);
                                halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                halyAuki.setAction(Intent.ACTION_SEND);
                                halyAuki.setType("automaattinen");
                                startActivity(halyAuki);
                            }
                        }, 3000);
                    } else {
                        notificationAlarmPhonecall("Hälytys tuli puheluna");
                    }

                } else if(automaticOpen){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            Intent halyAuki = new Intent(IsItAlarmService.this, AlarmActivity.class);
                            halyAuki.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            halyAuki.setAction(Intent.ACTION_SEND);
                            halyAuki.setType("automaattinen");
                            startActivity(halyAuki);
                        }
                    }, 3000);
                } else {
                    notificationAlarmPhonecall("Hälytys tuli puheluna");
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
        boolean saveToOHTO = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (number != null && !number.isEmpty()) {
                if (number.startsWith("O")) {
                    number = number.substring(1);
                    // this is OHTO alarm number, save to arraylist for later use.
                    saveToOHTO = true;
                }
                number = PhoneNumberUtils.formatNumber(number, Locale.getDefault().getCountry());
                if (number != null) {
                    if (number.charAt(0) == '0') {
                        number = "+358" + number.substring(1);
                    }
                    number = number.replaceAll("[()\\s-+]+", "");
                    number = "0" + number.substring(3);
                    if (saveToOHTO) {
                        OHTOnumbers.add(number);
                    }
                    return number;
                }
                return "99987654321";
            }
            return "99987654321";
        } else {
            if (number != null && !number.isEmpty()) {
                if (number.startsWith("O")) {
                    number = number.substring(1);
                    // this is OHTO alarm number, save to arraylist for later use.
                    OHTOnumbers.add(number);
                    saveToOHTO = true;
                    Log.e("åosdfk", "åoawk");
                }
                number = PhoneNumberUtils.formatNumber(number);
                if (number != null) {
                    if (number.charAt(0) == '0') {
                        number = "+358" + number.substring(1);
                    }
                    number = number.replaceAll("[()\\s-+]+", "");
                    number = "0" + number.substring(3);
                    if (saveToOHTO) {
                        OHTOnumbers.add(number);
                    }
                    return number;
                }
                return "99987654321";
            }
            return "99987654321";
        }
    }

    /**
     * Check incoming message and who sent it. If sender number matches number that is set in setting,
     * take all other information about that person and add to database Responder.
     * Database will be shown in ResponderFragment recyclerview.
     *
     * @param number  sender number
     * @param message sent message
     */
    private void whoIsComing(String number, String message) {
        ResponderRepository repository = new ResponderRepository(getApplication());
        number = numberFormat(number);

        if (!number.equals("99987654321")) {
            for (int i = 1; i <= 50; i++) {
                String numberFromSettings = sharedPreferences.getString("puhelinnumero" + i, null);
                numberFromSettings = numberFormat(numberFromSettings);

                if (numberFromSettings != null && !numberFromSettings.isEmpty()) {
                    if (numberFromSettings.equals(number)) {
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

                        Toast.makeText(this, name + " lähetti ilmoituksen.", Toast.LENGTH_SHORT).show();

                        break;
                    }
                }
            }
        }
    }

    private boolean isItAlarmSMS(String numero, String message) {
        numero = numberFormat(numero);

        if (!numero.equals("99987654321")) {
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

            for (String testattava : OHTOnumbers) {
                if (testattava.equals(numero)) {
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
                            break;
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

    void addressLookUp(String message, String timeStamp, String number) {

        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());

        String osoite = "";
        //String[] palautus = new String[5];
        int length = message.length();
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
            viestiTeksti.append(message.charAt(o));
        }

        // Chop message to words
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Break words according regex
            if (Character.toString(merkki).matches("[.,/:; ]")) {
                sanaYksin = sanatYksitellen.toString();
                if (sanaYksin.length() >= 1 || sanaYksin.matches("[0-9]")) {
                    sanatYksinaan.add(sanaYksin);
                }
                sanatYksitellen.delete(0, sanatYksitellen.length());
            } else {
                sanatYksitellen.append(message.charAt(i));
            }
        }

        // Break message sentences
        for (int i = 0; i <= length - 1; i++) {
            merkki = message.charAt(i);
            // Katko sanat regex:in mukaan
            if (Character.toString(merkki).matches("[;]")) {
                sana = viestinLauseet.toString();
                if (sana.length() > 1 || sana.matches("[0-9]")) {
                    viestinSanat.add(sana);
                }
                viestinLauseet.delete(0, viestinLauseet.length());
            } else {
                viestinLauseet.append(message.charAt(i));
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
                    if (halytunnukset.contains(valmisSana.substring(0, 3)) || valmisSana.startsWith("H35")) {

                        if (valmisSana.startsWith("H35")) {
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

        if (!kiire) {
            kiireellisyysLuokka = "Ei löytynyt";
        }
        if (!osoitet) {
            osoite = "Ei löytynyt";
        }

        String tallennettavaTunnus = "Ei löytynyt";

        if (loytyi) {
            tallennettavaTunnus = sanatYksinaan.get(halytunnusSijainti).trim() + " " + halytekstit.get(listaPaikka).trim();
            if (listaPaikka >= 98 && ensivaste) {
                alarmIsEnsivaste = true;
            }
        }

        if (!kiire && !osoitet && !loytyi) {
            // Tunnus, osoite ja kiireellisyysluokka ei löytynyt. Yhdistä edellisen hälytyksen kanssa.
            FireAlarm fireAlarmLastEntry = fireAlarmRepository.getLatest();
            if (fireAlarmLastEntry != null) {
                if (fireAlarmLastEntry.getTunnus().equals("OHTO Hälytys") || fireAlarmLastEntry.getTunnus().equals("999")) {
                    // Last alarm was OHTO or phonecall alarm. Make new alarm.
                    FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                            osoite.trim(), kommentti, "", timeStamp, number, "", "", "");

                    fireAlarmRepository.insert(fireAlarm);
                } else {
                    // Last alarm was not OHTO or phonecall alarm, update last alarm with new information if time difference is smaller than 30 minutes.
                    if (calculateTimeDifference(fireAlarmLastEntry.getTimeStamp(), timeStamp)) {
                        String addMessage = fireAlarmLastEntry.getViesti();
                        addMessage += "\n" +
                                "\n" + message;
                        fireAlarmLastEntry.setViesti(addMessage);
                        fireAlarmRepository.update(fireAlarmLastEntry);
                    } else {
                        FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                                osoite.trim(), kommentti, "", timeStamp, number, "", "", "");

                        fireAlarmRepository.insert(fireAlarm);
                    }
                }
            } else {
                FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                        osoite.trim(), kommentti, "", timeStamp, number, "", "", "");

                fireAlarmRepository.insert(fireAlarm);
            }

        } else if (sanatYksinaan.get(0).equals("PÄIVITYS")) {
            // päivitysviesti, hae tiedot ja päivitä message
            FireAlarm fireAlarmLastEntry = fireAlarmRepository.getLatest();

            if (fireAlarmLastEntry != null) {
                if (fireAlarmLastEntry.getTunnus().equals("OHTO Hälytys") || fireAlarmLastEntry.getTunnus().equals("999")) {
                    // Last alarm was OHTO or phonecall alarm. Make new alarm.
                    FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                            osoite.trim(), kommentti, "", timeStamp, number, "", "", "");

                    fireAlarmRepository.insert(fireAlarm);
                } else {
                    // Last alarm was not OHTO or phonecall alarm, update last alarm with new information if time difference is smaller than 30 minutes.
                    if (calculateTimeDifference(fireAlarmLastEntry.getTimeStamp(), timeStamp)) {
                        String LastEntryViesti = fireAlarmLastEntry.getViesti();
                        LastEntryViesti += "\n" +
                                "\n" + message;
                        if (kiire) {
                            fireAlarmLastEntry.setLuokka(kiireellisyysLuokka.trim());
                        }
                        if (osoitet) {
                            fireAlarmLastEntry.setOsoite(osoite.trim());
                        }
                        if (loytyi) {
                            fireAlarmLastEntry.setTunnus(tallennettavaTunnus);
                        }
                        fireAlarmLastEntry.setViesti(LastEntryViesti);
                        fireAlarmRepository.update(fireAlarmLastEntry);
                    } else {
                        // Time difference over 30 minutes. Make new alarm.
                        FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                                osoite.trim(), kommentti, "", timeStamp, number, "", "", "");

                        fireAlarmRepository.insert(fireAlarm);
                    }

                }

            } else {
                // Last fireAlarm was empty, make new alarm.
                FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                        osoite.trim(), kommentti, "", timeStamp, "", "", "", "");

                fireAlarmRepository.insert(fireAlarm);
            }
        } else {
            FireAlarm fireAlarm = new FireAlarm(tallennettavaTunnus, kiireellisyysLuokka.trim(), message,
                    osoite.trim(), kommentti, "", timeStamp, number, "", "", "");

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
            assert dateOld != null;
            long oldAlarmTime = dateOld.getTime();
            assert dateNew != null;
            long newAlarmTime = dateNew.getTime();
            //long difference = newAlarmTime - oldAlarmTime;
            //Log.e("TAG", "Time difference: " + difference);
            return newAlarmTime - oldAlarmTime <= (60000 * 30);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void saveOhtoAlarmToDatabase(String message, String timestamp, String number) {
        number = numberFormat(number);
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        FireAlarm fireAlarm = new FireAlarm("OHTO Hälytys", "", message,
                "", "", "", timestamp, number, "", "", "");

        fireAlarmRepository.insert(fireAlarm);
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
            audioManager.setStreamVolume(AudioManager.STREAM_RING, revertSound, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, revertStreamAlarmVolume, 0);
        }
        if (wakeLock != null) {
            try {
                wakeLock.release();
            } catch (Throwable th) {
                // No Need to do anything.
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void startForegroundNotification(String message) {
        Notification.Builder builder = new Notification.Builder(this, "ACTIVE SERVICE")
                .setContentTitle("VPK Apuri")
                .setContentText(message)
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

    /**
     * Creates notification that displays message in notification.
     * Replaces foreground notification. Without foreground notification android shuts down service.
     */
    public void notificationAlarmMessage(String message) {
        Intent intentsms = new Intent(getApplicationContext(), AlarmActivity.class);
        intentsms.setAction(Intent.ACTION_SEND);
        intentsms.setType("text/plain");
        intentsms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intentsms);
        PendingIntent pendingIntentWithBackStack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent stopAlarm = new Intent(this, StopIsItAlarmService.class);
        PendingIntent stop = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), stopAlarm, PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(IsItAlarmService.this, "HALYTYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.alarm))
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentWithBackStack)
                .addAction(R.mipmap.ic_launcher, "HILJENNÄ", stop)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDeleteIntent(stop)
                .setAutoCancel(true);

        Notification notification = mBuilder.build();
        startForeground(MY_ALARM_NOTIFICATION_ID, notification);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            try {
                notificationManager.cancel(15);
            } catch (Exception e) {
                Log.i("IsItAlarmService", "There was not notification to cancel.");
            }
        }
    }

    /**
     * Creates notification that displays message in notification.
     * Replaces foreground notification. Without foreground notification android shuts down service.
     */
    public void notificationAlarmPhonecall(String message) {
        Intent intentsms = new Intent(IsItAlarmService.this, AlarmActivity.class);
        intentsms.setAction(Intent.ACTION_SEND);
        intentsms.setType("text/plain");
        intentsms.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intentsms);
        PendingIntent pendingIntentWithBackStack = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(IsItAlarmService.this, "HALYTYS")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("HÄLYTYS")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntentWithBackStack)
                //.addAction(R.mipmap.ic_launcher, "HILJENNÄ", stop)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                //.setDeleteIntent(stop)
                //.setOngoing(true)
                .setAutoCancel(true);

        Notification notification = mBuilder.build();
        startForeground(MY_ALARM_NOTIFICATION_ID, notification);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            try {
                notificationManager.cancel(15);
            } catch (Exception e) {
                Log.i("IsItAlarmService", "There was not notification to cancel.");
            }
        }
    }

    public void selectAlarmSound(int startId) {

        String alarms = sharedPreferences.getString("ringtone", null);

        if (OHTO && !erica) {
            alarms = sharedPreferences.getString("ringtoneOHTO", null);
        } else if (alarmIsEnsivaste) {
            alarms = sharedPreferences.getString("ringtoneEnsivaste", null);
        } else if (puheluAani) {
            alarms = sharedPreferences.getString("ringtonePuhelu", null);
        }

        soundMode = sharedPreferences.getInt("aaneton_profiili", -1);
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
            throughSilentMode = sharedPreferences.getBoolean("throughSilentMode", false);
            soundVolume = sharedPreferences.getInt("SEEKBAR_VALUE", -1);
            int checkVolume = -1;

            if (audioManager != null) {
                checkVolume = audioManager.getStreamVolume(AudioManager.STREAM_RING);
                revertStreamAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                revertSound = checkVolume;

                audioManager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
                pitaaPalauttaa = true;
            }

            if (soundMode == 2) {
                if (tarina) {
                    //vibrateSilent();
                    selectVibratePattern();
                }
            } else if (soundMode == 3) {
                //Yötila
                if (throughSilentMode && checkVolume == 0) {
                    // ei saa tulla äänettömän läpi
                    return;
                }
                soundVolume = 10;
                volume = adjustVolume(soundVolume);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepareAsync();
                    if (tarina) {
                        selectVibratePattern();
                    }
                }
            } else {
                // Normaali
                if (throughSilentMode && checkVolume == 0) {
                    // ei saa tulla äänettömän läpi
                    return;
                }
                soundVolume = 50;
                soundVolume = sharedPreferences.getInt("SEEKBAR_VALUE", -1);
                volume = adjustVolume(soundVolume);
                if (audioManager != null) {
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                    mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
                    mMediaPlayer.setOnPreparedListener(this);
                    mMediaPlayer.setLooping(true);
                    mMediaPlayer.prepareAsync();
                    if (tarina) {
                        selectVibratePattern();
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

    public int adjustVolume(int volume) {
        final AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            this.volume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
            double aani = (double) this.volume / 100 * volume;
            this.volume = (int) aani;
        }

        if (this.volume == 0) {
            return 1;
        } else if (this.volume == 1) {
            return 1;
        } else if (this.volume == 2) {
            return 2;
        } else if (this.volume == 3) {
            return 3;
        } else if (this.volume == 4) {
            return 4;
        } else if (this.volume == 5) {
            return 5;
        } else if (this.volume == 6) {
            return 6;
        } else if (this.volume == 7) {
            return 7;
        }

        return this.volume;
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

    public void selectVibratePattern() {
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