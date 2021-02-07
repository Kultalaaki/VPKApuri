/*
 * Created by Kultala Aki on 2/7/21 9:48 AM
 * Copyright (c) 2021. All rights reserved.
 * Last modified 2/6/21 12:26 PM
 */

package kultalaaki.vpkapuri;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AffirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AffirmationFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private CheckBox approveAnalytics, tietosuoja, kayttoehdot;
    private TextView tietosuojaLink, kayttoehdotLink;
    private Button Ok;
    private SharedPreferences sharedPreferences;

    private AffirmationFragment.Listener mCallback;

    // The container Activity must implement this interface so the frag can deliver messages
    public interface Listener {
        /** Called when a button is clicked in AffirmationFragment */
        void loadEtusivuFromFragment();
    }
    public AffirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AffirmationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AffirmationFragment newInstance(String param1, String param2) {
        AffirmationFragment fragment = new AffirmationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().putBoolean("tietosuoja", false).commit();
        sharedPreferences.edit().putBoolean("kayttoehdot", false).commit();
        sharedPreferences.edit().putBoolean("analyticsEnabled", false).commit();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_kayttoehdot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        approveAnalytics = view.findViewById(R.id.approvalcheckBox);
        tietosuoja = view.findViewById(R.id.tietosuojacheckBox);
        kayttoehdot = view.findViewById(R.id.kayttoehdotcheckBox);
        tietosuojaLink = view.findViewById(R.id.tietosuoja);
        kayttoehdotLink = view.findViewById(R.id.kayttoehdot);
        Ok = view.findViewById(R.id.okButton);
    }

    public void onStart() {
        super.onStart();
        setCheckedStates();
        setLinks();
        checkUserConsentsAndApprovalOfThings();
    }

    private void setLinks() {
        tietosuojaLink.setMovementMethod(LinkMovementMethod.getInstance());
        kayttoehdotLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void setCheckedStates() {
        approveAnalytics.setChecked(sharedPreferences.getBoolean("analyticsEnabled", false));
        tietosuoja.setChecked(sharedPreferences.getBoolean("tietosuoja", false));
        kayttoehdot.setChecked(sharedPreferences.getBoolean("kayttoehdot", false));
    }

    private void checkUserConsentsAndApprovalOfThings() {
        Ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {

                if(sharedPreferences.getBoolean("kayttoehdot", false) && sharedPreferences.getBoolean("tietosuoja", false) &&
                        sharedPreferences.getBoolean("analyticsEnabled", false)) {
                    letIn();
                } else if (sharedPreferences.getBoolean("kayttoehdot", false) && sharedPreferences.getBoolean("tietosuoja", false) &&
                        !sharedPreferences.getBoolean("analyticsEnabled", false)) {
                    // show dialog and ask analytics again, if denied let user in anyway
                    showDialog(
                            "Et  antanut lupaa analytiikka tietojen keräämiseen.",
                            "Tämä auttaa sovelluksen kehittämisessä paremmaksi ja toimivammaksi.\n\nHaluatko jatkaa ilman tietojen keräämistä?",
                            "Peruuta",
                            "Jatka",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    letIn();
                                }
                            });
                } else if(!sharedPreferences.getBoolean("kayttoehdot", false) && sharedPreferences.getBoolean("tietosuoja", false) ||
                        sharedPreferences.getBoolean("kayttoehdot", false) && !sharedPreferences.getBoolean("tietosuoja", false)) {
                    // inform user that terms&conditions and privacy policy must be accepted to use this app
                    showDialog(
                            "Huomautus!",
                            "Tietosuoja ja käyttöehdot täytyy hyväksyä ennen kuin voit jatkaa sovelluksen käyttöä.",
                            "OK");
                }

            }
        });

        approveAnalytics.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("analyticsEnabled", false)) {
                    sharedPreferences.edit().putBoolean("analyticsEnabled", false).commit();
                    setCheckedStates();
                } else {
                    sharedPreferences.edit().putBoolean("analyticsEnabled", true).commit();
                    setCheckedStates();
                }
            }
        });

        tietosuoja.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("tietosuoja", false)) {
                    sharedPreferences.edit().putBoolean("tietosuoja", false).commit();
                    setCheckedStates();
                } else {
                    sharedPreferences.edit().putBoolean("tietosuoja", true).commit();
                    setCheckedStates();
                }
            }
        });

        kayttoehdot.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean("kayttoehdot", false)) {
                    sharedPreferences.edit().putBoolean("kayttoehdot", false).commit();
                    setCheckedStates();
                } else {
                    sharedPreferences.edit().putBoolean("kayttoehdot", true).commit();
                    setCheckedStates();
                }
            }
        });
    }

    @SuppressLint("ApplySharedPref")
    private void letIn() {
        sharedPreferences.edit().putBoolean("termsShown", true).commit();
        mCallback.loadEtusivuFromFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (AffirmationFragment.Listener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement Listener");
        }
    }

    private void showMessage(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("Huomautus!")
                .setMessage("Et  antanut lupaa analytiikka tietojen keräämiseen. Tämä auttaa sovelluksen kehittämisessä. Paina Ok jos haluat jatkaa ilman tietojen keräämistä.")
                .setNegativeButton("Peruuta", null)
                .setPositiveButton(android.R.string.ok, new Dialog.OnClickListener() {

                    public void onClick(DialogInterface dialogInterface, int i) {
                        letIn();
                    }
                });
        builder.create().show();
    }

    public void showDialog(String upperText, String lowerText, String positiveButtonText) {
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_permissions, null);
        TextView whatPermission = dialogLayout.findViewById(R.id.textViewWhatPermission);
        TextView whatReason = dialogLayout.findViewById(R.id.textViewReasoning);
        whatPermission.setText(upperText);
        whatReason.setText(lowerText);
        new AlertDialog.Builder(getActivity())
                .setView(dialogLayout)
                .setPositiveButton(positiveButtonText, null)
                .create()
                .show();
    }

    private void showDialog(String upperText, String lowerText, String neutralButtonText, String positiveButtonText, DialogInterface.OnClickListener okListener) {
        final View dialogLayout = getLayoutInflater().inflate(R.layout.dialog_permissions, null);
        TextView dialogUpperText = dialogLayout.findViewById(R.id.textViewWhatPermission);
        TextView dialogLowerText = dialogLayout.findViewById(R.id.textViewReasoning);
        dialogUpperText.setText(upperText);
        dialogLowerText.setText(lowerText);

        new AlertDialog.Builder(getActivity())
                .setView(dialogLayout)
                .setPositiveButton(positiveButtonText, okListener)
                .setNeutralButton(neutralButtonText, null)
                .create()
                .show();
    }

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();
        //setCheckedStates();
        //checkUserConsentsAndApprovalOfThings();
    }
}
