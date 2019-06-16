package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class HalytysActivity extends AppCompatActivity
        implements HalytysButtonsFragment.Listener {

    boolean koneluku, autoAukaisu, asemataulu;
    String action, type;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        asemataulu = preferences.getBoolean("asemataulu", false);
        if(!asemataulu) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.activity_halytys);
        Intent intent = getIntent();
        action = intent.getAction();
        type = intent.getType();
        koneluku = preferences.getBoolean("koneluku", false);
        autoAukaisu = preferences.getBoolean("autoAukaisu", false);
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
        if(asemataulu) {
            // TODO: Asemataulu käytössä
            loadResponderFragment();
        } else {
            loadhalytysButtonsFragment();
        }
        //Log.i("test", action + " " + type);
        getParameters(action, type);
    }

    public void loadResponderFragment() {
        if(findViewById(R.id.responder_view) != null) {
            FragmentManager fragmentManager = this.getSupportFragmentManager();
            ResponderFragment responderFragment = new ResponderFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.responder_view, responderFragment, "ResponderFragment").commit();
        }
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
        action = null;
        type = null;
    }

    @SuppressLint("ApplySharedPref")
    public void getParameters(String action, String type) {

        if (Intent.ACTION_SEND.equals(action)&& type != null) {
            if ("text/plain".equals(type)){
                Intent stopAlarm = new Intent(HalytysActivity.this, IsItAlarmService.class);
                HalytysActivity.this.stopService(stopAlarm);
                if(koneluku && !autoAukaisu) {
                    waitForFragment();
                }
            } else if("automaattinen".equals(type)) {
                preferences.edit().putBoolean("showHiljenna", true).commit();
                waitForButtonsFragment();
            }
        }
    }
}
