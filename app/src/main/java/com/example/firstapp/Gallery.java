package com.example.firstapp;

import android.app.RecoverableSecurityException;
import android.content.BroadcastReceiver;
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

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Gallery#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Gallery extends Fragment {

    RecyclerView rcvGallery;
    String imgUriToDel;

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
    public void onResume() {
        super.onResume();
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


        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver, new IntentFilter("delete-img"));

        return viewGroup;
    }

    public ArrayList<String> getGalleryPhotos(Context context) {
        ArrayList<String> photos = new ArrayList<String>();
        String[] colums = new String[] {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media._ID;
        Cursor cursor = context.getContentResolver().query(uri, colums, null, null, orderBy);

        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                int indexPath = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                photos.add(cursor.getString(indexPath));
            }
        } else {
            Log.e("getGalleryPhotos", "error getting URIs");
        }
        Collections.reverse(photos);
        return photos;
    }

    public GalleryData parsePhotosToGD(ArrayList<String> photos) {
        GalleryData galleryData = new GalleryData();

        for (int i = 0; i < photos.size(); i++) {
            galleryData.addImageURI(photos.get(i));
        }

        return galleryData;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            imgUriToDel = intent.getStringExtra("imgUri");
            File f = new File(imgUriToDel);
            f.delete();
//            try {
                Log.d("imgUriDel",imgUriToDel.toString());
//                removeMediaFile(context, imgUriToDel);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
        }
    };

//    private void removeMediaFile(Context context, Uri uri) {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            try {
//                context.getContentResolver().delete(uri, null, null);
//            } catch (RecoverableSecurityException e) {
//                IntentSender intentSender = e.getUserAction().getActionIntent().getIntentSender();
//                ActivityCompat.requestPermissions(this.getActivity(), intentSender,);
//            }
//        }else {
//            try {
//                context.getContentResolver().delete(uri, null, null);
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }
}