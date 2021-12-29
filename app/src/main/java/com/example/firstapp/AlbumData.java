package com.example.firstapp;

import java.util.ArrayList;
import java.util.Collections;

public class AlbumData implements Comparable<AlbumData> {
    private String albumLocation, albumName;
    private ArrayList<String> imageURIs;

    {
        imageURIs = new ArrayList<String>();
    }

    public String getAlbumName() { return albumName; }

    public String getAlbumLocation() { return albumLocation; }

    public String getImageURI(int index) {
        return imageURIs.get(index);
    }

    public int getSize() { return this.imageURIs.size(); }

    public void setAlbumName(String albumName) { this.albumName = albumName; }

    public void setAlbumLocation(String albumLocation) { this.albumLocation = albumLocation; }

    public int addImageURI(String imgURI) {
        this.imageURIs.add(imgURI);
        Collections.sort(imageURIs);
        return this.imageURIs.size();
    }

    @Override
    public int compareTo(AlbumData albumData) {
        return this.getAlbumName().compareTo(albumData.getAlbumName());
    }
}
