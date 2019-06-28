package kultalaaki.vpkapuri;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;



public class AnswerOHTOFragment extends Fragment {

    private EditText halyttavaNumero, vastausViesti;
    private CardView sendAnswer;

    private OnFragmentInteractionListener mListener;

    private FireAlarmViewModel fireAlarmViewModel;

    public AnswerOHTOFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context ctx = getActivity();
        if(ctx != null) {
            fireAlarmViewModel = ViewModelProviders.of(getActivity()).get(FireAlarmViewModel.class);
            fireAlarmViewModel.getAlarmingNumber().observe(getViewLifecycleOwner(), new Observer<CharSequence>() {
                @Override
                public void onChanged(CharSequence charSequence) {
                    halyttavaNumero.setText(charSequence);
                }
            });
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answer_ohto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        halyttavaNumero = view.findViewById(R.id.numero);
        vastausViesti = view.findViewById(R.id.vastaus_viesti);
        sendAnswer = view.findViewById(R.id.send_answer);
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
        sendAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!vastausViesti.getText().toString().trim().isEmpty() && !halyttavaNumero.getText().toString().trim().isEmpty()) {
                    // Vastausviesti ei ole tyhjä, lähetä vastaus
                    String vastaus = vastausViesti.getText().toString().trim();
                    try {
                        //int permissionChecks = ContextCompat.checkSelfPermission(aktiivinenHaly.this, Manifest.permission.SEND_SMS);
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(halyttavaNumero.getText().toString(), null, vastaus, null, null);
                        Toast.makeText(getActivity(),"Vastauksesi on lähetetty. (" + vastaus + ")", Toast.LENGTH_LONG).show();
                    } catch(Exception e) {
                        Toast.makeText(getActivity(),"Tekstiviestin lähetys ei onnistunut.",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity(), "Et voi lähettää tyhjää viestiä.", Toast.LENGTH_LONG).show();
                }
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
