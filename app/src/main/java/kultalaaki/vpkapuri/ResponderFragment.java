package kultalaaki.vpkapuri;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

public class ResponderFragment extends Fragment {

    private ResponderViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private CardView cardViewDeleteResponders;

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

        /**
         AutoFitGridLayoutManager that auto fits the cells by the column width defined.
         **/
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 800);
        mRecyclerView.setLayoutManager(layoutManager);

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
                mViewModel.deleteAll();
            }
        });
    }

}
