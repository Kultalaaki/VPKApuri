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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 500);
        mRecyclerView.setLayoutManager(layoutManager);

        // Basic GridLayoutManager
        /*GridLayoutManager manager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);*/

        mViewModel = ViewModelProviders.of(this).get(ResponderViewModel.class);
        mViewModel.getAllResponders().observe(this, new Observer<List<Responder>>() {
            @Override
            public void onChanged(@Nullable List<Responder> responders) {
                // TODO: update RecyclerView
                adapter.setResponders(responders);
            }
        });
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
