package kultalaaki.vpkapuri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class FireAlarmAdapter extends ListAdapter<FireAlarm, FireAlarmAdapter.FireAlarmHolder> {

    private OnItemClickListener listener;

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
                    oldItem.getLuokka().equals(newItem.getLuokka()) &&
                    oldItem.getTunnus().equals(newItem.getTunnus());
        }
    };

    @NonNull
    @Override
    public FireAlarmHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new FireAlarmHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull FireAlarmHolder holder, int position) {
        FireAlarm currentAlarm = getItem(position);
        holder.textViewSija.setText(currentAlarm.getId());
        holder.textViewTunnus.setText(currentAlarm.getTunnus());
        holder.textViewViesti.setText(currentAlarm.getViesti());
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
