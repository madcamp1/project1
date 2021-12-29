package com.example.firstapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GalleryData {
    private ArrayList<AlbumData> albums;

    public GalleryData() {
        albums = new ArrayList<AlbumData>();
    }

    public String getAlbumName(int index) {
        return this.albums.get(index).getAlbumName();
    }

    public int getSize() { return this.albums.size(); }

    public int addAlbum(AlbumData album) {
        this.albums.add(album);
        Collections.sort(this.albums);
        return this.albums.size();
    }

    public int deleteAlbum(int index) {
        this.albums.remove(index);
        return this.albums.size();
    }
}
