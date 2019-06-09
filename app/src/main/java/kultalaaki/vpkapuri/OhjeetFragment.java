package kultalaaki.vpkapuri;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OhjeetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OhjeetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OhjeetFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    ViewGroup transitionsContainer;
    Button alkusanat, halytys, asetukset, arkisto, whatsapp, yleista;
    TextView talkusanat, thalytys, tasetukset, tarkisto, twhatsapp, tyleista;

    private OnFragmentInteractionListener mListener;

    public OhjeetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OhjeetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OhjeetFragment newInstance(String param1, String param2) {
        OhjeetFragment fragment = new OhjeetFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ohjeet, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        transitionsContainer = view.findViewById(R.id.cont);
        alkusanat = view.findViewById(R.id.buttonAlkusanat);
        halytys = view.findViewById(R.id.buttonHalytys);
        asetukset = view.findViewById(R.id.buttonAsetukset);
        arkisto = view.findViewById(R.id.buttonarkisto);
        whatsapp = view.findViewById(R.id.buttonWhatsapp);
        yleista = view.findViewById(R.id.buttonYleista);
        talkusanat = view.findViewById(R.id.textAlkusanat);
        thalytys = view.findViewById(R.id.thalytys);
        tasetukset = view.findViewById(R.id.tasetukset);
        tarkisto = view.findViewById(R.id.tarkisto);
        twhatsapp = view.findViewById(R.id.twhatsapp);
        tyleista = view.findViewById(R.id.tyleista);
    }

    public void onStart() {
        super.onStart();
        //final ViewGroup transitionsContainer = findViewById(R.id.cont);
        //final Button alkusanat = findViewById(R.id.buttonAlkusanat);
        //final TextView talkusanat = findViewById(R.id.textAlkusanat);
        //talkusanat.setVisibility(View.GONE);
        alkusanat.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                talkusanat.setVisibility(visible ? View.GONE : View.VISIBLE);
            }
        });

        //final Button halytys = findViewById(R.id.buttonHalytys);
        //final TextView thalytys = findViewById(R.id.thalytys);
        thalytys.setVisibility(View.GONE);
        halytys.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                thalytys.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        //final Button asetukset = findViewById(R.id.buttonAsetukset);
        //final TextView tasetukset = findViewById(R.id.tasetukset);
        tasetukset.setVisibility(View.GONE);
        asetukset.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                tasetukset.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        //final Button arkisto = findViewById(R.id.buttonarkisto);
        //final TextView tarkisto = findViewById(R.id.tarkisto);
        tarkisto.setVisibility(View.GONE);
        arkisto.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                tarkisto.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        //final Button whatsapp = findViewById(R.id.buttonWhatsapp);
        //final TextView twhatsapp = findViewById(R.id.twhatsapp);
        twhatsapp.setVisibility(View.GONE);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                twhatsapp.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

        //final Button yleista = findViewById(R.id.buttonYleista);
        //final TextView tyleista = findViewById(R.id.tyleista);
        tyleista.setVisibility(View.GONE);
        yleista.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {
                TransitionManager.beginDelayedTransition(transitionsContainer);
                visible = !visible;
                tyleista.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });
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
