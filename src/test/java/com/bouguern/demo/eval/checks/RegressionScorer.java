package com.bouguern.demo.eval.checks;

import com.bouguern.demo.domain.entity.EvalRun;

public final class RegressionScorer {

    private RegressionScorer() {
    }

    public record Comparison(
            String versionA,
            double scoreA,
            String versionB,
            double scoreB,
            double delta,
            boolean improved,
            String summary
    ) {
    }

    public static Comparison compare(EvalRun baseline, EvalRun candidate) {

        double delta = candidate.getScore() - baseline.getScore();
        boolean improved = delta > 0;

        String summary;
        if (Math.abs(delta) < 0.1) {
            summary = "No meaningful change (delta < 0.1%)";
        } else if (improved) {
            summary = String.format("Improved by +%.1f%% - safe to deploy", delta);
        } else {
            summary = String.format("Regressed by %.1f%% - DO NOT deploy", delta);
        }

        return new Comparison(
                baseline.getPromptVersion(), baseline.getScore(),
                candidate.getPromptVersion(), candidate.getScore(),
                delta, improved, summary
        );
    }
}