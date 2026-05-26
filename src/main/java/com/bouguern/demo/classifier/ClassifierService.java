package com.bouguern.demo.classifier;

import com.bouguern.demo.domain.entity.ClassificationRecord;
import com.bouguern.demo.domain.entity.Complaint;
import com.bouguern.demo.domain.enums.ComplaintStatus;
import com.bouguern.demo.domain.repository.ClassificationRepository;
import com.bouguern.demo.domain.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassifierService {

    private final ComplaintRepository complaintRepository;
    private final ClassificationRepository classificationRepository;
    private final ComplaintClassifier classifier;

    @Value("${app.classifier.prompt-version}")
    private String promptVersion;

    @Transactional
    public ClassificationResponse classify(ComplaintRequest request) {

        Complaint complaint = complaintRepository.save(
                Complaint.builder()
                        .text(request.text())
                        .customerRef(request.customerRef())
                        .source(request.source())
                        .build()
        );
        log.info("Complaint saved [id={}], calling classifier...", complaint.getId());

        ClassificationResult result;
        try {
            result = classifier.classify(request.text());
        } catch (Exception ex) {
            throw new ClassificationException(
                    "AI classifier failed for complaint " + complaint.getId(), ex);
        }

        validateResult(result, complaint.getId());
        log.info("Classified [id={}]: category={}, urgency={}, team={}",
                complaint.getId(), result.category(),
                result.urgency(), result.suggestedTeam());

        ClassificationRecord myRecord = classificationRepository.save(
                ClassificationRecord.builder()
                        .complaint(complaint)
                        .category(result.category())
                        .urgency(result.urgency())
                        .suggestedTeam(result.suggestedTeam())
                        .reasoning(result.reasoning())
                        .promptVersion(promptVersion)
                        .build()
        );

        complaint.setStatus(ComplaintStatus.CLASSIFIED);

        return ClassificationResponse.from(complaint, myRecord);
    }

    private void validateResult(ClassificationResult result, UUID complaintId) {
        if (result == null) {
            throw new ClassificationException(
                    "AI returned null for complaint " + complaintId);
        }
        if (result.category() == null || result.urgency() == null) {
            throw new ClassificationException(
                    "AI returned an incomplete classification for complaint " + complaintId
                            + " - category=" + result.category()
                            + ", urgency=" + result.urgency());
        }
        if (result.suggestedTeam() == null || result.suggestedTeam().isBlank()) {
            throw new ClassificationException(
                    "AI returned an empty suggestedTeam for complaint " + complaintId);
        }
    }
}