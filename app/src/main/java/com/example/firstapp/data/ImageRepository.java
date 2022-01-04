package com.example.firstapp.data;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ImageRepository {
    private ImageDao imageDao;
    private LiveData<List<ImageUri>> imageUris;
    private String albumPath;

    public ImageRepository(Application application) {
        GalleryDatabase database = GalleryDatabase.getInstance(application);
        imageDao = database.imageDao();
    }

    public void setAlbumPath(String albumPath) {
        this.albumPath = albumPath;
        imageUris = imageDao.getAllImageUrisInAlbum(albumPath);
    }

    public void insert(ImageUri imageUri) {
        new InsertImageAsyncTask(imageDao).execute(imageUri);
    }

    public void delete(ImageUri imageUri) {
        new DeleteImageAsyncTask(imageDao).execute(imageUri);
    }

    public void deleteImagesInAlbum() {
        new DeleteImagesInAlbumAsyncTask(imageDao).execute(albumPath);
    }

    public LiveData<List<ImageUri>> getImageUris() {
        return imageUris;
    }

    private static class InsertImageAsyncTask extends AsyncTask<ImageUri, Void, Void> {
        private ImageDao imageDao;

        private InsertImageAsyncTask(ImageDao imageDao) {
            this.imageDao = imageDao;
        }

        @Override
        protected Void doInBackground(ImageUri... imageUris) {
            imageDao.insertImageUris(imageUris[0]);
            return null;
        }
    }

    private static class DeleteImageAsyncTask extends AsyncTask<ImageUri, Void, Void> {
        private ImageDao imageDao;

        private DeleteImageAsyncTask(ImageDao imageDao) {
            this.imageDao = imageDao;
        }

        @Override
        protected Void doInBackground(ImageUri... imageUris) {
            imageDao.deleteImageUris(imageUris[0]);
            return null;
        }
    }

    private static class DeleteImagesInAlbumAsyncTask extends AsyncTask<String, Void, Void> {
        private ImageDao imageDao;

        private DeleteImagesInAlbumAsyncTask(ImageDao imageDao) {
            this.imageDao = imageDao;
        }

        @Override
        protected Void doInBackground(String... albumPath) {
            imageDao.deleteImagesInAlbum(albumPath[0]);
            return null;
        }
    }
}
