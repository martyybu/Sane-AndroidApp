package com.example.tp.sane.Models;

import java.util.List;

public class EntryTextGetResponse {

    private boolean error;

    private String text;

    public EntryTextGetResponse(boolean error, String text) {
        this.error = error;
        this.text = text;
    }

    public boolean isErr() {
        return error;
    }

    public String getFullEntryText() {
        return text;
    }
}