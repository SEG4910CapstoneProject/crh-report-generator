package me.t65.reportgenerator.db.postgres.repository;

import me.t65.reportgenerator.db.postgres.entities.ReportEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportEntityRepository extends JpaRepository<ReportEntity, Integer> {}
