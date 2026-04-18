package com.codecollab.code_collab;

public class Message {

    private String type;    // "code" or "chat"
    private String room;    // room id (e.g. room1)
    private String user;    // username
    private String content; // code or chat message

    // Default constructor (IMPORTANT for JSON parsing)
    public Message() {
    }

    // Constructor
    public Message(String type, String room, String user, String content) {
        this.type = type;
        this.room = room;
        this.user = user;
        this.content = content;
    }

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}