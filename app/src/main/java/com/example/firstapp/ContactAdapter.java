package com.example.firstapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
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

    Context context;

    @NonNull //Automatically check null and throw exception
    @Override //Overrides parent class'
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create ViewHolder
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.contact_itemview, parent, false);

        //Return object to onBindViewHolder
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, final int position) {
        //Define Actions with ItemView - e.g. onClick
        //Position: final (Should be Immutable)
        ContactData indivContact = contactDatas.get(position);
        viewHolder.image.setImageBitmap(getPhotoFromId(context.getContentResolver(), indivContact.getContact_id(), indivContact.getPortraitSrc()));
        viewHolder.contactName.setText(indivContact.getName());
        viewHolder.phoneNumber.setText(indivContact.getPhoneNum());
        viewHolder.description.setText(indivContact.getDescription());
    }

    public Bitmap getPhotoFromId(ContentResolver contentResolver, long id, long photo_id) {
        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);
        Cursor cursor = contentResolver.query(photoUri, new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO}, null, null, null);
        try {
            if (cursor.moveToFirst()) photoBytes = cursor.getBlob(0);
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            cursor.close();
        }

        if (photoBytes != null) {
            return resizingBitmap(BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length));
        } else {
            Log.d("DEBUG", "there's no photoBytes. Return null");
        }
        return null;
    }

    public Bitmap resizingBitmap(Bitmap originalBitmap){
        if (originalBitmap == null){
            return null;
        }

        float width = originalBitmap.getWidth();
        float height = originalBitmap.getHeight();
        float resizing_size = 120;

        Bitmap resizedBitmap = null;
        if (width > resizing_size) {
            float fScale = (float)(width/((float)(width / 100)));
            width *= fScale/100;
            height *= fScale/100;
        } else {
            float fScale = (float)(resizing_size/(float)(height / 100));
            width *= fScale/100;
            height *= fScale/100;
        }
        resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, (int)width, (int)height, true);
        return resizedBitmap;
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