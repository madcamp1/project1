package com.example.firstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {

    private ArrayList<String> mData = null;

    public ContactAdapter(ArrayList<String> dataSet) {
        mData = dataSet;
    }

    @NonNull //Automatically check null and throw exception
    @Override //Overrides parent class'
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create ViewHolder
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.contact_itemview, parent, false);

        //Return object to onBindViewHolder
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, final int position) {
        //Define Actions with ItemView - e.g. onClick
        //Position: final (Should be Immutable)
        String text = mData.get(position);
        viewHolder.textView.setText(text);
    }

    public int getItemCount() {
        return mData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView textView;

        public Holder(View itemView) {
            //Initialize Components in Item View
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}