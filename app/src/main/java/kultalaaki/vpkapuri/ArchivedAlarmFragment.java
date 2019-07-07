/*
 * Created by Kultala Aki on 7.7.2019 12:26
 * Copyright (c) 2019. All rights reserved.
 * Last modified 4.7.2019 16:27
 */

package kultalaaki.vpkapuri;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArchivedAlarmFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArchivedAlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArchivedAlarmFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static String id;
    private static String tunnus;
    private static String luokka;
    private static String viesti;
    private static String osoite;
    private static String kommentti;
    private static String vastaus;
    private static String timeStamp;
    private static String optionalField2;
    private static String optionalField3;
    private static String optionalField4;
    private static String optionalField5;

    // TODO: Rename and change types of parameters
    private CardView save, delete, showOnMap;
    private TextView textViewTunnus, textViewLuokka, textViewViesti, textViewKommentti, textViewAika;
    private EditText tunnusteksti, kiireellisyys, osoiteteksti, viestiteksti, kommenttiteksti, aikaleima;

    private FireAlarmViewModel fireAlarmViewModel;
    private static FireAlarm mFireAlarm;

    private OnFragmentInteractionListener mListener;

    public ArchivedAlarmFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ArchivedAlarmFragment newInstance(FireAlarm fireAlarm) {
        mFireAlarm = fireAlarm;
        ArchivedAlarmFragment fragment = new ArchivedAlarmFragment();
        Bundle args = new Bundle();
        args.putInt("id", fireAlarm.getId());
        args.putString("tunnus", fireAlarm.getTunnus());
        args.putString("luokka", fireAlarm.getLuokka());
        args.putString("viesti", fireAlarm.getViesti());
        args.putString("osoite", fireAlarm.getOsoite());
        args.putString("kommentti", fireAlarm.getKommentti());
        args.putString("vastaus", fireAlarm.getVastaus());
        args.putString("timeStamp", fireAlarm.getTimeStamp());
        args.putString("optionalField2", fireAlarm.getOptionalField2());
        args.putString("optionalField3", fireAlarm.getOptionalField3());
        args.putString("optionalField4", fireAlarm.getOptionalField4());
        args.putString("optionalField5", fireAlarm.getOptionalField5());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            primaryKey = getArguments().getString(ARG_PARAM1);
        }*/
    }

    public void onStart() {
        super.onStart();
        fireAlarmViewModel = ViewModelProviders.of(this).get(FireAlarmViewModel.class);
        if (getArguments() != null) {
            tunnus = getArguments().getString("tunnus");
            osoite = getArguments().getString("osoite");
            viesti = getArguments().getString("viesti");
            luokka = getArguments().getString("luokka");
            kommentti = getArguments().getString("kommentti");
            timeStamp = getArguments().getString("timeStamp");
            tunnusteksti.setText(tunnus);
            osoiteteksti.setText(osoite);
            kiireellisyys.setText(luokka);
            viestiteksti.setText(viesti);
            aikaleima.setText(timeStamp);
            kommenttiteksti.setText(kommentti);
            //Toast.makeText(getActivity(), "Tunnus: " + tunnus, Toast.LENGTH_LONG).show();
        }

        /*Cursor cursor = db.halyID(primaryKey);
        if(cursor != null) {
            tunnusteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.TUNNUS)));
            luokkateksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.LUOKKA)));
            viestiteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.VIESTI)));
            kommenttiteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KOMMENTTI)));
        }*/

        if (getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String osoite = osoiteteksti.getText().toString();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoite);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                Context context = getActivity();
                if (context != null) {
                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                }

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMessageOKCancelPoistaHaly();
            }
        });

        lisaaKommentti();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_halytys_tietokannasta, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        textViewTunnus = view.findViewById(R.id.tunnus);
        tunnusteksti = view.findViewById(R.id.tunnusteksti);
        textViewLuokka = view.findViewById(R.id.luokka);
        osoiteteksti = view.findViewById(R.id.luokkateksti);
        textViewViesti = view.findViewById(R.id.viesti);
        viestiteksti = view.findViewById(R.id.viestiteksti);
        kiireellisyys = view.findViewById(R.id.kiireellisyys);
        textViewKommentti = view.findViewById(R.id.kommentti);
        textViewAika = view.findViewById(R.id.aika);
        aikaleima = view.findViewById(R.id.aikaLeima);
        kommenttiteksti = view.findViewById(R.id.kommenttiteksti);
        kommenttiteksti.setCursorVisible(false);
        save = view.findViewById(R.id.cardTallenna);
        delete = view.findViewById(R.id.cardPoista);
        showOnMap = view.findViewById(R.id.cardNaytaKartta);
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

    private void showMessageOKCancelPoistaHaly() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Poista hälytys.")
                .setMessage("Haluatko varmasti poistaa tämän hälytyksen tietokannasta?")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Poista hälytys tietokannasta ja palaa arkiston etusivulle ja hae päivitety tietokanta esiin.
                        fireAlarmViewModel.delete(mFireAlarm);
                        /*int paikka = Integer.parseInt(primaryKey);
                        db.deleteRow(paikka);
                        */
                        dialogInterface.dismiss();
                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                });
        builder.create().show();
    }

    private void lisaaKommentti() {
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String kommentti = kommenttiteksti.getText().toString().trim();
                        String tunnus = tunnusteksti.getText().toString().trim();
                        String osoite = osoiteteksti.getText().toString().trim();
                        String viesti = viestiteksti.getText().toString();
                        String luokka = kiireellisyys.getText().toString().trim();
                        String aikaLeima = aikaleima.getText().toString();

                        mFireAlarm.setViesti(viesti);
                        mFireAlarm.setTunnus(tunnus);
                        mFireAlarm.setOsoite(osoite);
                        mFireAlarm.setLuokka(luokka);
                        mFireAlarm.setKommentti(kommentti);
                        mFireAlarm.setTimeStamp(aikaLeima);

                        fireAlarmViewModel.update(mFireAlarm);
                        Toast.makeText(getActivity(), "Tallennettu.", Toast.LENGTH_SHORT).show();
                        kommenttiteksti.setCursorVisible(false);

                        if (getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                        //boolean lisattyKommentti = db.lisaaKommentti(primaryKey, tunnus, luokka, viesti, kommentti);
                        /*if(lisattyKommentti){
                            Toast.makeText(getActivity(), "Tallennettu", Toast.LENGTH_LONG).show();
                            kommenttiteksti.setCursorVisible(false);
                            if(getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Tallennus epäonnistui", Toast.LENGTH_LONG).show();
                        }*/
                    }
                }
        );
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
