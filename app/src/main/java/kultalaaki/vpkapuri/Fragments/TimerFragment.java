/*
 * Created by Kultala Aki on 6/26/22, 6:19 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;

import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.misc.DBTimer;


public class TimerFragment extends Fragment {

    private Button addTimer;
    private ListView listViewTimers;
    private DBTimer dbTimer;
    private Context ctx;

    private OnFragmentInteractionListener mListener;

    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ctx = getActivity();
        dbTimer = new DBTimer(ctx);
        listViewTimers = view.findViewById(R.id.listViewTimers);
        addTimer = view.findViewById(R.id.addTimer);
        //deleteTimers = view.findViewById(R.id.deleteTimers);
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
    public void onStart() {
        super.onStart();

        getTimers();
        clickListeners();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void clickListeners() {
        addTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openSetTimer();
            }
        });
    }

    private void getTimers() {
        if(ctx != null) {
            Cursor cursor = dbTimer.getAllRows();
            String[] fromFieldNames = new String[] {DBTimer.COL_1, DBTimer.NAME, DBTimer.STARTTIME, DBTimer.STOPTIME, DBTimer.MA, DBTimer.TI, DBTimer.KE, DBTimer.TO, DBTimer.PE, DBTimer.LA, DBTimer.SU,
                    DBTimer.SELECTOR};
            final int[] toViewIDs = new int[] {R.id.sijaID, R.id.timerName, R.id.startTime, R.id.stopTime, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday,
                    R.id.selectedState};
            SimpleCursorAdapter myCursorAdapter;
            myCursorAdapter = new SimpleCursorAdapter(ctx, R.layout.item_timer_layout, cursor, fromFieldNames, toViewIDs, 0);
            //ListView myList = (ListView) findViewById(R.id.listViewHalyt);
            listViewTimers.setAdapter(myCursorAdapter);
            listViewTimers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // When clicked perform some action...
                    Log.e("TAG", "tulee " + DBTimer.COL_1);
                    TextView textView = view.findViewById(R.id.sijaID);
                    String primaryKey = textView.getText().toString();
                    mListener.openSetTimerNewInstance(primaryKey);
                }
            });
        }
    }

    /*private void populateListView() {
            Cursor cursor = dbTimer.getAllRows();
            String[] fromFieldNames = new String[] {DBTimer.COL_1, DBTimer.NAME, DBTimer.STARTTIME, DBTimer.STOPTIME, DBTimer.MA, DBTimer.TI, DBTimer.KE, DBTimer.TO, DBTimer.PE, DBTimer.LA, DBTimer.SU,
                    DBTimer.SELECTOR};
            final int[] toViewIDs = new int[] {R.id.sijaID, R.id.timerName, R.id.startTime, R.id.stopTime, R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday,
                    R.id.selectedState};
            SimpleCursorAdapter myCursorAdapter;
            myCursorAdapter = new SimpleCursorAdapter(ctx, R.layout.item_timer_layout, cursor, fromFieldNames, toViewIDs, 0);
            listViewTimers.setAdapter(myCursorAdapter);
            listViewTimers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.e("TAG", "eikö tule");
                    TextView text = view.findViewById(R.id.sijaID);
                    String primary = text.getText().toString();
                    mListener.openSetTimerNewInstance(primary);
                }
            });
    }*/

    /*private void showMessageClearTimers() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Huomio!")
                .setMessage("Haluatko poistaa kaikki ajastimet käytöstä?")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        dbTimer.tyhjennaTietokanta();
                        getTimers();
                    }
                });
        builder.create().show();
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
        void openSetTimerNewInstance(String primaryKey);
        void openSetTimer();
    }
}
