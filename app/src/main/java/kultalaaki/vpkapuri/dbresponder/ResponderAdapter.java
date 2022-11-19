/*
 * Created by Kultala Aki on 10.7.2019 23:01
 * Copyright (c) 2019. All rights reserved.
 * Last modified 7.7.2019 12:26
 */

package kultalaaki.vpkapuri.dbresponder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import kultalaaki.vpkapuri.R;

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

        ResponderHolder(@NonNull View itemView) {
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
