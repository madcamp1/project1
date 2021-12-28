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

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.itemview_contact, parent, false);
        Holder vh = new Holder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        String text = mData.get(position);
        viewHolder.textView.setText(text);
    }

    public int getItemCount() {
        return mData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView textView;

        public Holder(View itemView) {
            super(itemView);
            // Define click listener for the ViewHolder's View
            textView = (TextView) itemView.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }
    }
}