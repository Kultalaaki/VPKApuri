package kultalaaki.vpkapuri;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class HalytysActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halytys);
    }

    protected void onStart() {
        super.onStart();
        loadhalytysFragment();
    }

    public void loadhalytysFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        HalytysFragment halytysFragment = new HalytysFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.HalytysYlaosa, halytysFragment).commit();
    }
}
