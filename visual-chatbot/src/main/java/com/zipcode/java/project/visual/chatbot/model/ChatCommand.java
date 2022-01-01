package com.zipcode.java.project.visual.chatbot.model;

public class ChatCommand {

    private String chatType;

    private String chatMessage;

    public ChatCommand(String type, String message) {
        this.chatType = type;
        this.chatMessage = message;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }
}
