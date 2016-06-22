package com.example.gabriel.iapp.Utils;

import android.graphics.Bitmap;

public class Fotos_Clase {
    private Bitmap image;
    private String title;
    private String url;

    public Fotos_Clase(Bitmap image, String title, String url) {
        super();
        this.image = image;
        this.title = title;
        this.url = url;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.title = title;
    }
}