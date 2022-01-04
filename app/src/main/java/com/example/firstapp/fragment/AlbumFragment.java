package com.example.firstapp.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.ViewModel.ImageViewModel;
import com.example.firstapp.adapter.ImageAdapter;
import com.example.firstapp.data.ImageUri;

import java.util.List;


public class AlbumFragment extends Fragment {

    private String albumPath, albumName;
    private Context context;

    private RecyclerView rcvImages;
    private TextView tvAlbumName, tvAlbumPath;

    private ImageViewModel imageViewModel;

    public AlbumFragment() {
        // Required empty public constructor
    }

    public static AlbumFragment newInstance(String albumPath, String albumName, Context context) {
        AlbumFragment fragment = new AlbumFragment();
        fragment.albumName = albumName;
        fragment.albumPath = albumPath;
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
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        if (albumPath == null) return view;
        tvAlbumName = view.findViewById(R.id.tv_albumName);
        tvAlbumPath = view.findViewById(R.id.tv_albumPath);
        rcvImages = view.findViewById(R.id.rcv_images);

        tvAlbumPath.setText(albumPath);
        tvAlbumName.setText(albumName);


        rcvImages.setLayoutManager(new GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false));
        ImageAdapter imageAdapter = new ImageAdapter(getContext());
        rcvImages.setAdapter(imageAdapter);

        imageViewModel = new ViewModelProvider(this).get(ImageViewModel.class);
        imageViewModel.setupNewAlbum(albumPath);

        imageViewModel.getImageUris().observe(getViewLifecycleOwner(), new Observer<List<ImageUri>>() {
            @Override
            public void onChanged(List<ImageUri> imageUris) {
                imageAdapter.setImageUris(imageUris);
                int images = imageUris.size();
                int spanCount = 1;
                if(images > 7) spanCount = 2;
                if (images > 11) spanCount = 3;
                ((GridLayoutManager)rcvImages.getLayoutManager()).setSpanCount(spanCount);
            }
        });


        tvAlbumName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                dialog.setTitle("삭제").setMessage(albumName +": 앨범을 정말로 삭제하시겠습니까?\n 앨범 내의 모든 사진이 함께 삭제됩니다.")
                        .setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

//                                ArrayList<ImageUri> imagesToDel = new ArrayList<ImageUri>();
//                                imagesToDel.addAll(((ImageAdapter)rcvImages.getAdapter()).getImageUris());
                                Intent intent = new Intent("delete-album");
//                                intent.putExtra("images-to-del", imagesToDel);
                                intent.putExtra("album-path", albumPath);
                                intent.putExtra("album-name", albumName);
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

        return view;
    }
}