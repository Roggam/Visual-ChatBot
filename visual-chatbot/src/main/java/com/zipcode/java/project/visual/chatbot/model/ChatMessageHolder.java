package com.zipcode.java.project.visual.chatbot.model;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope(scopeName="session")
public class ChatMessageHolder {

    private List<ChatCommand> messages;

    public ChatMessageHolder() {
    }

    public List<ChatCommand> getMessages() {
        return messages;
    }

    public List<ChatCommand> refreshMessages() {
        for (ChatCommand message : messages) {
            if (message.getChatType().equals("FA")) {
                message.setChatType("A");
            }
        }
        return messages;
    }

    public void setMessages(List<ChatCommand> messages) {
        this.messages = messages;
    }
}
