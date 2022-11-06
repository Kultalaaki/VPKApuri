/*
 * Created by Kultala Aki on 4/24/21 9:34 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 3/20/21 1:02 PM
 */

package kultalaaki.vpkapuri;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

import kultalaaki.vpkapuri.Fragments.AffirmationFragment;
import kultalaaki.vpkapuri.Fragments.ArchiveFragment;
import kultalaaki.vpkapuri.Fragments.ArchivedAlarmFragment;
import kultalaaki.vpkapuri.Fragments.ChangelogFragment;
import kultalaaki.vpkapuri.Fragments.FrontpageFragment;
import kultalaaki.vpkapuri.Fragments.GuidelineFragment;
import kultalaaki.vpkapuri.Fragments.SaveToArchiveFragment;
import kultalaaki.vpkapuri.Fragments.SetTimerFragment;
import kultalaaki.vpkapuri.Fragments.TimerFragment;
import kultalaaki.vpkapuri.json.FireAlarmJsonWriter;
import kultalaaki.vpkapuri.util.Constants;
import kultalaaki.vpkapuri.util.MyNotifications;


public class FrontpageActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, AffirmationFragment.Listener, FrontpageFragment.OnFragmentInteractionListener, ArchiveFragment.OnFragmentInteractionListener, GuidelineFragment.OnFragmentInteractionListener, SaveToArchiveFragment.OnFragmentInteractionListener, ArchivedAlarmFragment.OnFragmentInteractionListener, TimerFragment.OnFragmentInteractionListener, SetTimerFragment.OnFragmentInteractionListener, TimePickerDialog.OnTimeSetListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS = 3;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DrawerLayout mDrawerLayout;
    String[] emailAddress;
    String emailSubject;
    DBTimer dbTimer;
    SharedPreferences preferences;
    boolean analytics, asemataulu;
    SoundControls soundControls;
    FragmentManager fragmentManager;


    @SuppressLint("NonConstantResourceId")
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
        if (actionbar != null) {
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.ic_dehaze_white_36dp);
        }

        soundControls = new SoundControls();

        mDrawerLayout = findViewById(R.id.drawer_layout);

        // Setting Firebase
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(analytics);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(analytics);

        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            mDrawerLayout.closeDrawers();
            switch (menuItem.getItemId()) {
                case R.id.tallenna_arkistoon_haly:
                    loadTallennaArkistoonFragment();
                    //startTallennaArkistoon();
                    return true;
                case R.id.testaa_haly:
                    showDialog("Hälytyksen testaus! Hälytys tulee 5 sekunnin kuluttua.", "Voit laittaa puhelimen näppäinlukkoon tai poistua sovelluksesta. Älä sammuta sovellusta kokonaan taustalta, silloin sammuu myös ajastin joka lähettää hälytyksen.", "Testaa", "testAlarm");
                    return true;
                case R.id.hiljenna_halyt:
                    if (preferences.getInt("aaneton_profiili", -1) == 1) {
                        showDialog("Hälytysten hiljennys!", "Haluatko varmasti hiljentää hälytykset?", "Kyllä", "setSoundSilent");
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
                    showDialog("Haluatko tallentaa arkistossa olevat hälytykset?", "Tiedosto on avattavissa MS Excel tai jollain muulla ohjelmalla joka tukee .json tiedostoja.", "Kyllä", "saveDatabase");
                    return true;
                case R.id.palautatietokanta:
                    showDialog("Palauta tietokanta.", "Voit palauttaa arkiston .json tiedostosta mikä on tallennettu tästä sovelluksesta.", "Palauta", "returnDatabase");
                    return true;
                case R.id.tyhjennatietokanta:
                    showDialog("Arkiston tyhjentäminen!", "Arkistossa olevat hälytykset poistetaan.\nPoistamisen jälkeen arkistoa ei voida palauttaa.\nOletko varma että haluat poistaa hälytykset?", "Kyllä", "deleteDatabase");
                    return true;
                case R.id.palautetta:
                    startLahetaPalaute();
                    return true;
            }

            return true;
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

    @SuppressLint("SourceLockedOrientationActivity")
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

    public void loadLegalFragment() {
        AffirmationFragment affirmationFragment = new AffirmationFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.etusivuContainer, affirmationFragment, "etusivuLegal").commit();
    }

    public void loadArkistoFragment() {
        ArchiveFragment archiveFragment = new ArchiveFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, archiveFragment, "archiveFragment").commit();
    }

    public void loadSettingsFragment() {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(FrontpageActivity.this);
        Intent intent = new Intent(FrontpageActivity.this, SettingsActivity.class);
        startActivity(intent, options.toBundle());

    }

    public void loadOhjeetFragment() {
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
        FrontpageFragment frontpageFragment = new FrontpageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.etusivuContainer, frontpageFragment, "etusivuNavigation").commit();
    }

    public void loadEtusivuClearingBackstack() {
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FrontpageFragment frontpageFragment = new FrontpageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.etusivuContainer, frontpageFragment, "etusivuNavigation").commit();
    }

    public void loadEtusivuFromFragment() {
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(analytics);
        FrontpageFragment frontpageFragment = new FrontpageFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.etusivuContainer, frontpageFragment, "etusivuNavigation");
        fragmentTransaction.commit();
    }

    public void loadTallennaArkistoonFragment() {
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
        SetTimerFragment setTimerFragment = SetTimerFragment.newInstance(primaryKey);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, setTimerFragment, "setTimerFragment").commit();
    }

    public void openSetTimer() {
        SetTimerFragment setTimerFragment = new SetTimerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, setTimerFragment, "setTimerFragment").commit();
    }

    public void startTimerActivity() {
        TimerFragment timerFragment = new TimerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.etusivuContainer, timerFragment, "timerFragment").commit();
    }

    public long saveTimerToDB(String name, String startTime, String stopTime, String ma, String ti, String ke, String to, String pe, String la, String su, String selector, String isiton) {
        dbTimer = new DBTimer(this);
        long tallennettu = dbTimer.insertData(name, startTime, stopTime, ma, ti, ke, to, pe, la, su, selector, isiton);
        if (tallennettu != -1) {
            showToast("Ajastin", "Tallennettu.");
            //Toast.makeText(getApplicationContext(), "Tallennettu", Toast.LENGTH_LONG).show();
            return tallennettu;
        }
        return -1;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.i("TAG", "OnTimeSet reached");
        SetTimerFragment setTimerFragment = (SetTimerFragment) getSupportFragmentManager().findFragmentByTag("setTimerFragment");
        if (setTimerFragment != null) {
            setTimerFragment.setTimerTimes(hourOfDay, minute);
        }
    }

    public void createChannels() {

        // NotificationChannel alarms
        int importanceHigh = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel alarmChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_ALARM, Constants.NOTIFICATION_CHANNEL_ALARM, importanceHigh);
        alarmChannel.setDescription("Tämän kanavan ilmoitukset ovat hälytyksiä varten.");
        alarmChannel.enableVibration(false);
        alarmChannel.setSound(null, null);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(alarmChannel);
        }

        // NotificationChannel alarms silenced
        int importanceDefault = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel silentChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_SILENCE, Constants.NOTIFICATION_CHANNEL_SILENCE, importanceDefault);
        silentChannel.setDescription("Tämä ilmoituskanava ilmoittaa kun sovelluksesta on hälytykset hiljennetty");
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        silentChannel.setSound(null, null);
        silentChannel.enableVibration(false);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(silentChannel);
        }

        // NotificationChannel active service
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel serviceChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_SERVICE, Constants.NOTIFICATION_CHANNEL_SERVICE, importance);
        serviceChannel.setDescription("Tämä ilmoituskanava on käytössä kun sovelluksen taustapalvelu on käynnissä osoitteen hakua ja hälytysäänen soittamista varten.");
        serviceChannel.enableVibration(false);
        serviceChannel.setSound(null, null);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(serviceChannel);
        }

        // NotificationChannel error
        NotificationChannel mChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL_INFORMATION, Constants.NOTIFICATION_CHANNEL_INFORMATION, NotificationManager.IMPORTANCE_DEFAULT);
        mChannel.setDescription("Tämä ilmoituskanava on käytössä kun sovelluus ilmoittaa jostain virheestä.");
        mChannel.enableVibration(false);
        mChannel.setSound(null, null);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(mChannel);
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

        preferences.edit().putString("halyvastaanotto11", "0401234567").apply();
        Handler handler1 = new Handler();
        handler1.postDelayed(() -> {
            long aika = System.currentTimeMillis();
            String Aika = (String) DateFormat.format("EEE, dd.MMM yyyy, H:mm:ss", new Date(aika));
            String timeToMessage = (String) DateFormat.format("H:mm:ss_dd.MM.yyyy", new Date(aika));
            Intent halyaaniService = new Intent(getApplicationContext(), SMSBackgroundService.class);
            String alarmMessage = getString(R.string.testihalytysEricaEtuosa) + " " + timeToMessage + getString(R.string.testihalytysEricaTakaosa);
            halyaaniService.putExtra("message", alarmMessage);
            halyaaniService.putExtra("number", "0401234567");
            halyaaniService.putExtra("halytysaani", "false");
            halyaaniService.putExtra("timestamp", Aika);
            getApplicationContext().startService(halyaaniService);
        }, 5000);

    }

    public void startLahetaPalaute() {
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

    public void askPermissionReadExternalStorage() {
        if (ContextCompat.checkSelfPermission(FrontpageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Second time asking. Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FrontpageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showDialog("VPK Apuri pyytää lupaa käyttää laitteellasi olevia kuvia ja mediaa.", "Tämä lupa tarvitaan hälytysäänen asettamiseksi. Ilman tätä lupaa sovellus ei voi asettaa hälytysääntä. Pääsy asetuksiin on estetty kunnes lupa on myönnetty.", "Anna lupa", "askPermission");
            } else {
                // First time asking. No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(FrontpageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS);
            }
        } else {
            // We have permission
            loadSettingsFragment();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Lupa annettu mene asetuksiin
                loadSettingsFragment();
            } else {
                // ei lupaa. 1. kielto tulee tänne
                showDialog("Et antanut lupaa käyttää laitteellasi olevia kuvia ja mediaa.", "Pääsy sovelluksen asetuksiin on estetty kunnes annat luvan käyttää laitteellasi olevia kuvia ja mediaa.", "OK");
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

        buttonPositive.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void showDialog(String upperText, String lowerText, String positiveButtonText, String chooser) {
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
        buttonNegative.setText("Peruuta");

        switch (chooser) {
            case "testAlarm":
                buttonPositive.setOnClickListener(v -> {
                    testAlarm();
                    dialog.dismiss();
                });
                buttonNegative.setOnClickListener(v -> dialog.dismiss());
                break;
            case "setSoundSilent":
                buttonPositive.setOnClickListener(v -> {
                    setSoundSilent();
                    dialog.dismiss();
                });
                buttonNegative.setOnClickListener(v -> dialog.dismiss());
                break;
            case "askPermission":
                buttonPositive.setOnClickListener(v -> {
                    ActivityCompat.requestPermissions(FrontpageActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_SETTINGS);
                    dialog.dismiss();
                });
                buttonNegative.setOnClickListener(v -> dialog.dismiss());
                break;
            case "saveDatabase":
                buttonPositive.setOnClickListener(v -> {
                    saveDatabaseBackup();
                    dialog.dismiss();
                });
                buttonNegative.setOnClickListener(v -> dialog.dismiss());
                break;
            case "returnDatabase":
                buttonPositive.setOnClickListener(v -> {
                    openFile();
                    dialog.dismiss();
                });
                buttonNegative.setOnClickListener(v -> dialog.dismiss());
                break;
            case "deleteDatabase":
                buttonPositive.setOnClickListener(v -> {
                    deleteDatabase();
                    dialog.dismiss();
                });
                buttonNegative.setOnClickListener(v -> dialog.dismiss());
        }


        dialog.show();
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(null);
    }

    public void showToast(String headText, String toastText) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        TextView head = layout.findViewById(R.id.head_text);
        head.setText(headText);
        TextView toastMessage = layout.findViewById(R.id.toast_text);
        toastMessage.setText(toastText);

        Toast toast = new Toast(getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    private void saveDatabaseBackup() {
        if (isExternalStorageWritable()) {
            try {
                File file = getAlbumStorageDir("VPK Apuri", "Halytykset_tietokanta.json");
                FileOutputStream fos = new FileOutputStream(file);
                FireAlarmJsonWriter fireAlarmJsonWriter = new FireAlarmJsonWriter();
                FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
                fireAlarmJsonWriter.writeJsonStream(fos, fireAlarmRepository.getAllFireAlarmsToList());
                fos.close();
                Toast.makeText(this, "Tiedosto on tallennettu puhelimen muistiin. Dokumentit -> VPK Apuri", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
                crashlytics.log("IOException: " + e);
                MyNotifications notifications = new MyNotifications(this);
                notifications.showInformationNotification("Virhe tietokannan tallennuksessa.");
            }
        }
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public File getAlbumStorageDir(String albumName, String fileName) {
        // Get the directory for the user's public directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            Log.e("VPK Apuri", "Tiedostopolkua ei ollut. Luodaan uusi tiedostopolku.");
        }

        return new File(file, fileName);
    }

    // Request code for selecting a json file.
    private static final int PICK_JSON_FILE = 2;

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");

        startActivityForResult(intent, PICK_JSON_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);

        if (requestCode == PICK_JSON_FILE && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            Uri uri = null;
            String actualfilepath = "";

            if (resultData != null) {
                uri = resultData.getData();
                InputStream stream = null;
                String tempID = "", id = "";

                assert uri != null;
                if (Objects.equals(uri.getAuthority(), "com.android.externalstorage.documents")) {
                    tempID = DocumentsContract.getDocumentId(uri);
                    String[] split = tempID.split(":");
                    String type = split[0];
                    id = split[1];
                    if (type.equals("primary")) {
                        actualfilepath = Environment.getExternalStorageDirectory() + "/" + id;
                    }
                }

                // Perform operations on the document using its URI.
                // Todo: Open file and read content
                try {
                    //File file = new File(actualfilepath);
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(actualfilepath));
                    String line;
                    StringBuilder result = new StringBuilder();

                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line);
                    }
                    bufferedReader.close();
                    Log.i("Result of backup read: ", result.toString());
                } catch (NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void deleteDatabase() {
        FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
        fireAlarmRepository.deleteAllFireAlarms();
        //Toast.makeText(this, "Arkisto tyhjennetty.", Toast.LENGTH_LONG).show();
        showToast("Arkisto", "Arkisto tyhjennetty!");
    }


    private class WhatsNewScreen {
        private static final String LOG_TAG = "WhatsNewScreen";

        private static final String LAST_VERSION_CODE_KEY = "last_version_code";

        private final Activity mActivity;

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
                final int versionCode = packageInfo.versionCode;
                if (versionCode != lastVersionCode) {

                    if (prefs.getBoolean("firstrun", true)) {
                        prefs.edit().putInt("aaneton_profiili", 1).commit();
                        prefs.edit().putBoolean("firstrun", false).commit();
                    }
                    // App updated, add alarmdetection to database
                    FireAlarmRepository fireAlarmRepository = new FireAlarmRepository(getApplication());
                    FireAlarm fireAlarm = new FireAlarm("999", "C", "Uusi asennus tai sovellus on päivitetty.", "Ei osoitetta", "", "", "", "", "", "", "");
                    fireAlarmRepository.insert(fireAlarm);

                    // Delete app cache to prevent unnecessary mistakes.
                    deleteCache(getApplicationContext());
                    Log.i(LOG_TAG, "versionCode " + versionCode + "is different from the last known version " + lastVersionCode);

                    final String title = mActivity.getString(R.string.app_name) + " v" + packageInfo.versionName;

                    final String message = mActivity.getString(R.string.onlyNewest);

                    // Show the News since last version
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                        // Mark this version as read
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putLong(LAST_VERSION_CODE_KEY, versionCode);
                        editor.apply();
                        //showTiewtosuojaAfterWhatsnew();
                        dialogInterface.dismiss();
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
                Log.i("VPK Apuri", "Välimuistin tyhjennys epäonnistui.");
            }
        }

        boolean deleteDir(File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                assert children != null;
                for (String aChildren : children) {
                    boolean success = deleteDir(new File(dir, aChildren));
                    if (!success) {
                        return false;
                    }
                }
                return dir.delete();
            } else if (dir != null && dir.isFile()) {
                return dir.delete();
            } else {
                return false;
            }
        }
    }
}