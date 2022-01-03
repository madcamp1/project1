package com.example.firstapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;


import com.example.firstapp.R;
import com.example.firstapp.TabActivity;
import com.example.firstapp.data.AlbumUri;
import com.example.firstapp.fragment.AlbumFragment;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<AlbumUri> albumUris = new ArrayList<>();
    private Context context;
    private FragmentTransaction ft;
    private FragmentManager fm;

    public AlbumAdapter(FragmentManager fm, FragmentTransaction ft, Context context) {
        this.context = context;
        this.fm = fm;
        this.ft = ft;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        AlbumViewHolder viewHolder = new AlbumViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {

        final AlbumUri albumUri = albumUris.get(position);

        fm = ((TabActivity)context).getSupportFragmentManager();
        //기존 id를 이용해 원래 붙어있던 Fragment 제거
        int containerId = holder.fragmentContainerView.getId();
        Fragment oldFragment = fm.findFragmentById(containerId);
        if (oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }

        //고유 id를 발급받아 뷰에 적용시켜야 서로 다른 fragment를 붙일 수 있음.
        int newContainerId = View.generateViewId();
        holder.fragmentContainerView.setId(newContainerId);

        Log.e("AlbumAdapter", "albumpath:"+ albumUri.getAlbumPath());
        AlbumFragment albumFragment = AlbumFragment.newInstance(albumUri.getAlbumPath(), albumUri.getAlbumName(), context);

        ((TabActivity)context).getSupportFragmentManager().beginTransaction().replace(newContainerId, albumFragment).commit();
    }


    @Override
    public int getItemCount() {
        return albumUris.size();
    }

    public void setAlbumUris(List<AlbumUri> albumUris) {
        if (albumUris.size() == 0) return;
        this.albumUris = albumUris;
        notifyDataSetChanged();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout fragmentContainerView;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            fragmentContainerView = itemView.findViewById(R.id.fcv_album);
        }

    }
}
