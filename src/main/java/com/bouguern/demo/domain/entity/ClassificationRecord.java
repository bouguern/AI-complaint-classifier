package com.bouguern.demo.domain.entity;

import com.bouguern.demo.domain.enums.ComplaintCategory;
import com.bouguern.demo.domain.enums.UrgencyLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "classification_records")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private Complaint complaint;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComplaintCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UrgencyLevel urgency;

    @Column(name = "suggested_team", nullable = false)
    private String suggestedTeam;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "prompt_version", nullable = false)
    private String promptVersion;

    @CreationTimestamp
    @Column(name = "classified_at", updatable = false)
    private LocalDateTime classifiedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassificationRecord that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ClassificationRecord{id=" + id
                + ", category=" + category
                + ", urgency=" + urgency + "}";
    }
}