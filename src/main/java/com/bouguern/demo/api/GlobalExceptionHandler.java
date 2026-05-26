package com.bouguern.demo.api;

import com.bouguern.demo.classifier.ClassificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            MethodArgumentNotValidException ex) {

        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .badRequest()
                .body(errorBody("Validation failed", details));
    }

    @ExceptionHandler(ClassificationException.class)
    public ResponseEntity<Map<String, Object>> handleClassification(
            ClassificationException ex) {

        log.error("Classification failed: {}", ex.getMessage(), ex);

        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(errorBody("Classification failed", ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        return ResponseEntity
                .internalServerError()
                .body(errorBody("Internal error", "An unexpected error occurred"));
    }

    private Map<String, Object> errorBody(String error, String detail) {
        return Map.of(
                "error", error,
                "detail", detail,
                "timestamp", LocalDateTime.now().toString()
        );
    }
}