package com.example.tp.sane.Models;

public class Entry {

    String title, text;

    public Entry(String title, String text) {
        this.title = title;
        this.text = text;
    }

    public String getEntryTitle() {
        return title;
    }

    public String getEntryText() {
        return text;
    }
}
