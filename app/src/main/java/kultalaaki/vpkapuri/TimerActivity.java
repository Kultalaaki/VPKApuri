package kultalaaki.vpkapuri;

import android.app.TimePickerDialog;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class TimerActivity extends AppCompatActivity
        implements SetTimerFragment.OnFragmentInteractionListener, TimePickerDialog.OnTimeSetListener {

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        fab = findViewById(R.id.floatingActionButton);
    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public void onStart() {
        super.onStart();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSetTimer();
            }
        });
    }

    void openSetTimer() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        SetTimerFragment setTimerFragment = new SetTimerFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.showSetTimer, setTimerFragment, "setTimerFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.i("TAG", "OnTimeSet reached");
        SetTimerFragment setTimerFragment = (SetTimerFragment)
                getSupportFragmentManager().findFragmentByTag("setTimerFragment");
        if(setTimerFragment != null) {
            setTimerFragment.setTimerTimes(hourOfDay, minute);
        }
    }
}
