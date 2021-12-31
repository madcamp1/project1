package com.example.firstapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class Permissions {

    private Context context;
    private Activity activity;
    private final int MULTIPLE_PERMISSIONS = 1023;

    //Required Permissions of the Application
    private String[] permissions = {
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.INTERNET,
    };

    private List permissionList;

    public Permissions(Activity activity, Context context){
        this.activity = activity;
        this.context = context;
    }

    public boolean checkPermission(){
        int result;
        permissionList = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context, p);
            if (result != PackageManager.PERMISSION_GRANTED){
                permissionList.add(p);
            }
        }
        return permissionList.isEmpty();
    }

    public void requestPermission(){
        String[] plist = (String[])permissionList.toArray(new String[permissionList.size()]);
        ActivityCompat.requestPermissions(activity, plist, MULTIPLE_PERMISSIONS);
    }

}
