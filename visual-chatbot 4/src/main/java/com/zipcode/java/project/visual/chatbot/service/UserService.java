package com.zipcode.java.project.visual.chatbot.service;

import com.zipcode.java.project.visual.chatbot.model.User;

public interface UserService {
    public User findUserByEmail(String email);

    public void saveUser(User user);
    
}
