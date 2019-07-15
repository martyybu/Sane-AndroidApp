package com.example.tp.sane.Models;

import com.google.gson.annotations.SerializedName;

public class UsernameGetResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("username")
    private String username;

    public UsernameGetResponse(boolean error, String username) {
        this.error = error;
        this.username = username;
    }

    public boolean isErr() {
        return error;
    }

    public String getUsername() {
        return username;
    }
}
