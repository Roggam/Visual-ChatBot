package com.zipcode.java.project.visual.chatbot.controller;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.zipcode.java.project.visual.chatbot.api.google.DialogFlowClient;
import com.zipcode.java.project.visual.chatbot.api.google.VisionApiClient;
import com.zipcode.java.project.visual.chatbot.model.ChatCommand;
import com.zipcode.java.project.visual.chatbot.model.ChatConversion;
import com.zipcode.java.project.visual.chatbot.model.ChatMessageHolder;
import com.zipcode.java.project.visual.chatbot.model.User;
import com.zipcode.java.project.visual.chatbot.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileUrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@Scope("request")
public class UserController {

    @Autowired
    ChatMessageHolder chatMessageHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private DialogFlowClient dialogFlowClient;

    @Autowired
    private VisionApiClient visionApiClient;

    private String dirPath = "/images/custom/";

    private String imageBasePath = this.getClass().getClassLoader().getResource("static").getPath();

    @RequestMapping(value = {"/", "/login"}, method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();

        model.setViewName("user/login");
        return model;
    }

    @RequestMapping(value = {"/signup"}, method = RequestMethod.GET)
    public ModelAndView signup() {
        ModelAndView model = new ModelAndView();
        User user = new User();
        model.addObject("user", user);
        model.setViewName("user/signup");
        return model;
    }

    @RequestMapping(value = {"/signup"}, method = RequestMethod.POST)
    public ModelAndView createUser(@Validated User user, BindingResult bindingResult) {
        ModelAndView model = new ModelAndView();
        User userExists = userService.findUserByEmail(user.getEmail());
        if (userExists != null) {
            bindingResult.rejectValue("email", "error.user", "This email already exists!");
        }
        if (bindingResult.hasErrors()) {
            model.setViewName("user/signup");
        } else {
            userService.saveUser(user);
            model.addObject("msg", "User has been registered successfully!");
            model.addObject("user", new User());
            model.setViewName("user/login");
        }

        return model;
    }

    @RequestMapping(value = {"/home/home"}, method = RequestMethod.GET)
    public ModelAndView home() {
        ModelAndView model = new ModelAndView();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findUserByEmail(auth.getName());

        chatMessageHolder.setMessages(new ArrayList<>());
        chatMessageHolder.getMessages().add(new ChatCommand("A", "Hi "+ user.getFirstname()));
        ChatConversion chatConversion = new ChatConversion();
        chatConversion.setMessages(chatMessageHolder.getMessages());

        model.addObject("chatMessages", chatConversion);
        model.setViewName("home/home");
        return model;
    }

    @RequestMapping(value = {"/access_denied"}, method = RequestMethod.GET)
    public ModelAndView accessDenied() {
        ModelAndView model = new ModelAndView();
        model.setViewName("errors/access_denied");
        return model;
    }


    @RequestMapping(value = {"/chat"}, method = RequestMethod.POST)
    public ModelAndView chat(HttpSession session, @Validated ChatConversion requestedChat, @RequestParam("snapshot") MultipartFile snapshot, BindingResult bindingResult) throws IOException {

        ModelAndView model = new ModelAndView();

        ChatConversion chatConversion = new ChatConversion();
        chatConversion.setMessages(chatMessageHolder.refreshMessages());

        if (snapshot != null && !snapshot.isEmpty()) {

            snapshot.transferTo(new File(imageBasePath+dirPath+snapshot.getOriginalFilename()));
            chatConversion.getMessages().add(new ChatCommand("MQ", dirPath + snapshot.getOriginalFilename()));
            String label = visionApiClient.analyzeImage(new FileUrlResource(imageBasePath+dirPath+snapshot.getOriginalFilename()));
            chatConversion.getMessages().add(new ChatCommand("FA", label));

        }
        else {
            chatConversion.getMessages().add(new ChatCommand("Q", requestedChat.getQuestion()));
            Map<String, QueryResult> queryResultMap = dialogFlowClient.detectIntentTexts("vc-agent-powj", Arrays.asList(requestedChat.getQuestion()), session.getId(), "en-US");
            chatConversion.getMessages().add(new ChatCommand("FA", queryResultMap.get(requestedChat.getQuestion()).getFulfillmentText()));
        }

        model.addObject("chatMessages", chatConversion);
        model.setViewName("home/home");

        return model;
    }
}

