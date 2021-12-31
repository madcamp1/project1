package com.example.firstapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class Map extends Fragment {

    ViewGroup rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        int a = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int b = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int c = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.INTERNET);
        Log.d("PERMISSION", Integer.toString(a));
        Log.d("PERMISSION", Integer.toString(b));
        Log.d("PERMISSION", Integer.toString(c));
        rootView = (ViewGroup) inflater.inflate(R.layout.map_fragment, container, false);
        MapView mapView = new MapView(requireContext());
//        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(33.41, 126.52), true);
//        mapView.setZoomLevel(10, true);
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        ViewGroup mapViewGroup = rootView.findViewById(R.id.map_view);
        mapViewGroup.addView(mapView);
        return rootView;
    }

    public boolean locationServiceStatus(){
        return false;
    }
}