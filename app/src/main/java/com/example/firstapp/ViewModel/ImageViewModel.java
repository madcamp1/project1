package com.example.firstapp.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.firstapp.data.ImageRepository;
import com.example.firstapp.data.ImageUri;

import java.util.List;

public class ImageViewModel extends AndroidViewModel {
    //LiveData에는 mutable 자료형이 들어가면 안되므로 ArrayList를 List로 casting하여 사용하여야 한다.
    private LiveData<List<ImageUri>> imageUris;
    private ImageRepository imageRepository;
    private String albumName, albumPath;

    public ImageViewModel(@NonNull Application application) {
        super(application);
        imageRepository = new ImageRepository(application);
    }

    public void setupNewAlbum(String albumPath) {
        this.albumPath = albumPath;
        String[] tempSplit = albumPath.split("/");
        this.albumName = tempSplit[tempSplit.length - 1];
        imageRepository.setAlbumPath(albumPath);
        imageUris = imageRepository.getImageUris();
    }

    public void insert(ImageUri imageUri) {
        imageRepository.insert(imageUri);
    }

    public void delete(ImageUri imageUri) {
        imageRepository.delete(imageUri);
    }

    public void deleteImagesInAlbum() {
        imageRepository.deleteImagesInAlbum();
    }

    public LiveData<List<ImageUri>> getImageUris() {
        return imageUris;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumPath() {
        return albumPath;
    }
}
