package kultalaaki.vpkapuri;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
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
