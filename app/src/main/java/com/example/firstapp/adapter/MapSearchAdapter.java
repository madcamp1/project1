package com.example.firstapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firstapp.R;
import com.example.firstapp.fragment.ReviewDisplayFragment;
import com.example.firstapp.data.SearchResult;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.NaverMap;

import java.util.ArrayList;

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.Holder> {
    Context currentContext;
    private ArrayList<SearchResult> searchResultArrayList;
    NaverMap currentMap;

    FragmentManager fragmentManager;
    public MapSearchAdapter(Context context, ArrayList<SearchResult> currentList){
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
        holder.category.setText(individSearchResult.getCategory());
        holder.address.setText(individSearchResult.getAddress());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(individSearchResult.getCoordinate(), 17).animate(CameraAnimation.Fly, 800);
                currentMap.moveCamera(cameraUpdate);
                return false;
            }
        });
        holder.searchReview.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                ImageView myImage = (ImageView) view;
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d("TOUCH?", "TOUCHDOWN");
                    myImage.setColorFilter(Color.parseColor("#974A514C"), PorterDuff.Mode.SRC_OVER);
                } else {
                    myImage.setColorFilter(Color.parseColor("#00ABCAB2"), PorterDuff.Mode.SRC_OVER);
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Log.d("TOUCH?", "TOUCHUP");
                    myImage.setColorFilter(Color.parseColor("#00ABCAB2"), PorterDuff.Mode.SRC_OVER);
                    ReviewDisplayFragment e = new ReviewDisplayFragment(individSearchResult.getTitle());
                    e.show(((FragmentActivity)currentContext).getSupportFragmentManager(), "event");
                }
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
        public TextView category;
        public TextView address;
        public ImageView searchReview;

        public Holder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.search_title);
            category = (TextView) itemView.findViewById(R.id.search_category);
            address = (TextView) itemView.findViewById(R.id.search_address);
            searchReview = (ImageView) itemView.findViewById(R.id.review_search_button);
        }
    }
}
