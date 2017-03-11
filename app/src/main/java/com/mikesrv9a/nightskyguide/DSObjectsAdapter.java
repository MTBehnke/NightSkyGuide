// Subclass of RecyclerView.Adapter that binds dsObjects to RecyclerView

package com.mikesrv9a.nightskyguide;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObjectDB;

import java.util.ArrayList;

public class DSObjectsAdapter
    extends RecyclerView.Adapter<DSObjectsAdapter.ViewHolder> {

    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewR1C1;
        TextView textViewR1C2;
        TextView textViewR1C3;
        TextView textViewR1C4;
        TextView textViewR2C1;
        TextView textViewR2C2;
        TextView textViewR2C3;
        TextView textViewR2C4;

        // configures a RecyclerView item's ViewHolder
        public ViewHolder (View itemView) {
            super(itemView);
            textViewR1C1 = (TextView) itemView.findViewById(R.id.recyclerview1);
            textViewR1C2 = (TextView) itemView.findViewById(R.id.recyclerview2);
            textViewR1C3 = (TextView) itemView.findViewById(R.id.recyclerview3);
            textViewR1C4 = (TextView) itemView.findViewById(R.id.recyclerview4);
            textViewR2C1 = (TextView) itemView.findViewById(R.id.recyclerview5);
            textViewR2C2 = (TextView) itemView.findViewById(R.id.recyclerview6);
            textViewR2C3 = (TextView) itemView.findViewById(R.id.recyclerview7);
            textViewR2C4 = (TextView) itemView.findViewById(R.id.recyclerview8);
        }
    }

    private ArrayList<DSObject> dsObjectsArrayList;

    public DSObjectsAdapter(ArrayList<DSObject> arrayList) {
        dsObjectsArrayList = arrayList;
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return dsObjectsArrayList.size();
    }

    // sets up new list item and its ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // inflate the android.R.layout.simple_list_item_1 layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
            R.layout.dsobjects_recyclerview, parent, false);
        return new ViewHolder(view);  // return current item's ViewHolder
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DSObject object = dsObjectsArrayList.get(position);
        String r1c1Text = object.getDsoObjectID();
        String r1c2Text = object.getDsoConst();
        String r1c3Text = Double.toString(object.getDsoMag());
        String r1c4Text = Double.toString(Math.round(object.getDsoRA()));
        String r2c1Text = object.getDsoName();
        String r2c2Text = object.getDsoType();
        String r2c3Text = object.getDsoSize();
        String r2c4Text = Double.toString(Math.round(object.getDsoDec()));

        holder.textViewR1C1.setText(r1c1Text);
        holder.textViewR1C2.setText(r1c2Text);
        holder.textViewR1C3.setText(r1c3Text);
        holder.textViewR1C4.setText(r1c4Text);
        holder.textViewR2C1.setText(r2c1Text);  // initially RA, change to altitude
        holder.textViewR2C2.setText(r2c2Text);
        holder.textViewR2C3.setText(r2c3Text);
        holder.textViewR2C4.setText(r2c4Text);  // initially Dec, change to azimuth
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}