package com.bouguern.demo.domain.repository;

import com.bouguern.demo.domain.entity.EvalRun;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvalRunRepository extends JpaRepository<EvalRun, UUID> {

    List<EvalRun> findAllByOrderByRunAtDesc();
}