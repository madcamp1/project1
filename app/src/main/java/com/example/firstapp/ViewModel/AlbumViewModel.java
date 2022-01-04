package com.example.firstapp.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.firstapp.data.AlbumRepository;
import com.example.firstapp.data.AlbumUri;

import java.util.List;

public class AlbumViewModel extends AndroidViewModel {
    private LiveData<List<AlbumUri>> albumUris;
    private AlbumRepository albumRepository;

    public AlbumViewModel(@NonNull Application application) {
        super(application);
        albumRepository = new AlbumRepository(application);
        albumUris = albumRepository.getAlbumUris();
        Log.e("albumUris", albumUris.toString());
    }

    public void insert(AlbumUri albumUri) {
        albumRepository.insert(albumUri);
    }

    public void delete(AlbumUri albumUri) {
        albumRepository.delete(albumUri);
    }

    public LiveData<List<AlbumUri>> getAlbumUris() {
        return albumUris;
    }

}
