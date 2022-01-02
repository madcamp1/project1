package com.example.firstapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;

import java.util.ArrayList;

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.Holder> {
    Context currentContext;
    private ArrayList<SearchResult> searchResultArrayList;
    NaverMap currentMap;

    FragmentManager fragmentManager;
    MapSearchAdapter(Context context, ArrayList<SearchResult> currentList){
        currentContext = context;
        searchResultArrayList = currentList;
    }

    public void setCurrentMap(NaverMap loadedMap){
        currentMap = loadedMap;
    }

    public void upDateDataset(ArrayList<SearchResult> currentList){
        searchResultArrayList = currentList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.search_itemview, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {
        SearchResult individSearchResult = searchResultArrayList.get(position);
        holder.title.setText(Html.fromHtml(individSearchResult.getTitle()));
        holder.link.setText(individSearchResult.getLink());
        holder.category.setText(individSearchResult.getCategory());
        holder.telephone.setText(individSearchResult.getTelePhone());
        holder.address.setText(individSearchResult.getAddress());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ReviewDisplayFragment e = new ReviewDisplayFragment(individSearchResult.getTitle());
                e.show(((FragmentActivity)currentContext).getSupportFragmentManager(), "event");
                return false;
            }
        });
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(individSearchResult.getCoordinate(), 17).animate(CameraAnimation.Fly, 800);
                currentMap.moveCamera(cameraUpdate);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchResultArrayList.size();
    }

    private ArrayList<SearchResult> searchViaEngine() {
        ArrayList<SearchResult> results = new ArrayList<>();
        return results;
    }


    public static class Holder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView link;
        public TextView category;
        public TextView telephone;
        public TextView address;

        public Holder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.search_title);
            link = (TextView) itemView.findViewById(R.id.search_link);
            category = (TextView) itemView.findViewById(R.id.search_category);
            telephone = (TextView) itemView.findViewById(R.id.search_telephone);
            address = (TextView) itemView.findViewById(R.id.search_address);
        }
    }
}
