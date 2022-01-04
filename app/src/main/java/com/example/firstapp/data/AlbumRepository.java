package com.example.firstapp.data;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

public class AlbumRepository {
    private AlbumDao albumDao;
    private LiveData<List<AlbumUri>> albumUris;

    public AlbumRepository(Application application) {
        GalleryDatabase database = GalleryDatabase.getInstance(application);
        albumDao = database.albumDao();
        albumUris = albumDao.getAllAlbumUris();
    }

    public void insert(AlbumUri albumUri) {
        Log.d("AlbumRepository insert","Task");
        new InsertAlbumAsyncTask(albumDao).execute(albumUri);
    }

    public void delete(AlbumUri albumUri) {
        new DeleteAlbumAsyncTask(albumDao).execute(albumUri);
    }

    public LiveData<List<AlbumUri>> getAlbumUris() {
        return albumUris;
    }

    private static class InsertAlbumAsyncTask extends AsyncTask<AlbumUri, Void, Void> {
        private AlbumDao albumDao;

        private InsertAlbumAsyncTask(AlbumDao albumDao) {
            this.albumDao = albumDao;
        }

        @Override
        protected Void doInBackground(AlbumUri... albumUris) {
            albumDao.insertAlbumUris(albumUris[0]);
            return null;
        }
    }

    private static class DeleteAlbumAsyncTask extends AsyncTask<AlbumUri, Void, Void> {
        private AlbumDao albumDao;

        private DeleteAlbumAsyncTask(AlbumDao albumDao) {
            this.albumDao = albumDao;
        }

        @Override
        protected Void doInBackground(AlbumUri... albumUris) {
            albumDao.deleteAlbumUris(albumUris[0]);
            return null;
        }
    }

}
