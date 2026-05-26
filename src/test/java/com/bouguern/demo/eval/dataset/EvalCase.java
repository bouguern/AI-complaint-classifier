package com.bouguern.demo.eval.dataset;

import com.bouguern.demo.domain.enums.ComplaintCategory;
import com.bouguern.demo.domain.enums.UrgencyLevel;

public record EvalCase(

        String id,
        String complaintText,
        ComplaintCategory expectedCategory,
        UrgencyLevel expectedUrgency,
        String expectedTeam
) {
}