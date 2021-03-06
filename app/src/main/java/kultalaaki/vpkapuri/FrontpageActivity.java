/*
 * Created by Kultala Aki on 3/6/21 3:31 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 3/6/21 3:31 PM
 */

package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.File;
import java.util.Date;


public class FrontpageActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, AffirmationFragment.Listener, FrontpageFragment.OnFragmentInteractionListener,
        ArchiveFragment.OnFragmentInteractionListener, GuidelineFragment.OnFragmentInteractionListener, SaveToArchiveFragment.OnFragmentInteractionListener,
        ArchivedAlarmFragment.OnFragmentInteractionListener, TimerFragment.OnFragmentInteractionListener, SetTimerFragment.OnFragmentInteractionListener,
        TimePickerDialog.OnTimeSetListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS = 3;
    private static final int CREATE_FILE = 1;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DrawerLayout mDrawerLayout;
    String[] emailAddress;
    String emailSubject;
    DBTimer dbTimer;
    SharedPreferences preferences;
    boolean ericaEtusivu, analytics, asemataulu;
    SoundControls soundControls;
    FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        asemataulu = preferences.getBoolean("asemataulu", false);
        analytics = preferences.getBoolean("analyticsEnabled", false);
        preferences.edit().putBoolean("showHiljenna", false).apply();
        preferences.edit().putBoolean("HalytysOpen", false).apply();
        fragmentManager = this.getSupportFragmentManager();

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

        // Setting Firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics mFirebaseCrashlytics = FirebaseCrashlytics.getInstance();
        mFirebaseCrashlytics.setCrashlyticsCollectionEnabled(analytics);
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
                                showDialog(
                                        "Hälytyksen testaus! Hälytys tulee 5 sekunnin kuluttua.",
                                        "Voit laittaa puhelimen näppäinlukkoon tai poistua sovelluksesta. Älä sammuta sovellusta kokonaan taustalta, silloin sammuu myös ajastin joka lähettää hälytyksen.",
                                        "Peruuta",
                                        "Testaa",
                                        "testAlarm");
                                return true;
                            case R.id.check_settings:
                                loadTestSettingsFragment();
                                return true;
                            case R.id.hiljenna_halyt:
                                if (preferences.getInt("aaneton_profiili", -1) == 1) {
                                    showDialog(
                                            "Hälytysten hiljennys!",
                                            "Haluatko varmasti hiljentää hälytykset?",
                                            "Peruuta",
                                            "Kyllä",
                                            "setSoundSilent");
                                } else {
                                    setSoundSilent();
                                }
                                return true;
                            case R.id.timer:
                                startTimerActivity();
                                return true;
                            case R.id.changelog:
                                startChangelog();
                                return true;
                            case R.id.tallennatietokanta:
                                showDialog(
                                        "Haluatko tallentaa arkistossa olevat hälytykset?",
                                        "Tiedosto on avattavissa MS Excel tai jollain muulla ohjelmalla joka tukee .db tiedostoja.",
                                        "Peruuta",
                                        "Kyllä",
                                        "saveDatabase");
                                //saveFile();
                                /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    askPermissionWriteExternalStorages();
                                } else {
                                    showDialog(
                                            "Tietokannassa olevat hälytykset tallennetaan puhelimen muistiin nimellä: Hälytykset VPK Apuri.",
                                            "Tiedosto on avattavissa MS Excel tai jollain muulla ohjelmalla joka tukee .db tiedostoja.",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    backupDatabase();
                                                }
                                            });
                                }*/
                                return true;
                            case R.id.tyhjennatietokanta:
                                showDialog(
                                        "Arkiston tyhjentäminen!",
                                        "Arkistossa olevat hälytykset poistetaan.\nPoistamisen jälkeen arkistoa ei voida palauttaa.\nOletko varma että haluat poistaa hälytykset?",
                                        "Peruuta",
                                        "Kyllä",
                                        "deleteDatabase"
                                );
                                return true;
                            case R.id.palautetta:
                                startLahetaPalaute();
                                return true;
                        }

                        return true;
                    }
                });

        emailAddress = new String[1];
        emailAddress[0] = "kultalaaki@gmail.com";
        emailSubject = "VPK Apuri palaute";

        if (preferences.contains("termsShown")) {
            loadEtusivuFragment();
        } else {
            loadLegalFragment();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        createChannels();
        new WhatsNewScreen(this).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!asemataulu) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void loadTestSettingsFragment() {
        TestSettingsFragment testSettingsFragment = new TestSettingsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, testSettingsFragment, "testSettingsFragment").commit();
    }

    public void loadLegalFragment() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        AffirmationFragment affirmationFragment = new AffirmationFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.etusivuContainer, affirmationFragment, "etusivuLegal").commit();
    }

    public void loadArkistoFragment() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        ArchiveFragment archiveFragment = new ArchiveFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, archiveFragment, "archiveFragment").commit();
    }

    public void loadSettingsFragment() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(FrontpageActivity.this);
            Intent intent = new Intent(FrontpageActivity.this, SettingsActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(FrontpageActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    public void loadOhjeetFragment() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        GuidelineFragment guidelineFragment = new GuidelineFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (findViewById(R.id.etusivuContainerLandScape) != null) {
            fragmentTransaction.replace(R.id.etusivuContainerLandScape, guidelineFragment, "guidelineFragment").commit();
        } else {
            fragmentTransaction.replace(R.id.etusivuContainer, guidelineFragment, "guidelineFragment").commit();
        }
    }

    public void loadEtusivuFragment() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        FrontpageFragment frontpageFragment = new FrontpageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.etusivuContainer, frontpageFragment, "etusivuNavigation").commit();
    }

    public void loadEtusivuClearingBackstack() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FrontpageFragment frontpageFragment = new FrontpageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.etusivuContainer, frontpageFragment, "etusivuNavigation").commit();
    }

    public void loadEtusivuFromFragment() {
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(analytics);
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        FrontpageFragment frontpageFragment = new FrontpageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.etusivuContainer, frontpageFragment, "etusivuNavigation");
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void loadTallennaArkistoonFragment() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        SaveToArchiveFragment saveToArchiveFragment = new SaveToArchiveFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, saveToArchiveFragment, "saveToArchiveFragment").commit();
    }

    public void startChangelog() {
        ChangelogFragment changelogFragment = new ChangelogFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (findViewById(R.id.etusivuContainerLandScape) != null) {
            fragmentTransaction.replace(R.id.etusivuContainerLandScape, changelogFragment, "changelogFragment").commit();
        } else {
            fragmentTransaction.replace(R.id.etusivuContainer, changelogFragment, "changelogFragment").commit();
        }
    }

    public void loadHalytysTietokannastaFragment(FireAlarm fireAlarm) {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        ArchivedAlarmFragment archivedAlarmFragment = ArchivedAlarmFragment.newInstance(fireAlarm);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        if (findViewById(R.id.etusivuContainerLandScape) != null) {
            fragmentTransaction.replace(R.id.etusivuContainerLandScape, archivedAlarmFragment, "archivedAlarmFragment").commit();
        } else {
            fragmentTransaction.replace(R.id.etusivuContainer, archivedAlarmFragment, "archivedAlarmFragment").commit();
        }
    }

    public void openSetTimerNewInstance(String primaryKey) {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        SetTimerFragment setTimerFragment = SetTimerFragment.newInstance(primaryKey);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, setTimerFragment, "setTimerFragment").commit();
    }

    public void openSetTimer() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
        SetTimerFragment setTimerFragment = new SetTimerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, setTimerFragment, "setTimerFragment").commit();
    }

    public void startTimerActivity() {
        //FragmentManager fragmentManager = this.getSupportFragmentManager();
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
            showToast("Ajastin", "Tallennettu.");
            //Toast.makeText(getApplicationContext(), "Tallennettu", Toast.LENGTH_LONG).show();
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

    public void testAlarm() {

        ericaEtusivu = preferences.getBoolean("Erica", true);
        if (ericaEtusivu) {
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                public void run() {
                    long aika = System.currentTimeMillis();
                    String Aika = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(aika));
                    String timeToMessage = (String) DateFormat.format("H:mm:ss_dd.MM.yyyy", new Date(aika));
                    Intent halyaaniService = new Intent(getApplicationContext(), IsItAlarmService.class);
                    String alarmMessage = getString(R.string.testihalytysEricaEtuosa) + " " + timeToMessage + getString(R.string.testihalytysEricaTakaosa);
                    halyaaniService.putExtra("message", alarmMessage);
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
                    long aika = System.currentTimeMillis();
                    String Aika = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(aika));
                    Intent halyaaniService = new Intent(getApplicationContext(), IsItAlarmService.class);
                    String alarmMessage = "TESTIHÄLYTYS; Operaatio nro 220/Etsintä/Kankaanpää/12.10.18:30. Kuittaus: 220 ok/ei/pm hh:mm";
                    halyaaniService.putExtra("message", alarmMessage);
                    halyaaniService.putExtra("number", "+358401234567");
                    halyaaniService.putExtra("halytysaani", "false");
                    halyaaniService.putExtra("timestamp", Aika);
                    getApplicationContext().startService(halyaaniService);
                }
            }, 5000);
        }
    }

    public void startLahetaPalaute () {
        Intent intentemail = new Intent(Intent.ACTION_SENDTO);
        intentemail.setData(Uri.parse("mailto:"));
        intentemail.putExtra(Intent.EXTRA_EMAIL, emailAddress);
        intentemail.putExtra(Intent.EXTRA_SUBJECT, emailSubject);
        if (intentemail.resolveActivity(getPackageManager()) != null) {
            startActivity(intentemail);
        }
    }

    @SuppressLint("ApplySharedPref")
    public void setSoundSilent() {

        if (preferences.getInt("aaneton_profiili", -1) == 1) {
            soundControls.setSilent(this);
        } else {
            soundControls.setNormal(this);
        }
    }

    // Todo On devices that run Android 9 (API level 28) or lower, your app must request READ_EXTERNAL_STORAGE permission to access any media file, including the media files that your app created.
    public void askPermissionReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(FrontpageActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Second time asking. Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FrontpageActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showDialog(
                        "VPK Apuri pyytää lupaa käyttää laitteellasi olevia kuvia ja mediaa.",
                        "Tämä lupa tarvitaan hälytysäänen asettamiseksi. Ilman tätä lupaa sovellus ei voi asettaa hälytysääntä. Pääsy asetuksiin on estetty kunnes lupa on myönnetty.",
                        "Peruuta",
                        "Anna lupa",
                        "askPermission");
            } else {
                // First time asking. No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(FrontpageActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS);
            }
        } else {
            // We have permission
            loadSettingsFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Lupa annettu mene asetuksiin
                loadSettingsFragment();
            } else {
                // ei lupaa. 1. kielto tulee tänne
                showDialog(
                        "Et antanut lupaa käyttää laitteellasi olevia kuvia ja mediaa.",
                        "Pääsy sovelluksen asetuksiin on estetty kunnes annat luvan käyttää laitteellasi olevia kuvia ja mediaa.",
                        "OK");
            }
        }
    }

    public void showDialog(String upperText, String lowerText, String positiveButtonText) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_permissions, null);
        dialog.setView(dialogLayout);

        TextView whatPermission = dialogLayout.findViewById(R.id.textViewWhatPermission);
        TextView whatReason = dialogLayout.findViewById(R.id.textViewReasoning);
        whatPermission.setText(upperText);
        whatReason.setText(lowerText);

        Button buttonPositive = dialogLayout.findViewById(R.id.buttonPositive);
        buttonPositive.setText(positiveButtonText);

        buttonPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDialog(String upperText, String lowerText, String negativeButtonText, String positiveButtonText, String chooser) {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_permissions, null);
        dialog.setView(dialogLayout);

        TextView dialogUpperText = dialogLayout.findViewById(R.id.textViewWhatPermission);
        TextView dialogLowerText = dialogLayout.findViewById(R.id.textViewReasoning);
        dialogUpperText.setText(upperText);
        dialogLowerText.setText(lowerText);

        Button buttonPositive = dialogLayout.findViewById(R.id.buttonPositive);
        Button buttonNegative = dialogLayout.findViewById(R.id.buttonNegative);
        buttonPositive.setText(positiveButtonText);
        buttonNegative.setText(negativeButtonText);

        switch (chooser) {
            case "testAlarm":
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        testAlarm();
                        dialog.dismiss();
                    }
                });
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case "setSoundSilent":
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSoundSilent();
                        dialog.dismiss();
                    }
                });
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case "askPermission":
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(FrontpageActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS);
                        dialog.dismiss();
                    }
                });
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case "saveDatabase":
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveFile();
                        dialog.dismiss();
                    }
                });
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                break;
            case "deleteDatabase":
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDatabase();
                        dialog.dismiss();
                    }
                });
                buttonNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
        }


        dialog.show();
        dialog.getWindow().setBackgroundDrawable(null);
    }

    public void showToast(String headText, String toastText) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) findViewById(R.id.custom_toast_container));

        TextView head = (TextView) layout.findViewById(R.id.head_text);
        head.setText(headText);
        TextView toastMessage = (TextView) layout.findViewById(R.id.toast_text);
        toastMessage.setText(toastText);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void saveFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("VPKApuri/db");
        intent.putExtra(Intent.EXTRA_TITLE, "HälytyksetVPKApuri.db");

        startActivityForResult(intent, CREATE_FILE);
    }

    public void deleteDatabase() {
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.deleteAllFireAlarms();
        //Toast.makeText(this, "Arkisto tyhjennetty.", Toast.LENGTH_LONG).show();
        showToast("Arkisto", "Arkisto tyhjennetty!");
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
                    // App updated, add alarm to database
                    FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
                    FireAlarm fireAlarm = new FireAlarm("999", "C", "Uusi asennus tai sovellus on päivitetty.", "Ei osoitetta", "", "",
                            "", "", "", "", "");
                    fireAlarmRepository.insert(fireAlarm);

                    // Delete app cache to prevent unnecessary mistakes.
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