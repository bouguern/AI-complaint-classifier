package com.bouguern.demo.api;

import com.bouguern.demo.classifier.ClassificationResponse;
import com.bouguern.demo.classifier.ClassifierService;
import com.bouguern.demo.classifier.ComplaintRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ClassifierController {

    private final ClassifierService classifierService;

    @PostMapping
    public ResponseEntity<ClassificationResponse> classify(
            @Valid @RequestBody ComplaintRequest request) {

        log.info("Received complaint from source={}, customerRef={}",
                request.source(), request.customerRef());

        ClassificationResponse response = classifierService.classify(request);
        return ResponseEntity.ok(response);
    }
}