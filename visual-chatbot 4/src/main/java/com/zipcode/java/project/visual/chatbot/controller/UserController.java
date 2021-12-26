package com.zipcode.java.project.visual.chatbot.controller;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.zipcode.java.project.visual.chatbot.api.google.DialogFlowClient;
import com.zipcode.java.project.visual.chatbot.model.ChatCommand;
import com.zipcode.java.project.visual.chatbot.model.RequestMessage;
import com.zipcode.java.project.visual.chatbot.model.ResponseMessage;
import com.zipcode.java.project.visual.chatbot.model.User;
import com.zipcode.java.project.visual.chatbot.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DialogFlowClient dialogFlowClient;

    @RequestMapping(value = {"/","/login"},method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model = new ModelAndView();
        
        model.setViewName("user/login");
        return model;
       }

    @RequestMapping(value= {"/signup"}, method=RequestMethod.GET)
    public ModelAndView signup() {
     ModelAndView model = new ModelAndView();
     User user = new User();
     model.addObject("user", user);
     model.setViewName("user/signup");
     return model;
 }

 @RequestMapping(value= {"/signup"}, method=RequestMethod.POST)
 public ModelAndView createUser(@Validated User user, BindingResult bindingResult) {
  ModelAndView model = new ModelAndView();
  User userExists = userService.findUserByEmail(user.getEmail());
  if(userExists != null) {
    bindingResult.rejectValue("email", "error.user", "This email already exists!");
   }
   if(bindingResult.hasErrors()) {
    model.setViewName("user/signup");
   } else {
    userService.saveUser(user);
    model.addObject("msg", "User has been registered successfully!");
    model.addObject("user", new User());
    model.setViewName("user/login");
   }
   
   return model;
}
@RequestMapping(value= {"/home/home"}, method=RequestMethod.GET)
 public ModelAndView home() {
  ModelAndView model = new ModelAndView();
  Authentication auth = SecurityContextHolder.getContext().getAuthentication();
  User user = userService.findUserByEmail(auth.getName());
  
  model.addObject("userName", user.getFirstname() + " " + user.getLastname());
  model.addObject("chatMessage", new ChatCommand());
  model.setViewName("home/home");
  return model;
 }
 
 @RequestMapping(value= {"/access_denied"}, method=RequestMethod.GET)
 public ModelAndView accessDenied() {
  ModelAndView model = new ModelAndView();
  model.setViewName("errors/access_denied");
  return model;
 }



    @RequestMapping(value= {"/chat"}, method=RequestMethod.POST)
    public ModelAndView chat(@Validated ChatCommand chatMessage, BindingResult bindingResult) throws IOException {
        ModelAndView model = new ModelAndView();

        Map<String, QueryResult> queryResultMap = dialogFlowClient.detectIntentTexts("vc-agent-powj", Arrays.asList(chatMessage.getQuestion()), "keerthana_srinivasan", "en-US");
        chatMessage.setAnswer(queryResultMap.get(chatMessage.getQuestion()).getFulfillmentText());

        model.addObject("userName", "keerthana_srinivasan");
        model.addObject("chatMessage", chatMessage);
        model.setViewName("home/home");

        return model;
    }
}

