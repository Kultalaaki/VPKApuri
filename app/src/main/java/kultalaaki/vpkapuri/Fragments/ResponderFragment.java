/*
 * Created by Kultala Aki on 6/26/22, 6:19 PM
 * Copyright (c) 2022. All rights reserved.
 * Last modified 6/26/22, 6:02 PM
 */

package kultalaaki.vpkapuri.Fragments;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.TextView;

import java.util.List;

import kultalaaki.vpkapuri.AutoFitGridLayoutManager;
import kultalaaki.vpkapuri.R;
import kultalaaki.vpkapuri.Responder;
import kultalaaki.vpkapuri.ResponderAdapter;
import kultalaaki.vpkapuri.ResponderViewModel;

public class ResponderFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ResponderViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private CardView cardViewDeleteResponders;
    private TextView combinedComers, smokeDivers;
    private int deleteCounter = 0, combined = 0, smokes = 0;


    public static ResponderFragment newInstance() {
        return new ResponderFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ResponderFragment.OnFragmentInteractionListener) {
            mListener = (ResponderFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
        combinedComers = view.findViewById(R.id.combined);
        smokeDivers = view.findViewById(R.id.smokeDivers);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final ResponderAdapter adapter = new ResponderAdapter();
        mRecyclerView.setAdapter(adapter);

        mViewModel = ViewModelProviders.of(this).get(ResponderViewModel.class);
        mViewModel.getAllResponders().observe(getViewLifecycleOwner(), new Observer<List<Responder>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onChanged(@Nullable List<Responder> responders) {
                if (responders != null) {
                    if (!responders.isEmpty()) {
                        combined = responders.size();
                        /*
                         * Go through all responders
                         * Remove responder if message is "Peruutus"
                         *
                         * Calculate all responders and smoke divers.
                         * */
                        for (Responder responder : responders) {
                            if (responder.getAttributeSmoke().equals("S")) {
                                smokes++;
                            }
                            if (responder.getMessage().equals("Peruutus")) {
                                mViewModel.delete(responder);
                            }
                        }

                        combinedComers.setText("Yht: " + combined);
                        smokeDivers.setText("Savu: " + smokes);
                        smokes = 0;
                    } else {
                        combinedComers.setText("Yht: 0");
                        smokeDivers.setText("Savu: 0");
                    }
                } else {
                    combinedComers.setText("Yht: 0");
                    smokeDivers.setText("Savu: 0");
                }
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
                //Toast.makeText(getActivity(), "Lähtijä poistettu listalta!", Toast.LENGTH_LONG).show();
                mListener.showToast("", "Lähtijä poistettu listalta.");
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    public void onStart() {
        super.onStart();

        cardViewDeleteResponders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteCounter < 1) {
                    mListener.showToast("Lähtijät.", "Paina uudestaan tyhjentääksesi lista!");
                    deleteCounter++;
                } else {
                    mViewModel.deleteAll();
                    deleteCounter = 0;
                    mListener.showToast("Lähtijät.", "Tyhjennetty!");
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

        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // In landscape
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 400);
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
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // In portrait
            AutoFitGridLayoutManager layoutManager = new AutoFitGridLayoutManager(getActivity(), 800);
            mRecyclerView.setLayoutManager(layoutManager);
        }
    }

    public interface OnFragmentInteractionListener {
        void showToast(String head, String message);
    }
}
