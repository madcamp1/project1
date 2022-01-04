package com.example.firstapp.fragment;

import android.content.Intent;
import android.os.Bundle;

import android.provider.ContactsContract;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.firstapp.R;
import com.example.firstapp.modules.SwipeController;
import com.example.firstapp.adapter.ContactAdapter;

import java.io.Serializable;

public class ContactFragment extends Fragment implements Serializable {

    SwipeController swipeController;
    public ContactFragment() {
    }
    private View view;
    private EditText search;
    private ImageView addButton;
    private RecyclerView recyclerView;
    private ContactAdapter adapter;
    ViewGroup rootView;

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
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