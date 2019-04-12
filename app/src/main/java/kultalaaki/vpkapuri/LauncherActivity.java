package kultalaaki.vpkapuri;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class LauncherActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    protected void onStart() {
        super.onStart();
        loadLegalFragment();
    }

    public void loadLegalFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        kayttoehdotFragment kayttoehdotFragment = new kayttoehdotFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.wholeScreen, kayttoehdotFragment, "kayttoehdotFragment").commit();
    }
}
