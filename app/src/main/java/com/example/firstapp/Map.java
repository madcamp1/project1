package com.example.firstapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
        rootView = (ViewGroup) inflater.inflate(R.layout.map_fragment, container, false);
        MapView mapView = new MapView(requireContext());
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(33.41, 126.52), true);
        mapView.setZoomLevel(10, true);
        mapView.setMapViewEventListener((MapView.MapViewEventListener) requireContext());
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        ViewGroup mapViewGroup = rootView.findViewById(R.id.map_view);
        mapViewGroup.addView(mapView);
        return rootView;
    }
}