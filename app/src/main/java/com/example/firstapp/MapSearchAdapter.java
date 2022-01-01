package com.example.firstapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MapSearchAdapter extends RecyclerView.Adapter<MapSearchAdapter.Holder> {
    Context currentContext;
    private ArrayList<SearchResult> searchResultArrayList;
    MapSearchAdapter(Context context, ArrayList<SearchResult> currentList){
        currentContext = context;
        searchResultArrayList = currentList;
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
        holder.title.setText(individSearchResult.getTitle());
        holder.link.setText(individSearchResult.getLink());
        holder.category.setText(individSearchResult.getCategory());
        holder.telephone.setText(individSearchResult.getTelePhone());
        holder.address.setText(individSearchResult.getAddress());
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
