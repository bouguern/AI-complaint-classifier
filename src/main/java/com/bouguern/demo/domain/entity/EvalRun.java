package com.bouguern.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eval_runs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvalRun {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "prompt_version", nullable = false)
    private String promptVersion;

    @Column(name = "total_cases", nullable = false)
    private int totalCases;

    @Column(nullable = false)
    private int passed;

    @Column(nullable = false)
    private double score;

    @Column(name = "structural_passed")
    private int structuralPassed;

    @Column(name = "semantic_passed")
    private int semanticPassed;

    @CreationTimestamp
    @Column(name = "run_at", updatable = false)
    private LocalDateTime runAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvalRun that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "EvalRun{promptVersion='" + promptVersion + "', score=" + score + "}";
    }
}