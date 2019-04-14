package kultalaaki.vpkapuri;


import android.Manifest;
import android.annotation.TargetApi;
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
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

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
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
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
                // For all other preferences, set the summary to the value's
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
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
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
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
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
            bindPreferenceSummaryToValue(findPreference("avainsana1"));
            bindPreferenceSummaryToValue(findPreference("avainsana2"));
            bindPreferenceSummaryToValue(findPreference("avainsana3"));
            bindPreferenceSummaryToValue(findPreference("avainsana4"));
            bindPreferenceSummaryToValue(findPreference("avainsana5"));

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
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SeekBarPreference _seekBarPref;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_notification);
            addPreferencesFromResource(R.xml.preferences);

            //Do Not Disturb allowed if menu doesn't open
            Preference pref = getPreferenceManager().findPreference("DoNotDisturb");
            if(pref != null) {
                pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        NotificationManager notificationManager =
                                (NotificationManager) getActivity().getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        if(notificationManager != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                    && !notificationManager.isNotificationPolicyAccessGranted()) {
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

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
            bindPreferenceSummaryToValue(findPreference("ringtone"));
            bindPreferenceSummaryToValue(findPreference("stopTime"));
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            // Set seekbar summary :
            //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            //int rad = sharedPreferences.getInt("SEEKBAR_VALUE", 50);
            Activity activity = getActivity();
            if(activity != null) {
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
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {

        private String[] permissionsSms = {Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS};

        //, Manifest.permission.CALL_PHONE

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);

            //Battery optimization
            Preference prefi = getPreferenceManager().findPreference("batteryOptimization");
            if(prefi != null) {
                prefi.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                            startActivity(intent);
                            return true;
                        }

                        return false;
                    }
                });
            }

            Preference prefTietosuoja = getPreferenceManager().findPreference("tietosuoja");
            if(prefTietosuoja != null) {
                prefTietosuoja.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Avaa nettisivu tietosuoja
                        String url = "http://www.vpkapuri.fi/tietosuoja.html";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                });
            }

            Preference prefKayttoehdot= getPreferenceManager().findPreference("kayttoehdot");
            if(prefKayttoehdot != null) {
                prefKayttoehdot.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        // Avaa nettisivu käyttöehdot
                        String url = "http://www.vpkapuri.fi/kayttoehdot.html";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                });
            }

            Preference prefFacebook= getPreferenceManager().findPreference("facebook");
            if(prefFacebook != null) {
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
            if(prefa != null) {
                prefa.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                return true;
                            }

                        return false;
                    }
                });
            }

            //Sovelluksen luvat SMS hälytyksiä varten
            Preference prefsms = getPreferenceManager().findPreference("luvatSMS");
            if(prefsms != null) {
                prefsms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if(!arePermissionsEnabled()){
                                // permissions granted, continue flow normally
                                requestMultiplePermissions();
                            }//else{
                              //  requestMultiplePermissions();
                            //}
                        }
                        return true;
                    }
                });
            }

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private boolean arePermissionsEnabled(){
            for(String permission : permissionsSms){
                if(ActivityCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED)
                    return false;
            }
            return true;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        private void requestMultiplePermissions(){
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

        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                               @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            if(requestCode == 101){
                for(int i=0;i<grantResults.length;i++){
                    if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                        if(shouldShowRequestPermissionRationale(permissions[i])){
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
}
