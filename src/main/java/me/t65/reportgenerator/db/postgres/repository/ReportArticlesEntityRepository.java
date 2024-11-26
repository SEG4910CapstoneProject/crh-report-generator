package me.t65.reportgenerator.db.postgres.repository;

import me.t65.reportgenerator.db.postgres.entities.ReportArticlesEntity;
import me.t65.reportgenerator.db.postgres.entities.id.ReportArticlesId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportArticlesEntityRepository
        extends JpaRepository<ReportArticlesEntity, ReportArticlesId> {}
