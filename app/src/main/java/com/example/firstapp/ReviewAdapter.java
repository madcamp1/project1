package com.example.firstapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.Holder>{

    private ArrayList<ReviewData> reviewDatas;
    private Context currentContext;

    ReviewAdapter(Context context,ArrayList<ReviewData> datas){
        currentContext = context;
        reviewDatas = datas;
    }

    public void updateData(ArrayList<ReviewData> updatedList){
        reviewDatas = updatedList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(currentContext).inflate(R.layout.review_item_view, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        ReviewData individReviewData = reviewDatas.get(position);
        holder.title.setText(Html.fromHtml(individReviewData.getTitle()));
        holder.description.setText(Html.fromHtml(individReviewData.getDescription()));
        holder.postdate.setText(Html.fromHtml(individReviewData.getPostdate()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(individReviewData.getLink()));
                currentContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewDatas.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public TextView postdate;

        public Holder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.review_title);
            description = (TextView) itemView.findViewById(R.id.review_description);
            postdate = (TextView) itemView.findViewById(R.id.review_postdate);
        }
    }


}
