package com.example.firstapp;

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

    public int addImageURI(String imgURI) {
        int index = this.albums.size() - 1;
        String[] splitURIs = imgURI.split("/");
        for (;index >= 0; index--) {
            if(this.albums.get(index).getAlbumName().equals(splitURIs[splitURIs.length - 2])) break;
        }
        if (index < 0) {
            //새로운 앨범 추가 코드
            AlbumData newAlbum = new AlbumData();
            newAlbum.setAlbumName(splitURIs[splitURIs.length - 2]);
            String albumLocation = "";
            for (int i = 0; i < splitURIs.length - 1; i++) {
                albumLocation += splitURIs[i];
            }
            newAlbum.setAlbumLocation(albumLocation);
            newAlbum.addImageURI(imgURI);
            this.addAlbum(newAlbum);
            return 0;
        } else {
            this.albums.get(index).addImageURI(imgURI);
            return 1;
        }
    }
}
