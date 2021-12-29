package com.example.firstapp;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class Contact extends Fragment {


    public Contact() {
        // Required empty public constructor
    }

    private View view;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;

    public static Contact newInstance() {
        Contact fragment = new Contact();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.contact_fragment, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);

        ArrayList<ContactData> list = getContactData();

        adapter = new ContactAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    public ArrayList<ContactData> getContactData() {
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] qr = new String[]{
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
        };

        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        Cursor cursor = getContext().getContentResolver().query(uri, qr, null, null, sortOrder);
        //LinkedHashSet<ContactData> hashList = new LinkedHashSet<>();
        ArrayList<ContactData> result = new ArrayList<ContactData>();
        if (cursor.moveToFirst()) {
            do {
                ContactData contactData = new ContactData();
                contactData.setPortraitSrc(cursor.getLong(0));
                contactData.setPhoneNum(cursor.getString(1));
                contactData.setName(cursor.getString(2));
                contactData.setContact_id(cursor.getLong(3));
                contactData.setDescription("none");
                result.add(contactData);
            } while (cursor.moveToNext());
        }
        return result;
    }

}