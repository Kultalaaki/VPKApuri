package kultalaaki.vpkapuri;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HalytysTietokannastaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HalytysTietokannastaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HalytysTietokannastaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String primaryKey;
    DBHelper db;
    CardView save, delete, showOnMap;
    TextView tunnus, luokka, viesti, kommentti;
    EditText tunnusteksti, luokkateksti, viestiteksti, kommenttiteksti;

    private OnFragmentInteractionListener mListener;

    public HalytysTietokannastaFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static HalytysTietokannastaFragment newInstance(String param1) {
        HalytysTietokannastaFragment fragment = new HalytysTietokannastaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            primaryKey = getArguments().getString(ARG_PARAM1);
        }
    }

    public void onStart() {
        super.onStart();
        Cursor cursor = db.halyID(primaryKey);
        if(cursor != null) {
            tunnusteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.TUNNUS)));
            luokkateksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.LUOKKA)));
            viestiteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.VIESTI)));
            kommenttiteksti.setText(cursor.getString(cursor.getColumnIndex(DBHelper.KOMMENTTI)));
        }

        if(getActivity() != null) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        showOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String osoite = luokkateksti.getText().toString();
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoite);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                Context context = getActivity();
                if(context != null) {
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
        db = new DBHelper(getActivity());
        tunnus = view.findViewById(R.id.tunnus);
        tunnusteksti = view.findViewById(R.id.tunnusteksti);
        luokka = view.findViewById(R.id.luokka);
        luokkateksti = view.findViewById(R.id.luokkateksti);
        viesti = view.findViewById(R.id.viesti);
        viestiteksti = view.findViewById(R.id.viestiteksti);
        kommentti = view.findViewById(R.id.kommentti);
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
                        int paikka = Integer.parseInt(primaryKey);
                        db.deleteRow(paikka);

                        dialogInterface.dismiss();
                        if(getActivity() != null) {
                            getActivity().onBackPressed();
                        }
                    }
                });
        builder.create().show();
    }

    public void lisaaKommentti() {
        save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String kommentti = kommenttiteksti.getText().toString();
                        String tunnus = tunnusteksti.getText().toString();
                        String luokka = luokkateksti.getText().toString();
                        String viesti = viestiteksti.getText().toString();
                        boolean lisattyKommentti = db.lisaaKommentti(primaryKey, tunnus, luokka, viesti, kommentti);
                        if(lisattyKommentti){
                            Toast.makeText(getActivity(), "Tallennettu", Toast.LENGTH_LONG).show();
                            kommenttiteksti.setCursorVisible(false);
                            if(getActivity() != null) {
                                getActivity().onBackPressed();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Tallennus epäonnistui", Toast.LENGTH_LONG).show();
                        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
    }
}
