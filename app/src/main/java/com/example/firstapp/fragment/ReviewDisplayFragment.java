package com.example.firstapp.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.firstapp.R;
import com.example.firstapp.data.ReviewData;
import com.example.firstapp.adapter.ReviewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;


public class ReviewDisplayFragment extends DialogFragment {
    String targetPlace;
    ViewGroup rootView;
    RecyclerView recyclerView;
    Context parentContext;
    ArrayList<ReviewData> emptyList = new ArrayList<ReviewData>();
    ReviewAdapter reviewAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public ReviewDisplayFragment(String place){
        targetPlace = place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentContext = requireContext();
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_review_display, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.reviewView);
        reviewAdapter = new ReviewAdapter(parentContext, emptyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentContext));
        recyclerView.setAdapter(reviewAdapter);
        Log.d("OOOO", "1");
        new SearchTask_Review().execute();
        return rootView;
    }

    private class SearchTask_Review extends AsyncTask<Void, Void, ArrayList<ReviewData>>{
        String baseURL1 = "https://openapi.naver.com/v1/search/blog?query=";
        String baseURL2 = "&display=10&start=1&sort=sim";
        URL searchURL;
        HttpURLConnection connection;
        @Override
        protected ArrayList<ReviewData> doInBackground(Void... voids) {
            Log.d("OOOO", targetPlace);
            ArrayList<ReviewData> reviewDatas = new ArrayList<ReviewData>();
            try {
                String encodedQuery = URLEncoder.encode(targetPlace, "utf-8");
                String requestQuery = baseURL1+encodedQuery+baseURL2;
                searchURL = new URL(requestQuery);
                connection = (HttpURLConnection) searchURL.openConnection();
                if (connection != null){
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setRequestProperty("HOST", "openapi.naver.com");
                    connection.setRequestProperty("Content-Type", "plain/text");
                    connection.setRequestProperty("X-Naver-Client-Id", "xrCz0HdH_6Tp0ybIihWM");
                    connection.setRequestProperty("X-Naver-Client-Secret", "tquqm3upXo");
                    int code = connection.getResponseCode();
                    Log.d("OOOOCode", Integer.toString(code));
                    if (code == HttpURLConnection.HTTP_OK){
                        InputStream inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = bufferedReader.readLine()) != null){
                            stringBuilder.append(line+"\n");
                        }
                        bufferedReader.close();
                        String result = stringBuilder.toString();
                        JSONObject responseObject = new JSONObject(result);
                        JSONArray jsonArray = (JSONArray) responseObject.get("items");
                        for (int i = 0; i < jsonArray.length(); i++){
                            JSONObject currentObject = jsonArray.getJSONObject(i);
                            ReviewData reviewData = new ReviewData();
                            reviewData.setTitle(currentObject.get("title").toString());
                            reviewData.setLink(currentObject.get("link").toString());
                            reviewData.setDescription(currentObject.get("description").toString());
                            reviewData.setPostdate(currentObject.get("postdate").toString());
                            reviewDatas.add(reviewData);
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return reviewDatas;
        }

        @Override
        protected void onPostExecute(ArrayList<ReviewData> reviewData) {
            super.onPostExecute(reviewData);
            if (reviewAdapter != null){
                reviewAdapter.updateData(reviewData);
            }
        }
    }
}