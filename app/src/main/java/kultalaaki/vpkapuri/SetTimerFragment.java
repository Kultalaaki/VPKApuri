package kultalaaki.vpkapuri;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SetTimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SetTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetTimerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    DBTimer dbTimer;
    TextView hourSelector, minuteSelector, hourSelector2, minuteSelector2;
    EditText name;
    Switch stateSelector;
    Button monday, tuesday, wednesday, thursday, friday, saturday, sunday, cancel, save;
    boolean bMonday = false, bTuesday = false, bWednesday = false, bThursday = false, bFriday = false, bSaturday = false, bSunday = false, startOrStopSelector, selectoryo = false;
    String ma, ti, ke, to, pe, la, su, startTime, stopTime, state, timerName;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    Context ctx;

    private OnFragmentInteractionListener mListener;

    public SetTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     *
     * @return A new instance of fragment SetTimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SetTimerFragment newInstance(String primaryKey) {
        SetTimerFragment fragment = new SetTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, primaryKey);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getActivity();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    public void onStart() {
        super.onStart();
        dbTimer = new DBTimer(getActivity());
        if(getArguments()!= null) {
            populateTimer(mParam1);
            cancel.setText(R.string.poistaAjastin);
        } else {
            stateSelectorState();
            setOnClickListeners();
        }
    }

    void populateTimer(String primaryKey) {
        Cursor cursor = dbTimer.timerID(primaryKey);
        if(cursor != null) {
            timerName = cursor.getString(cursor.getColumnIndex(DBTimer.NAME));
            name.setText(timerName);
            ma = cursor.getString(cursor.getColumnIndex(DBTimer.MA));
            ti = cursor.getString(cursor.getColumnIndex(DBTimer.TI));
            ke = cursor.getString(cursor.getColumnIndex(DBTimer.KE));
            to = cursor.getString(cursor.getColumnIndex(DBTimer.TO));
            pe = cursor.getString(cursor.getColumnIndex(DBTimer.PE));
            la = cursor.getString(cursor.getColumnIndex(DBTimer.LA));
            su = cursor.getString(cursor.getColumnIndex(DBTimer.SU));
            startTime = cursor.getString(cursor.getColumnIndex(DBTimer.STARTTIME));
            stopTime = cursor.getString(cursor.getColumnIndex(DBTimer.STOPTIME));
            state = cursor.getString(cursor.getColumnIndex(DBTimer.SELECTOR));
            if(ma.equals("ma")) { bMonday = true; monday.setTextColor(getResources().getColor(R.color.orange)); }
            if(ti.equals("ti")) { bTuesday = true; tuesday.setTextColor(getResources().getColor(R.color.orange)); }
            if(ke.equals("ke")) { bWednesday = true; wednesday.setTextColor(getResources().getColor(R.color.orange)); }
            if(to.equals("to")) { bThursday = true; thursday.setTextColor(getResources().getColor(R.color.orange)); }
            if(pe.equals("pe")) { bFriday = true; friday.setTextColor(getResources().getColor(R.color.orange)); }
            if(la.equals("la")) { bSaturday = true; saturday.setTextColor(getResources().getColor(R.color.orange)); }
            if(su.equals("su")) { bSunday = true; sunday.setTextColor(getResources().getColor(R.color.orange)); }
            if(state.equals("yotila")) {
                selectoryo = true;
                stateSelector.setText(R.string.nightMode);
                stateSelector.setChecked(true);
            } else {
                selectoryo = false;
                stateSelector.setText(R.string.pref_ringtone_silent);
                stateSelector.setChecked(false);
            }
            hourSelector.setText(startTime.substring(0,2));
            minuteSelector.setText(startTime.substring(3,5));
            hourSelector2.setText(stopTime.substring(0,2));
            minuteSelector2.setText(stopTime.substring(3,5));
            setOnClickListeners();
            stateSelectorState();
        }
    }

    public void setOnClickListeners() {
        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bMonday) {
                    bMonday = true;
                    ma = "ma";
                    monday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bMonday = false;
                    ma = "";
                    monday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bTuesday) {
                    bTuesday = true;
                    ti = "ti";
                    tuesday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bTuesday = false;
                    ti = "";
                    tuesday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bWednesday) {
                    bWednesday = true;
                    ke = "ke";
                    wednesday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bWednesday = false;
                    ke = "";
                    wednesday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bThursday) {
                    bThursday = true;
                    to = "to";
                    thursday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bThursday = false;
                    to = "";
                    thursday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bFriday) {
                    bFriday = true;
                    pe = "pe";
                    friday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bFriday = false;
                    pe = "";
                    friday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bSaturday) {
                    bSaturday = true;
                    la = "la";
                    saturday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bSaturday = false;
                    la = "";
                    saturday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bSunday) {
                    bSunday = true;
                    su = "su";
                    sunday.setTextColor(getResources().getColor(R.color.orange));
                } else {
                    bSunday = false;
                    su = "";
                    sunday.setTextColor(getResources().getColor(R.color.text_color_primary));
                }
            }
        });
        hourSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopSelector = true;
                DialogFragment timePicker = new TimePickerFragment();
                if(getFragmentManager() != null) {
                    timePicker.show(getFragmentManager(), "time picker");
                }
            }
        });
        minuteSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopSelector = true;
                DialogFragment timePicker = new TimePickerFragment();
                if(getFragmentManager() != null) {
                    timePicker.show(getFragmentManager(), "time picker");
                }
            }
        });
        hourSelector2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopSelector = false;
                DialogFragment timePicker = new TimePickerFragment();
                if(getFragmentManager() != null) {
                    timePicker.show(getFragmentManager(), "time picker");
                }
            }
        });
        minuteSelector2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOrStopSelector = false;
                DialogFragment timePicker = new TimePickerFragment();
                if(getFragmentManager() != null) {
                    timePicker.show(getFragmentManager(), "time picker");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getArguments() != null) {
                    int sija = Integer.parseInt(mParam1);
                    dbTimer.deleteRow(sija);
                    if(getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    if(getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getArguments() != null) {
                    timerName = name.getText().toString();
                    startTime = hourSelector.getText().toString() + ":" + minuteSelector.getText().toString();
                    stopTime = hourSelector2.getText().toString() + ":" + minuteSelector2.getText().toString();
                    dbTimer.tallennaMuutokset(mParam1, timerName, startTime, stopTime, ma, ti, ke, to, pe, la, su, state, "on");
                    if(getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                } else {
                    saveTimerToDBs();
                }
            }
        });
    }

    public void stateSelectorState() {
        stateSelector.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    selectoryo = true;
                    state = "yotila";
                    stateSelector.setText(R.string.nightMode);
                } else {
                    selectoryo = false;
                    state = "aaneton";
                    stateSelector.setText(R.string.pref_ringtone_silent);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        stateSelector = view.findViewById(R.id.switchAaneton);
        monday = view.findViewById(R.id.buttonMonday);
        tuesday = view.findViewById(R.id.buttonTuesday);
        wednesday = view.findViewById(R.id.buttonWednesday);
        thursday = view.findViewById(R.id.buttonThursday);
        friday = view.findViewById(R.id.buttonFriday);
        saturday = view.findViewById(R.id.buttonSaturday);
        sunday = view.findViewById(R.id.buttonSunday);
        cancel = view.findViewById(R.id.buttonCancel);
        save = view.findViewById(R.id.buttonSave);
        name = view.findViewById(R.id.ajastinNimi2);
        hourSelector = view.findViewById(R.id.hourSelector);
        minuteSelector = view.findViewById(R.id.minuteSelector);
        hourSelector2 = view.findViewById(R.id.hourSelector2);
        minuteSelector2 = view.findViewById(R.id.minuteSelector2);
    }

    /*@Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.i("TAG", "OnTimeSet reached");
        if(startOrStopSelector) {
            hourSelector.setText(hourOfDay);
            minuteSelector.setText(minute);
        } else {
            hourSelector2.setText(hourOfDay);
            minuteSelector2.setText(minute);
        }
    }*/

    public void setTimerTimes(int hour, int minute) {
        String min = String.valueOf(minute);
        String hou = String.valueOf(hour);
        if(startOrStopSelector) {
            if(minute < 10) { min = "0" + minute; }
            if(hour < 10) {hou = "0" + hour; }
            hourSelector.setText(hou);
            minuteSelector.setText(min);
        } else {
            if(minute < 10) { min = "0" + minute; }
            if(hour < 10) {hou = "0" + hour; }
            hourSelector2.setText(hou);
            minuteSelector2.setText(min);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener.showAddTimer();
        mListener = null;
        dbTimer.close();
    }

    /*public void calendarTesting() {
        Calendar calendar = Calendar.getInstance();
        String date = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault());
        stateSelector.setText(date);
    }*/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        //void showAddTimer();
        //void hideAddTimer();
        void saveTimerToDB(String name, String startTime, String stopTime, String ma, String ti, String ke, String to,
                           String pe, String la, String su, String selector, String isiton);
        //void updateListview();
    }

    void saveTimerToDBs() {
        String ma="", ti="", ke="", to="", pe="", la="", su="", selector, timerName, startTime, stopTime;
        if(bMonday){ma="ma";}if(bTuesday){ti="ti";}if(bWednesday){ke="ke";}if(bThursday){to="to";}if(bFriday){pe="pe";}if(bSaturday){la="la";}if(bSunday){su="su";}
        if(selectoryo){ selector = "yotila"; } else { selector = "aaneton"; }
        timerName = name.getText().toString();
        startTime = hourSelector.getText().toString() + ":" + minuteSelector.getText().toString();
        stopTime = hourSelector2.getText().toString() + ":" + minuteSelector2.getText().toString();
        mListener.saveTimerToDB(timerName, startTime, stopTime, ma, ti, ke, to, pe, la, su, selector, "on");
        //mListener.updateListview();
        if(getActivity() != null) {
            getActivity().onBackPressed();
        }
    }

    // Using key (requestCode) to differentiate intents
    void setAlarms(String key, String ma, String ti, String ke, String to, String pe, String la, String su, String tila) {
        // TODO: Logic for user defined schedule
        if(ctx != null) {
            int requestCode = Integer.parseInt(key);
            alarmMgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            intent.putExtra("Ma", ma);
            intent.putExtra("Ti", ti);
            intent.putExtra("Ke", ke);
            intent.putExtra("To", to);
            intent.putExtra("Pe", pe);
            intent.putExtra("La", la);
            intent.putExtra("Su", su);
            intent.putExtra("Tila", tila);
            alarmIntent = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 30);

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        }
    }

    void deleteAlarms(String key, String ma, String ti, String ke, String to, String pe, String la, String su, String tila) {
        int requestCode = Integer.parseInt(key);
        alarmMgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlarmReceiver.class);
        intent.putExtra("Ma", ma);
        intent.putExtra("Ti", ti);
        intent.putExtra("Ke", ke);
        intent.putExtra("To", to);
        intent.putExtra("Pe", pe);
        intent.putExtra("La", la);
        intent.putExtra("Su", su);
        intent.putExtra("Tila", tila);
        alarmIntent = PendingIntent.getBroadcast(getActivity(), requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        if(alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
}