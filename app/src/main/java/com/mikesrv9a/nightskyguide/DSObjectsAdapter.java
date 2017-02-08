// Subclass of RecyclerView.Adapter that binds dsObjects to RecyclerView

package com.mikesrv9a.nightskyguide;

import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikesrv9a.nightskyguide.DatabaseDescription.DSObject;

public class DSObjectsAdapter
    extends RecyclerView.Adapter<DSObjectsAdapter.ViewHolder> {

    // interface implemented by DSObjectsFragment to respond
    // when the user touches an item in the RecyclerView
    public interface DSObjectClickListener {
        void onClick(Uri dsObject);
    }

    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        public final TextView textView2;
        private long rowID;

        // configures a RecyclerView item's ViewHolder
        public ViewHolder (View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
            textView2 = (TextView) itemView.findViewById(android.R.id.text2);

            // attach listener to itemView
            itemView.setOnClickListener(
                new View.OnClickListener() {
                    // executes when the contact in this ViewHolder is clicked
                    @Override
                    public void onClick(View view) {
                        clickListener.onClick(DSObject.buildDSObjectUri(rowID));
                    }
                }
            );
        }

        // set the database row ID for the dsObject in this ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }
    }

    // DSObjectsAdapter instance variables
    private Cursor cursor = null;
    private final DSObjectClickListener clickListener;

    // constructor
    public DSObjectsAdapter(DSObjectClickListener clickListener) {
        this.clickListener = clickListener;
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
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(DSObject._ID)));
        holder.textView.setText(cursor.getString(cursor.getColumnIndex(
            DSObject.DSO_OBJECTID)));
        holder.textView2.setText(cursor.getString(cursor.getColumnIndex(
                DSObject.DSO_NAME)));
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // swap this adapter's current Cursor for a new one
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}
