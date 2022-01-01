package com.example.firstapp;

import android.net.Uri;

import java.util.ArrayList;

public class ImageUris {

    private ArrayList<String> uris = new ArrayList<String>();

    public Uri getUri(int index) {
        return Uri.parse(uris.get(index));
    }

    public void addUri(String uri) {
        this.uris.add(uri);
    }

    public int findUri(String uri) {
        int i = uris.size()-1;
        for(;i >= 0; i--) {
            if (uris.get(i).equals(uri)) break;
        }
        return i;
    }

    public int getSize() { return this.uris.size(); }

    @Override
    public String toString() {
        return "ImageUris{" +
                "uris=" + uris +
                '}';
    }
}
