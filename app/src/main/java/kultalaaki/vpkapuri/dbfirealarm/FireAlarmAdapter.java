/*
 * Created by Kultala Aki on 2.4.2022 10.05
 * Copyright (c) 2022. All rights reserved.
 * Last modified 1.8.2021 15.43
 */

package kultalaaki.vpkapuri.dbfirealarm;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import kultalaaki.vpkapuri.R;

public class FireAlarmAdapter extends ListAdapter<FireAlarm, FireAlarmAdapter.FireAlarmHolder> {

    private OnItemClickListener listener;
    private Integer alarmNumber = 1;

    public FireAlarmAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<FireAlarm> DIFF_CALLBACK = new DiffUtil.ItemCallback<FireAlarm>() {
        @Override
        public boolean areItemsTheSame(@NonNull FireAlarm oldItem, @NonNull FireAlarm newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull FireAlarm oldItem, @NonNull FireAlarm newItem) {
            return oldItem.getViesti().equals(newItem.getViesti()) &&
                    oldItem.getKiireellisyystunnus().equals(newItem.getKiireellisyystunnus()) &&
                    oldItem.getTehtavaluokka().equals(newItem.getTehtavaluokka());
        }
    };

    @NonNull
    @Override
    public FireAlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new FireAlarmHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FireAlarmHolder holder, int position) {
        FireAlarm currentAlarm = getItem(position);
        // TODO: Sijanumero muuttaa niin että saadaan arkistossa näkyviin monesko hälytys uusin aina on
        //holder.textViewSija.setText(Integer.toString(currentAlarm.getId()));
        holder.textViewSija.setText(alarmNumber.toString());
        holder.textViewTunnus.setText(currentAlarm.getTehtavaluokka());
        holder.textViewViesti.setText(currentAlarm.getViesti());
        alarmNumber++;
    }

    public FireAlarm getFireAlarmAt(int position) {
        return getItem(position);
    }

    class FireAlarmHolder extends RecyclerView.ViewHolder {
        private TextView textViewSija;
        private TextView textViewTunnus;
        private TextView textViewViesti;

        public FireAlarmHolder(@NonNull View itemView) {
            super(itemView);
            textViewSija = itemView.findViewById(R.id.sija);
            textViewTunnus = itemView.findViewById(R.id.tunnus);
            textViewViesti = itemView.findViewById(R.id.viesti);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(FireAlarm fireAlarm);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
