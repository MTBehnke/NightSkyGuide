package com.mikesrv9a.nightskyguide;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ObservationsClickAdapter extends RecyclerView.Adapter<ObservationsClickAdapter.ObservationsClickViewHolder> {

    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView

    public class ObservationsClickViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        TextView textViewR1C1;
        TextView textViewR1C2;
        TextView textViewR2C1;
        TextView textViewR2C2;

        // configures a RecyclerView item's ViewHolder
        public ObservationsClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewR1C1 = (TextView) itemView.findViewById(R.id.recyclerview1);
            textViewR1C2 = (TextView) itemView.findViewById(R.id.recyclerview2);
            textViewR2C1 = (TextView) itemView.findViewById(R.id.recyclerview3);
            textViewR2C2 = (TextView) itemView.findViewById(R.id.recyclerview4);
        }

        @Override
        public void onClick(View v) {
            // The user may not set a click listener for list items, in which case our
            // listener will be null, no we need to check for this
            if (observationOnEntryClickListener != null) {
                observationOnEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    private ArrayList<Observation> observationArrayList;

    public ObservationsClickAdapter(ArrayList<Observation> arrayList) {
        observationArrayList = arrayList;
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return observationArrayList.size();
    }

    // sets up new list item and its ViewHolder
    @Override
    public ObservationsClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.observations_recyclerview,parent,false);
        return new ObservationsClickViewHolder(view);
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(ObservationsClickAdapter.ObservationsClickViewHolder holder, int position) {
        Observation object = observationArrayList.get(position);

        String r1c1Text = object.getObsCatalogue();

        String r2c1Text = "";
        if (object.getObsProgram() != null && !object.getObsProgram().isEmpty()) { r2c1Text = "AL: " + object.getObsProgram(); }

        String tempDateString = object.getObsDate();
        //Integer index = tempDateString.indexOf(" ");    // strips time portion of datetime string to display only date portion
        //if (index != -1) { tempDateString = tempDateString.substring(0, index); }
        String r1c2Text = tempDateString;

        String tempLocString = object.getObsLocation();
        Integer index = tempLocString.indexOf("°");
        if (index != -1) { tempLocString = "(gps)";}
        String r2c2Text = tempLocString;

        holder.textViewR1C1.setText(r1c1Text);
        holder.textViewR1C2.setText(r1c2Text);
        holder.textViewR2C1.setText(r2c1Text);
        holder.textViewR2C2.setText(r2c2Text);

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private ObservationsClickAdapter.onEntryClickListener observationOnEntryClickListener;

    public interface onEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(ObservationsClickAdapter.onEntryClickListener onEntryClickListener) {
        observationOnEntryClickListener = onEntryClickListener;
    }

}