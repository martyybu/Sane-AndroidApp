package com.example.tp.sane.Models;

import java.util.List;

public class EntryResponse {

    private boolean error;

    private List<Entry> entries;

    public EntryResponse(boolean error, List<Entry> entries) {
        this.error = error;
        this.entries = entries;
    }

    public boolean isErr() {
        return error;
    }

    public List<Entry> getEntries() {
        return entries;
    }
}