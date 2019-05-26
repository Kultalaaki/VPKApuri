package kultalaaki.vpkapuri;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HalytysActivity extends AppCompatActivity
        implements HalytysButtonsFragment.Listener {

    boolean koneluku, autoAukaisu;
    String action, type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halytys);
        Intent intent = getIntent();
        action = intent.getAction();
        type = intent.getType();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        koneluku = sharedPreferences.getBoolean("koneluku", false);
        autoAukaisu = sharedPreferences.getBoolean("autoAukaisu", false);
    }

    public void hiljenna() {
        HalytysFragment halytysFragment = (HalytysFragment)
                getSupportFragmentManager().findFragmentByTag("halytysFragment");
        if(halytysFragment != null) {
            halytysFragment.lopetaPuhe();
        }
    }

    public void autoAukaisuPuhu() {
        HalytysFragment halytysFragment = (HalytysFragment)
                getSupportFragmentManager().findFragmentByTag("halytysFragment");
        //Log.i("HalytysActivity", halytysFragment.toString());
        if(halytysFragment != null) {
            halytysFragment.txtToSpeech();
        }
    }

    public void avaaWebSivu(String url) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        WebviewFragment webviewFragment = WebviewFragment.newInstance(url);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.replace(R.id.HalytysYlaosa, webviewFragment, "webviewFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    protected void onStart() {
        super.onStart();
        loadhalytysFragment();
        loadhalytysButtonsFragment();
        //Log.i("test", action + " " + type);
        getParameters(action, type);
    }

    public void loadhalytysFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        HalytysFragment halytysFragment = new HalytysFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.HalytysYlaosa, halytysFragment, "halytysFragment").commit();
    }

    public void loadhalytysButtonsFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        HalytysButtonsFragment halytysButtonsFragment = new HalytysButtonsFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.HalytysAlaosa, halytysButtonsFragment, "halytysButtonsFragment").commit();
    }

    public void waitForFragment() {
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                HalytysFragment halytysFragment = (HalytysFragment)
                        getSupportFragmentManager().findFragmentByTag("halytysFragment");
                //Log.i("HalytysActivity", halytysFragment.toString());
                if(halytysFragment != null) {
                    halytysFragment.txtToSpeech();
                }
                HalytysButtonsFragment halytysButtonsFragment = (HalytysButtonsFragment)
                        getSupportFragmentManager().findFragmentByTag("halytysButtonsFragment");
                if(halytysButtonsFragment != null) {
                    halytysButtonsFragment.setTextHiljennaPuhe();
                }
            }
        }, 1000);
        action = null;
        type = null;
    }

    public void waitForButtonsFragment() {
        Handler handler2 = new Handler();
        handler2.postDelayed(new Runnable() {
            public void run() {
                HalytysButtonsFragment halytysButtonsFragment = (HalytysButtonsFragment)
                        getSupportFragmentManager().findFragmentByTag("halytysButtonsFragment");
                if(halytysButtonsFragment != null) {
                    halytysButtonsFragment.autoAukaisu();
                }
            }
        }, 1000);
    }

    public void getParameters(String action, String type) {

        if (Intent.ACTION_SEND.equals(action)&& type != null) {
            if ("text/plain".equals(type)){
                Intent stopAlarm = new Intent(HalytysActivity.this, IsItAlarmService.class);
                HalytysActivity.this.stopService(stopAlarm);
                if(koneluku && !autoAukaisu) {
                    waitForFragment();
                }
            } else if("automaattinen".equals(type)) {
                waitForButtonsFragment();
            }
        }

        //if(autoAukaisu) {
            //waitForButtonsFragment();
            /*hiljenna.setVisibility(View.VISIBLE);
            hiljenna.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent stopAlarm = new Intent(aktiivinenHaly.this, IsItAlarmService.class);
                    aktiivinenHaly.this.stopService(stopAlarm);
                    if(koneluku) {
                        AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                        if(ad != null) {
                            // teksti puheeksi äänenvoimakkuus
                            palautaMediaVol = ad.getStreamVolume(AudioManager.STREAM_MUSIC);
                            palautaMediaVolBoolean = true;
                            ad.setStreamVolume(AudioManager.STREAM_MUSIC, 4, 0);
                            try {
                                SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                tekstiPuheeksiVol = pref_general.getInt("tekstiPuheeksiVol", -1);
                                tekstiPuheeksiVol = saadaAani(tekstiPuheeksiVol);
                                ad.setStreamVolume(AudioManager.STREAM_MUSIC, tekstiPuheeksiVol, 0);
                            } catch (Exception e) {
                                Log.i("VPK Apuri", "Teksti puheeksi äänenvoimakkuuden lukeminen asetuksista epäonnistui.");
                            }
                        }
                        txtToSpeech();
                        hiljenna.setText(R.string.hiljenna_puhe);
                        hiljenna.setOnClickListener(new View.OnClickListener(){
                            public void onClick(View view){
                                AudioManager ad = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                                if(ad != null) {
                                    ad.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                                    hiljenna.setVisibility(View.INVISIBLE);
                                }
                                if(t1 != null) {
                                    t1.stop();
                                    t1.shutdown();
                                }
                            }
                        });
                    }
                }
            });*/
        //}
    }
}
