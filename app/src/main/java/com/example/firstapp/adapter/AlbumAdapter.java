package com.example.firstapp.adapter;

import android.content.Context;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentViewHolder;


import com.example.firstapp.R;
import com.example.firstapp.TabActivity;
import com.example.firstapp.data.AlbumUri;
import com.example.firstapp.fragment.AlbumFragment;

import java.util.ArrayList;
import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {
    private List<AlbumUri> albumUris = new ArrayList<>();
    private ArrayList<AlbumFragment> albumFragments = new ArrayList<AlbumFragment>();
    private Context context;
    private FragmentManager fm;

    public AlbumAdapter(Context context, FragmentManager fm) {
        this.context = context;
        this.fm = fm;
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

        //고유 id를 발급받아 뷰에 적용시켜야 서로 다른 fragment를 붙일 수 있음.
        holder.itemView.setId(View.generateViewId());


        int containerId = holder.itemView.getId();
        Fragment oldFragment = fm.findFragmentById(containerId);
        if (oldFragment != null) {
            fm.beginTransaction().remove(oldFragment).commit();
        }


        holder.drawed = 0;
        //Fragment는 draw 상태인 View에만 붙일 수 있다. 따라서 DrawListener에서 바인딩을 해주면 된다.
        //그냥 하면 뷰 없다고 에러
        AlbumFragment albumFragment = AlbumFragment.newInstance(albumUri.getAlbumPath(),albumUri.getAlbumName(), context);
        holder.itemView.getViewTreeObserver().addOnDrawListener(new ViewTreeObserver.OnDrawListener() {
            @Override
            public void onDraw() {
                if(holder.drawed == 0) {
                    fm.beginTransaction().replace(containerId, albumFragment).commit();
                    holder.drawed = 1;
                }
            }
        });

    }

    // Method that could us an unique id
    public int getUniqueId(){
        return (int) SystemClock.currentThreadTimeMillis();
    }

    public void reloadAdapters() {
        albumFragments = new ArrayList<AlbumFragment>();
        for (int i = 0; i < albumUris.size(); i++) {
            albumFragments.add(i, AlbumFragment.newInstance(albumUris.get(i).getAlbumPath(),albumUris.get(i).getAlbumName(), context));
        }
    }

    @Override
    public int getItemCount() {
        return albumUris.size();
    }

    public void setAlbumUris(List<AlbumUri> albumUris) {
        if (albumUris.size() == 0) return;
        this.albumUris = albumUris;
        reloadAdapters();
        notifyDataSetChanged();
    }

    class AlbumViewHolder extends RecyclerView.ViewHolder {
        private FrameLayout fragmentContainerView;
        int drawed = 0;

        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setId(View.generateViewId());
        }

    }
}
