package com.example.firstapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    private GalleryData galleryData;
    private Context context;

    public GalleryAdapter(GalleryData galleryData, Context context) {
        this.galleryData = galleryData;
        this.context = context;
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
        if(albumData.getSize() <= 4) spanCount = 1;
        else if(albumData.getSize() <= 8) spanCount = 2;
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
}
