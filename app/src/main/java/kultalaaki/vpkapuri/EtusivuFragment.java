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


public class EtusivuFragment extends Fragment {

    CardView halytys, carkisto, ohjeet, csettings;

    private OnFragmentInteractionListener mListener;

    public EtusivuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        void loadArkistoFragment();
        void loadOhjeetFragment();

        void testResponderFragment();
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
        mListener.loadArkistoFragment();
        mListener.testResponderFragment();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            Intent intent = new Intent(getActivity(), ArkistoActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), ArkistoActivity.class);
            startActivity(intent);
        }*/
    }

    public void avaaOhjeet () {
        mListener.loadOhjeetFragment();
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
            Intent intent = new Intent(getActivity(), OhjeitaActivity.class);
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(getActivity(), OhjeitaActivity.class);
            startActivity(intent);
        }*/
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
