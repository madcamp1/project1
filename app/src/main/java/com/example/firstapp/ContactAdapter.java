package com.example.firstapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {

    private ArrayList<ContactData> contactDatas;

    public ContactAdapter(ArrayList<ContactData> contactList) {
        contactDatas = contactList;
    }

    private final String[] putOrDeleteMenu = {"연락처 수정하기", "연락처 삭제하기"};

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
    public void onBindViewHolder(Holder viewHolder, @SuppressLint("RecyclerView") final int position) {
        //Define Actions with ItemView - e.g. onClick
        //Position: final (Should be Immutable)
        ContactData indivContact = contactDatas.get(position);
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(putOrDeleteMenu, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int menuPosition) {
                        if (menuPosition == 0) {
                            //
                            context.startActivity(new Intent(Intent.ACTION_EDIT, Uri.parse(ContactsContract.Contacts.CONTENT_URI + "/" + Long.toString(indivContact.getContact_id()))));
                            notifyItemChanged(position);
                        }
                        else if (menuPosition == 1) {
                            Log.d("ONDELETE", Integer.toString(removeContact(indivContact)));
                            contactDatas.remove(position); //db뿐만 아니라 recyclerview 내의 데이터도 삭제해줘야 함
                            notifyItemRemoved(position);
                        }
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
                return true;
            }
        });
        viewHolder.image.setImageBitmap(getPhotoFromId(context.getContentResolver(), indivContact.getContact_id(), indivContact.getPortraitSrc()));
        if (indivContact.getPortraitSrc() == 0) {
            viewHolder.image.setImageResource(R.drawable.user);
        }
        viewHolder.contactName.setText(indivContact.getName());
        viewHolder.phoneNumber.setText(indivContact.getPhoneNum());
        viewHolder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri telUri = Uri.parse("tel:"+indivContact.getPhoneNum());
                context.startActivity(new Intent("android.intent.action.CALL", telUri));
            }
        });
        viewHolder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri msgUri = Uri.parse("tel:"+indivContact.getPhoneNum());
                Intent intent = new Intent(Intent.ACTION_VIEW, msgUri);
                intent.putExtra("address", indivContact.getPhoneNum());
                intent.putExtra("sms_body", "");
                intent.setType("vnd.android-dir/mms-sms");
                context.startActivity(intent);
            }
        });
        viewHolder.description.setText(indivContact.getDescription());
    }

    public Bitmap getPhotoFromId(ContentResolver contentResolver, long id, long photo_id) {
        byte[] photoBytes = null;
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id); //uri path에 photo id 붙임
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
        public ImageView call;
        public ImageView message;
        public TextView contactName;
        public TextView phoneNumber;
        public TextView description;

        public Holder(View itemView) {
            //Initialize Components in Item View
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.portrait);
            call = (ImageView) itemView.findViewById(R.id.call);
            message = (ImageView) itemView.findViewById(R.id.message);
            contactName = (TextView) itemView.findViewById(R.id.name);
            phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);
            description = (TextView) itemView.findViewById(R.id.description);
        }
    }

    private int removeContact(ContactData toRemove) {
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        String selectionCause = ContactsContract.RawContacts.CONTACT_ID + "=" + Long.toString(toRemove.getContact_id());
        return context.getContentResolver().delete(uri, selectionCause, null);
    }
}