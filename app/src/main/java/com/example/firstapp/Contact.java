package com.example.firstapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class Contact extends Fragment {


    public Contact() {
    }

    private View view;
    private EditText search;
    private ImageView addButton;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    ViewGroup rootView;

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
    public void onResume() {
        super.onResume();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);
        recyclerView.setAdapter(new ContactAdapter(getContactData("")));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.contact_fragment, container, false);
        search = (EditText) rootView.findViewById(R.id.phoneNumberQuery);
        addButton = (ImageView) rootView.findViewById(R.id.add);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);
        ArrayList<ContactData> list = getContactData("");

        adapter = new ContactAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                requireContext().startActivity(intent);
            }
        });

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);
                recyclerView.setAdapter(new ContactAdapter(getContactData(editable.toString())));
            }
        });

        return rootView;
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
        if (getContext() == null) {
            Log.d("DEBUG", "No context");
            return null;
        }
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
        ArrayList<ContactData> result;
        String where = ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + input + "%'" + " OR " + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE '%" + input + "%'";
        if (input.matches("[+-]?\\d*(\\.\\d+)?")) {
            where += handleAdditionalQuery(input);
        }
        try (Cursor cursor = getContext().getContentResolver().query(uri, qr, where, null, sortOrder)) {
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