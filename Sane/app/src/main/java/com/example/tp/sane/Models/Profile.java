package com.example.tp.sane.Models;

public class Profile {

    Integer Image_ID;
    String about;

    public Profile(Integer Image_ID, String about) {
        this.Image_ID = Image_ID;
        this.about = about;
    }

    public Integer getImage_ID() {
        return Image_ID;
    }

    public String getAbout() {
        return about;
    }
}
