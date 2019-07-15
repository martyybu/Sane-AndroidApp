package com.example.tp.sane.Models;

import java.util.List;

public class MessageGetResponse {

    private boolean error;

    private List<Message> messages;

    public MessageGetResponse(boolean error, List<Message> messages) {
        this.error = error;
        this.messages = messages;
    }

    public boolean isErr() {
        return error;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
