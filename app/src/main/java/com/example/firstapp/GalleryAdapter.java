package com.example.firstapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


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
        //TODO
    }

    @Override
    public int getItemCount() {
        return this.galleryData.getSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView albumNameView;
        //TODO: RecyclerView Declare

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            albumNameView = itemView.findViewById(R.id.textView_albumName);
            //TODO: RecyclerView binding
        }
    }
}
