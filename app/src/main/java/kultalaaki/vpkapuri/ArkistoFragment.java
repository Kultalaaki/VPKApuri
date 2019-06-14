package kultalaaki.vpkapuri;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ArkistoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ArkistoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ArkistoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    DBHelper db;
    private RecyclerView mRecyclerView;
    private FireAlarmViewModel mViewModel;

    private OnFragmentInteractionListener mListener;

    public ArkistoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ArkistoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ArkistoFragment newInstance(String param1, String param2) {
        ArkistoFragment fragment = new ArkistoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) { }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_arkisto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        //ctx = getActivity();
        //db = new DBHelper(ctx);
        mRecyclerView = view.findViewById(R.id.listview_alarms);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final FireAlarmAdapter adapter = new FireAlarmAdapter();
        mRecyclerView.setAdapter(adapter);

        mViewModel = ViewModelProviders.of(this).get(FireAlarmViewModel.class);
        mViewModel.getAllFireAlarms().observe(this, new Observer<List<FireAlarm>>() {
            @Override
            public void onChanged(List<FireAlarm> fireAlarms) {
                adapter.submitList(fireAlarms);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mViewModel.delete(adapter.getFireAlarmAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), "Hälytys poistettu arkistosta!", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(mRecyclerView);

        adapter.setOnItemClickListener(new FireAlarmAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(FireAlarm fireAlarm) {
                mListener.loadHalytysTietokannastaFragment(fireAlarm);
            }
        });
    }

    public void onStart() {
        super.onStart();
        //populateListView();
    }

    // TODO: Rename method, update argument and hook method into UI event
    /*public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }*/

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
        void loadHalytysTietokannastaFragment(FireAlarm fireAlarm);
    }
}
