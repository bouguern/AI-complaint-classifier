package com.bouguern.demo.eval.checks;

import com.bouguern.demo.classifier.ClassificationResult;
import com.bouguern.demo.eval.dataset.EvalCase;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;

import java.util.ArrayList;
import java.util.List;

public final class SemanticCheck {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SemanticCheck() {
    }

    public record Result(
            boolean passed,
            boolean categoryMatch,
            boolean urgencyMatch,
            boolean teamMatch,
            List<String> mismatches
    ) {
    }

    public static Result check(ClassificationResult actual, EvalCase expected) {
        List<String> mismatches = new ArrayList<>();

        boolean categoryMatch = actual.category() == expected.expectedCategory();
        if (!categoryMatch) {
            mismatches.add("category: expected " + expected.expectedCategory()
                    + ", got " + actual.category());
        }

        boolean urgencyMatch = actual.urgency() == expected.expectedUrgency();
        if (!urgencyMatch) {
            mismatches.add("urgency: expected " + expected.expectedUrgency()
                    + ", got " + actual.urgency());
        }

        boolean teamMatch = actual.suggestedTeam() != null
                && actual.suggestedTeam().equals(expected.expectedTeam());
        if (!teamMatch) {
            mismatches.add("team: expected '" + expected.expectedTeam()
                    + "', got '" + actual.suggestedTeam() + "'");
        }

        boolean passed = categoryMatch && urgencyMatch && teamMatch;
        return new Result(passed, categoryMatch, urgencyMatch, teamMatch, mismatches);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JudgeVerdict(boolean reasonable, String explanation) {
    }

    public static JudgeVerdict judge(ChatClient chatClient,
                                     String complaintText,
                                     ClassificationResult actual,
                                     EvalCase expected) {

        String prompt = """
                You are a quality assurance judge for a customer complaint classifier.

                CUSTOMER COMPLAINT:
                "%s"

                AI CLASSIFIED AS:
                  Category: %s
                  Urgency: %s
                  Team: %s
                  Reasoning: %s

                EXPECTED ANSWER:
                  Category: %s
                  Urgency: %s
                  Team: %s

                Is the AI's classification reasonable for this complaint?
                The AI may differ from the expected answer and still be acceptable.

                Respond ONLY with this JSON, no other text:
                {"reasonable": true, "explanation": "one sentence why"}
                """.formatted(
                complaintText,
                actual.category(), actual.urgency(),
                actual.suggestedTeam(), actual.reasoning(),
                expected.expectedCategory(), expected.expectedUrgency(),
                expected.expectedTeam()
        );

        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            String clean = response
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            return MAPPER.readValue(clean, JudgeVerdict.class);

        } catch (Exception ex) {
            return new JudgeVerdict(false, "Judge call failed: " + ex.getMessage());
        }
    }
}