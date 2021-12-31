package com.example.firstapp;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private GalleryData galleryData;
    private Context context;
    private ContentResolver contentResolver;

    public void setGalleryData(GalleryData galleryData) {
        this.galleryData = galleryData;
    }

    public AlbumData getAlbum(String albumName) {
        for(int i = 0; i < galleryData.getSize(); i++) {
            if (galleryData.getAlbum(i).getAlbumName().equals(albumName)) return galleryData.getAlbum(i);
        }
        return null;
    }

    public GalleryAdapter(Context context) {
        this.context = context;
        galleryData = new GalleryData();
        Handler handler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                Log.d("Handler","Message Received");
                galleryData = (GalleryData) msg.obj;
                notifyDataSetChanged();
            }
        };
        Thread t = new Thread() {
            @Override
            public void run() {
                super.run();
                galleryData = parsePhotosToGD(getGalleryPhotos(context));
                Message message = handler.obtainMessage(1,galleryData);
                handler.sendMessage(message);
                Log.d("Thread","Message sent");
            }
        };
        t.start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final AlbumData albumData = galleryData.getAlbum(position);
        holder.albumNameView.setText(albumData.getAlbumName());

        int spanCount = 0;
        if(albumData.getSize() <= 7) spanCount = 1;
        else if(albumData.getSize() <= 11) spanCount = 2;
        else spanCount = 3;

        holder.rcvAlbum.setLayoutManager(new GridLayoutManager(this.context, spanCount, GridLayoutManager.HORIZONTAL, false));
        holder.rcvAlbum.setAdapter(new AlbumAdapter(albumData, this.context));
    }

    @Override
    public int getItemCount() {
        return this.galleryData.getSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView albumNameView;
        RecyclerView rcvAlbum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameView = itemView.findViewById(R.id.textView_albumName);
            rcvAlbum = itemView.findViewById(R.id.recyclerView_album);
        }
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
            cursor =context.getContentResolver().query(photos.get(i), null, null, null, null);
            cursor.moveToNext();
            String path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
            String[] splitUris = path.split("/");
            galleryData.addImageURI(splitUris[splitUris.length - 2], photos.get(i));
        }

        return galleryData;
    }
}
