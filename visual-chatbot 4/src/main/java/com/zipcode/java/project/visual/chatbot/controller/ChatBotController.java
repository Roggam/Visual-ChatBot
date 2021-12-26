package com.zipcode.java.project.visual.chatbot.controller;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.zipcode.java.project.visual.chatbot.api.google.DialogFlowClient;
import com.zipcode.java.project.visual.chatbot.model.RequestMessage;
import com.zipcode.java.project.visual.chatbot.model.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final DialogFlowClient dialogFlowClient;

    public ChatBotController(DialogFlowClient dialogFlowClient) {
        this.dialogFlowClient = dialogFlowClient;
    }

    @PostMapping("/messages/")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMessage getChatBotResponse(@RequestBody RequestMessage requestMessage) throws IOException {

        Map<String, QueryResult> queryResultMap = dialogFlowClient.detectIntentTexts("vc-agent-powj", Arrays.asList(requestMessage.getQuestion()), requestMessage.getUserId(), "en-US");
        return new ResponseMessage(queryResultMap.get(requestMessage.getQuestion()).getFulfillmentText());
    }
}
