/*
 * Created by Kultala Aki on 6/26/22, 6:18 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kultalaaki.vpkapuri.FireAlarm;
import kultalaaki.vpkapuri.FireAlarmViewModel;
import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.soundcontrols.SpeakText;

public class AlarmFragment extends Fragment {

    private TextView halytyksenviesti;
    private TextView halytyksentunnus;
    private TextView kiireellisyys;
    private TextView units;
    private boolean previousAlarmOHTO = false, asemataulu;
    private SharedPreferences preferences;
    private Chronometer chronometer;
    private boolean chronoInUse;
    private String alarmCounterTime, chronometerStartTimeString;
    private SpeakText speakText;
    private String toSpeech;

    private Listener mCallback;

    private FireAlarmViewModel fireAlarmViewModel;

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Context ctx = getActivity();
        if (ctx != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putBoolean("HalytysOpen", true).commit();
        asemataulu = preferences.getBoolean("asemataulu", false);

        chronoInUse = preferences.getBoolean("AlarmCounter", false);
        if (chronoInUse) {
            alarmCounterTime = preferences.getString("AlarmCounterTime", null);
            if (alarmCounterTime == null) {
                alarmCounterTime = "20";
            }
        }
    }

    public interface Listener {
        void loadOHTOAnswer();

        void changeLayout();

        void changeLayoutBack();

        void loadhalytysButtonsFragment();

        void loadAsematauluButtons();

        void showToast(String head, String message);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mCallback = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context
                    + " must implement Listener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.alarm_fragment_clear, parent, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context ctx = getActivity();
        if (ctx != null) {
            fireAlarmViewModel = ViewModelProviders.of(getActivity()).get(FireAlarmViewModel.class);
            fireAlarmViewModel.getLastEntry().observe(getViewLifecycleOwner(), fireAlarms -> {
                if (!fireAlarms.isEmpty()) {
                    FireAlarm currentAlarm = fireAlarms.get(0);
                    halytyksenviesti.setText(currentAlarm.getViesti());
                    halytyksentunnus.setText(currentAlarm.getTunnus());
                    kiireellisyys.setText(currentAlarm.getLuokka());
                    units.setText(currentAlarm.getOptionalField3());
                    toSpeech = currentAlarm.getTunnus() + " "
                            + currentAlarm.getLuokka() + " "
                            + currentAlarm.getOsoite() + " "
                            + currentAlarm.getOptionalField3();

                    fireAlarmViewModel.setAddress(currentAlarm.getOsoite());
                    fireAlarmViewModel.setAlarmingNumber(currentAlarm.getOptionalField2());
                    if (currentAlarm.getTunnus().equals("OHTO Hälytys")) {
                        mCallback.loadOHTOAnswer();
                        mCallback.changeLayout();
                        previousAlarmOHTO = true;
                    } else if (previousAlarmOHTO) {
                        if (asemataulu) {
                            mCallback.loadAsematauluButtons();
                        } else {
                            mCallback.loadhalytysButtonsFragment();
                        }
                        mCallback.changeLayoutBack();
                    }

                    if (chronoInUse) {
                        chronometerStartTimeString = currentAlarm.getTimeStamp();
                        chronometer.setVisibility(View.VISIBLE);
                        startChronometer();
                    } else {
                        chronometer.setVisibility(View.INVISIBLE);
                    }
                } else {
                    mCallback.showToast("Arkisto on tyhjä.", "Ei näytettävää hälytystä.");
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        checkDoNotDisturb();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void checkDoNotDisturb() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean disturb = pref.getBoolean("DoNotDisturb", false);
        boolean asemataulu = pref.getBoolean("asemataulu", false);
        if (!disturb && getActivity() != null && !asemataulu) {
            NotificationManager notificationManager =
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                if (!notificationManager.isNotificationPolicyAccessGranted()) {
                    mCallback.showToast("Älä häiritse", "Ei ole lupa muuttaa Älä häiritse tilaa.");
                }
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        halytyksentunnus = view.findViewById(R.id.halytyksenTunnus);
        halytyksenviesti = view.findViewById(R.id.halytyksenViesti);
        kiireellisyys = view.findViewById(R.id.kiireellisyys);
        units = view.findViewById(R.id.units);

        chronometer = view.findViewById(R.id.alarm_chronometer);
        if (!chronoInUse) {
            chronometer.setVisibility(View.INVISIBLE);
        }
    }

    private void startChronometer() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd.MMM yyyy, H:mm:ss", Locale.getDefault());
        try {
            long timeNow = System.currentTimeMillis();
            Date date = dateFormat.parse(chronometerStartTimeString);
            assert date != null;
            long timeWhenAlarmCame = date.getTime();
            chronometer.setBase(SystemClock.elapsedRealtime() - (timeNow - timeWhenAlarmCame));
            if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 60000L * Integer.parseInt(alarmCounterTime)) {
                chronometer.setVisibility(View.INVISIBLE);
            }
            chronometer.setOnChronometerTickListener(chronometer -> {
                if ((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 60000L * Integer.parseInt(alarmCounterTime)) {
                    chronometer.stop();
                }
            });
            if ((SystemClock.elapsedRealtime() - chronometer.getBase()) <= 60000L * Integer.parseInt(alarmCounterTime)) {
                chronometer.start();
            }
        } catch (ParseException e) {
            mCallback.showToast("Hälytysajastin", "Parsimisvirhe, kelloa ei voi käynnistää!");
        } catch (NumberFormatException e) {
            mCallback.showToast("Hälytysajastin", "Numerovirhe, kelloa ei voi käynnistää!");
        } catch (Exception e) {
            mCallback.showToast("Hälytysajastin", "Tuntematon virhe, kelloa ei voi käynnistää!");
        }
    }

    public void txtToSpeech() {
        speakText = new SpeakText(getActivity(), preferences);
        speakText.setTextToSpeak(toSpeech);
        speakText.initSpeakText();
    }

    public void lopetaPuhe() {
        speakText.stop();
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onPause() {
        super.onPause();

        preferences.edit().putBoolean("HalytysOpen", false).commit();
    }
}