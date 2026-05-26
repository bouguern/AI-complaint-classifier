package com.bouguern.demo.classifier;

import com.bouguern.demo.domain.entity.ClassificationRecord;
import com.bouguern.demo.domain.entity.Complaint;
import com.bouguern.demo.domain.enums.ComplaintCategory;
import com.bouguern.demo.domain.enums.UrgencyLevel;

import java.util.UUID;

public record ClassificationResponse(

        UUID complaintId,
        ComplaintCategory category,
        UrgencyLevel urgency,
        String suggestedTeam,
        String reasoning,
        String promptVersion
) {
    public static ClassificationResponse from(Complaint complaint,
                                              ClassificationRecord record) {
        return new ClassificationResponse(
                complaint.getId(),
                record.getCategory(),
                record.getUrgency(),
                record.getSuggestedTeam(),
                record.getReasoning(),
                record.getPromptVersion()
        );
    }
}