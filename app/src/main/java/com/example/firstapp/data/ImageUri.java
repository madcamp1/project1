package com.example.firstapp.data;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;

@Entity(tableName = "images", indices = {@Index(value = {"image_path"}, unique = true)}, primaryKeys = {"image_path"})
public class ImageUri {

    @ColumnInfo(name = "album_path")
    private String albumPath;

    @ColumnInfo(name = "image_name")
    private String imageName;

    @NonNull
    @ColumnInfo(name = "image_path")
    private String imagePath;

    public ImageUri(String imagePath, String imageName, String albumPath) {
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.albumPath = albumPath;
    }

    public String getAlbumPath() {
        return albumPath;
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}

