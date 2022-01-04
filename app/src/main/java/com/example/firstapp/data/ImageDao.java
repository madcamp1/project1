package com.example.firstapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertImageUris(ImageUri... imageUris);

    @Delete
    public void deleteImageUris(ImageUri... imageUris);

    @Query("DELETE FROM images WHERE album_path = :albumPath")
    public void deleteImagesInAlbum(String albumPath);

//    @Query("SELECT * FROM images")
//    public LiveData<List<ImageUri>> getAllImageUris();

    @Query("SELECT * FROM images WHERE album_path = :albumPath")
    public LiveData<List<ImageUri>> getAllImageUrisInAlbum(String albumPath);

    @Query("SELECT image_path FROM images WHERE image_path = :imagePath")
    public String findImageUri(String imagePath);
}
