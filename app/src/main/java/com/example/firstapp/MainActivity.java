package com.example.firstapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import com.example.firstapp.modules.Permissions;


public class MainActivity extends AppCompatActivity {

    public Button mainButton;

    private Permissions permissionManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.d("APPKEY", getKeyHashBase64(this));
        //Activity는 자체 출력 기능이 없기 때문에 setContentView로 Activity안에 뷰를 배치해야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //Marshmallow이상이면 권한 설정에 대한 내용이 존재하므로 Permissions class 호출
            permissionManager = new Permissions(this, this);
            if (!permissionManager.checkPermission()){
                //권한 설정이 되어있지 않은 경우 권한 요청
                permissionManager.requestPermission();
            }
        }
        setContentView(R.layout.activity_main);
        mainButton = findViewById(R.id.mainbutton);
        if (permissionManager.checkPermission()){
            Intent intent = new Intent(MainActivity.this, TabActivity.class);
            startActivity(intent);
        }
        //main page create 후 api level이 Marshmallow이상인지 확인

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //사용자가 초기 단계의 권한 요청에 불응했을 경우, 한번 더 누르면 권한 요청 알림을 못 띄우게 되어 있음. 따라서 대화창 상에 안내
                if (!permissionManager.checkPermission()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("해당 어플리케이션을 이용하기 위해서는 권한 설정이 필요합니다.\n어플리케이션 정보 - 권한에서 필요 권한에 대해 엑세스를 허용해 주세요")
                            .setPositiveButton("허용하기", new DialogInterface.OnClickListener() {
                                //어플리케이션 외부에서 권한을 허용할 것을 선택 시 어플리케이션 상세 페이지로 이동 (그 이상은 제조사 아니면 못 들어감)
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:"+getPackageName()));
                                    appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                                    appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(appDetail);
                                }
                            })
                            .setNegativeButton("거부", null)
                            .show();
                }
                else {
                    //intent - 앱 컴포넌트가 무엇을 할 지 담는 메시지 객체
                    //intent 생성자에 넣을 sub activity 는 manifest에 명시되어 있어야 한다.
                    //getApplicationContext는 액티비티에서 호출되며, singleton instance이다.
                    Intent intent = new Intent(MainActivity.this, TabActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

}