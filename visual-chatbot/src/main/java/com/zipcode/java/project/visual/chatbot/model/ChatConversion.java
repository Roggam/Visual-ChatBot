package com.zipcode.java.project.visual.chatbot.model;

import java.util.ArrayList;
import java.util.List;

public class ChatConversion {

    private String question;

    private List<ChatCommand> messages;

    public ChatConversion() {
    }

    public ChatConversion(String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(new ChatCommand("answer", message));
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<ChatCommand> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatCommand> messages) {
        this.messages = messages;
    }
}
