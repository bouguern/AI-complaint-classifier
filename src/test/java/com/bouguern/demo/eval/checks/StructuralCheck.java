package com.bouguern.demo.eval.checks;

import com.bouguern.demo.classifier.ClassificationResult;

import java.util.ArrayList;
import java.util.List;

public final class StructuralCheck {

    private static final List<String> VALID_TEAMS = List.of(
            "Billing Support Team",
            "Technical Support Team",
            "Logistics Team",
            "Account Management Team",
            "General Support Team"
    );

    private StructuralCheck() {
    }

    public record Result(boolean passed, List<String> failures) {

        public static Result pass() {
            return new Result(true, List.of());
        }

        public static Result fail(List<String> failures) {
            return new Result(false, failures);
        }
    }

    public static Result check(ClassificationResult result) {
        if (result == null) {
            return Result.fail(List.of("Result is null - AI returned nothing"));
        }

        List<String> failures = new ArrayList<>();

        if (result.category() == null) {
            failures.add("category is null");
        }
        if (result.urgency() == null) {
            failures.add("urgency is null");
        }
        if (result.suggestedTeam() == null || result.suggestedTeam().isBlank()) {
            failures.add("suggestedTeam is null or blank");
        } else if (!VALID_TEAMS.contains(result.suggestedTeam())) {
            failures.add("suggestedTeam '" + result.suggestedTeam()
                    + "' is not in the allowed list " + VALID_TEAMS);
        }
        if (result.reasoning() == null || result.reasoning().isBlank()) {
            failures.add("reasoning is null or blank");
        }

        return failures.isEmpty() ? Result.pass() : Result.fail(failures);
    }
}