/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 10.7.2019 22:36
 */

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResponderFragment extends Fragment {

    private ResponderViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private CardView cardViewDeleteResponders;
    private TextView combinedComers, smokeDivers;
    private int deleteCounter = 0, combined = 0, smokes = 0;
    private Map<String, Responder> responderHolderMap = new HashMap<>();
    //private ArrayList<Responder> responderList = new ArrayList<>();
    private ArrayList<String> responderNumbersHolder = new ArrayList<>();

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
                // TODO: update RecyclerView
                if (responders != null) {
                    if (!responders.isEmpty()) {
                        combined = responders.size();
                        /*
                         * Go through all responders
                         * Find duplicate responders based on number
                         * Show only last message per person
                         *
                         * Calculate all responders and smoke divers.
                         * */
                        for (Responder responder : responders) {
                            //responderNumbersHolder.add(responder.getAttributeOptional2());
                            //responderHolderMap.put(responder.getAttributeOptional2(), responder);
                            /*if (responderNumbersHolder.contains(responder.getAttributeOptional2())) {
                                // Contains same number. Get responder from Map and update the message.
                                Responder updater = responderHolderMap.get(responder.getAttributeOptional2());
                                if (updater != null) {
                                    // Order is this because ViewModel returns responders in descending list
                                    // Current responder number is equal to responder in responderHolderMap
                                    responder.setMessage(updater.getMessage());
                                    // Remove from list if person cancels
                                    if (updater.getMessage().trim().equals("Peruutus")) {
                                        mViewModel.delete(responder);
                                    } else {
                                        mViewModel.update(responder);
                                    }
                                    //mViewModel.delete(updater);
                                    //responderNumbersHolder.remove(responder.getAttributeOptional2());
                                    responderHolderMap.clear();
                                    responderNumbersHolder.clear();
                                }
                            } else {
                                responderNumbersHolder.add(responder.getAttributeOptional2());
                                responderHolderMap.put(responder.getAttributeOptional2(), responder);
                            }*/

                            String smokeDiver = responder.getAttributeSmoke();
                            if (smokeDiver.equals("S")) {
                                smokes++;
                            }
                        }
                        /*Set<String> set = new LinkedHashSet<>();
                        set.addAll(responderNumbersHolder);
                        responderNumbersHolder.clear();
                        responderNumbersHolder.addAll(set);
                        for (String str : responderNumbersHolder) {
                            if(responderHolderMap.containsKey(str)) {

                            }
                        }*/
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
                responderHolderMap.clear();
                responderNumbersHolder.clear();
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
                if (deleteCounter < 1) {
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
}
