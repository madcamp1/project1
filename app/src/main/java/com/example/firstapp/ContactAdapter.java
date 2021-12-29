package com.example.firstapp;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {

    private ArrayList<ContactData> contactDatas;

    public ContactAdapter(ArrayList<ContactData> contactList) {
        contactDatas = contactList;
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
        ContactData indivContact = contactDatas.get(position);
        viewHolder.contactName.setText(indivContact.getName());
        viewHolder.phoneNumber.setText(indivContact.getPhoneNum());
        viewHolder.description.setText(indivContact.getDescription());
    }

    public int getItemCount() {
        return contactDatas.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView contactName;
        public TextView phoneNumber;
        public TextView description;

        public Holder(View itemView) {
            //Initialize Components in Item View
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.portrait);
            contactName = (TextView) itemView.findViewById(R.id.name);
            phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }

}