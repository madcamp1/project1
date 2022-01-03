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
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.Editable;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.Guideline;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.nio.channels.InterruptedByTimeoutException;
import java.util.ArrayList;
import java.util.TreeMap;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.Holder> {

    private ArrayList<ContactData> contactDatas;

    private final String[] putOrDeleteMenu = {"연락처 수정하기", "연락처 삭제하기"};

    Context context;

    public ContactAdapter(Context context, String retrieve) {
        contactDatas = new ArrayList<ContactData>();
        this.context = context;
        Handler hd = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                contactDatas = (ArrayList<ContactData>) msg.obj;
                notifyDataSetChanged();
            }
        };
        new Thread(){
            @Override
            public void run() {
                super.run();
                contactDatas = getContactData(retrieve);
                Message msg = hd.obtainMessage(1, contactDatas);
                hd.sendMessage(msg);
            }
        }.start();
    }

    @NonNull //Automatically check null and throw exception
    @Override //Overrides parent class'
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create ViewHolder
//        context = parent.getContext();
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
//
//        viewHolder.phoneNumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (viewHolder.optionIsVisible == 0) {
//                    viewHolder.guideline_options.setGuidelinePercent(0.85F);
//                    viewHolder.optionIsVisible = 1;
//                } else {
//                    viewHolder.guideline_options.setGuidelinePercent(1.0F);
//                    viewHolder.optionIsVisible = 0;
//                }
//            }
//        });
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
//        viewHolder.message.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int i) {
//
//            }
//        });
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
        public Guideline guideline_options;
        public int optionIsVisible;

        public Holder(View itemView) {
            //Initialize Components in Item View
            super(itemView);
            optionIsVisible = 0;
            image = (ImageView) itemView.findViewById(R.id.portrait);
            call = (ImageView) itemView.findViewById(R.id.call);
            message = (ImageView) itemView.findViewById(R.id.message);
            contactName = (TextView) itemView.findViewById(R.id.name);
            phoneNumber = (TextView) itemView.findViewById(R.id.phone_number);

        }
    }

    private int removeContact(ContactData toRemove) {
        Uri uri = ContactsContract.RawContacts.CONTENT_URI;
        String selectionCause = ContactsContract.RawContacts.CONTACT_ID + "=" + Long.toString(toRemove.getContact_id());
        return context.getContentResolver().delete(uri, selectionCause, null);
    }

    public ArrayList<ContactData> getContactData(String input) {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; //android provider에서 제공하는 데이터 식별자
        //ContactsContract.Contacts - Constants for the Contact table
        // == 동일한 사람을 나타내는 연락처 집계당 하나의 레코드가 되는 연락처 테이
        //ContactsContract.CommonDataKinds = ContactsContract.Data 테이블의 common data type 을 정
        String[] qr = new String[]{
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID //??
        };
        if (context == null) {
            Log.d("DEBUG", "No context");
            return null;
        }
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        ArrayList<ContactData> result;
        String where = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + input + "%'" + " OR " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE '%" + input + "%'";
        if (input.matches("[+-]?\\d*(\\.\\d+)?")) {
            where += handleAdditionalQuery(input);
        }
        try (Cursor cursor = context.getContentResolver().query(uri, qr, where, null, sortOrder)) {
            //SELECT qr FROM ContactsContract.CommonDataKinds.Phone DESC/ASC ~~ 같은 느낌이라 보면 될 듯
            result = new ArrayList<ContactData>();
            if (cursor.moveToFirst()) {
                do {
                    ContactData contactData = new ContactData();
                    contactData.setPortraitSrc(cursor.getLong(0));
                    contactData.setPhoneNum(cursor.getString(1));
                    contactData.setName(cursor.getString(2));
                    contactData.setContact_id(cursor.getLong(3));
                    contactData.setDescription("");
                    result.add(contactData);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    public String handleAdditionalQuery(String targetNum) {
        int len = targetNum.length();
        String additionalQuery="";
        if (len <= 3) return additionalQuery;
        else if (len <= 7) {
            additionalQuery = targetNum.substring(0, 3) + "-" + targetNum.substring(3, len);
        }
        else {
            additionalQuery = targetNum.substring(0, 3) + "-" + targetNum.substring(3, 7) + "-" + targetNum.substring(7, len);
        }
        return " OR " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + additionalQuery + "%'";
    }
}