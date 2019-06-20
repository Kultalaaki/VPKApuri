package kultalaaki.vpkapuri;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class ResponderFragment extends Fragment {

    private ResponderViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private CardView cardViewDeleteResponders;
    private int deleteCounter = 0;

    public static ResponderFragment newInstance() {
        return new ResponderFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.responder_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mRecyclerView = view.findViewById(R.id.recycler_view_responders);
        cardViewDeleteResponders = view.findViewById(R.id.cardview_delete_responders);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ResponderAdapter adapter = new ResponderAdapter();
        mRecyclerView.setAdapter(adapter);

        // Basic GridLayoutManager
        /*GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);*/

        mViewModel = ViewModelProviders.of(this).get(ResponderViewModel.class);
        mViewModel.getAllResponders().observe(this, new Observer<List<Responder>>() {
            @Override
            public void onChanged(@Nullable List<Responder> responders) {
                // TODO: update RecyclerView
                adapter.submitList(responders);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mViewModel.delete(adapter.getResponderAt(viewHolder.getAdapterPosition()));
                Toast.makeText(getActivity(), "Lähtijä poistettu listalta!", Toast.LENGTH_LONG).show();
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    public void onStart() {
        super.onStart();

        cardViewDeleteResponders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(deleteCounter < 1) {
                    Toast.makeText(getActivity(), "Paina uudestaan tyhjentääksesi lähtijät lista!", Toast.LENGTH_LONG).show();
                    deleteCounter++;
                } else {
                    mViewModel.deleteAll();
                    deleteCounter = 0;
                    Toast.makeText(getActivity(), "Tyhjennetty!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @SuppressLint("ApplySharedPref")
    @Override
    public void onPause() {
        super.onPause();
        deleteCounter = 0;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().putBoolean("responderFragmentShowing", false).commit();
    }

    public void onResume() {
        super.onResume();
        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/
        int orientation = getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 500);
            mRecyclerView.setLayoutManager(layoutManager);
        } else {
            // In portrait
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 800);
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 500);
            mRecyclerView.setLayoutManager(layoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            // In portrait
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 800);
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }
}
