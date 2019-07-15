package com.example.tp.sane.Models;

public class Chat {

    int Chat_ID, UserID, TherapistID;

    public Chat(int Chat_ID, int UserID, int TherapistID) {
        this.Chat_ID = Chat_ID;
        this.UserID = UserID;
        this.TherapistID = TherapistID;
    }

    public int getChat_ID() {
        return Chat_ID;
    }

    public int getUserID() {
        return UserID;
    }

    public int getTherapistID() {
        return TherapistID;
    }
}
