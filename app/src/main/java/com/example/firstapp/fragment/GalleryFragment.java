package com.example.firstapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.ViewModel.AlbumViewModel;
import com.example.firstapp.adapter.AlbumAdapter;
import com.example.firstapp.data.AlbumUri;

import java.io.Serializable;
import java.util.List;


public class GalleryFragment extends Fragment {
    private RecyclerView rcvAlbums;
    private ImageButton btnRefresh;
    private Context context;

    private AlbumViewModel albumViewModel;

    public GalleryFragment() {
        // Required empty public constructor
    }

    public static GalleryFragment newInstance(Context context) {
        GalleryFragment fragment = new GalleryFragment();
        fragment.context = context;
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
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        rcvAlbums = view.findViewById(R.id.rcv_albums);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("refresh-db");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });


        rcvAlbums.setLayoutManager(new LinearLayoutManager(context));

        FragmentManager fm = getParentFragmentManager();
        AlbumAdapter albumAdapter = new AlbumAdapter(context, fm);
        rcvAlbums.setAdapter(albumAdapter);
        albumViewModel = new ViewModelProvider(this).get(AlbumViewModel.class);

        albumViewModel.getAlbumUris().observe(getViewLifecycleOwner(), new Observer<List<AlbumUri>>() {
            @Override
            public void onChanged(List<AlbumUri> albumUris) {
                albumAdapter.setAlbumUris(albumUris);
            }
        });

        return view;
    }
}