/*
 * Created by Kultala Aki on 3/6/21 12:26 PM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 3/6/21 12:22 PM
 */

package kultalaaki.vpkapuri;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import kultalaaki.vpkapuri.misc.SeekBarPreference;
import kultalaaki.vpkapuri.util.Constants;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list pref_sounds, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone pref_sounds, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other pref_sounds, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setupActionBar();
    }

    /*
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    */
    //Oman menun testailu


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName)
                || AsematauluPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general pref_sounds only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_numbers_sms);

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone pref_sounds
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("sms_numero"));
            bindPreferenceSummaryToValue(findPreference("sms_numero10"));
            bindPreferenceSummaryToValue(findPreference("sms_numero11"));
            bindPreferenceSummaryToValue(findPreference("fivemintextotsikko"));
            bindPreferenceSummaryToValue(findPreference("fivemintxt"));
            bindPreferenceSummaryToValue(findPreference("tenmintextotsikko"));
            bindPreferenceSummaryToValue(findPreference("tenmintxt"));
            bindPreferenceSummaryToValue(findPreference("tenplusmintextotsikko"));
            bindPreferenceSummaryToValue(findPreference("tenplusmintxt"));
            //bindPreferenceSummaryToValue(findPreference("example_list"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto1"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto2"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto3"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto4"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto5"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto6"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto7"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto8"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto9"));
            bindPreferenceSummaryToValue(findPreference("halyvastaanotto10"));
            bindPreferenceSummaryToValue(findPreference("unit1"));
            bindPreferenceSummaryToValue(findPreference("unit2"));
            bindPreferenceSummaryToValue(findPreference("unit3"));
            bindPreferenceSummaryToValue(findPreference("unit4"));
            bindPreferenceSummaryToValue(findPreference("unit5"));
            bindPreferenceSummaryToValue(findPreference("unit6"));
            bindPreferenceSummaryToValue(findPreference("unit7"));
            bindPreferenceSummaryToValue(findPreference("unit8"));
            bindPreferenceSummaryToValue(findPreference("unit9"));
            bindPreferenceSummaryToValue(findPreference("unit10"));
            bindPreferenceSummaryToValue(findPreference("AlarmCounterTime"));

            bindPreferenceSummaryToValue(findPreference("vapepanumber1"));
            bindPreferenceSummaryToValue(findPreference("vapepanumber2"));
            bindPreferenceSummaryToValue(findPreference("vapepanumber3"));
            bindPreferenceSummaryToValue(findPreference("vapepanumber4"));
            bindPreferenceSummaryToValue(findPreference("vapepanumber5"));

            /*
            Preference button = findPreference(getString(R.string.Aseta_numerot));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //code for what you want it to do
                    ArrayList <String> numerot = new ArrayList<>();
                    numerot.add(findPreference("halyvastaanotto1").toString());
                    numerot.add(findPreference("halyvastaanotto2").toString());
                    numerot.add(findPreference("halyvastaanotto3").toString());
                    numerot.add(findPreference("halyvastaanotto4").toString());
                    numerot.add(findPreference("halyvastaanotto5").toString());
                    numerot.add(findPreference("halyvastaanotto6").toString());
                    numerot.add(findPreference("halyvastaanotto7").toString());
                    numerot.add(findPreference("halyvastaanotto8").toString());
                    numerot.add(findPreference("halyvastaanotto9").toString());
                    numerot.add(findPreference("halyvastaanotto10").toString());
                    Toast.makeText(getActivity(), "KIITOS! ",
                            Toast.LENGTH_LONG).show();
                    return true;
                }
            });*/
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification pref_sounds only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class NotificationPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SeekBarPreference _seekBarPref;
        Vibrator viber;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_notification);
            addPreferencesFromResource(R.xml.pref_sounds);
            viber = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

            //Do Not Disturb allowed if menu doesn't open
            Preference pref = getPreferenceManager().findPreference("DoNotDisturb");
            if (pref != null) {
                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        NotificationManager notificationManager =
                                (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        if (notificationManager != null) {
                            if (!notificationManager.isNotificationPolicyAccessGranted()) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                startActivity(intent);
                                return true;
                            }
                        }

                        return false;
                    }
                });
            }

            _seekBarPref = (SeekBarPreference) this.findPreference("SEEKBAR_VALUE");
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);
            _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", "" + radius));

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone pref_sounds
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
            bindPreferenceSummaryToValue(findPreference("ringtone_rescue"));
            bindPreferenceSummaryToValue(findPreference("stopTime"));
            bindPreferenceSummaryToValue(findPreference("vibrate_pattern"));


        }

        public long[] genVibratorPattern(float intensity, long duration) {
            float dutyCycle = Math.abs((intensity * 2.0f) - 1.0f);
            long hWidth = (long) (dutyCycle * (duration - 1)) + 1;
            long lWidth = dutyCycle == 1.0f ? 0 : 1;

            int pulseCount = (int) (2.0f * ((float) duration / (float) (hWidth + lWidth)));
            long[] pattern = new long[pulseCount];

            for (int i = 0; i < pulseCount; i++) {
                pattern[i] = intensity < 0.5f ? (i % 2 == 0 ? hWidth : lWidth) : (i % 2 == 0 ? lWidth : hWidth);
            }

            return pattern;
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Set seekbar summary :
            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            //int rad = sharedPreferences.getInt("SEEKBAR_VALUE", 50);
            if (key.equals("vibrate_pattern")) {

                ListPreference vibratePattern = (ListPreference) findPreference("vibrate_pattern");
                if (vibratePattern != null) {
                    //CharSequence vibrateText = vibratePattern.getEntry();
                    //String vibrateValue = vibratePattern.getValue();
                    int vibrateValue = Integer.parseInt(vibratePattern.getValue());
                    long[] pattern = new long[]{};
                    int[] amplitude = new int[]{};

                    if (vibrateValue == 0) {
                        pattern = Constants.PULSE_PATTERN;
                        amplitude = Constants.PULSE_AMPLITUDE;
                    } else if (vibrateValue == 1) {
                        pattern = Constants.HURRY_PATTERN;
                        amplitude = Constants.HURRY_AMPLITUDE;
                    } else if (vibrateValue == 2) {
                        pattern = Constants.SLOW_PATTERN;
                        amplitude = Constants.SLOW_AMPLITUDE;
                    } else if (vibrateValue == 3) {
                        pattern = Constants.SOS_PATTERN;
                        amplitude = Constants.SOS_AMPLITUDE;
                    } else if (vibrateValue == 4) {
                        pattern = Constants.VIRVE_PATTERN;
                        amplitude = Constants.VIRVE_AMPLITUDE;
                    }
                    if (viber != null && viber.hasVibrator()) {
                        if (viber.hasAmplitudeControl()) {
                            viber.vibrate(VibrationEffect.createWaveform(pattern, amplitude, -1));
                        } else {
                            viber.vibrate(VibrationEffect.createWaveform(pattern, -1));
                        }
                    }
                }
            }
            Activity activity = getActivity();
            if (activity != null) {
                int radius = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).getInt("SEEKBAR_VALUE", 50);
                _seekBarPref.setSummary(this.getString(R.string.settings_summary).replace("$1", "" + radius));
            }

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync pref_sounds only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class DataSyncPreferenceFragment extends PreferenceFragment {

        private final String[] permissionsSms = {
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.REQUEST_COMPANION_START_FOREGROUND_SERVICES_FROM_BACKGROUND};

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_permission);

            //Battery optimization
            Preference prefi = getPreferenceManager().findPreference("batteryOptimization");
            if (prefi != null) {
                prefi.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                        startActivity(intent);
                        return true;

                    }
                });
            }

            //Opening app from background
            Preference prefOpenWindow = getPreferenceManager().findPreference("windowOperation");
            if (prefOpenWindow != null) {
                prefOpenWindow.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            startActivity(intent);
                            return true;
                        }

                        return false;
                    }
                });
            } else {
                Activity activity = getActivity();
                Toast.makeText(activity, "Tämä asetus on vain Android 10 ja uudemmille.", Toast.LENGTH_SHORT).show();
            }

            Preference prefTietosuoja = getPreferenceManager().findPreference("tietosuoja");
            if (prefTietosuoja != null) {
                prefTietosuoja.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Avaa nettisivu tietosuoja
                        String url = "https://kultalaaki.github.io/VPKApuri/tietosuoja.html";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                });
            }

            Preference prefKayttoehdot = getPreferenceManager().findPreference("kayttoehdot");
            if (prefKayttoehdot != null) {
                prefKayttoehdot.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Avaa nettisivu käyttöehdot
                        String url = "https://kultalaaki.github.io/VPKApuri/terms.html";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                });
            }

            Preference prefFacebook = getPreferenceManager().findPreference("facebook");
            if (prefFacebook != null) {
                prefFacebook.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Avaa nettisivu käyttöehdot
                        String url = "http://www.facebook.com/VPKApuri";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                });
            }

            /*final Preference analytics = getPreferenceManager().findPreference("analyticsEnabled");
            if(analytics != null) {
                analytics.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if(analytics.toString().equals("true")) {
                            FirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                        }

                        return false;
                    }
                });
            }*/

            //Sovelluksen luvat
            Preference prefa = getPreferenceManager().findPreference("luvatApuri");
            if (prefa != null) {
                prefa.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        return true;

                    }
                });
            }

            //Sovelluksen luvat SMS hälytyksiä varten
            Preference prefsms = getPreferenceManager().findPreference("luvatSMS");
            if (prefsms != null) {
                prefsms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (!arePermissionsEnabled()) {
                            // permissions granted, continue flow normally
                            requestMultiplePermissions();
                        }//else{
                        //  requestMultiplePermissions();
                        //}
                        return true;
                    }
                });
            }

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone pref_sounds
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        private boolean arePermissionsEnabled() {
            for (String permission : permissionsSms) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            return true;
        }

        private void requestMultiplePermissions() {
            List<String> remainingPermissions = new ArrayList<>();
            for (String permission : permissionsSms) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    remainingPermissions.add(permission);
                }
            }
            requestPermissions(remainingPermissions.toArray(new String[0]), 101);
            // testataan ylempää
            //requestPermissions(remainingPermissions.toArray(new String[remainingPermissions.size()]), 101);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if (requestCode == 101) {
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(permissions[i])) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage("Annoitko tarvittavat luvat?")
                                    .setPositiveButton("Näytä uudestaan", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            requestMultiplePermissions();
                                        }
                                    })
                                    .setNegativeButton("Valmis", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                        return;
                    }
                }
                //all is good, continue flow
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    public static class AsematauluPreferenceFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_asemataulu);

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone pref_sounds
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("nimi1"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero1"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero1"));
            bindPreferenceSummaryToValue(findPreference("optional1_1"));
            bindPreferenceSummaryToValue(findPreference("optional2_1"));
            bindPreferenceSummaryToValue(findPreference("optional3_1"));
            bindPreferenceSummaryToValue(findPreference("optional4_1"));
            bindPreferenceSummaryToValue(findPreference("optional5_1"));

            bindPreferenceSummaryToValue(findPreference("nimi2"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero2"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero2"));
            bindPreferenceSummaryToValue(findPreference("optional1_2"));
            bindPreferenceSummaryToValue(findPreference("optional2_2"));
            bindPreferenceSummaryToValue(findPreference("optional3_2"));
            bindPreferenceSummaryToValue(findPreference("optional4_2"));
            bindPreferenceSummaryToValue(findPreference("optional5_2"));

            bindPreferenceSummaryToValue(findPreference("nimi3"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero3"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero3"));
            bindPreferenceSummaryToValue(findPreference("optional1_3"));
            bindPreferenceSummaryToValue(findPreference("optional2_3"));
            bindPreferenceSummaryToValue(findPreference("optional3_3"));
            bindPreferenceSummaryToValue(findPreference("optional4_3"));
            bindPreferenceSummaryToValue(findPreference("optional5_3"));

            bindPreferenceSummaryToValue(findPreference("nimi4"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero4"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero4"));
            bindPreferenceSummaryToValue(findPreference("optional1_4"));
            bindPreferenceSummaryToValue(findPreference("optional2_4"));
            bindPreferenceSummaryToValue(findPreference("optional3_4"));
            bindPreferenceSummaryToValue(findPreference("optional4_4"));
            bindPreferenceSummaryToValue(findPreference("optional5_4"));

            bindPreferenceSummaryToValue(findPreference("nimi5"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero5"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero5"));
            bindPreferenceSummaryToValue(findPreference("optional1_5"));
            bindPreferenceSummaryToValue(findPreference("optional2_5"));
            bindPreferenceSummaryToValue(findPreference("optional3_5"));
            bindPreferenceSummaryToValue(findPreference("optional4_5"));
            bindPreferenceSummaryToValue(findPreference("optional5_5"));

            bindPreferenceSummaryToValue(findPreference("nimi6"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero6"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero6"));
            bindPreferenceSummaryToValue(findPreference("optional1_6"));
            bindPreferenceSummaryToValue(findPreference("optional2_6"));
            bindPreferenceSummaryToValue(findPreference("optional3_6"));
            bindPreferenceSummaryToValue(findPreference("optional4_6"));
            bindPreferenceSummaryToValue(findPreference("optional5_6"));

            bindPreferenceSummaryToValue(findPreference("nimi7"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero7"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero7"));
            bindPreferenceSummaryToValue(findPreference("optional1_7"));
            bindPreferenceSummaryToValue(findPreference("optional2_7"));
            bindPreferenceSummaryToValue(findPreference("optional3_7"));
            bindPreferenceSummaryToValue(findPreference("optional4_7"));
            bindPreferenceSummaryToValue(findPreference("optional5_7"));

            bindPreferenceSummaryToValue(findPreference("nimi8"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero8"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero8"));
            bindPreferenceSummaryToValue(findPreference("optional1_8"));
            bindPreferenceSummaryToValue(findPreference("optional2_8"));
            bindPreferenceSummaryToValue(findPreference("optional3_8"));
            bindPreferenceSummaryToValue(findPreference("optional4_8"));
            bindPreferenceSummaryToValue(findPreference("optional5_8"));

            bindPreferenceSummaryToValue(findPreference("nimi9"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero9"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero9"));
            bindPreferenceSummaryToValue(findPreference("optional1_9"));
            bindPreferenceSummaryToValue(findPreference("optional2_9"));
            bindPreferenceSummaryToValue(findPreference("optional3_9"));
            bindPreferenceSummaryToValue(findPreference("optional4_9"));
            bindPreferenceSummaryToValue(findPreference("optional5_9"));

            bindPreferenceSummaryToValue(findPreference("nimi10"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero10"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero10"));
            bindPreferenceSummaryToValue(findPreference("optional1_10"));
            bindPreferenceSummaryToValue(findPreference("optional2_10"));
            bindPreferenceSummaryToValue(findPreference("optional3_10"));
            bindPreferenceSummaryToValue(findPreference("optional4_10"));
            bindPreferenceSummaryToValue(findPreference("optional5_10"));

            bindPreferenceSummaryToValue(findPreference("nimi11"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero11"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero11"));
            bindPreferenceSummaryToValue(findPreference("optional1_11"));
            bindPreferenceSummaryToValue(findPreference("optional2_11"));
            bindPreferenceSummaryToValue(findPreference("optional3_11"));
            bindPreferenceSummaryToValue(findPreference("optional4_11"));
            bindPreferenceSummaryToValue(findPreference("optional5_11"));

            bindPreferenceSummaryToValue(findPreference("nimi12"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero12"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero12"));
            bindPreferenceSummaryToValue(findPreference("optional1_12"));
            bindPreferenceSummaryToValue(findPreference("optional2_12"));
            bindPreferenceSummaryToValue(findPreference("optional3_12"));
            bindPreferenceSummaryToValue(findPreference("optional4_12"));
            bindPreferenceSummaryToValue(findPreference("optional5_12"));

            bindPreferenceSummaryToValue(findPreference("nimi13"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero13"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero13"));
            bindPreferenceSummaryToValue(findPreference("optional1_13"));
            bindPreferenceSummaryToValue(findPreference("optional2_13"));
            bindPreferenceSummaryToValue(findPreference("optional3_13"));
            bindPreferenceSummaryToValue(findPreference("optional4_13"));
            bindPreferenceSummaryToValue(findPreference("optional5_13"));

            bindPreferenceSummaryToValue(findPreference("nimi14"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero14"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero14"));
            bindPreferenceSummaryToValue(findPreference("optional1_14"));
            bindPreferenceSummaryToValue(findPreference("optional2_14"));
            bindPreferenceSummaryToValue(findPreference("optional3_14"));
            bindPreferenceSummaryToValue(findPreference("optional4_14"));
            bindPreferenceSummaryToValue(findPreference("optional5_14"));

            bindPreferenceSummaryToValue(findPreference("nimi15"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero15"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero15"));
            bindPreferenceSummaryToValue(findPreference("optional1_15"));
            bindPreferenceSummaryToValue(findPreference("optional2_15"));
            bindPreferenceSummaryToValue(findPreference("optional3_15"));
            bindPreferenceSummaryToValue(findPreference("optional4_15"));
            bindPreferenceSummaryToValue(findPreference("optional5_15"));

            bindPreferenceSummaryToValue(findPreference("nimi16"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero16"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero16"));
            bindPreferenceSummaryToValue(findPreference("optional1_16"));
            bindPreferenceSummaryToValue(findPreference("optional2_16"));
            bindPreferenceSummaryToValue(findPreference("optional3_16"));
            bindPreferenceSummaryToValue(findPreference("optional4_16"));
            bindPreferenceSummaryToValue(findPreference("optional5_16"));

            bindPreferenceSummaryToValue(findPreference("nimi17"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero17"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero17"));
            bindPreferenceSummaryToValue(findPreference("optional1_17"));
            bindPreferenceSummaryToValue(findPreference("optional2_17"));
            bindPreferenceSummaryToValue(findPreference("optional3_17"));
            bindPreferenceSummaryToValue(findPreference("optional4_17"));
            bindPreferenceSummaryToValue(findPreference("optional5_17"));

            bindPreferenceSummaryToValue(findPreference("nimi18"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero18"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero18"));
            bindPreferenceSummaryToValue(findPreference("optional1_18"));
            bindPreferenceSummaryToValue(findPreference("optional2_18"));
            bindPreferenceSummaryToValue(findPreference("optional3_18"));
            bindPreferenceSummaryToValue(findPreference("optional4_18"));
            bindPreferenceSummaryToValue(findPreference("optional5_18"));

            bindPreferenceSummaryToValue(findPreference("nimi19"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero19"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero19"));
            bindPreferenceSummaryToValue(findPreference("optional1_19"));
            bindPreferenceSummaryToValue(findPreference("optional2_19"));
            bindPreferenceSummaryToValue(findPreference("optional3_19"));
            bindPreferenceSummaryToValue(findPreference("optional4_19"));
            bindPreferenceSummaryToValue(findPreference("optional5_19"));

            bindPreferenceSummaryToValue(findPreference("nimi20"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero20"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero20"));
            bindPreferenceSummaryToValue(findPreference("optional1_20"));
            bindPreferenceSummaryToValue(findPreference("optional2_20"));
            bindPreferenceSummaryToValue(findPreference("optional3_20"));
            bindPreferenceSummaryToValue(findPreference("optional4_20"));
            bindPreferenceSummaryToValue(findPreference("optional5_20"));

            bindPreferenceSummaryToValue(findPreference("nimi21"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero21"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero21"));
            bindPreferenceSummaryToValue(findPreference("optional1_21"));
            bindPreferenceSummaryToValue(findPreference("optional2_21"));
            bindPreferenceSummaryToValue(findPreference("optional3_21"));
            bindPreferenceSummaryToValue(findPreference("optional4_21"));
            bindPreferenceSummaryToValue(findPreference("optional5_21"));

            bindPreferenceSummaryToValue(findPreference("nimi22"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero22"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero22"));
            bindPreferenceSummaryToValue(findPreference("optional1_22"));
            bindPreferenceSummaryToValue(findPreference("optional2_22"));
            bindPreferenceSummaryToValue(findPreference("optional3_22"));
            bindPreferenceSummaryToValue(findPreference("optional4_22"));
            bindPreferenceSummaryToValue(findPreference("optional5_22"));

            bindPreferenceSummaryToValue(findPreference("nimi23"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero23"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero23"));
            bindPreferenceSummaryToValue(findPreference("optional1_23"));
            bindPreferenceSummaryToValue(findPreference("optional2_23"));
            bindPreferenceSummaryToValue(findPreference("optional3_23"));
            bindPreferenceSummaryToValue(findPreference("optional4_23"));
            bindPreferenceSummaryToValue(findPreference("optional5_23"));

            bindPreferenceSummaryToValue(findPreference("nimi24"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero24"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero24"));
            bindPreferenceSummaryToValue(findPreference("optional1_24"));
            bindPreferenceSummaryToValue(findPreference("optional2_24"));
            bindPreferenceSummaryToValue(findPreference("optional3_24"));
            bindPreferenceSummaryToValue(findPreference("optional4_24"));
            bindPreferenceSummaryToValue(findPreference("optional5_24"));

            bindPreferenceSummaryToValue(findPreference("nimi25"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero25"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero25"));
            bindPreferenceSummaryToValue(findPreference("optional1_25"));
            bindPreferenceSummaryToValue(findPreference("optional2_25"));
            bindPreferenceSummaryToValue(findPreference("optional3_25"));
            bindPreferenceSummaryToValue(findPreference("optional4_25"));
            bindPreferenceSummaryToValue(findPreference("optional5_25"));

            bindPreferenceSummaryToValue(findPreference("nimi26"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero26"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero26"));
            bindPreferenceSummaryToValue(findPreference("optional1_26"));
            bindPreferenceSummaryToValue(findPreference("optional2_26"));
            bindPreferenceSummaryToValue(findPreference("optional3_26"));
            bindPreferenceSummaryToValue(findPreference("optional4_26"));
            bindPreferenceSummaryToValue(findPreference("optional5_26"));

            bindPreferenceSummaryToValue(findPreference("nimi27"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero27"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero27"));
            bindPreferenceSummaryToValue(findPreference("optional1_27"));
            bindPreferenceSummaryToValue(findPreference("optional2_27"));
            bindPreferenceSummaryToValue(findPreference("optional3_27"));
            bindPreferenceSummaryToValue(findPreference("optional4_27"));
            bindPreferenceSummaryToValue(findPreference("optional5_27"));

            bindPreferenceSummaryToValue(findPreference("nimi28"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero28"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero28"));
            bindPreferenceSummaryToValue(findPreference("optional1_28"));
            bindPreferenceSummaryToValue(findPreference("optional2_28"));
            bindPreferenceSummaryToValue(findPreference("optional3_28"));
            bindPreferenceSummaryToValue(findPreference("optional4_28"));
            bindPreferenceSummaryToValue(findPreference("optional5_28"));

            bindPreferenceSummaryToValue(findPreference("nimi29"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero29"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero29"));
            bindPreferenceSummaryToValue(findPreference("optional1_29"));
            bindPreferenceSummaryToValue(findPreference("optional2_29"));
            bindPreferenceSummaryToValue(findPreference("optional3_29"));
            bindPreferenceSummaryToValue(findPreference("optional4_29"));
            bindPreferenceSummaryToValue(findPreference("optional5_29"));

            bindPreferenceSummaryToValue(findPreference("nimi30"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero30"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero30"));
            bindPreferenceSummaryToValue(findPreference("optional1_30"));
            bindPreferenceSummaryToValue(findPreference("optional2_30"));
            bindPreferenceSummaryToValue(findPreference("optional3_30"));
            bindPreferenceSummaryToValue(findPreference("optional4_30"));
            bindPreferenceSummaryToValue(findPreference("optional5_30"));

            bindPreferenceSummaryToValue(findPreference("nimi31"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero31"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero31"));
            bindPreferenceSummaryToValue(findPreference("optional1_31"));
            bindPreferenceSummaryToValue(findPreference("optional2_31"));
            bindPreferenceSummaryToValue(findPreference("optional3_31"));
            bindPreferenceSummaryToValue(findPreference("optional4_31"));
            bindPreferenceSummaryToValue(findPreference("optional5_31"));

            bindPreferenceSummaryToValue(findPreference("nimi32"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero32"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero32"));
            bindPreferenceSummaryToValue(findPreference("optional1_32"));
            bindPreferenceSummaryToValue(findPreference("optional2_32"));
            bindPreferenceSummaryToValue(findPreference("optional3_32"));
            bindPreferenceSummaryToValue(findPreference("optional4_32"));
            bindPreferenceSummaryToValue(findPreference("optional5_32"));

            bindPreferenceSummaryToValue(findPreference("nimi33"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero33"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero33"));
            bindPreferenceSummaryToValue(findPreference("optional1_33"));
            bindPreferenceSummaryToValue(findPreference("optional2_33"));
            bindPreferenceSummaryToValue(findPreference("optional3_33"));
            bindPreferenceSummaryToValue(findPreference("optional4_33"));
            bindPreferenceSummaryToValue(findPreference("optional5_33"));

            bindPreferenceSummaryToValue(findPreference("nimi34"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero34"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero34"));
            bindPreferenceSummaryToValue(findPreference("optional1_34"));
            bindPreferenceSummaryToValue(findPreference("optional2_34"));
            bindPreferenceSummaryToValue(findPreference("optional3_34"));
            bindPreferenceSummaryToValue(findPreference("optional4_34"));
            bindPreferenceSummaryToValue(findPreference("optional5_34"));

            bindPreferenceSummaryToValue(findPreference("nimi35"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero35"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero35"));
            bindPreferenceSummaryToValue(findPreference("optional1_35"));
            bindPreferenceSummaryToValue(findPreference("optional2_35"));
            bindPreferenceSummaryToValue(findPreference("optional3_35"));
            bindPreferenceSummaryToValue(findPreference("optional4_35"));
            bindPreferenceSummaryToValue(findPreference("optional5_35"));

            bindPreferenceSummaryToValue(findPreference("nimi36"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero36"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero36"));
            bindPreferenceSummaryToValue(findPreference("optional1_36"));
            bindPreferenceSummaryToValue(findPreference("optional2_36"));
            bindPreferenceSummaryToValue(findPreference("optional3_36"));
            bindPreferenceSummaryToValue(findPreference("optional4_36"));
            bindPreferenceSummaryToValue(findPreference("optional5_36"));

            bindPreferenceSummaryToValue(findPreference("nimi37"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero37"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero37"));
            bindPreferenceSummaryToValue(findPreference("optional1_37"));
            bindPreferenceSummaryToValue(findPreference("optional2_37"));
            bindPreferenceSummaryToValue(findPreference("optional3_37"));
            bindPreferenceSummaryToValue(findPreference("optional4_37"));
            bindPreferenceSummaryToValue(findPreference("optional5_37"));

            bindPreferenceSummaryToValue(findPreference("nimi38"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero38"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero38"));
            bindPreferenceSummaryToValue(findPreference("optional1_38"));
            bindPreferenceSummaryToValue(findPreference("optional2_38"));
            bindPreferenceSummaryToValue(findPreference("optional3_38"));
            bindPreferenceSummaryToValue(findPreference("optional4_38"));
            bindPreferenceSummaryToValue(findPreference("optional5_38"));

            bindPreferenceSummaryToValue(findPreference("nimi39"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero39"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero39"));
            bindPreferenceSummaryToValue(findPreference("optional1_39"));
            bindPreferenceSummaryToValue(findPreference("optional2_39"));
            bindPreferenceSummaryToValue(findPreference("optional3_39"));
            bindPreferenceSummaryToValue(findPreference("optional4_39"));
            bindPreferenceSummaryToValue(findPreference("optional5_39"));

            bindPreferenceSummaryToValue(findPreference("nimi40"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero40"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero40"));
            bindPreferenceSummaryToValue(findPreference("optional1_40"));
            bindPreferenceSummaryToValue(findPreference("optional2_40"));
            bindPreferenceSummaryToValue(findPreference("optional3_40"));
            bindPreferenceSummaryToValue(findPreference("optional4_40"));
            bindPreferenceSummaryToValue(findPreference("optional5_40"));

            bindPreferenceSummaryToValue(findPreference("nimi41"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero41"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero41"));
            bindPreferenceSummaryToValue(findPreference("optional1_41"));
            bindPreferenceSummaryToValue(findPreference("optional2_41"));
            bindPreferenceSummaryToValue(findPreference("optional3_41"));
            bindPreferenceSummaryToValue(findPreference("optional4_41"));
            bindPreferenceSummaryToValue(findPreference("optional5_41"));

            bindPreferenceSummaryToValue(findPreference("nimi42"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero42"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero42"));
            bindPreferenceSummaryToValue(findPreference("optional1_42"));
            bindPreferenceSummaryToValue(findPreference("optional2_42"));
            bindPreferenceSummaryToValue(findPreference("optional3_42"));
            bindPreferenceSummaryToValue(findPreference("optional4_42"));
            bindPreferenceSummaryToValue(findPreference("optional5_42"));

            bindPreferenceSummaryToValue(findPreference("nimi43"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero43"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero43"));
            bindPreferenceSummaryToValue(findPreference("optional1_43"));
            bindPreferenceSummaryToValue(findPreference("optional2_43"));
            bindPreferenceSummaryToValue(findPreference("optional3_43"));
            bindPreferenceSummaryToValue(findPreference("optional4_43"));
            bindPreferenceSummaryToValue(findPreference("optional5_43"));

            bindPreferenceSummaryToValue(findPreference("nimi44"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero44"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero44"));
            bindPreferenceSummaryToValue(findPreference("optional1_44"));
            bindPreferenceSummaryToValue(findPreference("optional2_44"));
            bindPreferenceSummaryToValue(findPreference("optional3_44"));
            bindPreferenceSummaryToValue(findPreference("optional4_44"));
            bindPreferenceSummaryToValue(findPreference("optional5_44"));

            bindPreferenceSummaryToValue(findPreference("nimi45"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero45"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero45"));
            bindPreferenceSummaryToValue(findPreference("optional1_45"));
            bindPreferenceSummaryToValue(findPreference("optional2_45"));
            bindPreferenceSummaryToValue(findPreference("optional3_45"));
            bindPreferenceSummaryToValue(findPreference("optional4_45"));
            bindPreferenceSummaryToValue(findPreference("optional5_45"));

            bindPreferenceSummaryToValue(findPreference("nimi46"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero46"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero46"));
            bindPreferenceSummaryToValue(findPreference("optional1_46"));
            bindPreferenceSummaryToValue(findPreference("optional2_46"));
            bindPreferenceSummaryToValue(findPreference("optional3_46"));
            bindPreferenceSummaryToValue(findPreference("optional4_46"));
            bindPreferenceSummaryToValue(findPreference("optional5_46"));

            bindPreferenceSummaryToValue(findPreference("nimi47"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero47"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero47"));
            bindPreferenceSummaryToValue(findPreference("optional1_47"));
            bindPreferenceSummaryToValue(findPreference("optional2_47"));
            bindPreferenceSummaryToValue(findPreference("optional3_47"));
            bindPreferenceSummaryToValue(findPreference("optional4_47"));
            bindPreferenceSummaryToValue(findPreference("optional5_47"));

            bindPreferenceSummaryToValue(findPreference("nimi48"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero48"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero48"));
            bindPreferenceSummaryToValue(findPreference("optional1_48"));
            bindPreferenceSummaryToValue(findPreference("optional2_48"));
            bindPreferenceSummaryToValue(findPreference("optional3_48"));
            bindPreferenceSummaryToValue(findPreference("optional4_48"));
            bindPreferenceSummaryToValue(findPreference("optional5_48"));

            bindPreferenceSummaryToValue(findPreference("nimi49"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero49"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero49"));
            bindPreferenceSummaryToValue(findPreference("optional1_49"));
            bindPreferenceSummaryToValue(findPreference("optional2_49"));
            bindPreferenceSummaryToValue(findPreference("optional3_49"));
            bindPreferenceSummaryToValue(findPreference("optional4_49"));
            bindPreferenceSummaryToValue(findPreference("optional5_49"));

            bindPreferenceSummaryToValue(findPreference("nimi50"));
            bindPreferenceSummaryToValue(findPreference("vakanssinumero50"));
            bindPreferenceSummaryToValue(findPreference("puhelinnumero50"));
            bindPreferenceSummaryToValue(findPreference("optional1_50"));
            bindPreferenceSummaryToValue(findPreference("optional2_50"));
            bindPreferenceSummaryToValue(findPreference("optional3_50"));
            bindPreferenceSummaryToValue(findPreference("optional4_50"));
            bindPreferenceSummaryToValue(findPreference("optional5_50"));
        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
