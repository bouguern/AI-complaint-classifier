package com.bouguern.demo.domain.repository;

import com.bouguern.demo.domain.entity.ClassificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassificationRepository extends JpaRepository<ClassificationRecord, UUID> {

    List<ClassificationRecord> findByPromptVersion(String promptVersion);
}