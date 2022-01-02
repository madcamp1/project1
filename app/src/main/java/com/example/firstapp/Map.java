package com.example.firstapp;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.file.attribute.AclEntryFlag;
import java.util.ArrayList;
import java.util.List;


public class Map extends Fragment implements OnMapReadyCallback {

    ViewGroup rootView;
    RecyclerView recyclerView;
    NaverMap currentNaverMap;
    Context parentContext;
    ArrayList<SearchResult> emptyList = new ArrayList<SearchResult>();
    private double latitude = 0;
    private double longitude = 0;
    private int isCurrentLocationMode = -1;

    ArrayList<Marker> currentMarkers = new ArrayList<Marker>();

    private FusedLocationSource fusedLocationSource;
    private MapSearchAdapter mapSearchAdapter;
    private Geocoder geocoder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fusedLocationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
        geocoder = new Geocoder(requireContext());
        parentContext = requireContext();
        rootView = (ViewGroup) inflater.inflate(R.layout.map_fragment, container, false);
        EditText searchContent = rootView.findViewById(R.id.search_engine);
        ToggleButton modeChange = rootView.findViewById(R.id.toggle_button);
        modeChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCurrentLocationMode *= -1;
            }
        });
        ImageView searchCommit = rootView.findViewById(R.id.search_commit);
        searchCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    searchViaEngine(searchContent.getText().toString());
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        recyclerView = (RecyclerView) rootView.findViewById(R.id.searchView);
        mapSearchAdapter = new MapSearchAdapter(requireContext(), emptyList);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentContext));
        recyclerView.setAdapter(mapSearchAdapter);

        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        naverMap.setLocationSource(fusedLocationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
        naverMap.getUiSettings().setLocationButtonEnabled(true);
        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        });
        currentNaverMap = naverMap;
        mapSearchAdapter.setCurrentMap(currentNaverMap);
    }

    public void searchViaEngine(String additionalQuery) throws IOException {
        List<Address> list = geocoder.getFromLocation(latitude, longitude, 10);
        if (additionalQuery.length() == 0){
            new AlertDialog.Builder(requireContext())
                    .setMessage("검색어를 입력해주세요!")
                    .setPositiveButton("확인", null).show();
            return;
        }
        if (list.size() == 0) {
            Log.d("ERROR", "No location found");
            return;
        }
        String query="";
        Address address = list.get(0);
        if (isCurrentLocationMode > 0){
            query = address.getAddressLine(0) + " " + additionalQuery;
        } else{
            query = additionalQuery;
        }
        String[] params = {query};
        Log.d("QUERYVIEW", query);
        new SearchTask().execute(params);
    }



    private class SearchTask extends AsyncTask<String, Void, ArrayList<SearchResult>> {
        String baseURL = "https://openapi.naver.com/v1/search/local.json";
        URL searchURL;
        String query;
        HttpURLConnection connection;

        @Override
        protected ArrayList<SearchResult> doInBackground(String... params) {
            query = params[0];
            ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
            try {
                String utf8query = URLEncoder.encode(query, "utf-8");
                String requestQuery = addQueryString(baseURL, utf8query, "10", "1", "random");
                searchURL = new URL(requestQuery);
                connection = (HttpURLConnection) searchURL.openConnection();
                if (connection != null){
                    connection.setConnectTimeout(10000);
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true); //no doOutPut
                    connection.setRequestProperty("HOST", "openapi.naver.com");
                    connection.setRequestProperty("Content-Type", "plain/text");
                    connection.setRequestProperty("X-Naver-Client-Id", "xrCz0HdH_6Tp0ybIihWM");
                    connection.setRequestProperty("X-Naver-Client-Secret", "tquqm3upXo");
                    int code = connection.getResponseCode();
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
                            SearchResult searchResult = new SearchResult();
                            searchResult.setTitle(currentObject.get("title").toString());
                            searchResult.setCategory(currentObject.get("category").toString());
                            searchResult.setTelePhone(currentObject.get("telephone").toString());
                            searchResult.setLink(currentObject.get("link").toString());
                            searchResult.setAddress(currentObject.get("address").toString());
                            searchResult.setCoordinate(translateCoordinate(Integer.parseInt(currentObject.get("mapx").toString()), Integer.parseInt(currentObject.get("mapy").toString())));
                            searchResults.add(searchResult);
                        }
                    }
                }
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return searchResults;
        }


        private String addQueryString(String url, String encodedQuery, String display, String start, String sort) {
            if (encodedQuery == null) encodedQuery=""; //예외처리 필요
            String result = url+"?query=" + encodedQuery;
            if (display != null){
                result += "&display=" + display;
            }
            if (start != null){
                result += "&start=" + start;
            }
            if (sort != null){
                result += "&sort=" + sort;
            }
            return result;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void onPostExecute(ArrayList<SearchResult> searchResults) {
            super.onPostExecute(searchResults);

            if (currentMarkers != null || currentMarkers.size() != 0){
                for (int i = 0; i < currentMarkers.size(); i++){
                    currentMarkers.get(i).setMap(null);
                }
                currentMarkers.clear();
            }

            for (int i = 0; i < searchResults.size(); i++){
                LatLng markerPosition = searchResults.get(i).getCoordinate();
                Marker marker = new Marker();
                marker.setPosition(markerPosition);
                marker.setMap(currentNaverMap);
                currentMarkers.add(marker);
                marker.setOnClickListener(new Overlay.OnClickListener() {
                    @Override
                    public boolean onClick(@NonNull Overlay overlay) {
                        CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(markerPosition, 17).animate(CameraAnimation.Fly, 800);
                        currentNaverMap.moveCamera(cameraUpdate);
                        return false;
                    }
                });
            }

            if (mapSearchAdapter != null) {
                mapSearchAdapter.upDateDataset(searchResults);
            }
        }

        public LatLng translateCoordinate(int coord_x, int coord_y){
            GeoTransPoint oKA = new GeoTransPoint(coord_x, coord_y);
            GeoTransPoint oGeo = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, oKA);
            double lat = oGeo.getY();
            double lng = oGeo.getX();

            return new LatLng(lat, lng);
        }
    }

}
