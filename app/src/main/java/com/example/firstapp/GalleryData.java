package com.example.firstapp;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GalleryData {
    private ArrayList<AlbumData> albums;

    public GalleryData() {
        albums = new ArrayList<AlbumData>();
    }

    public AlbumData getAlbum(int index) {
        return this.albums.get(index);
    }

    public int getSize() {
        return this.albums.size();
    }

    public int addAlbum(AlbumData album) {
        this.albums.add(album);
        Collections.sort(this.albums);
        return this.albums.size();
    }

    public int deleteAlbum(int index) {
        this.albums.remove(index);
        return this.albums.size();
    }

    public int addImageURI(String albumName, Uri uri) {


        int index = this.albums.size() - 1;
        for (;index >= 0; index--) {
            if(this.albums.get(index).getAlbumName().equals(albumName)) break;
        }
        if (index < 0) {
            //새로운 앨범 추가 코드
            AlbumData newAlbum = new AlbumData();
            newAlbum.setAlbumName(albumName);
            newAlbum.addImageURI(uri.toString());
            this.addAlbum(newAlbum);
            return 0;
        } else {
            this.albums.get(index).addImageURI(uri.toString());
            return 1;
        }
    }
}
