package com.example.firstapp;

import static androidx.core.app.ActivityCompat.startIntentSenderForResult;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Gallery extends Fragment {

    RecyclerView rcvGallery;
    ContentResolver contentResolver;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Gallery() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Gallery.
     */
    // TODO: Rename and change types and number of parameters
    public static Gallery newInstance(String param1, String param2) {
        Gallery fragment = new Gallery();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.gallery_fragment, container, false);

        rcvGallery = (RecyclerView)viewGroup.findViewById(R.id.recyclerView_gallery);
        rcvGallery.setLayoutManager(new LinearLayoutManager(getContext()));

        //TODO: load Images From Local Storage

        GalleryData glData = parsePhotosToGD(getGalleryPhotos(getContext()));
        rcvGallery.setAdapter(new GalleryAdapter(glData, getContext()));


        return viewGroup;
    }

    public ArrayList<Uri> getGalleryPhotos(Context context) {
        ArrayList<Uri> photos = new ArrayList<Uri>();
        String[] colums = new String[] {MediaStore.Images.Media._ID};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media._ID;
        contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(uri, colums, null, null, orderBy);

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()) {

                long id = cursor.getLong(idColumn);

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                photos.add(contentUri);
            }
        } else {
            Log.e("getGalleryPhotos", "error getting URIs");
        }
        Collections.reverse(photos);
        return photos;
    }

    public GalleryData parsePhotosToGD(ArrayList<Uri> photos) {
        GalleryData galleryData = new GalleryData();
        Cursor cursor;

        for (int i = 0; i < photos.size(); i++) {
            cursor = getContext().getContentResolver().query(photos.get(i), null, null, null, null);
            cursor.moveToNext();
            String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            String[] splitUris = path.split("/");
            galleryData.addImageURI(splitUris[splitUris.length - 2], photos.get(i));
        }

        return galleryData;
    }



}