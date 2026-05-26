package com.bouguern.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ComplaintCategory {

    BILLING,
    TECHNICAL,
    DELIVERY,
    ACCOUNT,
    OTHER;

    @JsonCreator
    public static ComplaintCategory fromString(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return ComplaintCategory.valueOf(value.trim().toUpperCase());
    }
}