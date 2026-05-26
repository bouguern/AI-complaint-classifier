package com.bouguern.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UrgencyLevel {

    LOW,
    MEDIUM,
    HIGH,
    CRITICAL;

    @JsonCreator
    public static UrgencyLevel fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return UrgencyLevel.valueOf(value.trim().toUpperCase());
    }
}