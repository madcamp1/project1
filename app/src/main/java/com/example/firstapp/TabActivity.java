package com.example.firstapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.firstapp.data.AlbumUri;
import com.example.firstapp.data.GalleryDatabase;
import com.example.firstapp.data.ImageUri;
import com.example.firstapp.fragment.ContactFragment;
import com.example.firstapp.fragment.GalleryFragment;
import com.example.firstapp.fragment.MapFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;


public class TabActivity extends AppCompatActivity {

    TabLayout tabLayout;
    FrameLayout fragmentLayout;

    ContactFragment contactFragment;
    GalleryFragment galleryFragment;
    MapFragment mapFragment;
    GalleryDatabase db;

    ImageUri imageUriToDel = null;
    Uri imageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        tabLayout = findViewById(R.id.mainTab);
        fragmentLayout = findViewById(R.id.fragmentContainerView);

        db = GalleryDatabase.getInstance(getApplication());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainerView);


        if(savedInstanceState != null) {
            tabLayout.selectTab(tabLayout.getTabAt(savedInstanceState.getInt("LastTab")));
        } else {
            if (fragment != null) ft.remove(fragment);
            contactFragment = new ContactFragment();
            mapFragment = new MapFragment();
            galleryFragment = GalleryFragment.newInstance(this);
            ft.add(R.id.fragmentContainerView, contactFragment);
            ft.commit();
        }
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        LocalBroadcastManager.getInstance(this).registerReceiver(deleteImageMessageReceiver, new IntentFilter("delete-img"));
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteImageOnlyDbMessageReceiver, new IntentFilter("delete-img-only-db"));
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteImagesInAlbumMessageReceiver, new IntentFilter("delete-album"));
        LocalBroadcastManager.getInstance(this).registerReceiver(refreshDBMessageReceiver, new IntentFilter("refresh-db"));


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
                        break;
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
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("LastTab", tabLayout.getSelectedTabPosition());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 0x1033) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (imageUriToDel != null) {
                    removeImg(this, imageUri);
                    return;
                }
            }
        }
    }

    public int removeImg(Context context, Uri imgUri) {

        try {
            int imageFd = context.getContentResolver()
                    .delete(imgUri,null,null);

            db.imageDao().deleteImageUris(imageUriToDel);

            imageUriToDel = null;
            imageUri = null;
        } catch (SecurityException securityException) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                RecoverableSecurityException recoverableSecurityException;
                if (securityException instanceof RecoverableSecurityException) {
                    recoverableSecurityException = (RecoverableSecurityException)securityException;
                } else {
                    throw new RuntimeException(securityException.getMessage(), securityException);
                }
                IntentSender intentSender =recoverableSecurityException.getUserAction().getActionIntent().getIntentSender();
                try {
                    startIntentSenderForResult(intentSender, 0x1033, null, 0, 0, 0, null);
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

    public void refreshDB() {
        ArrayList<Uri> photos = new ArrayList<Uri>();
        String[] colums = new String[] {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String orderBy = MediaStore.Images.Media._ID;
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, colums, null, null, orderBy);

        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                String imagePath;
                String imageName;
                String albumPath;
                String albumName;

                long id = cursor.getLong(idColumn);

                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                photos.add(contentUri);
                imagePath = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                String[] splitUris = imagePath.split("/");
                imageName = splitUris[splitUris.length - 1];
                albumName = splitUris[splitUris.length - 2];
                albumPath = imagePath.substring(0,imagePath.length() - imageName.length() - 1);

                if(db.imageDao().findImageUri(imagePath) == null) {
                    db.albumDao().insertAlbumUris(new AlbumUri(albumPath, albumName));
                    db.imageDao().insertImageUris(new ImageUri(imagePath, imageName, albumPath));
                }

            }
        } else {
            Log.e("getGalleryPhotos", "error getting URIs");
        }
    }

    public BroadcastReceiver refreshDBMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refreshDB();
        }
    };

    public BroadcastReceiver deleteImagesInAlbumMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            db.albumDao().deleteAlbumUris(new AlbumUri(
                    intent.getStringExtra("album-path"),
                    intent.getStringExtra("album-name")));
            db.imageDao().deleteImagesInAlbum(intent.getStringExtra("album-path"));

            File dir = new File(intent.getStringExtra("album-path"));
            File[] childFileList = dir.listFiles();
            if (dir.exists()) {
                for (File childFile : childFileList) {
                    childFile.delete(); //하위 파일
                }
                dir.delete();
            }
        }
    };

    public BroadcastReceiver deleteImageMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            imageUri = (Uri) intent.getExtras().get("image-uri");
            imageUriToDel = new ImageUri(intent.getStringExtra("image-path"),
                    intent.getStringExtra("image-name"),
                    intent.getStringExtra("album-path"));

            removeImg(context, imageUri);


        }
    };

    public BroadcastReceiver deleteImageOnlyDbMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            db.imageDao().deleteImageUris(new ImageUri(intent.getStringExtra("image-path"),
                    intent.getStringExtra("image-name"),
                    intent.getStringExtra("album-path")));
        }
    };

    @Override
    public void onBackPressed() {

    }
}