package com.example.firstapp;

import android.app.LoaderManager;
import android.content.ClipData;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class Contact extends Fragment {

    SwipeController swipeController;
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
        recyclerView.setAdapter(new ContactAdapter(requireContext()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = (ViewGroup) inflater.inflate(R.layout.contact_fragment, container, false);
        search = (EditText) rootView.findViewById(R.id.phoneNumberQuery);
        addButton = (ImageView) rootView.findViewById(R.id.add);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_contacts);

        adapter = new ContactAdapter(getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);

        swipeController = new SwipeController(requireContext(), adapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);

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
                ContactAdapter contactAdapter = (ContactAdapter) recyclerView.getAdapter();
                assert contactAdapter != null;
                contactAdapter.retrieveContact(editable.toString());
            }
        });


        return rootView;
    }


}