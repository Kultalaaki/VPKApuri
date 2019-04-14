package kultalaaki.vpkapuri;

import android.app.TimePickerDialog;
import android.database.Cursor;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TimerActivity extends AppCompatActivity
        implements SetTimerFragment.OnFragmentInteractionListener, TimePickerDialog.OnTimeSetListener {

    Button addTimer, deleteTimers;
    ListView listViewTimers;
    DBTimer dbTimer;
    FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        addTimer = findViewById(R.id.addTimer);
        deleteTimers = findViewById(R.id.deleteTimers);
        listViewTimers = findViewById(R.id.listViewTimers);
        frame = findViewById(R.id.showSetTimer);
    }

    private void populateListView() {
        Cursor cursor = dbTimer.getAllRows();
        String[] fromFieldNames = new String[] {DBTimer.COL_1, DBTimer.NAME, DBTimer.STARTTIME, DBTimer.STOPTIME, DBTimer.MA, DBTimer.TI, DBTimer.KE, DBTimer.TO, DBTimer.PE, DBTimer.LA, DBTimer.SU,
                DBTimer.SELECTOR};
        final int[] toViewIDs = new int[] {R.id.sijaID, R.id.timerName, R.id.startTime, R.id.stopTime, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday,
                R.id.selectedState};
        SimpleCursorAdapter myCursorAdapter;
        myCursorAdapter = new SimpleCursorAdapter(getBaseContext(),R.layout.item_timer_layout, cursor, fromFieldNames, toViewIDs, 0);
        //ListView myList = (ListView) findViewById(R.id.listViewHalyt);
        listViewTimers.setAdapter(myCursorAdapter);
        listViewTimers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked perform some action...
                //TODO
                frame.setVisibility(View.VISIBLE);
                TextView textView = view.findViewById(R.id.sijaID);
                String primaryKey = textView.getText().toString();
                openSetTimerNewInstance(primaryKey);
                Log.i("TAG", primaryKey);

                /*TextView textView = view.findViewById(R.id.sija);
                String primaryKey = textView.getText().toString();
                Intent myIntent = new Intent(ArkistoActivity.this, HalytysTietokannastaActivity.class);
                myIntent.putExtra("paikkanumero", position);
                myIntent.putExtra("primaryKey", primaryKey);
                ArkistoActivity.this.startActivity(myIntent);*/
            }
        });
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
        deleteTimers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTimersFromDatabase();
            }
        });
        frame.setVisibility(View.GONE);
        populateListView();
    }

    public void updateListview() {
        populateListView();
    }

    void openSetTimerNewInstance(String primaryKey) {
        frame.setVisibility(View.VISIBLE);
        FragmentManager fragmentManager = TimerActivity.this.getSupportFragmentManager();
        SetTimerFragment setTimerFragment = SetTimerFragment.newInstance(primaryKey);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);
        fragmentTransaction.add(R.id.showSetTimer, setTimerFragment, "setTimerFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    void openSetTimer() {
        frame.setVisibility(View.VISIBLE);
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
        deleteTimers.setVisibility(View.GONE);
    }

    public void showAddTimer() {
        frame.setVisibility(View.GONE);
        addTimer.setVisibility(View.VISIBLE);
        deleteTimers.setVisibility(View.VISIBLE);
    }

    public void saveTimerToDB(String name, String startTime, String stopTime, String ma, String ti, String ke, String to,
                              String pe, String la, String su, String selector, String isiton) {
        Toast.makeText(getApplicationContext(), "melkein " + name + startTime + stopTime + ma + ti+ke+to+pe+la+su+selector, Toast.LENGTH_LONG).show();
        boolean tallennettu = dbTimer.insertData(name, startTime, stopTime,
                ma, ti, ke, to, pe, la, su, selector, isiton);
        if(tallennettu) {
            Toast.makeText(getApplicationContext(), "Tallennettu", Toast.LENGTH_LONG).show();
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

    void deleteTimersFromDatabase() {
        dbTimer.tyhjennaTietokanta();
        updateListview();
    }


    /*class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.MyViewHolder> {
        LayoutInflater inflater;

        public TimerAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            View view =inflater.inflate(R.layout.activity_timer,parent,false);
            MyViewHolder holder=new MyViewHolder(view);
            return holder;
        }

        public void onBindViewHolder(MyViewHolder holder, int position) {
            Exercises exercises = new Exercises(getActivity());
            ArrayList<String> arrayList1 = new ArrayList<String>();
            ArrayList<String> arrayList2 = new ArrayList<String>();

            holder.Name.setText(arrayList1.get(position));
            holder.PhoneNumber.setText(arrayList2.get(position));



        }

        @Override
        public int getItemCount() {

            return array.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            TextView Name, PhoneNumber;
            CardView card;

            public MyViewHolder(View itemView) {
                super(itemView);

                PhoneNumber= (TextView) itemView.findViewById(R.id.imageViewPicLibrary);//change Here
                Name = (TextView) itemView.findViewById(R.id.textViewExerciseLibraryExerciseName);//change Here
                card= (CardView) itemView.findViewById(R.id.card_view_ex_lib);//change Here

                card.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //OnClick
                    }
                });


            }


        }
    }*/

}
