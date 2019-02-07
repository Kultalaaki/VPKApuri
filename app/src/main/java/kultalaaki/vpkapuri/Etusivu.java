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
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

//import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

//import io.fabric.sdk.android.Fabric;

public class Etusivu extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    FirebaseAnalytics mFirebaseAnalytics;

    private DrawerLayout mDrawerLayout;

    CardView halytys;
    CardView carkisto;
    CardView ohjeet;
    CardView csettings;
    String[] osoite;
    String aihe;
    DBHelper db;
    SharedPreferences aaneton;
    boolean ericaEtusivu;
    //private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 2;
    //private static final int MY_PERMISSIONS_REQUEST_CALL_LOG = 3;
    private static final int MY_NOTIFICATION_ID = 15245;
    //Boolean asetuksiin = false;
    //Boolean tietokantatxt = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.etusivusidepanel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        if(actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_36dp);
        }

        SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(this);
        ericaEtusivu = pref_general.getBoolean("Erica", false);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                        mDrawerLayout.closeDrawers();
                        switch(menuItem.getItemId()) {
                            case R.id.tallenna_arkistoon_haly:
                                startTallennaArkistoon();
                                return true;
                            case R.id.testaa_haly:
                                showMessageOKCancelTestaaHaly("Paina OK jos haluat testata hälytystä (5sek viive ennen kuin hälytys tulee). " +
                                        "Voit laittaa puhelimen näppäinlukkoon tai poistua sovelluksesta.");
                                return true;
                            case R.id.hiljenna_halyt:
                                //hiljennaHalytykset();
                                aaneton = getSharedPreferences("kultalaaki.vpkapuri.aaneton", Activity.MODE_PRIVATE);
                                if(aaneton.getInt("aaneton_profiili", -1) == 1) {
                                    showMessage("Hälytysten hiljennys", "Haluatko varmasti hiljentää hälytykset?");
                                } else {
                                    hiljennaHalytykset();
                                }
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



        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        carkisto = findViewById(R.id.card_viewArkisto);
        ohjeet = findViewById(R.id.card_viewOhjeet);
        csettings = findViewById(R.id.card_viewAsetukset);
        halytys = findViewById(R.id.card_viewHaly);

        //halytysSivu = findViewById(R.id.halytysSivu);
        //arkisto = findViewById(R.id.arkisto);
        //ohjeita = findViewById(R.id.ohjeita);
        //asetukset = findViewById(R.id.asetukset);

        new WhatsNewScreen(this).show();



        osoite = new String [1];
        osoite[0] = "kultalaaki@gmail.com";
        aihe = "VPK Apuri palaute";

        halytys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent2 = new Intent(Etusivu.this, aktiivinenHaly.class);
                //startActivity(intent2);
                //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //  pyydaLuvatCallLogs();
                //} else {
                    avaaHaly();
                //}

            }
        });

        carkisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //    pyydaLuvatTiedostot();
                //} else {
                    avaaArkisto();
                //}
            }
        });

        ohjeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avaaOhjeet();
            }
        });

        csettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //    asetuksiin = true;
                //    pyydaLuvatTiedostotKirjoita();
                //} else {
                    // SettingsActivity.class vaihdettu asetuksetRefined.class
                    avaaAsetukset();
                //}
            }
        });

        createChannels();

    }

    public void avaaHaly () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Etusivu.this);
            Intent intent = new Intent(Etusivu.this, aktiivinenHaly.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(Etusivu.this, aktiivinenHaly.class);
            startActivity(intent);
        }
    }

    public void avaaArkisto () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Etusivu.this);
            Intent intent = new Intent(Etusivu.this, ArkistoActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(Etusivu.this, ArkistoActivity.class);
            startActivity(intent);
        }
    }

    public void avaaOhjeet () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Etusivu.this);
            Intent intent = new Intent(Etusivu.this, OhjeitaActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(Etusivu.this, OhjeitaActivity.class);
            startActivity(intent);
        }
    }

    public void avaaAsetukset () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Etusivu.this);
            Intent intent = new Intent(Etusivu.this, SettingsActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(Etusivu.this, SettingsActivity.class);
            startActivity(intent);
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
            //PreferenceManager.getDefaultSharedPreferences(context);
            /*SharedPreferences settings = getApplicationContext().getSharedPreferences(getApplicationContext().getPackageName() + "_preferences", Context.MODE_PRIVATE);
            Uri uri;

            String alarms = settings.getString("ringtone", null);
            if(alarms != null) {
                uri = Uri.parse(alarms);
            } else {
                uri = Uri.parse("android.resource://kultalaaki.vpkapuri/" + R.raw.virve);
            }
            AudioAttributes myTrack = new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build();

            mChannel.setSound(uri, myTrack);*/
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
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
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startTestaaHalytys() {

        /*NotificationManager notify = (NotificationManager) getBaseContext().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        //Toast.makeText(getApplicationContext(), "Do Not Disturb " + notify, Toast.LENGTH_SHORT).show();
        if (notify != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && notify.isNotificationPolicyAccessGranted()) {
                Toast.makeText(getApplicationContext(), "Do Not Disturb lupa on myönnetty", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Do Not Disturb lupaa ei ole myönnetty", Toast.LENGTH_SHORT).show();
            }
        }*/
        SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(this);
        ericaEtusivu = pref_general.getBoolean("Erica", false);

        if(ericaEtusivu) {
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    Intent halyaaniService = new Intent(getApplicationContext(), halyaaniService.class);
                    halyaaniService.putExtra("message", getString(R.string.testihalytysErica));
                    halyaaniService.putExtra("number", "+358401234567");
                    halyaaniService.putExtra("halytysaani", "false");
                    getApplicationContext().startService(halyaaniService);
                }
            }, 5000);
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    Intent halyaaniService = new Intent(getApplicationContext(), halyaaniService.class);
                    halyaaniService.putExtra("message", getString(R.string.testihalytys));
                    halyaaniService.putExtra("number", "+358401234567");
                    halyaaniService.putExtra("halytysaani", "false");
                    getApplicationContext().startService(halyaaniService);
                }
            }, 5000);
        }
    }

    public void startTallennaArkistoon() {
        Intent tallennaArkistoon = new Intent(Etusivu.this, tallennaArkistoon.class);
        startActivity(tallennaArkistoon);
    }

    public void startChangelog() {
        Intent intentChangelog = new Intent(Etusivu.this, changelog.class);
        startActivity(intentChangelog);
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
        aaneton = getSharedPreferences("kultalaaki.vpkapuri.aaneton", Activity.MODE_PRIVATE);
        if (aaneton.getInt("aaneton_profiili", -1) == 1) {
            //hiljenna haly
            //HILJENNA_HALY = 2;
            aaneton.edit().putInt("aaneton_profiili", 2).commit();
            RemoteViews text = new RemoteViews(getPackageName(), R.layout.widget_layout);
            text.setTextViewText(R.id.teksti, "Äänetön");
            Toast.makeText(getApplicationContext(),"Äänetön tila käytössä.", Toast.LENGTH_SHORT).show();

            ComponentName thisWidget = new ComponentName(Etusivu.this,MyWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(Etusivu.this);

            Intent hiljennys = new Intent(Etusivu.this, Etusivu.class);
            PendingIntent hiljennetty = PendingIntent.getActivity(Etusivu.this, 0, hiljennys, PendingIntent.FLAG_CANCEL_CURRENT);
            manager.updateAppWidget(thisWidget, text);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(Etusivu.this, "HILJENNYS")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("VPK Apuri")
                    .setContentText("Hälytykset on hiljennetty.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                    .setContentIntent(hiljennetty)
                    .setVisibility(1)
                    .setOngoing(true)
                    .setAutoCancel(false);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Etusivu.this);
            notificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
        } else {
            //äänet päälle
            //HILJENNA_HALY = 1;
            aaneton.edit().putInt("aaneton_profiili", 1).commit();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Etusivu.this);
            notificationManager.cancel(MY_NOTIFICATION_ID);
            RemoteViews text = new RemoteViews(getPackageName(), R.layout.widget_layout);
            text.setTextViewText(R.id.teksti, "Normaali");
            Toast.makeText(getApplicationContext(),"Äänet kytketty.", Toast.LENGTH_SHORT).show();

            ComponentName thisWidget = new ComponentName(Etusivu.this,MyWidgetProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(Etusivu.this);
            manager.updateAppWidget(thisWidget, text);
        }
    }

    /*public void pyydaLuvatTiedostot() {
        if (ContextCompat.checkSelfPermission(Etusivu.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Etusivu.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel(new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                            }
                        });
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Etusivu.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            //luvat myönnetty
            avaaArkisto();
        }
    }*/

    public void pyydaLuvatTiedostotKirjoita() {
        if (ContextCompat.checkSelfPermission(Etusivu.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Etusivu.this,
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

                ActivityCompat.requestPermissions(Etusivu.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Luvat myönnetty
            //if(tietokantatxt) {
                //tietokantatxt = false;
                showMessageOKCanceltietokanta();
            //} else if(asetuksiin) {
            //    asetuksiin = false;
            //    avaaAsetukset();
            //}

        }
    }

    /*public void pyydaLuvatCallLogs(){
        // SMS Luvat
        if (ContextCompat.checkSelfPermission(Etusivu.this,
                Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Etusivu.this,
                    Manifest.permission.READ_CALL_LOG)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel(new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        requestPermissions(new String[] {Manifest.permission.READ_CALL_LOG},
                                MY_PERMISSIONS_REQUEST_CALL_LOG);
                    }
                });


                /*showMessageOKCancel("Sovelluksella ei ole lupaa lukea soittajan numeroa. Ilman tätä lupaa puheluilla tulevia hälytyksiä ei saada kirjattua ylös.",
                        new DialogInterface.OnClickListener() {
                            @TargetApi(Build.VERSION_CODES.M)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                requestPermissions(new String[] {Manifest.permission.READ_CALL_LOG},
                                        MY_PERMISSIONS_REQUEST_CALL_LOG);
                            }
                        });*/
            /*} else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Etusivu.this,
                        new String[]{Manifest.permission.READ_CALL_LOG},
                        MY_PERMISSIONS_REQUEST_CALL_LOG);
            }
        } else {
            //luvat on
            avaaHaly();
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            /*case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // lupa annettu
                    avaaArkisto();
                } else {
                    // lupaa ei ole. pysäytä toiminto
                    new AlertDialog.Builder(Etusivu.this)
                            .setMessage("Sovelluksella ei ole lupaa laitteen tiedostoihin. Et pääse arkistoon ennen kuin lupa on myönnetty.")
                            .setNegativeButton("Peruuta", null)
                            .create()
                            .show();
                }
                return;
            }*/
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // lupa on
                    //if(tietokantatxt) {
                        // tietokannan txt varmuuskopiointi
                    //    tietokantatxt = false;
                        showMessageOKCanceltietokanta();
                    //} else if(asetuksiin) {
                    //    asetuksiin = false;
                    //    avaaAsetukset();
                    //}

                } else {
                    // ei lupaa
                    new AlertDialog.Builder(Etusivu.this)
                            .setMessage("Sovelluksella ei ole lupaa laitteen tiedostoihin. Et voi tallentaa tietokantaa ilman lupaa.")
                            .setNegativeButton("Peruuta", null)
                            .create()
                            .show();
                }
            }
            /*case MY_PERMISSIONS_REQUEST_CALL_LOG: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // lupa on
                    avaaHaly();
                } else {
                    // ei lupaa
                    /*new AlertDialog.Builder(Etusivu.this)
                            .setMessage("Puheluilla tulevia hälytyksiä ei saada ilman tätä lupaa")
                            .setNegativeButton("Peruuta", null)
                            .create()
                            .show();*/
            //        avaaHaly();
            //    }
            //}*/
            // other 'case' lines to check for other
            // permissions this app might request.
            // remember return if isn't last switch
        }
    }

    private void showMessageOKCanceltietokanta() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Etusivu.this)
                .setTitle("Varmuuskopiointi!")
                .setMessage("Tietokannassa olevat hälytykset tallennetaan puhelimen muistiin nimellä: Hälytykset VPK Apuri. " +
                        "Tiedosto on avattavissa MS Excel tai jollain muulla ohjelmalla joka tukee .db tiedostoja.")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        tietokantaVarmuuskopio();
                    }
                });
        builder.create().show();
    }

    private void showMessageOKCanceltietokantaTyhjennys() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Etusivu.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Etusivu.this)
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
        AlertDialog.Builder builder = new AlertDialog.Builder(Etusivu.this)
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

    /*private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Etusivu.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }*/

    public void tietokantaVarmuuskopio() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "kultalaaki.vpkapuri/databases/halytyksetArkisto.db";
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
    }

    public void tietokantaTyhjennys () {
        db = new DBHelper(getApplicationContext());
        if (db.tyhjennaTietokanta()) {
            Toast.makeText(this, "Arkisto tyhjennetty.", Toast.LENGTH_LONG).show();
        }
    }

    private void showMessageOKCancel(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Etusivu.this)
                .setMessage("Sovelluksella ei ole lupaa laitteen tiedostoihin. Et voi asettaa viestiääntä/käyttää arkistoa jos et anna lupaa.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
    }

    // Lupien kysyminen testi

    // lupien kysyminen loppuu

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
                    aaneton = getSharedPreferences("kultalaaki.vpkapuri.aaneton", Activity.MODE_PRIVATE);
                    if (aaneton.getBoolean("firstrun", true)) {
                        aaneton.edit().putInt("aaneton_profiili", 1).commit();
                        aaneton.edit().putBoolean("firstrun", false).commit();
                    }
                    db = new DBHelper(getApplicationContext());
                    db.insertData("915C", "Ei osoitetta", "Uusi asennus tai sovellus on päivitetty. Tervetuloa käyttämään palokuntalaisille suunniteltua hälytys sovellusta. " +
                            "Ohjeet sivulta saat tietoa asetuksista.", "");
                    Log.i(LOG_TAG, "versionCode " + versionCode + "is different from the last known version " + lastVersionCode);

                    final String title = mActivity.getString(R.string.app_name) + " v" + packageInfo.versionName;

                    final String message = mActivity.getString(R.string.whatsnew);

                    // Show the News since last version
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity)
                            .setTitle(title)
                            .setMessage(message)
                            .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // Mark this version as read
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putLong(LAST_VERSION_CODE_KEY, versionCode);
                                    editor.apply();
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
    }
}
