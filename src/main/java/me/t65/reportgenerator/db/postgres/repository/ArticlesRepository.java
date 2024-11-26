package me.t65.reportgenerator.db.postgres.repository;

import me.t65.reportgenerator.db.postgres.entities.ArticlesEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ArticlesRepository extends JpaRepository<ArticlesEntity, UUID> {
    @Query("select a from ArticlesEntity a where a.dateIngested > ?1")
    Page<ArticlesEntity> findByDateIngestedAfter(Instant dateIngested, Pageable pageable);
}
