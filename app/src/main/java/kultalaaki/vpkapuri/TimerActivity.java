package kultalaaki.vpkapuri;

import android.app.TimePickerDialog;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TimerActivity extends AppCompatActivity
        implements SetTimerFragment.OnFragmentInteractionListener, TimePickerDialog.OnTimeSetListener {

    Button addTimer;
    DBTimer dbTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        addTimer = findViewById(R.id.addTimer);
    }

    @Override
    public void onFragmentInteraction() {

    }

    @Override
    public void onStart() {
        super.onStart();
        dbTimer = new DBTimer(getApplicationContext());
        addTimer.setOnClickListener(new View.OnClickListener() {
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

    public void hideAddTimer() {
        addTimer.setVisibility(View.GONE);
    }

    public void showAddTimer() {
        addTimer.setVisibility(View.VISIBLE);
    }

    public void saveTimerToDB(String name, String startTime, String stopTime, String ma, String ti, String ke, String to,
                              String pe, String la, String su, String selector, String isiton) {
        Toast.makeText(getApplicationContext(), "melkein " + name + startTime + stopTime + ma + ti+ke+to+pe+la+su+selector, Toast.LENGTH_LONG).show();
        boolean tallennettu = dbTimer.insertData(name, startTime, stopTime,
                ma, ti, ke, to, pe, la, su, selector, isiton);
        if(tallennettu) {
            Toast.makeText(getApplicationContext(), "Tallennettu", Toast.LENGTH_LONG).show();
            tietokantaVarmuuskopio();
        }
    }

    public void tietokantaVarmuuskopio() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source;
        FileChannel destination;
        String currentDBPath = "/data/" + "kultalaaki.vpkapuri/databases/timers.db";
        String backupDBPath = "Ajastin VPK Apuri";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
