package com.bouguern.demo.classifier;

import jakarta.validation.constraints.NotBlank;

public record ComplaintRequest(

        @NotBlank(message = "Complaint text is required")
        String text,

        String customerRef,

        String source
) {
}