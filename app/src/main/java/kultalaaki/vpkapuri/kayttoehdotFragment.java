package kultalaaki.vpkapuri;


import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link kayttoehdotFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class kayttoehdotFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    CheckBox approveAnalytics, tietosuoja, kayttoehdot;
    TextView tietosuojaLink, kayttoehdotLink;
    Button Ok;
    SharedPreferences sharedPreferences;


    public kayttoehdotFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment kayttoehdotFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static kayttoehdotFragment newInstance(String param1, String param2) {
        kayttoehdotFragment fragment = new kayttoehdotFragment();
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
        //sharedPreferences.edit().putBoolean("termsShown", true).commit();
        if(sharedPreferences.getBoolean("termsShown", true)) {
            sharedPreferences.edit().putBoolean("tietosuoja", false).commit();
            sharedPreferences.edit().putBoolean("kayttoehdot", false).commit();
            sharedPreferences.edit().putBoolean("analyticsEnabled", false).commit();
            sharedPreferences.edit().putBoolean("termsShown", false).commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
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

    void setLinks() {
        tietosuojaLink.setMovementMethod(LinkMovementMethod.getInstance());
        kayttoehdotLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    void setCheckedStates() {
        if(sharedPreferences.getBoolean("analyticsEnabled", false)) {
            approveAnalytics.setChecked(true);
        } else {
            approveAnalytics.setChecked(false);
        }
        if(sharedPreferences.getBoolean("tietosuoja", false)) {
            tietosuoja.setChecked(true);
        } else {
            tietosuoja.setChecked(false);
        }
        if(sharedPreferences.getBoolean("kayttoehdot", false)) {
            kayttoehdot.setChecked(true);
        } else {
            kayttoehdot.setChecked(false);
        }
    }

    void checkUserConsentsAndApprovalOfThings() {
        Ok.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity());
                    Intent intent = new Intent(getActivity(), Etusivu.class);
                    startActivity(intent, options.toBundle());
                } else {
                    Intent intent = new Intent(getActivity(), Etusivu.class);
                    startActivity(intent);
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

    public void onPause() {
        super.onPause();

    }

    public void onResume() {
        super.onResume();
        //setCheckedStates();
        //checkUserConsentsAndApprovalOfThings();
    }
}
