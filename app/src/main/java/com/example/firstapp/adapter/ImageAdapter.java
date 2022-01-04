package com.example.firstapp.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firstapp.R;
import com.example.firstapp.data.ImageUri;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    Context context;
    private List<ImageUri> imageUris;

    public List<ImageUri> getImageUris() {
        return imageUris;
    }

    public ImageAdapter(Context context) {
        this.context = context;
    }

    public void setImageUris(List<ImageUri> imageUris) {
        if (imageUris.size() == 0) return;
        this.imageUris = imageUris;
        Collections.reverse(this.imageUris);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        ImageViewHolder viewHolder = new ImageViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        final int posVal = position;
        Bitmap thumbnail = null;
        Uri uri = null;
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + imageUris.get(posVal).getImagePath() + "'", null, null);
        if(cursor.moveToNext()) {
            uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(cursor.getColumnIndex("_ID")));
            try {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    thumbnail = context.getContentResolver().loadThumbnail(uri, new Size(200, 200), null);
                    Glide.with(context).load(thumbnail).into(holder.imageView);
                } else {
                    Glide.with(context).load(uri).into(holder.imageView);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { //file not exists on storage -> delete from db only
            Intent intent = new Intent("delete-img-only-db");
            intent.putExtra("image-uri", uri);
            intent.putExtra("image-path",imageUris.get(posVal).getImagePath());
            intent.putExtra("album-path",imageUris.get(posVal).getAlbumPath());
            intent.putExtra("image-name",imageUris.get(posVal).getImageName());
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("삭제").setMessage("정말로 삭제하시겠습니까?")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + imageUris.get(posVal).getImagePath() + "'", null, null);
                                cursor.moveToNext();
                                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(cursor.getColumnIndex("_ID")));

                                Intent intent = new Intent("delete-img");
                                intent.putExtra("image-uri", uri);
                                intent.putExtra("image-path", imageUris.get(posVal).getImagePath());
                                intent.putExtra("album-name", imageUris.get(posVal).getAlbumPath());
                                intent.putExtra("image-name", imageUris.get(posVal).getImageName());
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

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, "_data = '" + imageUris.get(posVal).getImagePath() + "'", null, null);
                cursor.moveToNext();
                Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cursor.getInt(cursor.getColumnIndex("_ID")));
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setDataAndType(uri, "image/*");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(Intent.createChooser(intent, "Edit Image"));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (imageUris != null) return imageUris.size();
        else return 0;
    }

    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
