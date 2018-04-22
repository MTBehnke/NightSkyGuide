// Subclass of RecyclerView.Adapter that binds dsObjects to RecyclerView

package com.mikesrv9a.nightskyguide;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.TextViewCompat;
import android.widget.TextView;

import java.util.ArrayList;

public class DSObjectsClickAdapter
        extends RecyclerView.Adapter<DSObjectsClickAdapter.DSObjectsClickViewHolder> {

    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView

    public class DSObjectsClickViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener{

        TextView textViewR1C1;
        TextView textViewR1C2;
        TextView textViewR1C3;
        TextView textViewR1C4;
        TextView textViewR2C1;
        TextView textViewR2C2;
        TextView textViewR2C3;
        TextView textViewR2C4;

        // configures a RecyclerView item's ViewHolder
        public DSObjectsClickViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            textViewR1C1 = (TextView) itemView.findViewById(R.id.recyclerview1);
            textViewR1C2 = (TextView) itemView.findViewById(R.id.recyclerview2);
            textViewR1C3 = (TextView) itemView.findViewById(R.id.recyclerview3);
            textViewR1C4 = (TextView) itemView.findViewById(R.id.recyclerview4);
            textViewR2C1 = (TextView) itemView.findViewById(R.id.recyclerview5);
            textViewR2C2 = (TextView) itemView.findViewById(R.id.recyclerview6);
            textViewR2C3 = (TextView) itemView.findViewById(R.id.recyclerview7);
            textViewR2C4 = (TextView) itemView.findViewById(R.id.recyclerview8);
        }

        @Override
        public void onClick(View v) {
            // The user may not set a click listener for list items, in which case our
            // listener will be null, no we need to check for this
            if (dsObjectOnEntryClickListener != null) {
                dsObjectOnEntryClickListener.onEntryClick(v, getLayoutPosition());
            }
        }
    }

    private ArrayList<DSObject> dsObjectsArrayList;
    //int screenConfig;

    public void replaceData(ArrayList<DSObject> newObjectsArrayList) {
        dsObjectsArrayList = newObjectsArrayList;
        this.notifyDataSetChanged();
    }

    public DSObjectsClickAdapter(ArrayList<DSObject> arrayList) {
        dsObjectsArrayList = arrayList;
        //screenConfig = screenOrient;
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return dsObjectsArrayList.size();
    }

    // sets up new list item and its ViewHolder
    @Override
    public DSObjectsClickViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dsobjects_recyclerview,parent,false);
        return new DSObjectsClickViewHolder(view);
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(DSObjectsClickViewHolder holder, int position) {
        DSObject object = dsObjectsArrayList.get(position);
        String observed;
        if(object.getDsoObserved()==1){observed="  √";}
                else {observed = "";}
        String arrow;
        String magText = "";
        String dsoPath = "";
        Double dsoCosHA = object.getDsoOnHorizCosHA();
        if(object.getDsoAz()>= 180){arrow = "▼";}
                else {arrow = "▲";}
        if(object.getDsoMag() != 0) {
            magText = Double.toString(object.getDsoMag());
            if(object.getDsoMag() <= 7.0) {
                magText += " ●";
            }
        }
        if(dsoCosHA < -1) {dsoPath = "○ ";}
            else if (dsoCosHA > 1) {dsoPath = "ø ";}
            else {dsoPath = "";}

        String r1c1Text = object.getDsoCatalogue() + observed;
        /*if (screenConfig == 1) {  If portrait mode then use DsoObjectID, else use longer DsoCatalogue
            r1c1Text = object.getDsoObjectID() + observed;}
        else {
            r1c1Text = object.getDsoCatalogue() + observed;}*/

        String r1c2Text = object.getDsoConst();
        String r1c3Text = magText;
        String r1c4Text = dsoPath + Integer.toString((int) Math.round(object.getDsoAlt()))
                + "°" + arrow;

        //String r1c4Text = Double.toString(object.getDsoAlt());
        String r2c1Text = object.getDsoName();
        String r2c2Text = object.getDsoType();
        if (r2c2Text=="PL") {r2c2Text="";}
        String r2c3Text = object.getDsoSize();
        String r2c4Text = Integer.toString((int) Math.round(object.getDsoAz())) + "°";
        //String r2c4Text = Double.toString(object.getDsoAz());

        holder.textViewR1C1.setText(r1c1Text);
        holder.textViewR1C2.setText(r1c2Text);
        holder.textViewR1C3.setText(r1c3Text);
        holder.textViewR1C4.setText(r1c4Text);
        holder.textViewR2C1.setText(r2c1Text);
        holder.textViewR2C2.setText(r2c2Text);
        holder.textViewR2C3.setText(r2c3Text);
        holder.textViewR2C4.setText(r2c4Text);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private onEntryClickListener dsObjectOnEntryClickListener;

    public interface onEntryClickListener {
        void onEntryClick(View view, int position);
    }

    public void setOnEntryClickListener(onEntryClickListener onEntryClickListener) {
        dsObjectOnEntryClickListener = onEntryClickListener;
    }

}