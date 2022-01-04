package com.example.firstapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.ArrayList;

@Database(entities = {AlbumUri.class, ImageUri.class}, version = 1)
public abstract class GalleryDatabase extends RoomDatabase {
     private static GalleryDatabase instance;

     public abstract AlbumDao albumDao();
     public abstract ImageDao imageDao();
     private static Context activityContext;

     public static synchronized GalleryDatabase getInstance(Context context) {
         Log.d("GalleryDatabase", "getInstance called");
         activityContext = context;
         if (instance == null) {
             instance = Room.databaseBuilder(context.getApplicationContext(),
                     GalleryDatabase.class, "gallery_database")
                     .fallbackToDestructiveMigration()
                     .addCallback(roomCallback)
                     .allowMainThreadQueries()
                     .build();
         }
         return instance;
     }

     private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
         @Override
         public void onCreate(@NonNull SupportSQLiteDatabase db) {
             super.onCreate(db);
             //초기값 설정 시 코드 여기에 작성
             new PopulateDbAsyncTask(instance).execute(activityContext);
         }
     };

     private static class PopulateDbAsyncTask extends AsyncTask<Context, Void, Void> {
         private ImageDao imageDao;
         private AlbumDao albumDao;
         private  PopulateDbAsyncTask(GalleryDatabase db) {
             imageDao = db.imageDao();
             albumDao = db.albumDao();
         }

         @Override
         protected Void doInBackground(Context... contexts) {
             ArrayList<Uri> photos = new ArrayList<Uri>();
             String[] colums = new String[] {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
             Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
             String orderBy = MediaStore.Images.Media._ID;
             ContentResolver contentResolver = contexts[0].getContentResolver();
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
                     Log.e("albumPath: ", albumPath);
                     Log.e("imagePath: ", imagePath);

                     if(imageDao.findImageUri(imagePath) == null) {
                         albumDao.insertAlbumUris(new AlbumUri(albumPath, albumName));
                         imageDao.insertImageUris(new ImageUri(imagePath, imageName, albumPath));
                     }
                 }
             } else {
                 Log.e("getGalleryPhotos", "error getting URIs");
             }
             return null;
         }
     }
}
