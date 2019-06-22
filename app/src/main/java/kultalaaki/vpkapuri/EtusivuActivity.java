/*
 * Created by Kultala Aki on 9.9.2017 9:27
 * Copyright (c) 2017. All rights reserved.
 *
 * Last modified 19.8.2017 20:46
 */

package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Date;


public class EtusivuActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, KayttoehdotFragment.Listener, EtusivuFragment.OnFragmentInteractionListener,
                                ArkistoFragment.OnFragmentInteractionListener, OhjeetFragment.OnFragmentInteractionListener, TallennaArkistoonFragment.OnFragmentInteractionListener,
                                HalytysTietokannastaFragment.OnFragmentInteractionListener, TimerFragment.OnFragmentInteractionListener, SetTimerFragment.OnFragmentInteractionListener,
                                TimePickerDialog.OnTimeSetListener {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DrawerLayout mDrawerLayout;
    String[] osoite;
    String aihe;
    DBTimer dbTimer;
    SharedPreferences sharedPreferences;
    boolean ericaEtusivu, analytics, asemataulu;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    SoundControls soundControls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        asemataulu = sharedPreferences.getBoolean("asemataulu", false);
        ericaEtusivu = sharedPreferences.getBoolean("Erica", true);
        analytics = sharedPreferences.getBoolean("analyticsEnabled", false);
        sharedPreferences.edit().putBoolean("showHiljenna", false).apply();
        sharedPreferences.edit().putBoolean("HalytysOpen", false).apply();

        if(!asemataulu) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.etusivusidepanel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_36dp);
        }

        soundControls = new SoundControls();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if(analytics) {
            Fabric.with(this, new Crashlytics());
        }

        mFirebaseAnalytics.setAnalyticsCollectionEnabled(analytics);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        mDrawerLayout.closeDrawers();
                        switch(menuItem.getItemId()) {
                            case R.id.tallenna_arkistoon_haly:
                                loadTallennaArkistoonFragment();
                                //startTallennaArkistoon();
                                return true;
                            case R.id.testaa_haly:
                                showMessageOKCancelTestaaHaly("Paina OK jos haluat testata hälytystä (5sek viive ennen kuin hälytys tulee). " +
                                        "Voit laittaa puhelimen näppäinlukkoon tai poistua sovelluksesta.");
                                return true;
                            case R.id.hiljenna_halyt:
                                //hiljennaHalytykset();
                                if(sharedPreferences.getInt("aaneton_profiili", -1) == 1) {
                                    showMessage("Hälytysten hiljennys", "Haluatko varmasti hiljentää hälytykset?");
                                } else {
                                    hiljennaHalytykset();
                                }
                                return true;
                            case R.id.timer:
                                startTimerActivity();
                                //showMessageOKCancelAjastin();
                                return true;
                            case R.id.changelog:
                                startChangelog();
                                return true;
                            case R.id.tallennatietokanta:
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    //tietokantatxt = true;
                                    pyydaLuvatTiedostotKirjoita();
                                } else {
                                    showMessageOKCanceltietokanta();
                                }
                                return true;
                            case R.id.tyhjennatietokanta:
                                showMessageOKCanceltietokantaTyhjennys();
                                return true;
                            case R.id.palautetta:
                                startLahetaPalaute();
                                return true;
                        }

                        return true;
                    }
        });

        osoite = new String [1];
        osoite[0] = "info@vpkapuri.fi";
        aihe = "VPK Apuri palaute";

        if(sharedPreferences.contains("termsShown")) {
            loadEtusivuFragment();
        } else {
            loadLegalFragment();
        }
    }

    public void loadLegalFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        KayttoehdotFragment kayttoehdotFragment = new KayttoehdotFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.etusivuContainer, kayttoehdotFragment, "etusivuLegal").commit();
    }

    public void loadArkistoFragment() {
        //Crashlytics.getInstance().crash(); // Force a crash
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        ArkistoFragment arkistoFragment = new ArkistoFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, arkistoFragment, "arkistoFragment").commit();
    }

    public void loadOhjeetFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        OhjeetFragment ohjeetFragment = new OhjeetFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, ohjeetFragment, "ohjeetFragment").commit();
    }

    public void loadEtusivuFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        EtusivuFragment etusivuFragment = new EtusivuFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.etusivuContainer, etusivuFragment, "etusivuNavigation").commit();
    }

    public void loadEtusivuClearingBackstack() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        EtusivuFragment etusivuFragment = new EtusivuFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.etusivuContainer, etusivuFragment, "etusivuNavigation").commit();
    }

    public void loadEtusivuFromFragment() {
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(analytics);
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        EtusivuFragment etusivuFragment = new EtusivuFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.etusivuContainer, etusivuFragment, "etusivuNavigation");
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void loadTallennaArkistoonFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        TallennaArkistoonFragment tallennaArkistoonFragment = new TallennaArkistoonFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, tallennaArkistoonFragment, "tallennaArkistoonFragment").commit();
    }

    public void loadHalytysTietokannastaFragment(FireAlarm fireAlarm) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        HalytysTietokannastaFragment halytysTietokannastaFragment = HalytysTietokannastaFragment.newInstance(fireAlarm);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, halytysTietokannastaFragment, "halytysTietokannastaFragment").commit();
    }

    public void openSetTimerNewInstance(String primaryKey) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        SetTimerFragment setTimerFragment = SetTimerFragment.newInstance(primaryKey);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, setTimerFragment, "setTimerFragment").commit();
    }

    public void openSetTimer() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        SetTimerFragment setTimerFragment = new SetTimerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, setTimerFragment, "setTimerFragment").commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        createChannels();
        new WhatsNewScreen(this).show();
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void startTimerActivity() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        TimerFragment timerFragment = new TimerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, timerFragment, "timerFragment").commit();
    }

    public long saveTimerToDB(String name, String startTime, String stopTime, String ma, String ti, String ke, String to,
                              String pe, String la, String su, String selector, String isiton) {
        dbTimer = new DBTimer(this);
        //Toast.makeText(getApplicationContext(), "melkein " + name + startTime + stopTime + ma + ti+ke+to+pe+la+su+selector, Toast.LENGTH_LONG).show();
        long tallennettu = dbTimer.insertData(name, startTime, stopTime,
                ma, ti, ke, to, pe, la, su, selector, isiton);
        if(tallennettu != -1) {
            Toast.makeText(getApplicationContext(), "Tallennettu", Toast.LENGTH_LONG).show();
            return tallennettu;
        }
        return -1;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.i("TAG", "OnTimeSet reached");
        SetTimerFragment setTimerFragment = (SetTimerFragment)
                getSupportFragmentManager().findFragmentByTag("setTimerFragment");
        if(setTimerFragment != null) {
            setTimerFragment.setTimerTimes(hourOfDay, minute);
        }
    }

    public void createChannels() {
        //Ilmoituskanava hälytyksille
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel halyChannel = new NotificationChannel("HALYTYS", "HÄLYTYS", importance);
            halyChannel.setDescription("Tämän kanavan ilmoitukset ovat hälytyksiä varten.");
            halyChannel.enableVibration(false);
            halyChannel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(halyChannel);
            }
        }

        //Ilmoituskanava kun hälytykset on hiljennetty
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel ilmChannel = new NotificationChannel("HILJENNYS", "HILJENNYS", importance);
            ilmChannel.setDescription("Tämä ilmoituskanava ilmoittaa kun sovelluksesta on hälytykset hiljennetty");
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            ilmChannel.setSound(null, null);
            ilmChannel.enableVibration(false);
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(ilmChannel);
            }
        }

        //Ilmoituskanava aktiiviselle servicelle
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel mChannel = new NotificationChannel("ACTIVE SERVICE", "ACTIVE SERVICE", importance);
            mChannel.setDescription("Tämä ilmoituskanava on käytössä kun sovelluksen taustapalvelu on käynnissä osoitteen hakua ja hälytysäänen soittamista varten.");
            mChannel.enableVibration(false);
            mChannel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = (NotificationManager) getSystemService(
                    NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(mChannel);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTestaaHalytys() {

        ericaEtusivu = sharedPreferences.getBoolean("Erica", false);

        if(ericaEtusivu) {
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    long aika = System.currentTimeMillis();
                    String Aika = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(aika));
                    Intent halyaaniService = new Intent(getApplicationContext(), IsItAlarmService.class);
                    halyaaniService.putExtra("message", getString(R.string.testihalytysErica));
                    halyaaniService.putExtra("number", "+358401234567");
                    halyaaniService.putExtra("halytysaani", "false");
                    halyaaniService.putExtra("timestamp", Aika);
                    getApplicationContext().startService(halyaaniService);
                }
            }, 5000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent halyaaniService = new Intent(getApplicationContext(), IsItAlarmService.class);
                    halyaaniService.putExtra("message", getString(R.string.testihalytys));
                    halyaaniService.putExtra("number", "+358401234567");
                    halyaaniService.putExtra("halytysaani", "false");
                    getApplicationContext().startService(halyaaniService);
                }
            }, 5000);
        }
    }

    public void startChangelog() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        ChangelogFragment changelogFragment = new ChangelogFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, changelogFragment, "changelogFragment").commit();
        /*Intent intentChangelog = new Intent(EtusivuActivity.this, ChangelogActivity.class);
        startActivity(intentChangelog);*/
    }

    public void startLahetaPalaute (){
        Intent intentemail = new Intent(Intent.ACTION_SENDTO);
        intentemail.setData(Uri.parse("mailto:"));
        intentemail.putExtra(Intent.EXTRA_EMAIL, osoite);
        intentemail.putExtra(Intent.EXTRA_SUBJECT, aihe);
        if (intentemail.resolveActivity(getPackageManager()) != null) {
            startActivity(intentemail);
        }
    }

    @SuppressLint("ApplySharedPref")
    public void hiljennaHalytykset() {

        if (sharedPreferences.getInt("aaneton_profiili", -1) == 1) {
            //hiljenna haly
            soundControls.setSilent(this);
            //HILJENNA_HALY = 2;
            /*aaneton.edit().putInt("aaneton_profiili", 2).commit();
            RemoteViews text = new RemoteViews(getPackageName(), R.layout.widget_layout);
            text.setTextViewText(R.id.teksti, "Äänetön");
            Toast.makeText(getApplicationContext(),"Äänetön tila käytössä.", Toast.LENGTH_SHORT).show();

            ComponentName thisWidget = new ComponentName(EtusivuActivity.this,MyWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(EtusivuActivity.this);

            Intent hiljennys = new Intent(EtusivuActivity.this, EtusivuActivity.class);
            PendingIntent hiljennetty = PendingIntent.getActivity(EtusivuActivity.this, 0, hiljennys, PendingIntent.FLAG_CANCEL_CURRENT);
            manager.updateAppWidget(thisWidget, text);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(EtusivuActivity.this, "HILJENNYS")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("VPK Apuri")
                    .setContentText("Hälytykset on hiljennetty.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setContentIntent(hiljennetty)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setOngoing(true)
                    .setAutoCancel(false);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(EtusivuActivity.this);
            notificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());*/
        } else {
            //äänet päälle
            soundControls.setNormal(this);
            //HILJENNA_HALY = 1;
            /*aaneton.edit().putInt("aaneton_profiili", 1).commit();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(EtusivuActivity.this);
            notificationManager.cancel(MY_NOTIFICATION_ID);
            RemoteViews text = new RemoteViews(getPackageName(), R.layout.widget_layout);
            text.setTextViewText(R.id.teksti, "Normaali");
            Toast.makeText(getApplicationContext(),"Äänet kytketty.", Toast.LENGTH_SHORT).show();

            ComponentName thisWidget = new ComponentName(EtusivuActivity.this,MyWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(EtusivuActivity.this);
            manager.updateAppWidget(thisWidget, text);*/
        }
    }

    public void pyydaLuvatTiedostotKirjoita() {
        if (ContextCompat.checkSelfPermission(EtusivuActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(EtusivuActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel(new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    }
                });
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(EtusivuActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            showMessageOKCanceltietokanta();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showMessageOKCanceltietokanta();
            } else {
                // ei lupaa
                new AlertDialog.Builder(EtusivuActivity.this)
                        .setMessage("Sovelluksella ei ole lupaa laitteen tiedostoihin. Et voi tallentaa tietokantaa ilman lupaa.")
                        .setNegativeButton("Peruuta", null)
                        .create()
                        .show();
            }
        }
    }

    private void showMessageOKCanceltietokanta() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EtusivuActivity.this)
                .setTitle("Varmuuskopiointi!")
                .setMessage("Tietokannassa olevat hälytykset tallennetaan puhelimen muistiin nimellä: Hälytykset VPK Apuri. " +
                        "Tiedosto on avattavissa MS Excel tai jollain muulla ohjelmalla joka tukee .db tiedostoja.")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        //tietokantaVarmuuskopio();
                        tietokantaBackUp();
                    }
                });
        builder.create().show();
    }

    private void showMessageOKCanceltietokantaTyhjennys() {

        AlertDialog.Builder builder = new AlertDialog.Builder(EtusivuActivity.this)
                .setTitle("Tyhjennä arkisto!")
                .setMessage("Arkistossa olevat hälytykset poistetaan. Oletko varma että haluat poistaa hälytykset?")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        tietokantaTyhjennys();
                    }
                });
        builder.create().show();
    }

    public void showMessageOKCancelTestaaHaly(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(EtusivuActivity.this)
                .setTitle("Testaa hälytys")
                .setMessage(message)
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        startTestaaHalytys();
                    }
                });
        builder.create().show();
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(EtusivuActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        hiljennaHalytykset();
                    }
                });
        builder.create().show();
    }

    /*public void tietokantaVarmuuskopio() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = getDatabasePath("VPK_Apuri_Halytykset").getAbsolutePath();
        //String currentDBPath = "/data/" + "kultalaaki.vpkapuri/databases/VPK_Apuri_Halytykset.db";
        String backupDBPath = "Hälytykset VPK Apuri";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "Tietokanta tallennettu nimellä: Hälytykset VPK Apuri", Toast.LENGTH_LONG).show();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }*/

    public void tietokantaBackUp() {
        try {
            File sd = Environment.getExternalStorageDirectory();

            if(sd.canWrite()) {
                String currentDBPath = getDatabasePath("VPK_Apuri_Halytykset").getAbsolutePath();
                String backUpPath = "Hälytykset_VPK_Apuri";
                File currentDB = new File(currentDBPath);
                File backUpDP = new File(sd, backUpPath);

                if(currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backUpDP).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
                Toast.makeText(this, "Tietokanta tallennettu nimellä: Hälytykset VPK Apuri", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tietokantaTyhjennys () {
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.deleteAllFireAlarms();
        Toast.makeText(this, "Arkisto tyhjennetty.", Toast.LENGTH_LONG).show();
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EtusivuActivity.this)
                .setMessage("Sovelluksella ei ole lupaa laitteen tiedostoihin. Et voi asettaa viestiääntä/käyttää arkistoa jos et anna lupaa.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }

    private class WhatsNewScreen {
        private static final String LOG_TAG                 = "WhatsNewScreen";

        private static final String LAST_VERSION_CODE_KEY   = "last_version_code";

        private Activity            mActivity;

        // Constructor memorize the calling Activity ("context")
        private WhatsNewScreen(Activity context) {
            mActivity = context;
        }

        // Show the dialog only if not already shown for this version of the application
        @SuppressLint("ApplySharedPref")
        private void show() {
            try {
                // Get the versionCode of the Package, which must be different (incremented) in each release on the market in the AndroidManifest.xml
                final PackageInfo packageInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_ACTIVITIES);

                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
                final long lastVersionCode = prefs.getLong(LAST_VERSION_CODE_KEY, 0);

                // Kokeillaan versionCode == getLongVersionCode()
                final int versionCode = BuildConfig.VERSION_CODE;
                if (versionCode != lastVersionCode) {

                    if (prefs.getBoolean("firstrun", true)) {
                        prefs.edit().putInt("aaneton_profiili", 1).commit();
                        prefs.edit().putBoolean("firstrun", false).commit();
                    }
                    // TODO: add updated app alarm to database
                    deleteCache(getApplicationContext());
                    Log.i(LOG_TAG, "versionCode " + versionCode + "is different from the last known version " + lastVersionCode);

                    final String title = mActivity.getString(R.string.app_name) + " v" + packageInfo.versionName;

                    final String message = mActivity.getString(R.string.onlyNewest);

                    // Show the News since last version
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                            .setTitle(title)
                            .setMessage(message)
                            .setCancelable(false)
                            .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Mark this version as read
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putLong(LAST_VERSION_CODE_KEY, versionCode);
                                    editor.apply();
                                    //showTiewtosuojaAfterWhatsnew();
                                    dialogInterface.dismiss();
                                }
                            });
                    builder.create().show();
                } else {
                    Log.i(LOG_TAG, "versionCode " + versionCode + "is already known");
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        void deleteCache(Context context) {
            try {
                File dir = context.getCacheDir();
                deleteDir(dir);
            } catch (Exception e) {
                Log.i("VPK Apuri","Välimuistin tyhjennys epäonnistui.");
            }
        }
        boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (String aChildren : children) {
                    boolean success = deleteDir(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
                return dir.delete();
            } else if(dir!= null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }
        }
    }
}
