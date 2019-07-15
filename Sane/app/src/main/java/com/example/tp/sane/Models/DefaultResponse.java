package com.example.tp.sane.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DefaultResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    public DefaultResponse(boolean error, String message) {
        this.error = error;
        this.message = message;
    }

    public boolean isErr() {
        return error;
    }

    public String getMsg() {
        return message;
    }
}
