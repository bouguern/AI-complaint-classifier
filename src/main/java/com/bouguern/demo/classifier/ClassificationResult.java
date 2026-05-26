package com.bouguern.demo.classifier;

import com.bouguern.demo.domain.enums.ComplaintCategory;
import com.bouguern.demo.domain.enums.UrgencyLevel;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ClassificationResult(

        @JsonPropertyDescription("Complaint category: BILLING, TECHNICAL, DELIVERY, ACCOUNT, or OTHER")
        ComplaintCategory category,

        @JsonPropertyDescription("Urgency level: LOW, MEDIUM, HIGH, or CRITICAL")
        UrgencyLevel urgency,

        @JsonPropertyDescription("Exact name of the team to route this complaint to")
        String suggestedTeam,

        @JsonPropertyDescription("One sentence explaining the classification decision")
        String reasoning
) {
}