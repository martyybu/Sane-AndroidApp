package com.example.tp.sane.Models;

public class Message {

    private int Message_ID;
    private int ID;
    private String message;

    public Message(int message_ID, int ID, String message) {
        Message_ID = message_ID;
        this.ID = ID;
        this.message = message;
    }

    public int getMessage_ID() {
        return Message_ID;
    }

    public int getID() { return ID; }

    public String getMessage() {
        return message;
    }
}
