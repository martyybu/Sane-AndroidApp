package com.example.tp.sane.Models;

import java.util.List;

public class ChatGetResponse {

    private boolean error;

    private List<Chat> chats;

    public ChatGetResponse(boolean error, List<Chat> chats) {
        this.error = error;
        this.chats = chats;
    }

    public boolean isErr() {
        return error;
    }

    public List<Chat> getChats() {
        return chats;
    }
}
