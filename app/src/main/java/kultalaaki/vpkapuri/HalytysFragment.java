package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HalytysFragment extends Fragment {

    private EditText halytyksenviesti;
    private TextView halytyksentunnus;
    private TextView kiireellisyys;
    private TextToSpeech t1;
    private int palautaMediaVol, tekstiPuheeksiVol;
    private boolean palautaMediaVolBoolean = false;
    private SharedPreferences preferences;
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
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
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

        /*FireAlarmViewModel mViewModel = ViewModelProviders.of(this).get(FireAlarmViewModel.class);

        mViewModel.getLastEntry().observe(this, new Observer<List<FireAlarm>>() {
            @Override
            public void onChanged(List<FireAlarm> fireAlarms) {
                if(!fireAlarms.isEmpty()) {
                    FireAlarm currentAlarm = fireAlarms.get(0);
                    halytyksenviesti.setText(currentAlarm.getViesti());
                    halytyksentunnus.setText(currentAlarm.getTunnus());
                    kiireellisyys.setText(currentAlarm.getLuokka());
                    String newAddress = currentAlarm.getOsoite();
                    mCallback.updateAddress(newAddress);
                    if(currentAlarm.getTunnus().equals("OHTO Hälytys")) {
                        mCallback.loadOHTOAnswer(currentAlarm.getOptionalField2());
                    }
                    if(chronoInUse) {
                        chronometerStartTimeString = currentAlarm.getTimeStamp();
                        chronometer.setVisibility(View.VISIBLE);
                        startChronometer();
                    } else {
                        chronometer.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), "Arkisto on tyhjä. Ei näytettävää hälytystä.", Toast.LENGTH_LONG).show();
                }
            }
        });*/
    }

    @Override
    public void onStart() {
        super.onStart();
        checkDoNotDisturb();
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

    private void checkDoNotDisturb() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean disturb = pref.getBoolean("DoNotDisturb", false);
        boolean asemataulu = pref.getBoolean("asemataulu", false);
        if(!disturb && getActivity() != null && !asemataulu) {
            NotificationManager notificationManager =
                    (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if(notificationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !notificationManager.isNotificationPolicyAccessGranted()) {
                    Toast.makeText(getActivity(), "Sovelluksella ei ole lupaa säädellä Älä häiritse tilaa.", Toast.LENGTH_LONG).show();
                }
            }
        }
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

    private void txtToSpeechVolume() {
        Context ctx = getActivity();
        if(ctx != null) {
            AudioManager ad = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if(ad != null) {
                palautaMediaVol = ad.getStreamVolume(AudioManager.STREAM_MUSIC);
                palautaMediaVolBoolean = true;
                ad.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0);
                // teksti puheeksi äänenvoimakkuus
                try {
                    SharedPreferences prefe_general = PreferenceManager.getDefaultSharedPreferences(ctx);
                    tekstiPuheeksiVol = prefe_general.getInt("tekstiPuheeksiVol", -1);
                    tekstiPuheeksiVol = saadaAani(tekstiPuheeksiVol);
                    ad.setStreamVolume(AudioManager.STREAM_MUSIC, tekstiPuheeksiVol, 0);
                    puhu();
                } catch (Exception e) {
                    Log.i("VPK Apuri", "Teksti puheeksi äänenvoimakkuuden lukeminen asetuksista epäonnistui.");
                }
            }
        }
    }

    private int saadaAani(int voima) {
        Context ctx = getActivity();
        if(ctx != null) {
            final AudioManager audioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                tekstiPuheeksiVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                double aani = (double)tekstiPuheeksiVol/100*voima;
                tekstiPuheeksiVol = (int) aani;
            }

            if(tekstiPuheeksiVol == 0) { return 1;
            } else if(tekstiPuheeksiVol == 1) {
                return 1;
            } else if(tekstiPuheeksiVol == 2) {
                return 2;
            } else if(tekstiPuheeksiVol == 3) {
                return 3;
            } else if(tekstiPuheeksiVol == 4) {
                return 4;
            } else if(tekstiPuheeksiVol == 5) {
                return 5;
            } else if(tekstiPuheeksiVol == 6) {
                return 6;
            } else if(tekstiPuheeksiVol == 7) {
                return 7;
            }

            return tekstiPuheeksiVol;
        }
        return 0;
    }

    void txtToSpeech(){
        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(Locale.getDefault());
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(getActivity(), "Kieli ei ole tuettu.", Toast.LENGTH_LONG).show();
                    }
                    txtToSpeechVolume();
                } else {
                    Toast.makeText(getActivity(), "Virhe", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void lopetaPuhe() {
        Context ctx = getActivity();
        if(ctx != null) {
            AudioManager ad = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
            if(ad != null) {
                ad.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                if(t1 != null) {
                    t1.stop();
                    t1.shutdown();
                }
            }
        }
    }

    private void puhu() {
        String puheeksi = halytyksentunnus.getText().toString() + " " + halytyksenviesti.getText().toString();
        if(Build.VERSION.SDK_INT >= 21) {
            t1.playSilentUtterance(1000, TextToSpeech.QUEUE_FLUSH, null);
        } else {
            t1.playSilence(1000, TextToSpeech.QUEUE_FLUSH, null);
        }
        t1.speak(puheeksi, TextToSpeech.QUEUE_FLUSH, null);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onPause() {
        super.onPause();
        if(t1 != null) {
            t1.stop();
            t1.shutdown();
        }
        if(palautaMediaVolBoolean) {
            Context ctx = getActivity();
            if(ctx != null) {
                AudioManager ad = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
                if(ad != null) {
                    ad.setStreamVolume(AudioManager.STREAM_MUSIC, palautaMediaVol, 0);
                }
            }
        }
        preferences.edit().putBoolean("HalytysOpen", false).commit();
    }
}