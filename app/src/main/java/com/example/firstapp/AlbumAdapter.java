package com.example.firstapp;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private AlbumData albumData;
    private Context context;

    public AlbumAdapter(AlbumData albumData, Context context) {
        this.albumData = albumData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final String imageURI = albumData.getImageURI(position);
        final int positionOfHolder = position;

        Bitmap thumbnail = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            try {
                thumbnail = context.getContentResolver().loadThumbnail(Uri.parse(imageURI), new Size(240, 240), null);
                Glide.with(context).load(thumbnail).into(holder.imgView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Glide.with(context).load(imageURI).into(holder.imgView);
        }

        holder.imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setDataAndType(Uri.parse(imageURI), "image/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(intent, "Edit Image"));
            }
        });
        holder.imgView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("삭제").setMessage("정말로 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Uri uri = Uri.parse(imageURI);
                                Log.d("URIisThis",imageURI);

                                Intent intent = new Intent("delete-img");
                                intent.putExtra("imgUri",uri);
                                intent.putExtra("imgPosition",positionOfHolder);
                                intent.putExtra("albumName",albumData.getAlbumName());
                                //TabActivity에서 Broadcast Receive
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(context, "삭제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });
                dialog.show();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        return this.albumData.getSize();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgView = itemView.findViewById(R.id.imageView_img);
        }
    }
}
