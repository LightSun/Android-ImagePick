package com.heaven7.android.imagepick0;

public class ImageParam{

    public byte[] data;
    public int width;
    public int height;

    public ImageParam(){

    }
    public void set(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public ImageParam(ImageParam src){
        this.data = src.data;
        this.width = src.width;
        this.height = src.height;
    }

    public boolean isValid(){
        return data != null;
    }
}
