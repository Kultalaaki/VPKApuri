/*
 * Created by Kultala Aki on 2/7/21 9:48 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 2/6/21 11:29 AM
 */

package kultalaaki.vpkapuri;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;


public class FrontpageFragment extends Fragment {

    private CardView halytys, carkisto, ohjeet, csettings;

    private OnFragmentInteractionListener mListener;

    public FrontpageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() != null) {
            SharedPreferences pref_general = PreferenceManager.getDefaultSharedPreferences(getActivity());

            boolean analytics = pref_general.getBoolean("analyticsEnabled", false);

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

            mFirebaseAnalytics.setAnalyticsCollectionEnabled(analytics);

            /*if(analytics) {
                Fabric.with(getActivity(), new Crashlytics());
            }*/
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                    Intent intent = new Intent(getActivity(), AlarmActivity.class);
                    startActivity(intent, options.toBundle());
                } else {
                    Intent intent = new Intent(getActivity(), AlarmActivity.class);
                    startActivity(intent);
                }
            }
        });

        carkisto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadArkistoFragment();
            }
        });

        ohjeet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadOhjeetFragment();
            }
        });

        csettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Todo ask permissions and let activity start setting page
                mListener.askPermissionReadExternalStorage();
                /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent, options.toBundle());
                } else {
                    Intent intent = new Intent(getActivity(), SettingsActivity.class);
                    startActivity(intent);
                }*/
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

        void askPermissionReadExternalStorage();
    }
}
