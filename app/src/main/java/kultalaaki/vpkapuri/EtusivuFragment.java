package kultalaaki.vpkapuri;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EtusivuFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EtusivuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EtusivuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    CardView halytys, carkisto, ohjeet, csettings;

    private OnFragmentInteractionListener mListener;

    public EtusivuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EtusivuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EtusivuFragment newInstance(String param1, String param2) {
        EtusivuFragment fragment = new EtusivuFragment();
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
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        carkisto = view.findViewById(R.id.card_viewArkisto);
        ohjeet = view.findViewById(R.id.card_viewOhjeet);
        csettings = view.findViewById(R.id.card_viewAsetukset);
        halytys = view.findViewById(R.id.card_viewHaly);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_etusivu, container, false);
    }

    public void onStart() {
        super.onStart();
        halytys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avaaHaly();
            }
        });

        carkisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avaaArkisto();
            }
        });

        ohjeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avaaOhjeet();
            }
        });

        csettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                avaaAsetukset();
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

    public void avaaHaly () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            Intent intent = new Intent(getActivity(), HalytysActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), HalytysActivity.class);
            startActivity(intent);
        }
    }

    public void avaaArkisto () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            Intent intent = new Intent(getActivity(), ArkistoActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), ArkistoActivity.class);
            startActivity(intent);
        }
    }

    public void avaaOhjeet () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            Intent intent = new Intent(getActivity(), OhjeitaActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), OhjeitaActivity.class);
            startActivity(intent);
        }
    }

    public void avaaAsetukset () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
    }


}
