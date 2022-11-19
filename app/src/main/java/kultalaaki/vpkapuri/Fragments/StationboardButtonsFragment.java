/*
 * Created by Kultala Aki on 6/26/22, 6:19 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.dbfirealarm.FireAlarmViewModel;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StationboardButtonsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StationboardButtonsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StationboardButtonsFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int MY_PERMISSION_REQUEST_CAMERA = 1;

    private CardView osoiteKortti;
    private CardView responderKortti;
    private CardView kameraKortti;
    private CardView manpowerCard;

    private TextView osoite;
    private String osoiteFromDB;

    private OnFragmentInteractionListener mListener;

    public StationboardButtonsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StationboardButtonsFragment.
     */
    public static StationboardButtonsFragment newInstance(String param1, String param2) {
        StationboardButtonsFragment fragment = new StationboardButtonsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_asemataulu_buttons, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        osoiteKortti = view.findViewById(R.id.addressCard);
        kameraKortti = view.findViewById(R.id.cameraCard);
        responderKortti = view.findViewById(R.id.responderCard);
        manpowerCard = view.findViewById(R.id.manpowerCard);
        //manpowerCard.setVisibility(View.INVISIBLE);
        osoite = view.findViewById(R.id.osoiteteksti);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context ctx = getActivity();
        if(ctx != null) {
            FireAlarmViewModel fireAlarmViewModel = ViewModelProviders.of(getActivity()).get(FireAlarmViewModel.class);
            fireAlarmViewModel.getAddress().observe(getViewLifecycleOwner(), new Observer<CharSequence>() {
                @Override
                public void onChanged(CharSequence charSequence) {
                    osoiteFromDB = charSequence.toString();
                    osoite.setText(charSequence);
                }
            });
        }

        /*FireAlarmViewModel mViewModel = ViewModelProviders.of(this).get(FireAlarmViewModel.class);

        try {
            FireAlarm fireAlarm = mViewModel.lastEntry();
            osoiteFromDB = fireAlarm.getOsoite();
            message = fireAlarm.getViesti();
            osoite.setText(osoiteFromDB);
        } catch (Exception e) {
            // Empty database
            osoiteFromDB = "";
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        osoiteKortti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + osoiteFromDB);
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

        kameraKortti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraPermissionCheck();
            }
        });

        responderKortti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.loadResponderFragment();
            }
        });

        manpowerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mListener.loadManpowerFragment();
                //Toast.makeText(getActivity(), "Näytä vahvuudet fragment", Toast.LENGTH_LONG).show();
                Intent tokeva = new Intent(Intent.ACTION_VIEW);
                tokeva.setData(Uri.parse("https://tokeva.fi/#/tervetuloa"));
                startActivity(tokeva);
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

    private void cameraPermissionCheck() {
        if(getActivity() != null) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                // Pitäisikö näyttää selite?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.SEND_SMS)) {

                    // Näytä selite, älä blokkaa threadia.
                    showMessage(
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    requestPermissions(new String[] {Manifest.permission.CAMERA},
                                            MY_PERMISSION_REQUEST_CAMERA);
                                }
                            });
                } else {

                    // Selitettä ei tarvita.

                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSION_REQUEST_CAMERA);
                }
            } else {
                // Permission granted
                mListener.openCamera();
            }
        }
    }

    private void showMessage(DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getActivity())
                .setMessage("Sovelluksella ei ole lupaa käyttää kameraa.")
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Peruuta", null)
                .create()
                .show();
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
        void openCamera();
        void loadResponderFragment();
        //void loadManpowerFragment();
    }
}
