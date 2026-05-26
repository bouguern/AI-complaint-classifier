package com.bouguern.demo.eval;

import com.bouguern.demo.classifier.ClassificationResult;
import com.bouguern.demo.classifier.ComplaintClassifier;
import com.bouguern.demo.domain.entity.EvalRun;
import com.bouguern.demo.domain.repository.EvalRunRepository;
import com.bouguern.demo.eval.checks.RegressionScorer;
import com.bouguern.demo.eval.checks.SemanticCheck;
import com.bouguern.demo.eval.checks.StructuralCheck;
import com.bouguern.demo.eval.dataset.EvalCase;
import com.bouguern.demo.eval.dataset.EvalDatasetLoader;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Tag("eval")
@SpringBootTest
class EvalRunnerTest {

    private static final boolean RUN_LLM_JUDGE = false;

    @Autowired
    private ComplaintClassifier classifier;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private EvalRunRepository evalRunRepository;

    @Value("${app.classifier.prompt-version}")
    private String promptVersion;

    private record FailedCase(EvalCase evalCase, ClassificationResult actual) {
    }

    @Test
    void runEval() throws IOException {

        List<EvalCase> cases = EvalDatasetLoader.load();

        System.out.println("\n========================================");
        System.out.println("  EVAL RUN - prompt " + promptVersion);
        System.out.println("  Cases: " + cases.size());
        System.out.println("========================================\n");

        int structuralPassed = 0;
        int semanticPassed = 0;
        List<String> failureReport = new ArrayList<>();
        List<FailedCase> failedCases = new ArrayList<>();

        for (EvalCase evalCase : cases) {

            System.out.printf("  [%s] classifying... ", evalCase.id());

            ClassificationResult result;
            try {
                result = classifier.classify(evalCase.complaintText());
                Thread.sleep(4000);
            } catch (Exception ex) {
                System.out.println("ERROR - " + ex.getMessage());
                failureReport.add(evalCase.id() + ": classifier threw - " + ex.getMessage());
                continue;
            }

            StructuralCheck.Result structural = StructuralCheck.check(result);
            if (structural.passed()) {
                structuralPassed++;
            } else {
                System.out.println("STRUCTURAL FAIL - " + structural.failures());
                failureReport.add(evalCase.id() + ": structural - "
                        + String.join(", ", structural.failures()));
                continue;
            }

            SemanticCheck.Result semantic = SemanticCheck.check(result, evalCase);
            if (semantic.passed()) {
                semanticPassed++;
                System.out.println("PASS");
            } else {
                System.out.println("FAIL - " + String.join(", ", semantic.mismatches()));
                failureReport.add(evalCase.id() + ": "
                        + String.join(", ", semantic.mismatches()));
                failedCases.add(new FailedCase(evalCase, result));
            }
        }

        List<String> judgeReport = new ArrayList<>();
        if (RUN_LLM_JUDGE && !failedCases.isEmpty()) {
            System.out.println("\n-- LLM judge reviewing " + failedCases.size()
                    + " failed case(s) --\n");
            for (FailedCase fc : failedCases) {
                SemanticCheck.JudgeVerdict verdict = SemanticCheck.judge(
                        chatClient, fc.evalCase().complaintText(),
                        fc.actual(), fc.evalCase());
                String status = verdict.reasonable() ? "REASONABLE" : "INCORRECT";
                String line = String.format("  [%s] %s - %s",
                        fc.evalCase().id(), status, verdict.explanation());
                System.out.println(line);
                judgeReport.add(line);
            }
        }

        int total = cases.size();
        double score = total > 0 ? (double) semanticPassed / total * 100 : 0;

        EvalRun run = evalRunRepository.save(
                EvalRun.builder()
                        .promptVersion(promptVersion)
                        .totalCases(total)
                        .passed(semanticPassed)
                        .score(score)
                        .structuralPassed(structuralPassed)
                        .semanticPassed(semanticPassed)
                        .build()
        );

        System.out.println("\n========================================");
        System.out.println("  RESULTS - prompt " + promptVersion);
        System.out.println("========================================");
        System.out.printf("  Structural:  %d/%d%n", structuralPassed, total);
        System.out.printf("  Semantic:    %d/%d%n", semanticPassed, total);
        System.out.printf("  Score:       %.1f%%%n", score);

        if (!failureReport.isEmpty()) {
            System.out.println("\n  Failed cases:");
            failureReport.forEach(f -> System.out.println("    - " + f));
        }
        if (!judgeReport.isEmpty()) {
            System.out.println("\n  LLM judge verdicts:");
            judgeReport.forEach(System.out::println);
        }

        System.out.println("\n  Saved to eval_runs [id=" + run.getId() + "]");
        System.out.println("========================================\n");

        runRegressionCheck();
    }

    private void runRegressionCheck() {
        List<EvalRun> history = evalRunRepository.findAllByOrderByRunAtDesc();

        if (history.size() < 2) {
            System.out.println("  No previous eval run found - skipping regression check.\n");
            return;
        }

        EvalRun current = history.get(0);
        EvalRun previous = history.get(1);

        RegressionScorer.Comparison comparison =
                RegressionScorer.compare(previous, current);

        System.out.println("-- Regression check --");
        System.out.printf("  %s: %.1f%%%n", comparison.versionA(), comparison.scoreA());
        System.out.printf("  %s: %.1f%%  (delta: %+.1f%%)%n",
                comparison.versionB(), comparison.scoreB(), comparison.delta());
        System.out.println("  -> " + comparison.summary());
        System.out.println();
    }
}