package com.example.firstapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void insertAlbumUris(AlbumUri... albumUri);

    @Delete
    public void deleteAlbumUris(AlbumUri... AlbumUris);

    @Query("SELECT * FROM albums")
    public LiveData<List<AlbumUri>> getAllAlbumUris();
}
