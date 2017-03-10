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
        TextView textView;
        TextView textView2;

        // configures a RecyclerView item's ViewHolder
        public ViewHolder (View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
            textView2 = (TextView) itemView.findViewById(android.R.id.text2);
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
            android.R.layout.simple_list_item_2, parent, false);
        return new ViewHolder(view);  // return current item's ViewHolder
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DSObject object = dsObjectsArrayList.get(position);
        String firstText = object.getDsoObjectID();
        String secondText = object.getDsoName();

        holder.textView.setText(firstText);
        holder.textView2.setText(secondText);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
