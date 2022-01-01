package com.example.firstapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;


public class TabActivity extends AppCompatActivity {

    TabLayout tabLayout;
    FrameLayout fragmentLayout;

    Uri imgUriToDel;
    String albumNameToMod;
    int positionOfImgToDel = -1;

    Contact contactFragment;
    Gallery galleryFragment;
    Map mapFragment;


    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            imgUriToDel = (Uri) intent.getExtras().get("imgUri");
            positionOfImgToDel = intent.getIntExtra("imgPosition", -1);
            albumNameToMod = intent.getStringExtra("albumName");
            removeImg(context, imgUriToDel);
        }
    };

    private int removeImg(Context context, Uri ImgUriToDel) {
        try {
            int imageFd = context.getContentResolver()
                    .delete(imgUriToDel,null,null);
            Log.d("imageFd", imageFd + "");
            Log.d("imgUri", imgUriToDel.toString());
            if (imageFd > 0) {
                ((GalleryAdapter)((RecyclerView)findViewById(R.id.recyclerView_gallery)).getAdapter()).getAlbum(albumNameToMod)
                        .deleteURI(positionOfImgToDel);
                ((RecyclerView)findViewById(R.id.recyclerView_gallery)).getAdapter().notifyDataSetChanged();
                imgUriToDel = null;
                albumNameToMod = null;
                positionOfImgToDel = -1;
            }
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException =
                            (RecoverableSecurityException)securityException;
                } else {
                    throw new RuntimeException(
                            securityException.getMessage(), securityException);
                }
                IntentSender intentSender =recoverableSecurityException.getUserAction()
                        .getActionIntent().getIntentSender();
                try {
                    startIntentSenderForResult(intentSender, 0x1033,
                            null, 0, 0, 0, null);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                throw new RuntimeException(
                        securityException.getMessage(), securityException);
            }
        }
        return 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        tabLayout = findViewById(R.id.mainTab);
        fragmentLayout = findViewById(R.id.fragmentContainerView);
        contactFragment = new Contact();
        galleryFragment = new Gallery();
        mapFragment = new Map();


        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment fragment = fm.findFragmentById(R.id.fragmentContainerView);
        if (fragment != null) ft.remove(fragment);
        ft.add(R.id.fragmentContainerView, contactFragment);
        ft.commitNow();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);



        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("delete-img"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tab.getPosition()){
                    case 0:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, contactFragment).commit();
                        break;
                    case 1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, galleryFragment).commit();
                        break;
                    case 2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, mapFragment).commit();
                    default: break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 0x1033) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (imgUriToDel != null) {
                    removeImg(this, imgUriToDel);
                    return;
                }
            }
        }
    }



}