package kultalaaki.vpkapuri;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ResponderAdapter extends ListAdapter<Responder, ResponderAdapter.ResponderHolder> {

    public ResponderAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Responder> DIFF_CALLBACK = new DiffUtil.ItemCallback<Responder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Responder responder, @NonNull Responder t1) {
            return responder.getId() == t1.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Responder responder, @NonNull Responder t1) {
            return responder.getName().equals(t1.getName()) &&
                    responder.getVacancyNumber().equals(t1.getVacancyNumber()) &&
                    responder.getMessage().equals(t1.getMessage());
        }
    };


    @NonNull
    @Override
    public ResponderHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.responder_item, viewGroup, false);
        return new ResponderHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ResponderHolder responderHolder, int i) {
        Responder currentResponder = getItem(i);
        responderHolder.textViewName.setText(currentResponder.getName());
        responderHolder.textViewVacancyNumber.setText(currentResponder.getVacancyNumber());
        responderHolder.textViewMessage.setText(currentResponder.getMessage());
        responderHolder.textViewAttributeLeader.setText(currentResponder.getAttributeLeader());
        responderHolder.textViewAttributeDriverLicense.setText(currentResponder.getAttributeDriverLicense());
        responderHolder.textViewAttributeSmoke.setText(currentResponder.getAttributeSmoke());
        responderHolder.textViewAttributeChemical.setText(currentResponder.getAttributeChemical());
        responderHolder.textViewAttributeOptional1.setText(currentResponder.getAttributeOptional1());
        responderHolder.textViewAttributeOptional2.setText(currentResponder.getAttributeOptional2());
        responderHolder.textViewAttributeOptional3.setText(currentResponder.getAttributeOptional3());
        responderHolder.textViewAttributeOptional4.setText(currentResponder.getAttributeOptional4());
        responderHolder.textViewAttributeOptional5.setText(currentResponder.getAttributeOptional5());
    }

    /*@Override
    public int getItemCount() {
        return responders.size();
    }*/

    public Responder getResponderAt(int position) {
        return getItem(position);
    }

    /*public void setResponders(List<Responder> responders) {
        this.responders = responders;
        notifyDataSetChanged();
    }*/

    class ResponderHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewVacancyNumber;
        private TextView textViewMessage;
        private TextView textViewAttributeLeader;
        private TextView textViewAttributeDriverLicense;
        private TextView textViewAttributeSmoke;
        private TextView textViewAttributeChemical;
        private TextView textViewAttributeOptional1;
        private TextView textViewAttributeOptional2;
        private TextView textViewAttributeOptional3;
        private TextView textViewAttributeOptional4;
        private TextView textViewAttributeOptional5;

        public ResponderHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewVacancyNumber = itemView.findViewById(R.id.text_view_attribute_vacancy);
            textViewMessage = itemView.findViewById(R.id.text_view_message);
            textViewAttributeLeader = itemView.findViewById(R.id.text_view_attribute_leader);
            textViewAttributeDriverLicense = itemView.findViewById(R.id.text_view_attribute_driver_license);
            textViewAttributeSmoke = itemView.findViewById(R.id.text_view_attribute_smoke);
            textViewAttributeChemical = itemView.findViewById(R.id.text_view_attribute_chemical);
            textViewAttributeOptional1 = itemView.findViewById(R.id.text_view_attribute_optional1);
            textViewAttributeOptional2 = itemView.findViewById(R.id.text_view_attribute_optional2);
            textViewAttributeOptional3 = itemView.findViewById(R.id.text_view_attribute_optional3);
            textViewAttributeOptional4 = itemView.findViewById(R.id.text_view_attribute_optional4);
            textViewAttributeOptional5 = itemView.findViewById(R.id.text_view_attribute_optional5);
        }
    }
}
/**
 * public FireAlarmAdapter() {
 *         super(DIFF_CALLBACK);
 *     }
 *
 *     private static final DiffUtil.ItemCallback<FireAlarm> DIFF_CALLBACK = new DiffUtil.ItemCallback<FireAlarm>() {
 *         @Override
 *         public boolean areItemsTheSame(@NonNull FireAlarm oldItem, @NonNull FireAlarm newItem) {
 *             return oldItem.getId() == newItem.getId();
 *         }
 *
 *         @Override
 *         public boolean areContentsTheSame(@NonNull FireAlarm oldItem, @NonNull FireAlarm newItem) {
 *             return oldItem.getViesti().equals(newItem.getViesti()) &&
 *                     oldItem.getLuokka().equals(newItem.getLuokka()) &&
 *                     oldItem.getTunnus().equals(newItem.getTunnus());
 *         }
 *     };
 *
 *     @NonNull
 *     @Override
 *     public FireAlarmHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
 *         View itemView = LayoutInflater.from(viewGroup.getContext())
 *                 .inflate(R.layout.item_layout, viewGroup, false);
 *         return new FireAlarmHolder(itemView);
 *     }
 *
 *     @Override
 *     public void onBindViewHolder(@NonNull FireAlarmHolder fireAlarmHolder, int i) {
 *         FireAlarm currentAlarm = getItem(i);
 *         fireAlarmHolder.textViewSija.setText(currentAlarm.getId());
 *         fireAlarmHolder.textViewTunnus.setText(currentAlarm.getTunnus());
 *         fireAlarmHolder.textViewViesti.setText(currentAlarm.getViesti());
 *     }
 *
 *     public FireAlarm getFireAlarmAt(int position) {return getItem(position);}
 *
 *     class FireAlarmHolder extends RecyclerView.ViewHolder {
 *         private TextView textViewSija;
 *         private TextView textViewTunnus;
 *         private TextView textViewViesti;
 *
 *         public FireAlarmHolder (@NonNull View itemView) {
 *             super(itemView);
 *             textViewSija = itemView.findViewById(R.id.sija);
 *             textViewTunnus = itemView.findViewById(R.id.tunnus);
 *             textViewViesti = itemView.findViewById(R.id.viesti);
 *         }
 *     }
 */
