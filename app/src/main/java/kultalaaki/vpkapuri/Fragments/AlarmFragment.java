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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import kultalaaki.vpkapuri.FireAlarm;
import kultalaaki.vpkapuri.FireAlarmViewModel;
import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.soundcontrols.SpeakText;

public class AlarmFragment extends Fragment {

    private TextView alarmMessage;

    private TextView alarmId;
    private TextView urgencyClass;
    private CardView cardViewAlarm;
    private CardView cardViewUnits;
    private TextView textClarificationPart1, textClarificationPart2, textClarificationPart3, textClarificationPart4;

    private Button unit1, unit2, unit3, unit4, unit5, unit6, unit7, unit8;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alarm_cards, parent, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        alarmId = view.findViewById(R.id.alarm_id);
        alarmMessage = view.findViewById(R.id.halytyksenViesti);
        urgencyClass = view.findViewById(R.id.urgency_class);
        cardViewAlarm = view.findViewById(R.id.cardView_alarm);
        cardViewUnits = view.findViewById(R.id.cardView_units);
        textClarificationPart1 = view.findViewById(R.id.text_clarification_part_1);
        textClarificationPart2 = view.findViewById(R.id.text_clarification_part_2);
        textClarificationPart3 = view.findViewById(R.id.text_clarification_part_3);
        textClarificationPart4 = view.findViewById(R.id.text_clarification_part_4);
        unit1 = view.findViewById(R.id.unit1);
        unit2 = view.findViewById(R.id.unit2);
        unit3 = view.findViewById(R.id.unit3);
        unit4 = view.findViewById(R.id.unit4);
        unit5 = view.findViewById(R.id.unit5);
        unit6 = view.findViewById(R.id.unit6);
        unit7 = view.findViewById(R.id.unit7);
        unit8 = view.findViewById(R.id.unit8);
        //units = view.findViewById(R.id.units);

        chronometer = view.findViewById(R.id.alarm_chronometer);
        if (!chronoInUse) {
            chronometer.setVisibility(View.INVISIBLE);
        }
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
                    String[] alarmSplitted = currentAlarm.getTunnus().split("([:/])+");
                    alarmMessage.setText(currentAlarm.getViesti());

                    try {
                        alarmId.setText(alarmSplitted[0]);
                        textClarificationPart1.setText(alarmSplitted[1].trim());
                        textClarificationPart2.setText(alarmSplitted[2].trim());
                        textClarificationPart3.setText(alarmSplitted[3].trim());
                        textClarificationPart4.setText(alarmSplitted[4].trim());
                    } catch (Exception e) {
                        FirebaseCrashlytics.getInstance().log("Alarm fragment: Could not set all texts for alarm card.");
                    }


                    urgencyClass.setText(currentAlarm.getLuokka());
                    //units.setText(currentAlarm.getOptionalField3());
                    Log.i("VPK Apuri", currentAlarm.getOptionalField3());
                    String[] units = currentAlarm.getOptionalField3().split("([,])");

                    for(int i = 0; i < units.length; i++) {
                        if(units[i] != null) {
                            setUnitToButton(i, units[i]);
                        }
                    }

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

    private void setUnitToButton (int position, String unit) {
        if(cardViewUnits.getVisibility() == View.GONE) {
            cardViewUnits.setVisibility(View.VISIBLE);
        }
        switch(position) {
            case 0:
                unit1.setVisibility(View.VISIBLE);
                unit1.setText(unit);
                break;
            case 1:
                unit2.setText(unit);
                unit2.setVisibility(View.VISIBLE);
                break;
            case 2:
                unit3.setText(unit);
                unit3.setVisibility(View.VISIBLE);
                break;
            case 3:
                unit4.setText(unit);
                unit4.setVisibility(View.VISIBLE);
                break;
            case 4:
                unit5.setText(unit);
                unit5.setVisibility(View.VISIBLE);
                break;
            case 5:
                unit6.setText(unit);
                unit6.setVisibility(View.VISIBLE);
                break;
            case 6:
                unit7.setText(unit);
                unit7.setVisibility(View.VISIBLE);
                break;
            case 7:
                unit8.setText(unit);
                unit8.setVisibility(View.VISIBLE);
                break;
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

    public interface Listener {
        void loadOHTOAnswer();

        void changeLayout();

        void changeLayoutBack();

        void loadhalytysButtonsFragment();

        void loadAsematauluButtons();

        void showToast(String head, String message);
    }
}