package com.bouguern.demo.classifier;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class ComplaintClassifier {

    private final ChatClient chatClient;

    //@Value("classpath:${app.classifier.prompt-file}")
    @Value("classpath:prompts/classifier-prompt.txt")
    private Resource promptResource;

    private String promptTemplate;

    @PostConstruct
    public void init() throws IOException {
        this.promptTemplate = promptResource.getContentAsString(StandardCharsets.UTF_8);
        log.info("Classifier prompt loaded ({} characters)", promptTemplate.length());
    }

    public ClassificationResult classify(String complaintText) {

        String rendered = promptTemplate.replace("{complaint}", complaintText);

        return chatClient.prompt()
                .user(rendered)
                .call()
                .entity(ClassificationResult.class);
    }
}