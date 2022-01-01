package com.zipcode.java.project.visual.chatbot.controller;

import com.google.cloud.dialogflow.v2.QueryResult;
import com.zipcode.java.project.visual.chatbot.api.google.DialogFlowClient;
import com.zipcode.java.project.visual.chatbot.api.google.VisionApiClient;
import com.zipcode.java.project.visual.chatbot.model.RequestMessage;
import com.zipcode.java.project.visual.chatbot.model.ChatConversion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
public class ChatBotController {

    private final DialogFlowClient dialogFlowClient;

    private final VisionApiClient visionApiClient;

    public ChatBotController(DialogFlowClient dialogFlowClient, VisionApiClient visionApiClient) {
        this.dialogFlowClient = dialogFlowClient;
        this.visionApiClient = visionApiClient;
    }

    @PostMapping("/messages/")
    @ResponseStatus(HttpStatus.OK)
    public ChatConversion getChatBotResponse(@RequestBody RequestMessage requestMessage) throws IOException {

        Map<String, QueryResult> queryResultMap = dialogFlowClient.detectIntentTexts("vc-agent-powj", Arrays.asList(requestMessage.getQuestion()), requestMessage.getUserId(), "en-US");
        return new ChatConversion(queryResultMap.get(requestMessage.getQuestion()).getFulfillmentText());
    }

    @GetMapping("/visual/")
    @ResponseStatus(HttpStatus.OK)
    public ChatConversion getLabelDetection() throws IOException {

        String label = visionApiClient.extract();
        return new ChatConversion(label);
    }

    @PostMapping("/upload")
    public ResponseEntity<ChatConversion> uploadFiles(@RequestParam("snapshot") MultipartFile snapshot) {
        String message = "";
        try {

            System.out.println(snapshot.getResource());
            String label = visionApiClient.analyzeImage(snapshot.getResource());
            message = "Uploaded the files successfully: " + snapshot.getOriginalFilename();
            label = "File Processed: " + label;
            return ResponseEntity.status(HttpStatus.OK).body(new ChatConversion(label));
        } catch (Exception e) {
            message = "Fail to upload files!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ChatConversion(message));
        }
    }
}
