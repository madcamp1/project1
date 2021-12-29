package com.example.firstapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    public Button mainButton;

    private Permissions permissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainButton = findViewById(R.id.mainbutton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            permissionManager = new Permissions(this, this);
            if (!permissionManager.checkPermission()){
                permissionManager.requestPermission();
            }
        }

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!permissionManager.checkPermission()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("해당 어플리케이션은 연락처와 저장공간에 대한 권한을 필요로 합니다.\n어플리케이션 정보 - 앱 권한에서 연락처와 저장공간에 대한 엑세스 권한을 허용해 주세요")
                            .setPositiveButton("허용하기", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                                    appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                                    appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(appDetail);
                                }
                            })
                            .setNegativeButton("안해!", null)
                            .show();
                }
                else {
                    Intent intent = new Intent(getApplicationContext(), TabActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}