package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HalytysFragment extends Fragment {

    private EditText halytyksenviesti;
    private TextView halytyksentunnus;
    private TextView kiireellisyys;
    private Chronometer chronometer;
    private boolean chronoInUse;
    private String alarmCounterTime;

    private Listener mCallback;

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context ctx = getActivity();
        if(ctx != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit().putBoolean("HalytysOpen", true).commit();

        chronoInUse = preferences.getBoolean("AlarmCounter", false);
        if(chronoInUse) {
            alarmCounterTime = preferences.getString("AlarmCounterTime", null);
            if(alarmCounterTime == null) {
                alarmCounterTime = "20";
            }
        }
    }

    /*public static HalytysFragment newInstance(String newAlarmComing) {
        HalytysFragment halytys = new HalytysFragment();
        Bundle args = new Bundle();
        args.putString("newAlarm", newAlarmComing);
        halytys.setArguments(args);
        return halytys;
    }*/

    public interface Listener {
        String returnViesti();
        String returnTunnus();
        String returnKiireellisyysLuokka();
        String returnAikaleima();
        void loadhalytysButtonsFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.halytys_fragment, parent, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkResources();
    }

    private void checkResources() {
        halytyksenviesti.setText(mCallback.returnViesti());
        halytyksentunnus.setText(mCallback.returnTunnus());
        kiireellisyys.setText(mCallback.returnKiireellisyysLuokka());

        if(chronoInUse) {
            chronometer.setVisibility(View.VISIBLE);
            startChronometer(mCallback.returnAikaleima());
        } else {
            chronometer.setVisibility(View.INVISIBLE);
        }
    }

    void setTexts(String viesti, String tunnus, String kiireellisyysLuokka, String aikaleima) {
        halytyksenviesti.setText(viesti);
        halytyksentunnus.setText(tunnus);
        kiireellisyys.setText(kiireellisyysLuokka);
        startChronometer(aikaleima);
    }



    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        halytyksentunnus = view.findViewById(R.id.halytyksenTunnus);
        halytyksenviesti = view.findViewById(R.id.halytyksenViesti);
        kiireellisyys = view.findViewById(R.id.kiireellisyys);

        chronometer = view.findViewById(R.id.alarm_chronometer);
        if(!chronoInUse) {
            chronometer.setVisibility(View.INVISIBLE);
        }
    }

    private void startChronometer(String aika) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd.MMM yyyy, H:mm:ss", Locale.getDefault());
        try {
            long timeNow = System.currentTimeMillis();
            Date date = dateFormat.parse(aika);
            long timeWhenAlarmCame = date.getTime();
            chronometer.setBase(SystemClock.elapsedRealtime() - (timeNow - timeWhenAlarmCame));
            if((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 60000 * Integer.valueOf(alarmCounterTime)) {
                chronometer.setVisibility(View.INVISIBLE);
            }
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if((SystemClock.elapsedRealtime() - chronometer.getBase()) >= 60000 * Integer.valueOf(alarmCounterTime)) {
                        chronometer.stop();
                        //chronometer.setBase(SystemClock.elapsedRealtime());
                    }
                }
            });
            if((SystemClock.elapsedRealtime() - chronometer.getBase()) <= 60000 * Integer.valueOf(alarmCounterTime)) {
                chronometer.start();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onPause() {
        super.onPause();
    }
}