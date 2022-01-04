package com.example.firstapp.data;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "albums", indices = {@Index(value = {"album_path"}, unique = true)}, primaryKeys = {"album_path"})
public class AlbumUri {

    @ColumnInfo(name = "album_name")
    private String albumName;

    @NonNull
    @ColumnInfo(name = "album_path")
    private String albumPath;

    public AlbumUri(String albumPath, String albumName) {
        this.albumName = albumName;
        this.albumPath = albumPath;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }
}
