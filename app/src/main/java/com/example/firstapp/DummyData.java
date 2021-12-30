package com.example.firstapp;

public class DummyData {
    static AlbumData album1 = new AlbumData() {
        {

            setAlbumName("album1");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
        }
    };
    static AlbumData album2 = new AlbumData() {
        {

            setAlbumName("album2");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
        }
    };
    static AlbumData album3 = new AlbumData() {
        {

            setAlbumName("album3");
            addImageURI("app/src/main/res/imgs/resource01.png");
            addImageURI("app/src/main/res/imgs/resource01.png");
        }
    };
    static GalleryData glData = new GalleryData() {
        {
            addAlbum(album1);
            addAlbum(album3);
            addAlbum(album2);
        }
    };
}
